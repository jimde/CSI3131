public class Instruction {
	public String inst;
	public long codeAdr;
	public long dataAdr;
	public int loopNum;
	public int increment;
	public long[] adrList;
	
	public Instruction(String inst, long addr) // Simple read or write
	// instruction
	{
		this.inst = inst;
		this.dataAdr = addr;
		// the following are not used
		this.increment = 0;
		this.loopNum = 0;
		this.codeAdr = 0;
		this.adrList = null;
	}
	
	// for readseq and writeseq instructions
	public Instruction(String inst, int loopnum, long codeAdr, long dataAdr,
					   int incr) {
		this.inst = inst;
		this.loopNum = loopnum;
		this.codeAdr = codeAdr;
		this.dataAdr = dataAdr;
		this.increment = incr;
		// the following are not used
		this.adrList = null;
	}
	
	// for loop instructions
	public Instruction(String inst, int loopnum, long codeAdr, long[] adrLst) {
		this.inst = inst;
		this.loopNum = loopnum;
		this.codeAdr = codeAdr;
		this.adrList = adrLst;
		// the following are not used
		this.dataAdr = 0;
		this.increment = 0;
	}

}
