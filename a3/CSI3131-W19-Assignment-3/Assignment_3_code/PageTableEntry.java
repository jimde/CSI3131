public class PageTableEntry {
	public int vPage; // Virtual page number
	public int frame; // Frame number (physical page number
	public int p; // Present bit
	public byte r; // Read bit (page read)
	public byte m; // Modified bit (page modified since loaded
	public byte u; // Used bit
	public int inMemTime; // Time virtual page loaded into frame
	public int lastTouchTime; // Last time page was accessed
	public long high; // Upper address of virtual page
	public long low; // Lower address of virtual page
	
	public PageTableEntry(int vPage, int frame, byte p, byte r, byte m, byte u,
						  int inMemTime, int lastTouchTime, long high, long low) // Constructor
	{
		this.vPage = vPage;
		this.frame = frame;
		this.p = p;
		this.r = r;
		this.m = m;
		this.u = u;
		this.inMemTime = inMemTime;
		this.lastTouchTime = lastTouchTime;
		this.high = high;
		this.low = low;
	}

}
