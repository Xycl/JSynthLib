package core;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.event.*;
import java.io.*;
import javax.swing.border.*;

/**
 * A base class of a patch editor.
 *
 * @author ???
 * @version $Id$
 */
public class PatchEditorFrame extends JInternalFrame implements PatchBasket {
    /** This is the patch we are working on. */
    public Patch p;	    // accessed by YamahaFS1RPerformanceEditor
    /** Note that calling addWidget() method may change the value of this. */
    protected GridBagConstraints gbc;
    /** Scroll Pane for the editor frame. */
    protected JPanel scrollPane;
    /** A list of slider added by addWidget method.  Only for ScrollBarWidgets and EnvelopeWidget. */
    protected ArrayList sliderList = new ArrayList(); //workaround for Java Swing Bug (What is this comment? Does anyone knows about this bug?)
    /** A list of widget added by addWidget method. */
    protected ArrayList widgetList = new ArrayList();

    /**
     * Information about BankEditorFrame which created this
     * PatchEditor frame (if applicable) so we can update that frame
     * with the edited data on close.
     */
    public  BankEditorFrame bankFrame = null; // accessed by YamahaFS1RBankEditor

    /** Bank of fader.  Set by faderMoved method. */
    int faderBank;
    /** Numfer of fader banks.  Set by show method. */
    int numFaderBanks;

    /**
     * This is a copy of the patch when we started editing (in case
     * user wants to revert).
     */
    private final byte[] originalPatch;
    /** which patch in bank we're editing */
    private int patchRow;
    private int patchCol;
    /** The last recently moved widget by fader. */
    private SysexWidget recentWidget;
    private byte lastFader;
    private JScrollPane scroller;

    /**
     * Creates a new <code>PatchEditorFrame</code> instance.
     *
     * @param name a name to display in the title bar.
     * @param patch a <code>Patch</code> value
     */
    protected PatchEditorFrame(String name, Patch patch) {
	// create a resizable, closable, maximizable, and iconifiable frame.
	super(name, true, true, true, true);
	p = patch;
	// make a backup copy
	originalPatch = new byte[p.sysex.length];
	System.arraycopy(p.sysex, 0, originalPatch, 0, p.sysex.length);

	gbc = new GridBagConstraints();
	scrollPane = new JPanel();
	scrollPane.setLayout(new GridBagLayout());
	scrollPane.setSize(600, 400);
	scroller = new JScrollPane(scrollPane);
	getContentPane().add(scroller);
	setSize(600, 400);

	scroller.getVerticalScrollBar().addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
                    repaint();
                }
	    });
	scroller.getHorizontalScrollBar().addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
                    repaint();
                }
	    });
	addInternalFrameListener(new InternalFrameListener() {
		public void internalFrameClosing(InternalFrameEvent e) {
		    String[] choices = new String[] {"Keep Changes",
						     "Revert to Original",
						     "Place Changed Version on Clipboard"
		    };
		    int choice = JOptionPane.CLOSED_OPTION;
		    while (choice == JOptionPane.CLOSED_OPTION)
			choice = JOptionPane.showOptionDialog
			    ((Component) null,
			     "What do you wish to do with the changed copy of the Patch?",
			     "Save Changes?",
			     JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE,
			     null, choices, choices[0]);
		    if (choice == 0) { // "Keep Changes"
			if (bankFrame != null)
			    bankFrame.myModel.setPatchAt(p, patchRow, patchCol);
			return;
		    }
		    if (choice == 2) // "Place Changed Version on Clipboard"
			//put on clipboard but don't 'return' just yet
			CopySelectedPatch();
		    //restore backup
		    System.arraycopy(originalPatch, 0, p.sysex, 0, p.sysex.length);
		}

		public void internalFrameOpened(InternalFrameEvent e) {
		}

		public void internalFrameActivated(InternalFrameEvent e) {
		    p.getDriver().calculateChecksum(p);
		    p.getDriver().sendPatch(p);
		    gotFocus();
		    PatchEdit.receiveAction.setEnabled(false);
		    PatchEdit.pasteAction.setEnabled(false);
		    PatchEdit.sendAction.setEnabled(true);
		    PatchEdit.sendToAction.setEnabled(true);
		    PatchEdit.playAction.setEnabled(true);
		    PatchEdit.storeAction.setEnabled(true);
		    PatchEdit.copyAction.setEnabled(true);
		    PatchEdit.reassignAction.setEnabled(true);
		    PatchEdit.exportAction.setEnabled(true);
		}

		public void internalFrameClosed(InternalFrameEvent e) {
		}

		public void internalFrameDeactivated(InternalFrameEvent e) {
		    PatchEdit.sendAction.setEnabled(false);
		    PatchEdit.playAction.setEnabled(false);
		    PatchEdit.storeAction.setEnabled(false);
		    PatchEdit.sendToAction.setEnabled(false);
		    PatchEdit.copyAction.setEnabled(false);
		    PatchEdit.reassignAction.setEnabled(false);
		    PatchEdit.exportAction.setEnabled(false);
		    lostFocus();
		}

		public void internalFrameDeiconified(InternalFrameEvent e) {
		}

		public void internalFrameIconified(InternalFrameEvent e) {
		}
	    });
    }

    // PatchBasket methods

    public ArrayList getPatchCollection() {
	return null;
    }

    public void ImportPatch (File file) throws FileNotFoundException {
    }

    public void ExportPatch (File file) throws FileNotFoundException {
    }

    public void DeleteSelectedPatch () {
    }

    public void CopySelectedPatch() {
	try {
	    PatchEdit.Clipboard = (Patch) p.clone();
	} catch (Exception e) {
	}
    }

    public void SendSelectedPatch() {
	p.getDriver().calculateChecksum(p);
	p.getDriver().sendPatch(p);
    }

    public void SendToSelectedPatch() {
	p.getDriver().calculateChecksum(p);
	new SysexSendToDialog(p);
    }

    public void ReassignSelectedPatch() {
	p.getDriver().calculateChecksum(p);
	new ReassignPatchDialog(p);
    }

    public void PlaySelectedPatch() {
	p.getDriver().calculateChecksum(p);
	p.getDriver().sendPatch(p);
	p.getDriver().playPatch(p);
    }

    public void StoreSelectedPatch() {
    }

    public JInternalFrame EditSelectedPatch() {
	return null;
    }

    public void PastePatch() {
    }

    // end of PatchBasket methods

    /**
     * Add <code>SysexWidget</code> <code>widget</code> to
     * <code>JComponent</code> <code>parent</code> by using specified
     * GridBagConstraints.
     *
     * @param parent a parent <code>JComponent</code> to which
     * <code>widget</code> is added.
     * @param widget a <code>SysexWidget</code> to be added.
     * @param gridx see {@link GridBagConstraints#gridx}.
     * @param gridy see {@link GridBagConstraints#gridy}.
     * @param gridwidth see {@link GridBagConstraints#gridwidth}.
     * @param gridheight see {@link GridBagConstraints#gridheight}.
     * @param anchor see {@link GridBagConstraints#anchor}.
     * @param fill see {@link GridBagConstraints#fill}.
     * @param slidernum a slider number.  Only used by ScrollBar Widgets.
     * @see GridBagConstraints
     */
    protected void addWidget(JComponent parent, SysexWidget widget,
			     int gridx, int gridy, int gridwidth, int gridheight,
			     int anchor, int fill,
			     int slidernum) {
	try {
	    gbc.gridx = gridx;
	    gbc.gridy = gridy;
	    gbc.gridwidth = gridwidth;
	    gbc.gridheight = gridheight;
	    gbc.anchor = anchor;
	    gbc.fill = fill;
	    parent.add(widget, gbc);

	    widgetList.add(widget);

	    widget.setSliderNum(slidernum);
	    if (widget instanceof ScrollBarWidget)
		sliderList.add(((ScrollBarWidget) widget).slider);
	    if (widget instanceof VertScrollBarWidget)
		sliderList.add(((VertScrollBarWidget) widget).slider);
	    if (widget instanceof ScrollBarLookupWidget)
		sliderList.add(((ScrollBarLookupWidget) widget).slider);
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	}
    }

    /**
     * Add <code>SysexWidget</code> <code>widget</code> to
     * <code>JComponent</code> <code>parent</code> by using specified
     * GridBagConstraints.<p>
     *
     * <code>NORTHEAST</code> is used for the <code>anchor</code>
     * constraint and <code>HORIZONTAL</code> is used for the
     * <code>fill</code> constraint.
     */
    protected void addWidget(JComponent parent, SysexWidget widget,
			     int gridx, int gridy, int gridwidth, int gridheight,
			     int slidernum) {
	this.addWidget(parent, widget, gridx, gridy, gridwidth, gridheight,
		       gbc.NORTHEAST, gbc.HORIZONTAL, slidernum);
    }

    /**
     * Add <code>SysexWidget</code> <code>widget</code> to
     * <code>scrollPane</code> by using specified
     * GridBagConstraints.<p>
     *
     * <code>EAST</code> is used for the <code>anchor</code>
     * constraint and <code>BOTH</code> is used for the
     * <code>fill</code> constraint.
     */
    protected void addWidget(SysexWidget widget,
			     int gridx, int gridy, int gridwidth, int gridheight,
			     int slidernum) {
	this.addWidget(scrollPane, widget, gridx, gridy, gridwidth, gridheight,
		       gbc.EAST, gbc.BOTH, slidernum);
    }

    void faderMoved(byte fader, byte value) {
	if (fader == 32) {
	    faderBank = (faderBank + 1) % numFaderBanks;
	    faderHighlight();
	    return;
	}
	if (fader == 31) {
	    faderBank = faderBank - 1;
	    if (faderBank < 0)
		faderBank = numFaderBanks - 1;
	    faderHighlight();
	    return;
	}
	if (fader > 16)
	    fader = (byte) (0 - (fader - 16) - (faderBank * 16));
	else
	    fader += (faderBank * 16);

	if (recentWidget != null) {
	    SysexWidget w = recentWidget;
	    if (fader == faderBank * 16)
		fader = lastFader;
	    if (w.getSliderNum() == fader && w.isShowing()) {
		if (w.getNumFaders() == 1)
		    w.setValue((int) (w.getValueMin() + ((float) (value) / 127 * (w.getValueMax() - w.getValueMin()))));
		else		// EnvelopeWidget
		    w.setFaderValue(fader, (int) value);
		w.repaint();
		return;
	    }
	}
	lastFader = fader;

	for (int i = 0; i < widgetList.size(); i++) {
	    SysexWidget w = (SysexWidget) widgetList.get(i);
	    if ((w.getSliderNum() == fader
		 || (w.getSliderNum() < fader && w.getSliderNum() + w.getNumFaders() > fader))
		&& w.isShowing()) {
		if (w.getNumFaders() == 1)
		    w.setValue((int) (w.getValueMin() + ((float) (value) / 127 * (w.getValueMax() - w.getValueMin()))));
		else		// EnvelopeWidget
		    w.setFaderValue(fader, (int) value);
		w.repaint();
		recentWidget = w;
		return;
	    }
	}
    }

    void faderHighlight() {
	for (int i = 0; i < widgetList.size(); i++) {
	    SysexWidget w = (SysexWidget) widgetList.get(i);
	    if (w.getLabel() != null) {
		if (((Math.abs(w.getSliderNum() - 1) & 0xf0)) == faderBank * 16) {
		    Color c = UIManager.getColor("controlText");
		    if (c == null)
			c = new Color(75, 75, 100);
		    w.getJLabel().setForeground(c);
		} else {
		    Color c = UIManager.getColor("textInactiveText");
		    if (c == null)
			c = new Color(102, 102, 153);
		    w.getJLabel().setForeground(c);
		}
		w.getJLabel().repaint();
	    }
	}
    }

    /**
     * When showing the dialog, also check how many components there
     * are to determine the number of widget banks needed.
     */
    public void show() {	// override a method of JInternalFrame
	int high = 0;
	for (int i = 0; i < widgetList.size(); i++) {
	    SysexWidget w = (SysexWidget) widgetList.get(i);
	    if ((w.getSliderNum() + w.getNumFaders() - 1) > high)
		high = w.getSliderNum() + w.getNumFaders() - 1;
	}
	numFaderBanks = (high / 16) + 1;
	faderHighlight();
	ErrorMsg.reportStatus("PatchEditorFrame:Show   Num Fader Banks =  "
			      + numFaderBanks);

	Dimension screenSize = PatchEdit.desktop.getSize();
        Dimension frameSize = this.getSize();
        if (frameSize.height > screenSize.height) {
            // Add necessary place for the vertical Scrollbar
            frameSize.width += scroller.getVerticalScrollBar().getPreferredSize().width;
            this.setSize(frameSize.width, screenSize.height);
        }
        if (frameSize.width > screenSize.width) {
            // Add necessary place for the horizontal Scrollbar.
            frameSize.height += scroller.getHorizontalScrollBar().getPreferredSize().height;
            // If the entire frame doen't fit in the window, then
            // rescale it to fit.
            if (frameSize.height > screenSize.height)
                frameSize.height = screenSize.height;
            this.setSize(screenSize.width, frameSize.height);
        }
        super.show();
    } // end of method

    /**
     * Let bankeditorframe set information about itself when it
     * creates a patch editor frame.
     */
    public void setBankEditorInformation(BankEditorFrame bf, int row, int col) { // accessed by YamahaFS1RBankEditor
	bankFrame = bf;
	patchRow = row;
	patchCol = col;
    }

    void revalidateDriver() {
	if (!p.chooseDriver()) {
	    try {
		setClosed(true);
	    } catch (Exception e) {
	    }
	    return;
	}
    }

    /** A hook called when the frame is activated. */
    protected void gotFocus() {
    }

    /** A hook called when the frame is deactivated. */
    protected void lostFocus() {
    }
}
