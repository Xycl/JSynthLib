package org.jsynthlib.core.viewcontroller.preferences;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;
import javax.swing.table.TableColumn;

import org.jsynthlib.core.Utility;
import org.jsynthlib.core.viewcontroller.desktop.JSLDesktop;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.device.model.DeviceList;
import org.jsynthlib.device.model.DeviceManager;
import org.jsynthlib.device.viewcontroller.BankEditorFrame;
import org.jsynthlib.device.viewcontroller.DeviceAddDialog;
import org.jsynthlib.device.viewcontroller.PatchEditorFrame;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.midi.domain.MidiSettings;
import org.jsynthlib.midi.service.MidiScanService;
import org.jsynthlib.patch.model.impl.PatchEdit;
import org.jsynthlib.patch.viewcontroller.LibraryFrame;

/**
 * ConfigPanel for Synthesizer Configuration
 * @author ???
 * @author Hiroo Hayashi
 * @version $Id: SynthConfigPanel.java 1112 2011-09-04 18:46:41Z frankster $
 */
public class SynthConfigPanel extends ConfigPanel {
    private static final long serialVersionUID = 1L;

    {
        panelName = "Synth Driver";
        nameSpace = "synthDriver";
    }

    /** Multiple MIDI Interface CheckBox */
    private final JCheckBox cbxMMI;
    private boolean multiMIDI;
    private final JTable table;
    private final MidiScanService midiScanService;
    private final MidiSettings midiSettings;
    private final JPopupMenu popup;
    private final DeviceManager deviceManager;

    SynthConfigPanel(PrefsDialog parent) {
        super(parent);

        setLayout(new BorderLayout());
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;

        // create synth driver table
        table = new JTable(JSynthLibInjector.getInstance(DeviceList.class));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setPreferredScrollableViewportSize(new Dimension(750, 150)); // wirski@op.pl
        TableColumn column = table.getColumnModel().getColumn(DeviceList.Columns.SYNTH_NAME.ordinal());
        column.setPreferredWidth(75);
        column = table.getColumnModel().getColumn(DeviceList.Columns.DEVICE.ordinal());
        column.setPreferredWidth(250);

        midiSettings = JSynthLibInjector.getInstance(MidiSettings.class);
        midiScanService = JSynthLibInjector.getInstance(MidiScanService.class);
        deviceManager = JSynthLibInjector.getInstance(DeviceManager.class);

        column = table.getColumnModel().getColumn(DeviceList.Columns.MIDI_IN.ordinal());
        column.setPreferredWidth(200); // wirski@op.pl
        JComboBox comboBox = new JComboBox(midiSettings.getInputNames());
        column.setCellEditor(new DefaultCellEditor(comboBox));

        column = table.getColumnModel().getColumn(DeviceList.Columns.MIDI_OUT.ordinal());
        column.setPreferredWidth(200); // wirski@op.pl
        comboBox = new JComboBox(midiSettings.getOutputNames());
        column.setCellEditor(new DefaultCellEditor(comboBox));

        column = table.getColumnModel().getColumn(DeviceList.Columns.MIDI_CHANNEL.ordinal());
        column.setPreferredWidth(90); // wirski@op.pl

        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setPreferredSize(new Dimension(750, 150));
        p.add(scrollpane, c);

        // multiple MIDI interface check box
        cbxMMI = new JCheckBox("Use Multiple MIDI Interface");
        cbxMMI.setToolTipText("Allows users to select different MIDI port for each synth.");
        cbxMMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                multiMIDI = cbxMMI.isSelected();
                setModified(true);
            }
        });
        ++c.gridy;
        p.add(cbxMMI, c);

        // create buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton add = new JButton("Add Device...");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDevice();
            }
        });
        buttonPanel.add(add);

        // BUTTON ADDED BY GERRIT GEHNEN
        JButton scan = new JButton("Auto-Scan...");
        scan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scanMidi();
            }
        });
        buttonPanel.add(scan);
        // END OF ADDED BUTTON

        ++c.gridy;
        p.add(buttonPanel, c);
        add(p, BorderLayout.CENTER);

        // popup menu
        popup = new JPopupMenu();
        JMenuItem mi;
        mi = new JMenuItem("Delete");
        // This works only for JMenuBar.
        // mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeDevice();
            }
        });
        popup.add(mi);
        mi = new JMenuItem("Property...");
        mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDeviceProperty();
            }
        });
        popup.add(mi);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private void removeDevice() {
        if ((table.getSelectedRow() == -1) || (table.getSelectedRow() == 0)) {
            return;
        }
        if (JOptionPane.showConfirmDialog(null, "Are you sure?",
                "Remove Device?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
            return;
        }
        deviceManager.removeDevice(table.getSelectedRow());
        revalidateLibraries();
        table.repaint();
    }

    private void showDeviceProperty() {
        if ((table.getSelectedRow() == -1)) {
            return;
        }
        deviceManager.getDevice(table.getSelectedRow()).showDetails(
                Utility.getFrame(this));
        // table.getModel().fireTableDataChanged();
    }

    private void addDevice() {
        DeviceAddDialog dad = new DeviceAddDialog(null);
        dad.setVisible(true);
        revalidateLibraries();
    }

    // METHOD ADDED BY GERRIT GEHNEN
    private void scanMidi() {
        if (JOptionPane
                .showConfirmDialog(
                        null,
                        "Scanning the System for supported Synthesizers may take\n"
                                + "a few minutes if you have many MIDI ports. During the scan\n"
                                + "it is normal for the system to be unresponsive.\n"
                                + "Do you wish to scan?",
                        "Scan for Synthesizers", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
            return;
        }

        if (midiScanService != null) {
            midiScanService.close();
        }

        ProgressMonitor pm =
                new ProgressMonitor(null, "Scanning for SupportedSynthesizers",
                        "Initializing Midi Devices", 0, 100);

        // TODO: possibly need to pass in a parent so that the
        // "scan unknown device dialogue can be displayed"
        midiScanService.scanMidiDevices(pm, null);

        revalidateLibraries();
    }

    // END OF METHOD ADDED BY GERRIT GEHNEN

    // ConfigPanel interface methods
    @Override
    protected void init() {
        multiMIDI = midiSettings.getMultiMIDI();
        cbxMMI.setSelected(multiMIDI);
        cbxMMI.setEnabled(midiSettings.getMidiEnable());
        // table.setRowSelectionInterval(0, 0); // why this does not work? Hiroo
    }

    // I gave up using 'Apply' botton for Synth Table. It's is
    // difficult to defer 'add device' and 'remove device' event.
    @Override
    protected void commitSettings() {
        midiSettings.setMultiMIDI(multiMIDI);
        if (!multiMIDI) {
            int out = midiSettings.getInitPortOut();
            int in = midiSettings.getInitPortIn();
            String outputName = midiSettings.getOutputName(out);
            for (int i = 0; i < deviceManager.deviceCount(); i++) {
                deviceManager.getDevice(i).setOutPortName(outputName);
                deviceManager.getDevice(i).setInPort(in);
            }
        }

        setModified(false);
    }

    /**
     * Revalidate Library. Internally this calls <code>revalidateDriver()</code>
     * method of each frame.
     */
    void revalidateLibraries() {
        JSLDesktop desktop = JSLDesktop.Factory.getDesktop();
        Iterator<JSLFrame> iterator = desktop.getJSLFrameIterator();
        boolean first = true;
        while (iterator.hasNext()) {
            // Before first iteration
            if (first) {
            PatchEdit.showWaitDialog();
            }

            JSLFrame jslFrame = iterator.next();
            if (jslFrame instanceof LibraryFrame) {
                ((LibraryFrame) (jslFrame)).revalidateDrivers();
            } else if (jslFrame instanceof BankEditorFrame) {
                ((BankEditorFrame) (jslFrame)).revalidateDriver();
            } else if (jslFrame instanceof PatchEditorFrame) {
                ((PatchEditorFrame) (jslFrame)).revalidateDriver();
            }

            // After last iteration
            if (!iterator.hasNext()) {
            PatchEdit.hideWaitDialog();
            }
        }
    }

}
