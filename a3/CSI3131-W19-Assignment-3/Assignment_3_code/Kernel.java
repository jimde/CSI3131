import java.io.*;
import java.util.*;

public class Kernel {
	// The number of virtual pages must be fixed at 63 due to
	// dependencies in the GUI
	
	private ControlPanel controlPanel;
	private boolean doStdoutLog = false;
	private boolean doFileLog = false;
	private Process process;
	public long block = (int) Math.pow(2, 12);
	public byte offsetBits = 12;
	public byte addressradix = 10;
	public Paging page; // for managing paging
	public int clock; // measured in nanoseconds
	
	private String traceFile = null;
	private PrintStream traceStream; // stream
	
	public Kernel(Process prc, String replOpt) // Constructor
	{
		process = prc; // To access process stuff
		page = new Paging(replOpt, this);
	}
	
	public void updateClock() // Updates clock with random time between 1 and 10
	// ns
	{
		clock += (int) (Math.random() * 10) + 1;
	}
	
	public void init(String config) {
		String line;
		String tmp = null;
		byte P = 0;
		byte R = 0;
		byte M = 0;
		byte U = 0;
		int i = 0;
		int j = 0;
		int id = 0;
		int physical = 0;
		int physical_count = 0;
		int inMemTime = 0;
		int lastTouchTime = 0;
		int map_count = 0;
		double power = 14;
		
		long address_limit;
		int numVirtualPages = 64;
		int numPhysicalPages = 0; // set to zero for counting
		File traceFd; // open file for logging
		
		clock = 0; // default value
		if (config != null) {
			File f;
			
			f = new File(config);
			try { // First scan through the configuration file to get
				// important information
				BufferedReader in = new BufferedReader(new InputStreamReader(
																			 new FileInputStream(f)));
				while ((line = in.readLine()) != null) {
					if (line.startsWith("numpages")) {
						StringTokenizer st = new StringTokenizer(line);
						while (st.hasMoreTokens()) {
							tmp = st.nextToken();
							numVirtualPages = Common.s2i(st.nextToken());
							if (numVirtualPages < 3 || numVirtualPages > 64) {
								System.out
								.println("MemoryManagement: numpages out of bounds.");
								System.exit(-1);
							}
							address_limit = (block * numVirtualPages) - 1;
						}
					} else if (line.startsWith("memset")) {
						StringTokenizer st = new StringTokenizer(line);
						st.nextToken();
						id = Common.s2i(st.nextToken());
						tmp = st.nextToken();
						if (!tmp.startsWith("x"))
							numPhysicalPages++; // counting physical pages
					}
				}
				in.close();
			} catch (IOException e) { /* Handle exceptions */
			}
			
			page.init(numPhysicalPages, numVirtualPages, offsetBits, block);
			
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
																			 new FileInputStream(f)));
				while ((line = in.readLine()) != null) {
					if (line.startsWith("memset")) {
						StringTokenizer st = new StringTokenizer(line);
						st.nextToken();
						while (st.hasMoreTokens()) {
							id = Common.s2i(st.nextToken());
							tmp = st.nextToken();
							if (tmp.startsWith("x")) {
								physical = -1;
							} else {
								physical = Common.s2i(tmp);
								page.addPageBuf(id); // add to page buffer
							}
							if ((0 > id || id > numVirtualPages - 1)) {
								System.out
								.println("MemoryManagement: Invalid page value in "
										 + config);
								System.exit(-1);
							}
							P = Common.s2b(st.nextToken());
							if (P < 0 || P > 1) {
								System.out
								.println("MemoryManagement: Invalid P value in "
										 + config);
								System.exit(-1);
							}
							R = Common.s2b(st.nextToken());
							if (R < 0 || R > 1) {
								System.out
								.println("MemoryManagement: Invalid R value in "
										 + config);
								System.exit(-1);
							}
							M = Common.s2b(st.nextToken());
							if (M < 0 || M > 1) {
								System.out
								.println("MemoryManagement: Invalid M value in "
										 + config);
								System.exit(-1);
							}
							U = Common.s2b(st.nextToken());
							if (U < 0 || U > 1) {
								System.out
								.println("MemoryManagement: Invalid U value in "
										 + config);
								System.exit(-1);
							}
							inMemTime = Common.s2i(st.nextToken());
							if (inMemTime < 0) {
								System.out
								.println("MemoryManagement: Invalid inMemTime in "
										 + config);
								System.exit(-1);
							}
							lastTouchTime = Common.s2i(st.nextToken());
							if (lastTouchTime < 0) {
								System.out
								.println("MemoryManagement: Invalid lastTouchTime in "
										 + config);
								System.exit(-1);
							}
							if (lastTouchTime > clock)
								clock = lastTouchTime;
							if (inMemTime > clock)
								clock = inMemTime;
							page.updatePageTableEntry(id, physical, P, R, M, U,
													  inMemTime, lastTouchTime); // update page
							// table
						}
					}
					if (line.startsWith("enable_logging")) {
						StringTokenizer st = new StringTokenizer(line);
						while (st.hasMoreTokens()) {
							if (st.nextToken().startsWith("true")) {
								doStdoutLog = true;
							}
						}
					}
					if (line.startsWith("log_file")) {
						StringTokenizer st = new StringTokenizer(line);
						while (st.hasMoreTokens()) {
							tmp = st.nextToken();
						}
						if (tmp.startsWith("log_file")) {
							doFileLog = false;
							traceFile = "tracefile";
						} else {
							doFileLog = true;
							doStdoutLog = false;
							traceFile = tmp;
						}
					}
					if (line.startsWith("pagesize")) {
						StringTokenizer st = new StringTokenizer(line);
						while (st.hasMoreTokens()) {
							tmp = st.nextToken();
							tmp = st.nextToken();
							if (tmp.startsWith("power")) {
								power = (double) Integer.parseInt(st
																  .nextToken());
								offsetBits = (byte) power;
								block = (int) Math.pow(2, power);
								page.offsetBits = offsetBits;
							} else {
								System.out
								.println("MemoryManagement: please specify as power of 2");
								System.exit(-1);
							}
							address_limit = (block * numVirtualPages) - 1;
						}
						if (block < 64 || block > Math.pow(2, 26)) {
							System.out
							.println("MemoryManagement: pagesize is out of bounds");
							System.exit(-1);
						}
						for (i = 0; i < numVirtualPages; i++) {
							page.pageTable[i].high = (block * (i + 1)) - 1;
							page.pageTable[i].low = block * i;
						}
					}
					if (line.startsWith("addressradix")) {
						StringTokenizer st = new StringTokenizer(line);
						while (st.hasMoreTokens()) {
							tmp = st.nextToken();
							tmp = st.nextToken();
							addressradix = Byte.parseByte(tmp);
							if (addressradix < 0 || addressradix > 20) {
								System.out
								.println("MemoryManagement: addressradix out of bounds.");
								System.exit(-1);
							}
						}
					}
				}
				in.close();
			} catch (IOException e) { /* Handle exceptions */
			}
		}
		
		// setup log file for logging
		if (doFileLog) {
			traceFd = new File(traceFile);
			traceFd.delete();
			try {
				traceStream = new PrintStream(new FileOutputStream(traceFile));
			} catch (IOException e) {
				System.out
				.println("MemoryManagement: cannot set up trace stream: "
						 + e.getMessage());
				System.exit(-1);
			}
		}
		for (i = 0; i < numVirtualPages; i++) {
			if (page.pageTable[i].p == 1)
				map_count++;
			for (j = 0; j < numVirtualPages; j++) {
				if (page.pageTable[j].frame == page.pageTable[i].frame
					&& page.pageTable[i].frame >= 0) {
					physical_count++;
				}
			}
			if (physical_count > 1) {
				System.out
				.println("MemoryManagement: Duplicate physical page's in "
						 + config);
				System.exit(-1);
			}
			physical_count = 0;
		}
	}
	
	public void setupControlPanel(ControlPanel cntrlPan) // to be run after
	// control panel is
	// setup
	{
		int i;
		
		controlPanel = cntrlPan;
		page.controlPanel = controlPanel;
		
		for (i = 0; i < page.numVirtualPages; i++) {
			if (page.pageTable[i].p == 0) {
				controlPanel.removePhysicalPage(i);
			} else {
				controlPanel.addPhysicalPage(i, page.pageTable[i].frame);
			}
		}
	}
	
	public void readAddr(long memAddr) // kernel read system call
	{
		int pageNum; // Virtual page number
		
		updateClock(); // increments time
		
		pageNum = page.checkAddress(memAddr); // checks if page is loaded
		if (pageNum == -1)
			return; // Addressing violation
		// update info in page entry table
		page.pageTable[pageNum].r = 1; // 1 means has been read
		page.pageTable[pageNum].lastTouchTime = clock;
		logMessage("READ " + Long.toString(memAddr, addressradix)
				   + " Virtual Addr " + page.translate2Virt(memAddr)
				   + " Physical Addr " + page.translate2Phy(memAddr));
		getPage(pageNum);
	}
	
	public void writeAddr(long memAddr) // kernel write system call
	{
		int pageNum; // Virtual page number
		
		updateClock(); // increments time
		
		pageNum = page.checkAddress(memAddr); // checks if page is loaded
		if (pageNum == -1)
			return; // Addressing violation
		page.pageTable[pageNum].m = 1;
		page.pageTable[pageNum].lastTouchTime = clock;
		logMessage("WRITE " + Long.toString(memAddr, addressradix)
				   + " Virtual Addr " + page.translate2Virt(memAddr)
				   + " Physical Addr " + page.translate2Phy(memAddr));
		getPage(pageNum);
	}

	public void getPage(int pageNum) {
		controlPanel.paintPage(page.pageTable[pageNum]);
	}

	public void logMessage(String message) {
		if (doFileLog) {
			traceStream.println(clock + " " + message);
			traceStream.flush();
		}
		if (doStdoutLog)
			System.out.println(clock + " " + message);
	}
}
