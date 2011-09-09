/**
 * Single Editor for TC Electronics M350.
 * @version $Id: KawaiK4SingleEditor.java 859 2005-01-30 22:15:49Z hayashi $
 */
package synthdrivers.TCElectronicM350;
import core.*;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

class TCElectronicM350SingleEditor extends PatchEditorFrame {

	private static final String [] EffectsNames = new String [] { "Off", "Comp", "Hard Comp", "De-Esser", "Smooth Chorus", 
		"Lush Chorus", "Inst. Flanger", "Tremelo", "Vintage Phaser", "Smooth Phaser", "Delay Slapback", "Delay Pingpong", 
		"Soft Delay", "Triplets Delay", "Studio Delay", "Dynamic Delay"
		};
	
	private static final String [] ReverbNames = new String [] { "Off", "TC Classic Hall", "Cathedral", "Vocal Reverb", 
		"Live Vocal", "Hall Acoustic", "Drum Ambience", "Drum Room", "Ambience", "Living Room", "Nearfield", "Damped Room",
		"Silver Plate", "Gold Plate", "Spring Vintage", "Live Stage" 
	};
		
	
    public TCElectronicM350SingleEditor(Patch patch) {
	super ("TC Electronic M350 Single Editor", patch);

	gbc.weightx = 5;
	JPanel cmnPane = new JPanel();
	cmnPane.setLayout(new GridBagLayout());	 
	gbc.weightx = 0;
	
	
	addWidget(cmnPane,
			  new ComboBoxWidget("Delay|Effects", patch, 0, 
					      new M350Model(patch, 3),
					      new M350Sender(3), EffectsNames),
			  0, 0, 3, 1, 4);
	
	
	addWidget(cmnPane,
		  new PatchNameWidget(" Name  ", patch),
		  3, 0, 5, 1, 0);
	

	addWidget(cmnPane,
			  new ComboBoxWidget("Reverb", patch, 0, 
					      new M350Model(patch, 6),
					      new M350Sender(6), ReverbNames),
			  8, 0, 3, 1, 7);
	
	
	gbc.weightx=1;
	addWidget(cmnPane,
		  new KnobWidget("Input Gain", patch, 0, 127, 0, 
				      new M350Model(patch, 0),
				      new M350Sender(0)),
		      0, 1, 1, 1, 1);
	
	addWidget(cmnPane,
			  new KnobWidget("Mix Ratio", patch, 0, 127, 0, 
					      new M350Model(patch, 1),
					      new M350Sender(1)),
			  1, 1, 1, 1, 2);
	
	addWidget(cmnPane,
			  new KnobWidget("Effect Bal", patch, 0, 127, 0, 
					      new M350Model(patch, 2),
					      new M350Sender(2)),
			  2, 1, 1, 1, 3);


	addWidget(cmnPane,
			  new KnobWidget("Delay/Timing", patch, 0, 127, 0, 
					      new M350Model(patch, 4),
					      new M350Sender(4)),
			  3, 1, 1, 1, 5);
	
	addWidget(cmnPane,
			  new KnobWidget("Feedback/Depth", patch, 0, 127, 0, 
					      new M350Model(patch, 5),
					      new M350Sender(5)),
			  4, 1, 1, 1, 6);
	

	
	addWidget(cmnPane,
			  new KnobWidget("Predelay", patch, 0, 127, 0, 
					      new M350Model(patch, 7),
					      new M350Sender(7)),
			  9, 1, 1, 1, 8);
	
	addWidget(cmnPane,
			  new KnobWidget("Decay Time", patch, 0, 127, 0, 
					      new M350Model(patch, 8),
					      new M350Sender(8)),
			  10, 1, 1, 1, 9);
	
	addWidget(cmnPane,
			  new KnobWidget("Colour Filter", patch, 0, 127, 0, 
					      new M350Model(patch, 9),
					      new M350Sender(9)),
			  11, 1, 1, 1, 10);
	
	addWidget(cmnPane,
			  new ScrollBarWidget("Tap (ms)", patch, 0, 16383, 0,
					  	  new M350Model(patch, 13),
					  	  new M350Sender(13)),
			  0, 2, 11,1,11);
	
	
	/*

	addWidget(cmnPane,
		  new ScrollBarWidget("Effect", patch, 0, 31, 1, lw,
				      new K4Model(patch, 11),
				      new K4Sender(11)),
		  0, 2, 5, 1, 2);
	addWidget(cmnPane,
		  new ComboBoxWidget("Source Mode", patch,
				     new K4Model(patch, 13, 03),
				     new K4Sender(13),  new String[] {
					 "NORMAL", "TWIN", "DOUBLE"
				     }),
		  0, 3, 2, 1, 3);
	addWidget(cmnPane,
		  new ComboBoxWidget("Poly Mode", patch,
				     new K4Model(patch, 13, 12),
				     new K4Sender(14), new String[] {
					 "POLY 1", "POLY 2", "SOLO 1", "SOLO 2"
				     }),
		  2, 3, 2, 1, 4);

	//setLongestLabel("Mod Wheel Depth");
	lw = getLabelWidth("Mod Wheel Depth");
	addWidget(cmnPane,
		  new ScrollBarWidget("Pitchbend Depth", patch, 0, 12, 0, lw,
				      new K4Model(patch, 15, 15),
				      new K4Sender(18)),
		  0, 4, 4, 1, 5);
	addWidget(cmnPane,
		  new ScrollBarWidget("Mod Wheel Depth", patch, 0, 100, -50, lw,
				      new K4Model(patch, 17),
				      new K4Sender(21)),
		  0, 5, 5, 1, 6);
	addWidget(cmnPane,
		  new ComboBoxWidget("Mod Wheel ->", patch,
				     new K4Model(patch, 15, 48),
				     new K4Sender(19), new String[] {
					 "VIB", "LFO", "DCF"
				     }),
		  0, 6, 2, 1, 7);
	addWidget(cmnPane,
		  new ComboBoxWidget("Out Select", patch,
				     new K4Model(patch, 12),
				     new K4Sender(12), new String[] {
					 "A", "B", "C", "D", "E", "F", "G", "H"
				     }),
		  2, 6, 1, 1, 8);
	addWidget(cmnPane,
		  new CheckBoxWidget("AM 1->2", patch,
				     new K4Model(patch, 13, 16),
				     new K4Sender(15)),
		  0, 7, 1, 1, -1);
	addWidget(cmnPane,
		  new CheckBoxWidget("AM 3->4", patch,
				     new K4Model(patch, 13, 32),
				     new K4Sender(16)),
		  1, 7, 1, 1, -2);*/

	gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 5; gbc.gridheight = 3;
	gbc.fill = GridBagConstraints.BOTH; gbc.anchor = GridBagConstraints.EAST;
	cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "Common", TitledBorder.CENTER, TitledBorder.CENTER));
	scrollPane.add(cmnPane, gbc);
	/*
	gbc.weightx = 5;
	//Vibrato Pane
	JPanel vibPane = new JPanel();
	vibPane.setLayout(new GridBagLayout());
	//setLongestLabel("Pressure to Depth");
	lw = getLabelWidth("Pressure to Depth");
	addWidget(vibPane,
		  new ScrollBarWidget("Depth", patch, 0, 100, -50, lw,
				      new K4Model(patch, 23),
				      new K4Sender(27)),
		  0, 0, 5, 1, 9);
	gbc.weightx = 0;
	addWidget(vibPane,
		  new ScrollBarWidget("Speed", patch, 0, 100, 0, lw,
				      new K4Model(patch, 16),
				      new K4Sender(20)),
		  0, 1, 5, 1, 10);
	addWidget(vibPane,
		  new ScrollBarWidget("Pressure to ", patch, 0, 100, -50, lw,
				      new K4Model(patch, 22),
				      new K4Sender(26)),
		  0, 2, 5, 1, 11);
	addWidget(vibPane,
		  new ComboBoxWidget("Shape", patch,
				     new K4Model(patch, 14, 48),
				     new K4Sender(17), new String[] {
					 "TRIANGLE", "SAW", "SQUARE", "RANDOM"
				     }),
		  0, 3, 1, 1, 12);
	gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 5; gbc.gridheight = 3;
	gbc.fill = GridBagConstraints.BOTH; gbc.anchor = GridBagConstraints.EAST;
	vibPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "Vibrato", TitledBorder.CENTER, TitledBorder.CENTER));
	scrollPane.add(vibPane, gbc);
	gbc.weightx = 5;
	// LFO Pane
	JPanel lfoPane = new JPanel();
	lfoPane.setLayout(new GridBagLayout());
	addWidget(lfoPane,
		  new ScrollBarWidget("Depth", patch, 0, 100, -50, lw,
				      new K4Model(patch, 27),
				      new K4Sender(31)),
		  0, 0, 5, 1, 17);
	gbc.weightx = 0;
	addWidget(lfoPane,
		  new ScrollBarWidget("Speed", patch, 0, 100, 0, lw,
				      new K4Model(patch, 25),
				      new K4Sender(29)),
		  0, 1, 5, 1, 18);
	addWidget(lfoPane,
		  new ScrollBarWidget("Delay", patch, 0, 100, 0, lw,
				      new K4Model(patch, 26),
				      new K4Sender(30)),
		  0, 2, 5, 1, 19);
	addWidget(lfoPane,
		  new ScrollBarWidget("Pressure to Depth", patch, 0, 100, -50, lw,
				      new K4Model(patch, 28),
				      new K4Sender(32)),
		  0, 3, 5, 1, 20);
	addWidget(lfoPane,
		  new ComboBoxWidget("Shape", patch,
				     new K4Model(patch, 24),
				     new K4Sender(28), new String[] {
					 "TRIANGLE", "SAW", "SQUARE", "RANDOM"
				     }),
		  0, 4, 1, 1, 21);
	gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 5; gbc.gridheight = 3;
	gbc.fill = GridBagConstraints.BOTH; gbc.anchor = GridBagConstraints.EAST;
	lfoPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "LFO Settings", TitledBorder.CENTER, TitledBorder.CENTER));
	scrollPane.add(lfoPane, gbc);
	// Bend Pane
	JPanel bndPane = new JPanel();
	bndPane.setLayout(new GridBagLayout());	 gbc.weightx = 1;
	addWidget(bndPane,
		  new ScrollBarWidget("Time", patch, 0, 100, 0, lw,
				      new K4Model(patch, 18),
				      new K4Sender(22)),
		  0, 0, 5, 1, 22);
	gbc.weightx = 0;
	addWidget(bndPane,
		  new ScrollBarWidget("Depth", patch, 0, 100, -50, lw,
				      new K4Model(patch, 19),
				      new K4Sender(23)),
		  0, 1, 5, 1, 23);
	addWidget(bndPane,
		  new ScrollBarWidget("Key Scale to Time", patch, 0, 100, -50, lw,
				      new K4Model(patch, 20),
				      new K4Sender(24)),
		  0, 2, 5, 1, 24);
	addWidget(bndPane,
		  new ScrollBarWidget("Velocity to Depth", patch, 0, 100, -50, lw,
				      new K4Model(patch, 21),
				      new K4Sender(25)),
		  0, 3, 5, 1, 25);

	gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 5; gbc.gridheight = 3;
	gbc.fill = GridBagConstraints.BOTH; gbc.anchor = GridBagConstraints.EAST;
	bndPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "Auto Bend", TitledBorder.CENTER, TitledBorder.CENTER));
	scrollPane.add(bndPane, gbc);

	JTabbedPane oscPane = new JTabbedPane();
	//setLongestLabel("Vel to Cutoff");
	lw = getLabelWidth("Vel to Cutoff");
	for (int i = 0; i < 4; i++) {
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    oscPane.addTab("Source" + (i + 1), panel); gbc.weightx = 0;
	    EnvelopeWidget.Node[] nodes = new EnvelopeWidget.Node[] {
	        // origin
		new EnvelopeWidget.Node(0, 0, null, 0, 0, null, 0, false, null, null, null, null),
	        // delay time
		new EnvelopeWidget.Node(0, 100, new K4Model(patch, 30 + i),
		        		0, 0, null,
		        		0, false, new K4Sender(34, i), null, "Dly", null),
		// atack time
		new EnvelopeWidget.Node(0, 100, new K4Model(patch, 62 + i),
		        		100, 100, null,
		        		25, false, new K4Sender(45, i), null, "A", null),
		// decay time and sustain level
		new EnvelopeWidget.Node(0, 100, new K4Model(patch, 66 + i),
					0, 100, new K4Model(patch, 70 + i),
					25, false, new K4Sender(46, i), new K4Sender(47, i), "D", "S"),
		// null node for constant length horizontal line
		new EnvelopeWidget.Node(100, 100, null,
		        		EnvelopeWidget.Node.SAME, 0, null,
					0, false, null, null, null, null),
		// release time
		new EnvelopeWidget.Node(0, 100, new K4Model(patch, 74 + i),
					0, 0, null,
					0, false, new K4Sender(48, i), null, "R", null),
	    };
	    addWidget(panel,
		      new EnvelopeWidget("DCA Envelope", patch, nodes),
		      0, 0, 3, 5, 33);
	    addWidget(panel,
		      new ScrollBarWidget("Level", patch, 0, 100, 0, lw,
					  new K4Model(patch, 58 + i),
					  new K4Sender(44, i)),
		      0, 5, 3, 1, 38);
	    addWidget(panel,
		      new ComboBoxWidget("Wave", patch,
					 new WaveModel(patch, i),
					 new K4Sender(36, i), waveName),
		      0, 6, 2, 1, 39);
	    addWidget(panel,
		      new ScrollBarWidget("Transpose", patch, 0, 48, -24, lw,
					  new K4Model(patch, 42 + i, 63),
					  new K4Sender(37, i)),
		      0, 7, 3, 1, 40);
	    addWidget(panel,
		      new ComboBoxWidget("Fixed", patch,
					 new K4Model(patch, 46 + i),
					 new K4Sender(39, i), noteName),
		      0, 8, 1, 1, 41);
	    addWidget(panel,
		      new CheckBoxWidget("Key Track", patch,
					 new K4Model(patch, 42, 64),
					 new K4Sender(38, i)),
		      1, 8, 2, 1, -33);
	    addWidget(panel,
		      new ScrollBarWidget("Tune", patch, 0, 100, -50, lw,
					  new K4Model(patch, 50 + i),
					  new K4Sender(40, i)),
		      0, 9, 3, 1, 42);
	    addWidget(panel,
		      new ScrollBarWidget("Vel to Level", patch, 0, 100, -50, lw,
					  new K4Model(patch, 78 + i),
					  new K4Sender(49, i)),
		      0, 10, 3, 1, 43);
	    addWidget(panel,
		      new ScrollBarWidget("Prs to Level", patch, 0, 100, -50, lw,
					  new K4Model(patch, 82 + i),
					  new K4Sender(50, i)),
		      0, 11, 3, 1, 44);
	    addWidget(panel,
		      new ScrollBarWidget("KS to Level", patch, 0, 100, -50, lw,
					  new K4Model(patch, 86 + i),
					  new K4Sender(51, i)),
		      0, 12, 3, 1, 45);
	    addWidget(panel,
		      new ScrollBarWidget("OnVel Time", patch, 0, 100, -50, lw,
					  new K4Model(patch, 90 + i),
					  new K4Sender(52, i)),
		      0, 13, 3, 1, 46);
	    addWidget(panel,
		      new ScrollBarWidget("OffVel Time", patch, 0, 100, -50, lw,
					  new K4Model(patch, 94 + i),
					  new K4Sender(53, i)),
		      0, 14, 3, 1, 47);
	    addWidget(panel,
		      new ScrollBarWidget("KS to Time", patch, 0, 100, -50, lw,
					  new K4Model(patch, 98 + i),
					  new K4Sender(54, i)),
		      0, 15, 3, 1, 48);
	    addWidget(panel,
		      new ComboBoxWidget("KS Curve", patch,
					 new K4Model(patch, 34, 112),
					 new K4Sender(35, i),
					 new String[] {
					     "1", "2", "3", "4",
					     "5", "6", "7", "8"
					 }),
		      0, 16, 1, 1, 49);
	    addWidget(panel,
		      new ComboBoxWidget("Vel Curve", patch,
					 new K4Model(patch, 54, 28),
					 new K4Sender(43, i),
					 new String[] {
					     "1", "2", "3", "4",
					     "5", "6", "7", "8"
					 }),
		      1, 16, 2, 1, 50);
	    addWidget(panel,
		      new CheckBoxWidget("Prs to Freq", patch,
					 new K4Model(patch, 54, 1),
					 new K4Sender(41, i)),
		      0, 17, 1, 1, -34);
	    addWidget(panel,
		      new CheckBoxWidget("Vibrato/ Auto Bend", patch,
					 new K4Model(patch, 54, 2),
					 new K4Sender(42, i)),
		      1, 17, 1, 1, -35);
	}*/
    
    /*
	gbc.gridx = 5; gbc.gridy = 0; gbc.gridwidth = 5; gbc.gridheight = 9;

	scrollPane.add(oscPane, gbc);
	JTabbedPane dcfPane = new JTabbedPane();

	for (int i = 0; i < 2; i++) {
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    dcfPane.addTab("Filter" + (i + 1), panel); gbc.weightx = 0;
	    EnvelopeWidget.Node[] nodes = new EnvelopeWidget.Node[] {
		new EnvelopeWidget.Node(0, 0, null, 0, 0, null, 0, false, null, null, null, null),
		new EnvelopeWidget.Node(0, 100, new K4Model(patch, 116 + i),
					100, 100, null,
					25, false, new K4Sender(63, i), null, "A", null),
		new EnvelopeWidget.Node(0, 100, new K4Model(patch, 118 + i),
					0, 100, new K4Model(patch, 120 + i),
					25, false, new K4Sender(64, i), new K4Sender(65, i), "D", "S"),
		new EnvelopeWidget.Node(100, 100, null,
					EnvelopeWidget.Node.SAME, 0, null,
					0, false, null, null, null, null),
		new EnvelopeWidget.Node(0, 100, new K4Model(patch, 122 + i),
					0, 0, null,
					0, false, new K4Sender(66, i), null, "R", null),
	    };
	    addWidget(panel,
		      new EnvelopeWidget("DCF Envelope", patch, nodes),
		      0, 0, 3, 5, 51);
	    addWidget(panel,
		      new ScrollBarWidget("Cutoff", patch, 0, 100, 0, lw,
					  new K4Model(patch, 102 + i),
					  new K4Sender(55, i)),
		      0, 5, 3, 1, 55);
	    addWidget(panel,
		      new ScrollBarWidget("DCF Depth", patch, 0, 100, -50, lw,
					  new K4Model(patch, 112 + i),
					  new K4Sender(61, i)),
		      0, 6, 3, 1, 56);
	    addWidget(panel,
		      new ScrollBarWidget("Resonance", patch, 0, 7, 1, lw,
					  new K4Model(patch, 104 + i, 7),
					  new K4Sender(56, i)),
		      0, 7, 3, 1, 57);
	    addWidget(panel,
		      new CheckBoxWidget("LFO to Cutoff", patch,
					 new K4Model(patch, 104, 8),
					 new K4Sender(57, i)),
		      0, 8, 2, 1, -36);
	    addWidget(panel,
		      new ScrollBarWidget("Vel to Cutoff", patch, 0, 100, -50, lw,
					  new K4Model(patch, 106 + i),
					  new K4Sender(58, i)),
		      0, 9, 3, 1, 58);
	    addWidget(panel,
		      new ScrollBarWidget("Prs to Cutoff", patch, 0, 100, -50, lw,
					  new K4Model(patch, 108 + i),
					  new K4Sender(59, i)),
		      0, 10, 3, 1, 59);
	    addWidget(panel,
		      new ScrollBarWidget("KS to Cutoff", patch, 0, 100, -50, lw,
					  new K4Model(patch, 110 + i),
					  new K4Sender(60, i)),
		      0, 11, 3, 1, 60);
	    addWidget(panel,
		      new ScrollBarWidget("Vel Depth", patch, 0, 100, -50, lw,
					  new K4Model(patch, 114 + i),
					  new K4Sender(62, i)),
		      0, 12, 3, 1, 61);
	    addWidget(panel,
		      new ScrollBarWidget("OnVel Time", patch, 0, 100, -50, lw,
					  new K4Model(patch, 124 + i),
					  new K4Sender(67, i)),
		      0, 13, 3, 1, 62);
	    addWidget(panel,
		      new ScrollBarWidget("OffVel Time", patch, 0, 100, -50, lw,
					  new K4Model(patch, 126 + i),
					  new K4Sender(68, i)),
		      0, 14, 3, 1, 63);
	    addWidget(panel,
		      new ScrollBarWidget("KS to Time", patch, 0, 100, -50, lw,
					  new K4Model(patch, 128 + i),
					  new K4Sender(69, i)),
		      0, 15, 3, 1, 64);
	}
	gbc.gridx = 10; gbc.gridy = 0; gbc.gridwidth = 5; gbc.gridheight = 9;
	scrollPane.add(dcfPane, gbc);
	*/
	pack();
    }

}