/* 
 * File: Paging.java
 * This file contains all the methods used to provide paging functions.
 * The checkAddress() is the method called by the kernel readAddr and writeAddr
 * methodes that reads and writes addresses. 
 */

/*
 * Student Name:
 * Student Number: 
 */

/**
 * Two major data structures have been defined in this class
 * 
 * Page Table (pageTable) The variable pageTable references an array of object
 * elements PageTableEntry (see PageTableEntry.java for details on its members).
 * Basically a PagetableEntry object contains the virtual page number, the
 * physical frame number, the present (P) bit, and other control bits (read and
 * write bits) and two times - the time the virtual page was loaded into memory,
 * and the time the page was last accessed. The address range of the virtual
 * page is also store in the entry.
 * 
 * Page buffer (pageBuffer) This is a simple integer array used to create a
 * cyclic buffer for implementing a FIFO buffer. It is used for implementing the
 * FIFO replacement algorithm. It contains the values of the virtual pages
 * (indexes into the Page Table above) of the virtual pages that have been
 * allocated physical frames.
 */

public class Paging {
	
	public PageTableEntry[] pageTable; // the Page Table
	public int[] pageBuffer; // Page buffer - for replacement algorithm
	private int numPhysicalPages; // Number of physical pages in the system
	public int numVirtualPages; // Number of virtual pages occupied by process
	public byte offsetBits; // defines the number of bits in the addr the
	// represents offset
	private int bufPointer; // pointer into page buffer (circular)
	private Kernel kernel; // reference to kernel object
	public ControlPanel controlPanel; // for updating the control panel
	private String replaceOption; // option for selecting replace algorithm
	private int numPageFaults; // Number of page faults incurred
	private int numAdrViolations; // number of address violations incurred
	private int numAccesses; // number of memory accesses
	
	/*
	 * The following constructor gets a lot of data from the kernel that creates
	 * a paging object. the numFrames, numPages, offbts, and block information
	 * are defined from the configuration file. The other parameters are
	 * references to other objects, used to access their data and methods. For
	 * example, the ctrPan parameter provides the reference to the control panel
	 * to update the simulator display
	 */
	
	public Paging(String replOpt, Kernel krnl) // Constructor
	{
		replaceOption = replOpt; // replacement option from command line (FIFO,
		// LRU, or CLOCK)
		kernel = krnl; // reference to kernel object
	}
	
	// initialization
	public void init(int numFrames, int numPages, byte offbts, long block) {
		int i;
		long high;
		long low;
		
		numVirtualPages = numPages; // number of virtual pages
		numPhysicalPages = numFrames; // number of physical frames
		offsetBits = offbts; // number of offset bits in the address
		
		// Setup the page table
		pageTable = new PageTableEntry[numPages]; // defines array of page table
		// entries
		for (i = 0; i < numVirtualPages; i++) // Create the entries in the page
			// table
		{
			high = (block * (i + 1)) - 1;
			low = block * i;
			pageTable[i] = new PageTableEntry(i, -1, (byte) 0, (byte) 0,
											  (byte) 0, (byte) 0, 0, 0, high, low);
		}
		// Setup the page buffer
		pageBuffer = new int[numPhysicalPages]; // for listing used virtual
		// pages
		for (i = 0; i < numPhysicalPages; i++)
			pageBuffer[i] = -1; // initialise contents
		bufPointer = 0; // Buffer pointers is index into pageBuffer
		
		numPageFaults = numAdrViolations = numAccesses = 0;
	}
	
	/*----------------------------------------------------------------------------------
	 * Page buffering methods - for implementing FIFO
	 * These methods provide the means for FIFO buffering
	 * addPageBuf replaces a virtual page number to the FIFO at the head of the FIFO
	 * This buffer is initialised by the kernel from the configuration file "memset" lines
	 * Note that this buffer is full, i.e. all physical frames are allocated to virtual pages
	 * ---------------------------------------------------------------------------------*/
	
	public int addPageBuf(int virtualPageNum) {
		int removePageNum = pageBuffer[bufPointer]; // get value at pointer
		pageBuffer[bufPointer] = virtualPageNum; // replace it
		bufPointer++; // move to next
		if (bufPointer == numPhysicalPages) // cycle to start if at end
			bufPointer = 0;
		return (removePageNum); // return number removed
	}
	
	/*----------------------------------------------------------------------------------
	 * The following methods deal with the paging process
	 * --------------------------------------------------------------------------------*/
	
	public void updatePageTableEntry(int vPg, int frm, byte p, byte r, byte m,
									 byte u, int memTime, int tchTm) {
		// For initialisation purposes and updating purposes - modify a page
		// table entry
		pageTable[vPg].frame = frm;
		pageTable[vPg].p = p;
		pageTable[vPg].r = r;
		pageTable[vPg].m = m;
		pageTable[vPg].u = u;
		pageTable[vPg].inMemTime = memTime;
		pageTable[vPg].lastTouchTime = tchTm;
	}
	
	/*----------------------------------------------------------------------------------
	 * checkAddress simulates page faults - that is will generate a page fault (log message)
	 * if the memAddr argument is an address in a virtual page that has not been loaded
	 * into memory.  If a fault occurs, a log message indicates the page fault, and a
	 * the replacePage method is invoked that uses the FIFO algorithm to simulate the
	 * loading of a virtual page into a physical frame. 
	 *
	 * This method also checks for memory access violations - i.e. access outside the
	 * virtual memory space. 
	 * --------------------------------------------------------------------------------*/
	public int checkAddress(long memAddr) // checks if page is loaded into physical
	// mem
	{
		int pageNum; // virtual page number
		
		numAccesses++;
		pageNum = virtualPageNum(memAddr); // obtain the page number from the
		// address
		if (pageNum == -1) // virtualPageNum returns -1 if address is invalid
		{
			numAdrViolations++;
			kernel.logMessage("Addressing violation for "
							  + Long.toString(memAddr, kernel.addressradix)
							  + " (total so far " + numAdrViolations + ")");
			controlPanel.pageFaultValueLabel.setText("Adr Violation");
			return (-1);
		}
		if (pageTable[pageNum].p == 0) // check if virtual page is not present
			// in memory P bit
		{
			numPageFaults++;
			kernel.logMessage("Page Fault accessing "
							  + Long.toString(memAddr, kernel.addressradix)
							  + " (Total so far " + numPageFaults + ")");
			replacePage(pageNum); // call the replacePage to select a physical
			// frame
			controlPanel.pageFaultValueLabel.setText("YES");
		} else // no page fault
		{
			controlPanel.pageFaultValueLabel.setText("NO");
		}
		return (pageNum);
	}
	
	/*----------------------------------------------------------------------------------
	 * replacePage loads  a virtual page into a physical frame being used by another
	 * virtual page. It uses the FIFO algorithm described in the Stallings Memory 
	 * Management section.
	 * --------------------------------------------------------------------------------*/
	
	public int replacePage(int replacePageNum) {
		if (replaceOption.equals("FIFO"))
			return replacePgFIFO(replacePageNum);
		if (replaceOption.equals("LRU"))
			return replacePgLRU(replacePageNum);
		if (replaceOption.equals("CLOCK"))
			return replacePgCLOCK(replacePageNum);
		return (-1);
	}
	
	// This PageFault file is an example of the FIFO Page Replacement
	// Algorithm as described in the Memory Management section.
	public int replacePgFIFO(int replacePageNum) {
		int oldPageNum;
		int freedFrame;
		
		// Free the frame from page at the head of the queue
		oldPageNum = addPageBuf(replacePageNum); // replaces virtual page number
		// at head of FIFO
		freedFrame = pageTable[oldPageNum].frame; // get frame number
		
		// clear the page table entry
		// normally the M bit is checked to see if the
		// physical frame has been changed since loaded
		// If so (M=1), then the contents must be saved to the
		// swap to update the virtual page there with the changes
		// we do not need to do that in this sumulation
		updatePageTableEntry(oldPageNum, -1, (byte) 0, (byte) 0, (byte) 0,
							 (byte) 0, 0, 0);
		controlPanel.removePhysicalPage(oldPageNum);
		
		// Assign the Free frame to the requesting page
		// add link to frame
		updatePageTableEntry(replacePageNum, freedFrame, (byte) 1, (byte) 0,
							 (byte) 0, (byte) 0, kernel.clock, 0);
		controlPanel.addPhysicalPage(replacePageNum, freedFrame);
		
		// logging and leave
		logReplacement(oldPageNum, replacePageNum, freedFrame);
		return (replacePageNum);
	}
	
	// complete this method
	// <----------------------------------------------------------------------------
	public int replacePgLRU(int replacePageNum) {
		return (replacePageNum);
	}
	
	// complete this method
	// <----------------------------------------------------------------------------
	public int replacePgCLOCK(int replacePageNum) {
		return (replacePageNum);
	}
	
	private void logReplacement(int oldPage, int newPage, int frame) {
		kernel.logMessage("Replace Page (" + replaceOption + ") replaced page "
						  + Integer.toString(oldPage) + " with "
						  + Integer.toString(newPage) + " in frame "
						  + Integer.toString(frame));
	}
	
	// extracts the virtual page number from a memory address
	public int virtualPageNum(long memaddr) {
		long pageNum;
		
		pageNum = memaddr >> offsetBits;
		if (pageNum > numVirtualPages - 1)
			return -1;
		return (int) pageNum;
	}
	
	// translates a virtual memory address to the physical address components
	// <frame number> <offset>
	// This is used for logging into the tracefile
	public String translate2Phy(long memaddr) {
		int pageNum;
		long offset;
		long mask = ~0; // initialise to all 1's
		
		mask <<= offsetBits; // Sets 0's in the offset part
		offset = memaddr & (~mask); // one's complement creates mask for offset
		
		pageNum = (int) (memaddr >> offsetBits); // get rid of offset and leave
		// Virtual page #
		if (pageNum > numVirtualPages - 1)
			return "Address Violation";
		
		// create pagenum and offset
		return Long.toString(pageTable[pageNum].frame, kernel.addressradix)
		+ " " + Long.toString(offset, kernel.addressradix);
	}
	
	// translates a virtual memory address to it's components
	// <virtual page number> <offset>
	// This is used for logging into the tracefile
	public String translate2Virt(long memaddr) {
		long pageNum;
		long offset;
		long mask = ~0; // initialise to all 1's
		
		mask <<= offsetBits; // Sets 0's in the offset part
		offset = memaddr & (~mask); // one's complement creates mask for offset
		
		pageNum = memaddr >> offsetBits; // get rid of offset and leave Virtual
		// page #
		if (pageNum > numVirtualPages - 1)
			return "Address Violation";
		
		// create pagenum and offset
		return Long.toString(pageNum, kernel.addressradix) + " "
		+ Long.toString(offset, kernel.addressradix);
	}
	
	// This method will dump the contents of the Page Table
	// This can be used for debugging code
	public void dumpPageTable() {
		int i;
		kernel.logMessage("-----------------Page table---------------");
		kernel.logMessage("Vp Fr P R M U Mt Tt Hi Lo");
		for (i = 0; i < numVirtualPages; i++) {
			kernel.logMessage(pageTable[i].vPage + " " + pageTable[i].frame
							  + " " + pageTable[i].p + " " + pageTable[i].r + " "
							  + pageTable[i].m + " " + pageTable[i].u + " "
							  + pageTable[i].inMemTime + " " + pageTable[i].lastTouchTime
							  + " " + pageTable[i].high + " " + pageTable[i].low);
		}
		kernel.logMessage("----------------------");
	}
	
	// This method will dump the contents of the buffer
	// starting at bufPointer
	// This can be used for debugging code
	public void dumpBuffer() {
		int i;
		kernel.logMessage("-----------------Page buffer---------------");
		i = bufPointer;
		do {
			kernel.logMessage("Page buffer position " + i + " Page Num "
							  + pageBuffer[i] + " Frame Num "
							  + pageTable[pageBuffer[i]].frame);
			i++;
			if (i == numPhysicalPages)
				i = 0;
		} while (i != bufPointer);
		kernel.logMessage("------------------------------------");
	}
	
	// adds compiled statistics at the end of the log file
	public void logFinal() {
		kernel.logMessage("------------------------------------");
		kernel.logMessage("Final statistics:");
		kernel.logMessage("     Total memory accesses   : " + numAccesses);
		kernel.logMessage("     Total address violations: " + numAdrViolations);
		kernel.logMessage("     Total page faults       : " + numPageFaults);
	}
}
