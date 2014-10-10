package org.jsynthlib.midi.viewcontroller;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.inject.Inject;
import javax.sound.midi.InvalidMidiDataException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.Utility;
import org.jsynthlib.core.viewcontroller.desktop.JSLDesktop;
import org.jsynthlib.midi.domain.MidiLogEntry;
import org.jsynthlib.midi.service.MidiLogListener;
import org.jsynthlib.midi.service.MidiMessageFormatter;
import org.jsynthlib.midi.service.MidiMonitorService;

public class MidiMonitor extends JDialog implements MidiLogListener {

    private static final long serialVersionUID = 1L;

    private static final String XMIT = "XMIT: ";
    private static final String RECV = "RECV: ";

    private final JEditorPane jt;
    private JCheckBox csm;
    private JButton clr;
    private JButton ok;
    private boolean csmState;
    private MidiMonitorService midiMonitorService;

    @Inject
    public MidiMonitor(MidiMonitorService midiMonitorService) {
        super(JSLDesktop.Factory.getRootFrame(), "JSynthLib Midi Monitor",
                false);

        this.midiMonitorService = midiMonitorService;
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        jt = new JEditorPane();
        JScrollPane pane = new JScrollPane();
        pane.getViewport().add(jt);

        getContentPane().add(pane, BorderLayout.CENTER);
        pane.getVerticalScrollBar().addAdjustmentListener(
                new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent e) {
                        jt.repaint();
                    }
                });
        try {
            jt.setCaretPosition(0);
            jt.setEditable(false);
            jt.setFont(new Font("monospaced", Font.PLAIN, 12));

            // create an own panel for "clear" and "close" buttons
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BorderLayout());

            ok = new JButton("Close");
            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    MidiMonitor.this.setVisible(false);
                }
            });
            buttonPanel.add(ok, BorderLayout.EAST);

            csm = new JCheckBox("Complete SysexMessages?");
            buttonPanel.add(csm, BorderLayout.CENTER);

            clr = new JButton("Clear");
            clr.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jt.setText("");
                }
            });
            buttonPanel.add(clr, BorderLayout.WEST);

            getContentPane().add(buttonPanel, BorderLayout.SOUTH);
            getRootPane().setDefaultButton(ok);
            setSize(500, 400);

            csm.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    toggleCSM();
                }
            });
            csm.setSelected(csmState);

            setVisible(true);
            Utility.centerWindow(this);
        } catch (Exception e) {
            ErrorMsg.reportError("Error", "Error opening Monitor", e);
        }
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            midiMonitorService.addLogListener(this);
        } else {
            midiMonitorService.removeLogListener(this);
        }
        super.setVisible(b);
    }

    public void log(final MidiLogEntry logEntry) {
        // move the selection at the end of text
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                jt.select(Integer.MAX_VALUE, Integer.MAX_VALUE);
                jt.setEditable(true);
                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    if (logEntry.isReceive()) {
                        stringBuilder.append(RECV);
                    } else {
                        stringBuilder.append(XMIT);
                    }
                    if (logEntry.getPort() >= 0) {
                        stringBuilder.append("Port ")
                                .append(logEntry.getPort()).append(" ");
                    }

                    String midiString =
                            MidiMessageFormatter.midiMessageToString(
                                    logEntry.getMessage(), csmState);
                    stringBuilder.append(midiString).append("\n");
                    jt.replaceSelection(stringBuilder.toString());
                } catch (InvalidMidiDataException e) {
                    jt.replaceSelection(e.getMessage() + " "
                            + logEntry.toString() + "\n");
                }
                jt.setEditable(false);
            }
        });
    }

    /**
     * Toggle the state of displaying Midi messages in the MIDI Monitor.
     * (Complete Sysex Message)
     */
    public void toggleCSM() {
        csmState = !csmState;
    }
}
