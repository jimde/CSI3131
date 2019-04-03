import java.io.*;

public class MemoryManagement {
	public static void main(String[] args) {
		ControlPanel controlPanel;
		Process process;
		String replaceOpt = "FIFO";
		
		if (args.length < 2 || args.length > 3) {
			System.out
			.println("Usage: 'java MemoryManagement <COMMAND FILE> <PROPERTIES FILE> <REPLACEMENT OPTION'");
			System.exit(-1);
		}
		
		File f = new File(args[0]);
		
		if (!(f.exists())) {
			System.out.println("MemoryM: error, file '" + f.getName()
							   + "' does not exist.");
			System.exit(-1);
		}
		if (!(f.canRead())) {
			System.out.println("MemoryM: error, read of " + f.getName()
							   + " failed.");
			System.exit(-1);
		}
		
		if (args.length == 2 || args.length == 3) {
			f = new File(args[1]);
			
			if (!(f.exists())) {
				System.out.println("MemoryM: error, file '" + f.getName()
								   + "' does not exist.");
				System.exit(-1);
			}
			if (!(f.canRead())) {
				System.out.println("MemoryM: error, read of " + f.getName()
								   + " failed.");
				System.exit(-1);
			}
		}
		
		if (args.length == 3) // replacement option LRU or CLOCK
		{
			replaceOpt = args[2];
			if (!(replaceOpt.equals("LRU") || replaceOpt.equals("CLOCK") || replaceOpt
				  .equals("FIFO"))) {
				System.out
				.println("MemoryM: error, replacement option must be one of FIFO, LRU, or CLOCK - "
						 + args[2] + " is invalid.");
				System.exit(-1);
			}
		}
		
		process = new Process(replaceOpt);
		controlPanel = new ControlPanel("Memory Management");
		// Initialize the process object
		process.init(args[0], args[1]);
		// Initialize the control panel
		controlPanel.init(process);
		// Complete the setup of the control panel
		process.setupControlPanel(controlPanel);
	}
}
