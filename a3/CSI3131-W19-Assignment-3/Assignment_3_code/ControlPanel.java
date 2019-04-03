import java.applet.*;
import java.awt.*;

public class ControlPanel extends Frame {
	Process process;
	Button runButton = new Button("run");
	Button runButtonDelay = new Button("run delay");
	Button stepButton = new Button("step");
	Button resetButton = new Button("reset");
	Button exitButton = new Button("exit");
	Button[] buttons; // maximum 64 virtual pages - set in kernel
	Label statusValueLabel = new Label("STOP", Label.LEFT);
	Label timeValueLabel = new Label("0", Label.LEFT);
	Label compoundInstructValueLabel = new Label("NONE", Label.LEFT);
	Label instructionValueLabel = new Label("", Label.LEFT);
	Label addressValueLabel = new Label("NULL", Label.LEFT);
	Label pageFaultValueLabel = new Label("NO", Label.LEFT);
	Label virtualPageValueLabel = new Label("x", Label.LEFT);
	Label physicalPageValueLabel = new Label("0", Label.LEFT);
	Label PValueLabel = new Label("0", Label.LEFT);
	Label RValueLabel = new Label("0", Label.LEFT);
	Label MValueLabel = new Label("0", Label.LEFT);
	Label UValueLabel = new Label("0", Label.LEFT);
	Label inMemTimeValueLabel = new Label("0", Label.LEFT);
	Label lastTouchTimeValueLabel = new Label("0", Label.LEFT);
	Label lowValueLabel = new Label("0", Label.LEFT);
	Label highValueLabel = new Label("0", Label.LEFT);
	Label l0 = new Label(null, Label.CENTER);
	Label l1 = new Label(null, Label.CENTER);
	Label l2 = new Label(null, Label.CENTER);
	Label l3 = new Label(null, Label.CENTER);
	Label l4 = new Label(null, Label.CENTER);
	Label l5 = new Label(null, Label.CENTER);
	Label l6 = new Label(null, Label.CENTER);
	Label l7 = new Label(null, Label.CENTER);
	Label l8 = new Label(null, Label.CENTER);
	Label l9 = new Label(null, Label.CENTER);
	Label l10 = new Label(null, Label.CENTER);
	Label l11 = new Label(null, Label.CENTER);
	Label l12 = new Label(null, Label.CENTER);
	Label l13 = new Label(null, Label.CENTER);
	Label l14 = new Label(null, Label.CENTER);
	Label l15 = new Label(null, Label.CENTER);
	Label l16 = new Label(null, Label.CENTER);
	Label l17 = new Label(null, Label.CENTER);
	Label l18 = new Label(null, Label.CENTER);
	Label l19 = new Label(null, Label.CENTER);
	Label l20 = new Label(null, Label.CENTER);
	Label l21 = new Label(null, Label.CENTER);
	Label l22 = new Label(null, Label.CENTER);
	Label l23 = new Label(null, Label.CENTER);
	Label l24 = new Label(null, Label.CENTER);
	Label l25 = new Label(null, Label.CENTER);
	Label l26 = new Label(null, Label.CENTER);
	Label l27 = new Label(null, Label.CENTER);
	Label l28 = new Label(null, Label.CENTER);
	Label l29 = new Label(null, Label.CENTER);
	Label l30 = new Label(null, Label.CENTER);
	Label l31 = new Label(null, Label.CENTER);
	Label l32 = new Label(null, Label.CENTER);
	Label l33 = new Label(null, Label.CENTER);
	Label l34 = new Label(null, Label.CENTER);
	Label l35 = new Label(null, Label.CENTER);
	Label l36 = new Label(null, Label.CENTER);
	Label l37 = new Label(null, Label.CENTER);
	Label l38 = new Label(null, Label.CENTER);
	Label l39 = new Label(null, Label.CENTER);
	Label l40 = new Label(null, Label.CENTER);
	Label l41 = new Label(null, Label.CENTER);
	Label l42 = new Label(null, Label.CENTER);
	Label l43 = new Label(null, Label.CENTER);
	Label l44 = new Label(null, Label.CENTER);
	Label l45 = new Label(null, Label.CENTER);
	Label l46 = new Label(null, Label.CENTER);
	Label l47 = new Label(null, Label.CENTER);
	Label l48 = new Label(null, Label.CENTER);
	Label l49 = new Label(null, Label.CENTER);
	Label l50 = new Label(null, Label.CENTER);
	Label l51 = new Label(null, Label.CENTER);
	Label l52 = new Label(null, Label.CENTER);
	Label l53 = new Label(null, Label.CENTER);
	Label l54 = new Label(null, Label.CENTER);
	Label l55 = new Label(null, Label.CENTER);
	Label l56 = new Label(null, Label.CENTER);
	Label l57 = new Label(null, Label.CENTER);
	Label l58 = new Label(null, Label.CENTER);
	Label l59 = new Label(null, Label.CENTER);
	Label l60 = new Label(null, Label.CENTER);
	Label l61 = new Label(null, Label.CENTER);
	Label l62 = new Label(null, Label.CENTER);
	Label l63 = new Label(null, Label.CENTER);
	
	public ControlPanel() {
		super();
	}
	
	public ControlPanel(String title) {
		super(title);
	}
	
	public void init(Process useProcess) {
		process = useProcess;
		setLayout(null);
		setBackground(Color.white);
		setForeground(Color.black);
		resize(635, 545);
		setFont(new Font("Courier", 0, 12));
		int i;
		int row;
		int column;
		
		// Setup buttons array - max num virtuals set up in kernel
		buttons = new Button[process.kernel.page.numVirtualPages];
		for (i = 0; i < process.kernel.page.numVirtualPages; i++) {
			if (i > 31) {
				row = i - 32;
				column = 140;
			} else {
				row = i;
				column = 0;
			}
			buttons[i] = new Button("page " + i);
			buttons[i].reshape(column, (row + 2) * 15 + 25, 70, 15);
			buttons[i].setForeground(Color.magenta);
			buttons[i].setBackground(Color.lightGray);
			add(buttons[i]);
		}
		
		runButton.setForeground(Color.blue);
		runButton.setBackground(Color.lightGray);
		runButton.reshape(0, 25, 70, 15);
		add(runButton);
		
		runButtonDelay.setForeground(Color.blue);
		runButtonDelay.setBackground(Color.lightGray);
		runButtonDelay.reshape(500, 25, 70, 15);
		add(runButtonDelay);
		
		stepButton.setForeground(Color.blue);
		stepButton.setBackground(Color.lightGray);
		stepButton.reshape(70, 25, 70, 15);
		add(stepButton);
		
		resetButton.setForeground(Color.blue);
		resetButton.setBackground(Color.lightGray);
		resetButton.reshape(140, 25, 70, 15);
		add(resetButton);
		
		exitButton.setForeground(Color.blue);
		exitButton.setBackground(Color.lightGray);
		exitButton.reshape(210, 25, 70, 15);
		add(exitButton);
		
		statusValueLabel.reshape(345, 0 + 25, 100, 15);
		add(statusValueLabel);
		
		timeValueLabel.reshape(345, 15 + 25, 100, 15);
		add(timeValueLabel);
		
		compoundInstructValueLabel.reshape(500, 45 + 25, 100, 15);
		add(compoundInstructValueLabel);
		
		instructionValueLabel.reshape(385, 45 + 25, 100, 15);
		add(instructionValueLabel);
		
		addressValueLabel.reshape(385, 60 + 25, 230, 15);
		add(addressValueLabel);
		
		pageFaultValueLabel.reshape(385, 90 + 25, 100, 15);
		add(pageFaultValueLabel);
		
		virtualPageValueLabel.reshape(395, 120 + 25, 200, 15);
		add(virtualPageValueLabel);
		
		physicalPageValueLabel.reshape(395, 135 + 25, 200, 15);
		add(physicalPageValueLabel);
		
		PValueLabel.reshape(395, 150 + 25, 200, 15);
		add(PValueLabel);
		
		RValueLabel.reshape(395, 165 + 25, 200, 15);
		add(RValueLabel);
		
		MValueLabel.reshape(395, 180 + 25, 200, 15);
		add(MValueLabel);
		
		UValueLabel.reshape(395, 195 + 25, 200, 15);
		add(UValueLabel);
		
		inMemTimeValueLabel.reshape(395, 210 + 25, 200, 15);
		add(inMemTimeValueLabel);
		
		lastTouchTimeValueLabel.reshape(395, 225 + 25, 200, 15);
		add(lastTouchTimeValueLabel);
		
		lowValueLabel.reshape(395, 240 + 25, 230, 15);
		add(lowValueLabel);
		
		highValueLabel.reshape(395, 255 + 25, 230, 15);
		add(highValueLabel);
		
		Label virtualOneLabel = new Label("virtual", Label.CENTER);
		virtualOneLabel.reshape(0, 15 + 25, 70, 15);
		add(virtualOneLabel);
		
		Label virtualTwoLabel = new Label("virtual", Label.CENTER);
		virtualTwoLabel.reshape(140, 15 + 25, 70, 15);
		add(virtualTwoLabel);
		
		Label physicalOneLabel = new Label("physical", Label.CENTER);
		physicalOneLabel.reshape(70, 15 + 25, 70, 15);
		add(physicalOneLabel);
		
		Label physicalTwoLabel = new Label("physical", Label.CENTER);
		physicalTwoLabel.reshape(210, 15 + 25, 70, 15);
		add(physicalTwoLabel);
		
		Label statusLabel = new Label("status: ", Label.LEFT);
		statusLabel.reshape(285, 0 + 25, 65, 15);
		add(statusLabel);
		
		Label timeLabel = new Label("time: ", Label.LEFT);
		timeLabel.reshape(285, 15 + 25, 50, 15);
		add(timeLabel);
		
		Label instructionLabel = new Label("instruction: ", Label.LEFT);
		instructionLabel.reshape(285, 45 + 25, 100, 15);
		add(instructionLabel);
		
		Label addressLabel = new Label("address: ", Label.LEFT);
		addressLabel.reshape(285, 60 + 25, 85, 15);
		add(addressLabel);
		
		Label pageFaultLabel = new Label("page fault: ", Label.LEFT);
		pageFaultLabel.reshape(285, 90 + 25, 100, 15);
		add(pageFaultLabel);
		
		Label virtualPageLabel = new Label("virtual page: ", Label.LEFT);
		virtualPageLabel.reshape(285, 120 + 25, 110, 15);
		add(virtualPageLabel);
		
		Label physicalPageLabel = new Label("physical page: ", Label.LEFT);
		physicalPageLabel.reshape(285, 135 + 25, 110, 15);
		add(physicalPageLabel);
		
		Label PLabel = new Label("P: ", Label.LEFT);
		PLabel.reshape(285, 150 + 25, 110, 15);
		add(PLabel);
		
		Label RLabel = new Label("R: ", Label.LEFT);
		RLabel.reshape(285, 165 + 25, 110, 15);
		add(RLabel);
		
		Label MLabel = new Label("M: ", Label.LEFT);
		MLabel.reshape(285, 180 + 25, 110, 15);
		add(MLabel);
		
		Label ULabel = new Label("U: ", Label.LEFT);
		ULabel.reshape(285, 195 + 25, 110, 15);
		add(ULabel);
		
		Label inMemTimeLabel = new Label("inMemTime: ", Label.LEFT);
		inMemTimeLabel.reshape(285, 210 + 25, 110, 15);
		add(inMemTimeLabel);
		
		Label lastTouchTimeLabel = new Label("lastTouchTime: ", Label.LEFT);
		lastTouchTimeLabel.reshape(285, 225 + 25, 110, 15);
		add(lastTouchTimeLabel);
		
		Label lowLabel = new Label("low: ", Label.LEFT);
		lowLabel.reshape(285, 240 + 25, 110, 15);
		add(lowLabel);
		
		Label highLabel = new Label("high: ", Label.LEFT);
		highLabel.reshape(285, 255 + 25, 110, 15);
		add(highLabel);
		
		l0.reshape(70, (2) * 15 + 25, 60, 15);
		l0.setForeground(Color.red);
		l0.setFont(new Font("Courier", 0, 10));
		add(l0);
		
		l1.reshape(70, (3) * 15 + 25, 60, 15);
		l1.setForeground(Color.red);
		l1.setFont(new Font("Courier", 0, 10));
		add(l1);
		
		l2.reshape(70, (4) * 15 + 25, 60, 15);
		l2.setForeground(Color.red);
		l2.setFont(new Font("Courier", 0, 10));
		add(l2);
		
		l3.reshape(70, (5) * 15 + 25, 60, 15);
		l3.setForeground(Color.red);
		l3.setFont(new Font("Courier", 0, 10));
		add(l3);
		
		l4.reshape(70, (6) * 15 + 25, 60, 15);
		l4.setForeground(Color.red);
		l4.setFont(new Font("Courier", 0, 10));
		add(l4);
		
		l5.reshape(70, (7) * 15 + 25, 60, 15);
		l5.setForeground(Color.red);
		l5.setFont(new Font("Courier", 0, 10));
		add(l5);
		
		l6.reshape(70, (8) * 15 + 25, 60, 15);
		l6.setForeground(Color.red);
		l6.setFont(new Font("Courier", 0, 10));
		add(l6);
		
		l7.reshape(70, (9) * 15 + 25, 60, 15);
		l7.setForeground(Color.red);
		l7.setFont(new Font("Courier", 0, 10));
		add(l7);
		
		l8.reshape(70, (10) * 15 + 25, 60, 15);
		l8.setForeground(Color.red);
		l8.setFont(new Font("Courier", 0, 10));
		add(l8);
		
		l9.reshape(70, (11) * 15 + 25, 60, 15);
		l9.setForeground(Color.red);
		l9.setFont(new Font("Courier", 0, 10));
		add(l9);
		
		l10.reshape(70, (12) * 15 + 25, 60, 15);
		l10.setForeground(Color.red);
		l10.setFont(new Font("Courier", 0, 10));
		add(l10);
		
		l11.reshape(70, (13) * 15 + 25, 60, 15);
		l11.setForeground(Color.red);
		l11.setFont(new Font("Courier", 0, 10));
		add(l11);
		
		l12.reshape(70, (14) * 15 + 25, 60, 15);
		l12.setForeground(Color.red);
		l12.setFont(new Font("Courier", 0, 10));
		add(l12);
		
		l13.reshape(70, (15) * 15 + 25, 60, 15);
		l13.setForeground(Color.red);
		l13.setFont(new Font("Courier", 0, 10));
		add(l13);
		
		l14.reshape(70, (16) * 15 + 25, 60, 15);
		l14.setForeground(Color.red);
		l14.setFont(new Font("Courier", 0, 10));
		add(l14);
		
		l15.reshape(70, (17) * 15 + 25, 60, 15);
		l15.setForeground(Color.red);
		l15.setFont(new Font("Courier", 0, 10));
		add(l15);
		
		l16.reshape(70, (18) * 15 + 25, 60, 15);
		l16.setForeground(Color.red);
		l16.setFont(new Font("Courier", 0, 10));
		add(l16);
		
		l17.reshape(70, (19) * 15 + 25, 60, 15);
		l17.setForeground(Color.red);
		l17.setFont(new Font("Courier", 0, 10));
		add(l17);
		
		l18.reshape(70, (20) * 15 + 25, 60, 15);
		l18.setForeground(Color.red);
		l18.setFont(new Font("Courier", 0, 10));
		add(l18);
		
		l19.reshape(70, (21) * 15 + 25, 60, 15);
		l19.setForeground(Color.red);
		l19.setFont(new Font("Courier", 0, 10));
		add(l19);
		
		l20.reshape(70, (22) * 15 + 25, 60, 15);
		l20.setForeground(Color.red);
		l20.setFont(new Font("Courier", 0, 10));
		add(l20);
		
		l21.reshape(70, (23) * 15 + 25, 60, 15);
		l21.setForeground(Color.red);
		l21.setFont(new Font("Courier", 0, 10));
		add(l21);
		
		l22.reshape(70, (24) * 15 + 25, 60, 15);
		l22.setForeground(Color.red);
		l22.setFont(new Font("Courier", 0, 10));
		add(l22);
		
		l23.reshape(70, (25) * 15 + 25, 60, 15);
		l23.setForeground(Color.red);
		l23.setFont(new Font("Courier", 0, 10));
		add(l23);
		
		l24.reshape(70, (26) * 15 + 25, 60, 15);
		l24.setForeground(Color.red);
		l24.setFont(new Font("Courier", 0, 10));
		add(l24);
		
		l25.reshape(70, (27) * 15 + 25, 60, 15);
		l25.setForeground(Color.red);
		l25.setFont(new Font("Courier", 0, 10));
		add(l25);
		
		l26.reshape(70, (28) * 15 + 25, 60, 15);
		l26.setForeground(Color.red);
		l26.setFont(new Font("Courier", 0, 10));
		add(l26);
		
		l27.reshape(70, (29) * 15 + 25, 60, 15);
		l27.setForeground(Color.red);
		l27.setFont(new Font("Courier", 0, 10));
		add(l27);
		
		l28.reshape(70, (30) * 15 + 25, 60, 15);
		l28.setForeground(Color.red);
		l28.setFont(new Font("Courier", 0, 10));
		add(l28);
		
		l29.reshape(70, (31) * 15 + 25, 60, 15);
		l29.setForeground(Color.red);
		l29.setFont(new Font("Courier", 0, 10));
		add(l29);
		
		l30.reshape(70, (32) * 15 + 25, 60, 15);
		l30.setForeground(Color.red);
		l30.setFont(new Font("Courier", 0, 10));
		add(l30);
		
		l31.reshape(70, (33) * 15 + 25, 60, 15);
		l31.setForeground(Color.red);
		l31.setFont(new Font("Courier", 0, 10));
		add(l31);
		
		l32.reshape(210, (2) * 15 + 25, 60, 15);
		l32.setForeground(Color.red);
		l32.setFont(new Font("Courier", 0, 10));
		add(l32);
		
		l33.reshape(210, (3) * 15 + 25, 60, 15);
		l33.setForeground(Color.red);
		l33.setFont(new Font("Courier", 0, 10));
		add(l33);
		
		l34.reshape(210, (4) * 15 + 25, 60, 15);
		l34.setForeground(Color.red);
		l34.setFont(new Font("Courier", 0, 10));
		add(l34);
		
		l35.reshape(210, (5) * 15 + 25, 60, 15);
		l35.setForeground(Color.red);
		l35.setFont(new Font("Courier", 0, 10));
		add(l35);
		
		l36.reshape(210, (6) * 15 + 25, 60, 15);
		l36.setForeground(Color.red);
		l36.setFont(new Font("Courier", 0, 10));
		add(l36);
		
		l37.reshape(210, (7) * 15 + 25, 60, 15);
		l37.setForeground(Color.red);
		l37.setFont(new Font("Courier", 0, 10));
		add(l37);
		
		l38.reshape(210, (8) * 15 + 25, 60, 15);
		l38.setForeground(Color.red);
		l38.setFont(new Font("Courier", 0, 10));
		add(l38);
		
		l39.reshape(210, (9) * 15 + 25, 60, 15);
		l39.setForeground(Color.red);
		l39.setFont(new Font("Courier", 0, 10));
		add(l39);
		
		l40.reshape(210, (10) * 15 + 25, 60, 15);
		l40.setForeground(Color.red);
		l40.setFont(new Font("Courier", 0, 10));
		add(l40);
		
		l41.reshape(210, (11) * 15 + 25, 60, 15);
		l41.setForeground(Color.red);
		l41.setFont(new Font("Courier", 0, 10));
		add(l41);
		
		l42.reshape(210, (12) * 15 + 25, 60, 15);
		l42.setForeground(Color.red);
		l42.setFont(new Font("Courier", 0, 10));
		add(l42);
		
		l43.reshape(210, (13) * 15 + 25, 60, 15);
		l43.setForeground(Color.red);
		l43.setFont(new Font("Courier", 0, 10));
		add(l43);
		
		l44.reshape(210, (14) * 15 + 25, 60, 15);
		l44.setForeground(Color.red);
		l44.setFont(new Font("Courier", 0, 10));
		add(l44);
		
		l45.reshape(210, (15) * 15 + 25, 60, 15);
		l45.setForeground(Color.red);
		l45.setFont(new Font("Courier", 0, 10));
		add(l45);
		
		l46.reshape(210, (16) * 15 + 25, 60, 15);
		l46.setForeground(Color.red);
		l46.setFont(new Font("Courier", 0, 10));
		add(l46);
		
		l47.reshape(210, (17) * 15 + 25, 60, 15);
		l47.setForeground(Color.red);
		l47.setFont(new Font("Courier", 0, 10));
		add(l47);
		
		l48.reshape(210, (18) * 15 + 25, 60, 15);
		l48.setForeground(Color.red);
		l48.setFont(new Font("Courier", 0, 10));
		add(l48);
		
		l49.reshape(210, (19) * 15 + 25, 60, 15);
		l49.setForeground(Color.red);
		l49.setFont(new Font("Courier", 0, 10));
		add(l49);
		
		l50.reshape(210, (20) * 15 + 25, 60, 15);
		l50.setForeground(Color.red);
		l50.setFont(new Font("Courier", 0, 10));
		add(l50);
		
		l51.reshape(210, (21) * 15 + 25, 60, 15);
		l51.setForeground(Color.red);
		l51.setFont(new Font("Courier", 0, 10));
		add(l51);
		
		l52.reshape(210, (22) * 15 + 25, 60, 15);
		l52.setForeground(Color.red);
		l52.setFont(new Font("Courier", 0, 10));
		add(l52);
		
		l53.reshape(210, (23) * 15 + 25, 60, 15);
		l53.setForeground(Color.red);
		l53.setFont(new Font("Courier", 0, 10));
		add(l53);
		
		l54.reshape(210, (24) * 15 + 25, 60, 15);
		l54.setForeground(Color.red);
		l54.setFont(new Font("Courier", 0, 10));
		add(l54);
		
		l55.reshape(210, (25) * 15 + 25, 60, 15);
		l55.setForeground(Color.red);
		l55.setFont(new Font("Courier", 0, 10));
		add(l55);
		
		l56.reshape(210, (26) * 15 + 25, 60, 15);
		l56.setForeground(Color.red);
		l56.setFont(new Font("Courier", 0, 10));
		add(l56);
		
		l57.reshape(210, (27) * 15 + 25, 60, 15);
		l57.setForeground(Color.red);
		l57.setFont(new Font("Courier", 0, 10));
		add(l57);
		
		l58.reshape(210, (28) * 15 + 25, 60, 15);
		l58.setForeground(Color.red);
		l58.setFont(new Font("Courier", 0, 10));
		add(l58);
		
		l59.reshape(210, (29) * 15 + 25, 60, 15);
		l59.setForeground(Color.red);
		l59.setFont(new Font("Courier", 0, 10));
		add(l59);
		
		l60.reshape(210, (30) * 15 + 25, 60, 15);
		l60.setForeground(Color.red);
		l60.setFont(new Font("Courier", 0, 10));
		add(l60);
		
		l61.reshape(210, (31) * 15 + 25, 60, 15);
		l61.setForeground(Color.red);
		l61.setFont(new Font("Courier", 0, 10));
		add(l61);
		
		l62.reshape(210, (32) * 15 + 25, 60, 15);
		l62.setForeground(Color.red);
		l62.setFont(new Font("Courier", 0, 10));
		add(l62);
		
		l63.reshape(210, (33) * 15 + 25, 60, 15);
		l63.setForeground(Color.red);
		l63.setFont(new Font("Courier", 0, 10));
		add(l63);
		
		show();
	}
	
	public void paintPage(PageTableEntry page) {
		virtualPageValueLabel.setText(Integer.toString(page.vPage));
		physicalPageValueLabel.setText(Integer.toString(page.frame));
		PValueLabel.setText(Integer.toString(page.p));
		RValueLabel.setText(Integer.toString(page.r));
		MValueLabel.setText(Integer.toString(page.m));
		UValueLabel.setText(Integer.toString(page.u));
		inMemTimeValueLabel.setText(Integer.toString(page.inMemTime));
		lastTouchTimeValueLabel.setText(Integer.toString(page.lastTouchTime));
		lowValueLabel.setText(Long.toString(page.low,
											process.kernel.addressradix));
		highValueLabel.setText(Long.toString(page.high,
											 process.kernel.addressradix));
	}
	
	public void setStatus(String status) {
		statusValueLabel.setText(status);
	}
	
	public void addPhysicalPage(int pageNum, int physicalPage) {
		if (pageNum == 0) {
			l0.setText("page " + physicalPage);
		} else if (pageNum == 1) {
			l1.setText("page " + physicalPage);
		} else if (pageNum == 2) {
			l2.setText("page " + physicalPage);
		} else if (pageNum == 3) {
			l3.setText("page " + physicalPage);
		} else if (pageNum == 4) {
			l4.setText("page " + physicalPage);
		} else if (pageNum == 5) {
			l5.setText("page " + physicalPage);
		} else if (pageNum == 6) {
			l6.setText("page " + physicalPage);
		} else if (pageNum == 7) {
			l7.setText("page " + physicalPage);
		} else if (pageNum == 8) {
			l8.setText("page " + physicalPage);
		} else if (pageNum == 9) {
			l9.setText("page " + physicalPage);
		} else if (pageNum == 10) {
			l10.setText("page " + physicalPage);
		} else if (pageNum == 11) {
			l11.setText("page " + physicalPage);
		} else if (pageNum == 12) {
			l12.setText("page " + physicalPage);
		} else if (pageNum == 13) {
			l13.setText("page " + physicalPage);
		} else if (pageNum == 14) {
			l14.setText("page " + physicalPage);
		} else if (pageNum == 15) {
			l15.setText("page " + physicalPage);
		} else if (pageNum == 16) {
			l16.setText("page " + physicalPage);
		} else if (pageNum == 17) {
			l17.setText("page " + physicalPage);
		} else if (pageNum == 18) {
			l18.setText("page " + physicalPage);
		} else if (pageNum == 19) {
			l19.setText("page " + physicalPage);
		} else if (pageNum == 20) {
			l20.setText("page " + physicalPage);
		} else if (pageNum == 21) {
			l21.setText("page " + physicalPage);
		} else if (pageNum == 22) {
			l22.setText("page " + physicalPage);
		} else if (pageNum == 23) {
			l23.setText("page " + physicalPage);
		} else if (pageNum == 24) {
			l24.setText("page " + physicalPage);
		} else if (pageNum == 25) {
			l25.setText("page " + physicalPage);
		} else if (pageNum == 26) {
			l26.setText("page " + physicalPage);
		} else if (pageNum == 27) {
			l27.setText("page " + physicalPage);
		} else if (pageNum == 28) {
			l28.setText("page " + physicalPage);
		} else if (pageNum == 29) {
			l29.setText("page " + physicalPage);
		} else if (pageNum == 30) {
			l30.setText("page " + physicalPage);
		} else if (pageNum == 31) {
			l31.setText("page " + physicalPage);
		} else if (pageNum == 32) {
			l32.setText("page " + physicalPage);
		} else if (pageNum == 33) {
			l33.setText("page " + physicalPage);
		} else if (pageNum == 34) {
			l34.setText("page " + physicalPage);
		} else if (pageNum == 35) {
			l35.setText("page " + physicalPage);
		} else if (pageNum == 36) {
			l36.setText("page " + physicalPage);
		} else if (pageNum == 37) {
			l37.setText("page " + physicalPage);
		} else if (pageNum == 38) {
			l38.setText("page " + physicalPage);
		} else if (pageNum == 39) {
			l39.setText("page " + physicalPage);
		} else if (pageNum == 40) {
			l40.setText("page " + physicalPage);
		} else if (pageNum == 41) {
			l41.setText("page " + physicalPage);
		} else if (pageNum == 42) {
			l42.setText("page " + physicalPage);
		} else if (pageNum == 43) {
			l43.setText("page " + physicalPage);
		} else if (pageNum == 44) {
			l44.setText("page " + physicalPage);
		} else if (pageNum == 45) {
			l45.setText("page " + physicalPage);
		} else if (pageNum == 46) {
			l46.setText("page " + physicalPage);
		} else if (pageNum == 47) {
			l47.setText("page " + physicalPage);
		} else if (pageNum == 48) {
			l48.setText("page " + physicalPage);
		} else if (pageNum == 49) {
			l49.setText("page " + physicalPage);
		} else if (pageNum == 50) {
			l50.setText("page " + physicalPage);
		} else if (pageNum == 51) {
			l51.setText("page " + physicalPage);
		} else if (pageNum == 52) {
			l52.setText("page " + physicalPage);
		} else if (pageNum == 53) {
			l53.setText("page " + physicalPage);
		} else if (pageNum == 54) {
			l54.setText("page " + physicalPage);
		} else if (pageNum == 55) {
			l55.setText("page " + physicalPage);
		} else if (pageNum == 56) {
			l56.setText("page " + physicalPage);
		} else if (pageNum == 57) {
			l57.setText("page " + physicalPage);
		} else if (pageNum == 58) {
			l58.setText("page " + physicalPage);
		} else if (pageNum == 59) {
			l59.setText("page " + physicalPage);
		} else if (pageNum == 60) {
			l60.setText("page " + physicalPage);
		} else if (pageNum == 61) {
			l61.setText("page " + physicalPage);
		} else if (pageNum == 62) {
			l62.setText("page " + physicalPage);
		} else if (pageNum == 63) {
			l63.setText("page " + physicalPage);
		} else {
			return;
		}
	}
	
	public void removePhysicalPage(int physicalPage) {
		if (physicalPage == 0) {
			l0.setText(null);
		} else if (physicalPage == 1) {
			l1.setText(null);
		} else if (physicalPage == 2) {
			l2.setText(null);
		} else if (physicalPage == 3) {
			l3.setText(null);
		} else if (physicalPage == 4) {
			l4.setText(null);
		} else if (physicalPage == 5) {
			l5.setText(null);
		} else if (physicalPage == 6) {
			l6.setText(null);
		} else if (physicalPage == 7) {
			l7.setText(null);
		} else if (physicalPage == 8) {
			l8.setText(null);
		} else if (physicalPage == 9) {
			l9.setText(null);
		} else if (physicalPage == 10) {
			l10.setText(null);
		} else if (physicalPage == 11) {
			l11.setText(null);
		} else if (physicalPage == 12) {
			l12.setText(null);
		} else if (physicalPage == 13) {
			l13.setText(null);
		} else if (physicalPage == 14) {
			l14.setText(null);
		} else if (physicalPage == 15) {
			l15.setText(null);
		} else if (physicalPage == 16) {
			l16.setText(null);
		} else if (physicalPage == 17) {
			l17.setText(null);
		} else if (physicalPage == 18) {
			l18.setText(null);
		} else if (physicalPage == 19) {
			l19.setText(null);
		} else if (physicalPage == 20) {
			l20.setText(null);
		} else if (physicalPage == 21) {
			l21.setText(null);
		} else if (physicalPage == 22) {
			l22.setText(null);
		} else if (physicalPage == 23) {
			l23.setText(null);
		} else if (physicalPage == 24) {
			l24.setText(null);
		} else if (physicalPage == 25) {
			l25.setText(null);
		} else if (physicalPage == 26) {
			l26.setText(null);
		} else if (physicalPage == 27) {
			l27.setText(null);
		} else if (physicalPage == 28) {
			l28.setText(null);
		} else if (physicalPage == 29) {
			l29.setText(null);
		} else if (physicalPage == 30) {
			l30.setText(null);
		} else if (physicalPage == 31) {
			l31.setText(null);
		} else if (physicalPage == 32) {
			l32.setText(null);
		} else if (physicalPage == 33) {
			l33.setText(null);
		} else if (physicalPage == 34) {
			l34.setText(null);
		} else if (physicalPage == 35) {
			l35.setText(null);
		} else if (physicalPage == 36) {
			l36.setText(null);
		} else if (physicalPage == 37) {
			l37.setText(null);
		} else if (physicalPage == 38) {
			l38.setText(null);
		} else if (physicalPage == 39) {
			l39.setText(null);
		} else if (physicalPage == 40) {
			l40.setText(null);
		} else if (physicalPage == 41) {
			l41.setText(null);
		} else if (physicalPage == 42) {
			l42.setText(null);
		} else if (physicalPage == 43) {
			l43.setText(null);
		} else if (physicalPage == 44) {
			l44.setText(null);
		} else if (physicalPage == 45) {
			l45.setText(null);
		} else if (physicalPage == 46) {
			l46.setText(null);
		} else if (physicalPage == 47) {
			l47.setText(null);
		} else if (physicalPage == 48) {
			l48.setText(null);
		} else if (physicalPage == 49) {
			l49.setText(null);
		} else if (physicalPage == 50) {
			l50.setText(null);
		} else if (physicalPage == 51) {
			l51.setText(null);
		} else if (physicalPage == 52) {
			l52.setText(null);
		} else if (physicalPage == 53) {
			l53.setText(null);
		} else if (physicalPage == 54) {
			l54.setText(null);
		} else if (physicalPage == 55) {
			l55.setText(null);
		} else if (physicalPage == 56) {
			l56.setText(null);
		} else if (physicalPage == 57) {
			l57.setText(null);
		} else if (physicalPage == 58) {
			l58.setText(null);
		} else if (physicalPage == 59) {
			l59.setText(null);
		} else if (physicalPage == 60) {
			l60.setText(null);
		} else if (physicalPage == 61) {
			l61.setText(null);
		} else if (physicalPage == 62) {
			l62.setText(null);
		} else if (physicalPage == 63) {
			l63.setText(null);
		} else {
			return;
		}
	}
	
	public boolean action(Event e, Object arg) {
		int i;
		
		if (e.target == runButton) {
			setStatus("RUN");
			runButton.disable();
			stepButton.disable();
			resetButton.disable();
			runButtonDelay.disable();
			process.run();
			setStatus("STOP");
			resetButton.enable();
			return true;
		}
		if (e.target == runButtonDelay) {
			setStatus("RUN");
			runButton.disable();
			stepButton.disable();
			resetButton.disable();
			runButtonDelay.disable();
			process.runDelay();
			setStatus("STOP");
			resetButton.enable();
			return true;
		}
		if (e.target == stepButton) {
			setStatus("STEP");
			process.step();
			if (process.runcycles == process.runs) {
				stepButton.disable();
				runButton.disable();
			}
			setStatus("STOP");
			return true;
		}
		if (e.target == resetButton) {
			process.reset();
			runButton.enable();
			runButtonDelay.enable();
			stepButton.enable();
			return true;
		}
		if (e.target == exitButton) {
			System.exit(0);
			return true;
		}
		// check for virtual page button
		for (i = 0; i < process.kernel.page.numVirtualPages; i++) {
			if (e.target == buttons[i]) {
				process.kernel.getPage(i);
				return true;
			}
		}
		return false; // unknown event
	}
}
