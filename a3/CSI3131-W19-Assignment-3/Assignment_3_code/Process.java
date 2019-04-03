import java.lang.Thread;
import java.io.*;
import java.util.*;

public class Process extends Thread {
	// This class is meant to run a process
	// that uses kernel calls for reading
	// and writing. It will process
	// a command file.
	// Calls kernel.init() to initialise
	// paging system.
	//
	private String command_file; // Command file
	private String config_file; // Command file
	private Vector instructVector = new Vector(); // Vector that contains
	// instructions
	private ControlPanel controlPanel;
	public Kernel kernel;
	public int runs; // number of runs - steps of 10 ns
	public int runcycles; // total number of cycles (i.e. number of commands)
	private boolean haveDelay; // default to false
	private long address_limit;
	
	public Process(String replOpt) // Constructor - create kernel for process
	{
		kernel = new Kernel(this, replOpt);
	}
	
	public void init(String commands, String config)
	// commands is command file
	// config is config file (to pass to kernel.init())
	{
		File f = new File(commands);
		command_file = commands;
		config_file = config;
		String line;
		String command = "";
		
		kernel.init(config);
		runs = 0;
		address_limit = (kernel.block * kernel.page.numVirtualPages) - 1;
		try {
		    BufferedReader in  = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			while ((line = in.readLine()) != null) {
				// parse type of command
				command = "";
				if (line.startsWith("READ") || line.startsWith("read"))
					command = "READ";
				if (line.startsWith("WRITE") || line.startsWith("write"))
					command = "WRITE";
				if (line.startsWith("READSEQ") || line.startsWith("readseq"))
					command = "READSEQ";
				if (line.startsWith("WRITESEQ") || line.startsWith("writeseq"))
					command = "WRITESEQ";
				if (line.startsWith("LOOP") || line.startsWith("loop"))
					command = "LOOP";
				// simple read and write commands
				if (command.equals("READ") || command.equals("WRITE"))
					parseReadWriteCommand(line, command);
				// read and write sequence commands
				else if (command.equals("READSEQ")
						 || command.equals("WRITESEQ"))
					parseSequenceCommand(line, command);
				else if (command.equals("LOOP"))
					parseLoopCommand(line, command);
				// check for comment lines
				else if (line.trim().startsWith("//")) /* ignore */
					;
				else if (line.trim().equals("")) /* ignore */
					;
				// flag other lines
				else
					System.out.println("Ignoring command line " + line);
			}
			in.close();
		} catch (IOException e) { /* Handle exceptions */
		}
		runcycles = instructVector.size();
		if (runcycles < 1) {
			System.out
			.println("MemoryManagement: no instructions present for execution.");
			System.exit(-1);
		}
	}
	
	private void parseReadWriteCommand(String line, String cmd) { // READ <adr>
		// WRITE
		// <adr>
		long adr;
		
		StringTokenizer st = new StringTokenizer(line);
		st.nextToken(); // skip command
		adr = getValue(st);
		if (adr == -1) {
			System.out.println("Invalid address in command: " + line);
			return;
		}
		instructVector.addElement(new Instruction(cmd, adr));
	}
	
	private void parseSequenceCommand(String line, String cmd) { // READSEQ
		// <num>
		// <codeAdr>
		// <dataAdr>
		// <inc>
		// WRITESEQ
		// <num>
		// <codeAdr>
		// <dataAdr>
		// <inc>
		int num;
		long cdAdr;
		long dtAdr;
		int inc;
		Instruction in;
		
		StringTokenizer st = new StringTokenizer(line);
		st.nextToken(); // skip command
		
		num = (int) getValue(st);
		if (num == -1) {
			System.out.println("Invalid number in command: " + line);
			return;
		}
		cdAdr = getValue(st);
		if (cdAdr == -1) {
			System.out.println("Invalid code address in command: " + line);
			return;
		}
		dtAdr = getValue(st);
		if (dtAdr == -1) {
			System.out.println("Invalid data address in command: " + line);
			return;
		}
		inc = (int) getValue(st);
		if (inc == -1) {
			System.out.println("Invalid increment in command: " + line);
			return;
		}
		in = new Instruction(cmd, num, cdAdr, dtAdr, inc);
		instructVector.addElement(in);
	}
	
	private void parseLoopCommand(String line, String cmd) { // LOOP <num>
		// <codeAdr>
		// <dataAdr> ...
		// up to 5
		// <dataAdr>
		int num;
		long cdAdr;
		long[] dtAdrs = new long[5];
		long[] realAdrs;
		int i, j;
		
		StringTokenizer st = new StringTokenizer(line);
		cmd = st.nextToken(); // skip command
		
		num = (int) getValue(st);
		if (num == -1) {
			System.out.println("Invalid number in command: " + line);
			return;
		}
		cdAdr = getValue(st);
		if (cdAdr == -1) {
			System.out.println("Invalid code address in command: " + line);
			return;
		}
		for (i = 0; i < 5; i++) {
			dtAdrs[i] = getValue(st);
			if (dtAdrs[i] == -1)
				break; // done
		}
		if (i == 0) // no values found
		{
			System.out.println("Invalid data addresses in command: " + line);
			return;
		} else {
			realAdrs = new long[i];
			for (j = 0; j < i; j++)
				realAdrs[j] = dtAdrs[j];
		}
		instructVector.addElement(new Instruction(cmd, num, cdAdr, realAdrs));
	}
	
	private long getValue(StringTokenizer st) {
		String tmp;
		//long addr;
		
		// Get next token
		if (!st.hasMoreTokens())
			return ((long) -1);
		tmp = st.nextToken();
		// check random
		if (tmp.equals("random"))
			return (long) Common.randomLong(address_limit);
		
		// check for bin, oct or hex
		if (tmp.startsWith("bin")) {
			if (!st.hasMoreTokens())
				return ((long) -1);
			else
				return Long.parseLong(st.nextToken(), 2);
		}
		if (tmp.startsWith("oct")) {
			if (!st.hasMoreTokens())
				return ((long) -1);
			else
				return Long.parseLong(st.nextToken(), 8);
		}
		if (tmp.startsWith("hex")) {
			if (!st.hasMoreTokens())
				return ((long) -1);
			else
				return Long.parseLong(st.nextToken(), 16);
		}
		// must be decimal
		return Long.parseLong(tmp);
	}
	
	public void setupControlPanel(ControlPanel ctrlPan) // to by run after
	// control panel is
	// setup
	{
		controlPanel = ctrlPan;
		controlPanel.timeValueLabel.setText(Integer.toString(kernel.clock)
											+ " (ns)");
		kernel.setupControlPanel(controlPanel);
	}
	
	public void run() // not delay between steps
	{
		haveDelay = false;
		while (runs < runcycles)
			step();
	}
	
	public void runDelay() // add 1/2 second between steps
	{
		haveDelay = true;
		step();
		while (runs < runcycles) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) { /* Do nothing */
			}
			step();
		}
		haveDelay = false;
	}
	
	public void step() {
		//int i = 0;
		Instruction instruct;
		
		controlPanel.timeValueLabel.setText(Integer.toString(kernel.clock)
											+ " (ns)");
		instruct = (Instruction) instructVector.elementAt(runs);
		
		if (instruct.inst.equals("READ") || instruct.inst.equals("WRITE"))
			runReadWriteCommand(instruct);
		if (instruct.inst.equals("READSEQ") || instruct.inst.equals("WRITESEQ"))
			runSeqCommand(instruct);
		if (instruct.inst.equals("LOOP"))
			runLoopCommand(instruct);
		runs++;
		if (runs >= runcycles)
			kernel.page.logFinal();
	}
	
	private void runReadWriteCommand(Instruction instruct) {
		controlPanel.instructionValueLabel.setText(instruct.inst);
		controlPanel.addressValueLabel.setText(Long.toString(instruct.dataAdr,
															 kernel.addressradix));
		if (instruct.inst.equals("READ"))
			kernel.readAddr(instruct.dataAdr);
		else
			kernel.writeAddr(instruct.dataAdr);
	}
	
	private void runSeqCommand(Instruction instruct) {
		int i;
		long dtAdr;
		
		controlPanel.compoundInstructValueLabel.setText(instruct.inst);
		dtAdr = instruct.dataAdr;
		kernel.logMessage("-------------------------------------");
		kernel.logMessage("Running " + instruct.inst + " " + "Num "
						  + instruct.loopNum + " " + "CdAdr " + instruct.codeAdr + " "
						  + "DtAdr " + instruct.dataAdr + " " + "Inc "
						  + instruct.increment);
		for (i = 0; i < instruct.loopNum; i++) {
			// Read code
			controlPanel.instructionValueLabel.setText("READ");
			controlPanel.addressValueLabel.setText(Long.toString(
																 instruct.codeAdr, kernel.addressradix));
			kernel.readAddr(instruct.codeAdr);
			if (haveDelay)
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) { /* Do nothing */
				}
			// Read data
			if (instruct.inst.equals("READSEQ")) {
				controlPanel.instructionValueLabel.setText("READ");
				controlPanel.addressValueLabel.setText(Long.toString(dtAdr,
																	 kernel.addressradix));
				kernel.readAddr(dtAdr);
			} else {
				controlPanel.instructionValueLabel.setText("WRITE");
				controlPanel.addressValueLabel.setText(Long.toString(dtAdr,
																	 kernel.addressradix));
				kernel.writeAddr(dtAdr);
			}
			dtAdr += instruct.increment; // increment address
			if (haveDelay){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) { /* Do nothing */
				}
			}
		}
		kernel.logMessage("Completed " + instruct.inst + " " + "Num "
						  + instruct.loopNum + " " + "CdAdr " + instruct.codeAdr + " "
						  + "DtAdr " + instruct.dataAdr + " " + "Inc "
						  + instruct.increment);
		kernel.logMessage("-------------------------------------");
	}
	
	private void runLoopCommand(Instruction instruct) {
		int i, j;
		String dtadrs;
		
		controlPanel.compoundInstructValueLabel.setText(instruct.inst);
		dtadrs = Long.toString(instruct.adrList[0]);
		for (i = 1; i < instruct.adrList.length; i++)
			dtadrs = dtadrs + " " + instruct.adrList[i];
		kernel.logMessage("-------------------------------------");
		kernel.logMessage("Running " + instruct.inst + " " + "Num "
						  + instruct.loopNum + " " + "CdAdr " + instruct.codeAdr + " "
						  + "AdrLst " + dtadrs);
		for (i = 0; i < instruct.loopNum; i++) // run specified times
		{
			for (j = 0; j < instruct.adrList.length; j++) // run for each data
				// address
			{
				// Read code
				controlPanel.instructionValueLabel.setText("READ");
				controlPanel.addressValueLabel.setText(Long.toString(
																	 instruct.codeAdr, kernel.addressradix));
				kernel.readAddr(instruct.codeAdr);
				if (haveDelay){
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) { /* Do nothing */
					}
				}
				// Read data
				controlPanel.instructionValueLabel.setText("WRITE");
				controlPanel.addressValueLabel.setText(Long.toString(
																	 instruct.adrList[j], kernel.addressradix));
				kernel.writeAddr(instruct.adrList[j]);
				if (haveDelay){
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) { /* Do nothing */
					}
				}
			}
		}
		kernel.logMessage("Completed " + instruct.inst + " " + "Num "
						  + instruct.loopNum + " " + "CdAdr " + instruct.codeAdr + " "
						  + "AdrLst " + dtadrs);
		kernel.logMessage("-------------------------------------");
	}
	
	public void reset() {
		instructVector.removeAllElements();
		controlPanel.statusValueLabel.setText("STOP");
		controlPanel.timeValueLabel.setText("0");
		controlPanel.instructionValueLabel.setText("NONE");
		controlPanel.addressValueLabel.setText("NULL");
		controlPanel.pageFaultValueLabel.setText("NO");
		controlPanel.virtualPageValueLabel.setText("x");
		controlPanel.physicalPageValueLabel.setText("0");
		controlPanel.RValueLabel.setText("0");
		controlPanel.MValueLabel.setText("0");
		controlPanel.inMemTimeValueLabel.setText("0");
		controlPanel.lastTouchTimeValueLabel.setText("0");
		controlPanel.lowValueLabel.setText("0");
		controlPanel.highValueLabel.setText("0");
		init(command_file, config_file);
		setupControlPanel(controlPanel);
	}
}
