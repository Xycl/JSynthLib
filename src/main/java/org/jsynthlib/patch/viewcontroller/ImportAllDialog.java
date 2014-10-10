package org.jsynthlib.patch.viewcontroller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;
import org.jsynthlib.core.ColumnLayout;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.Utility;
import org.jsynthlib.core.viewcontroller.desktop.JSLDesktop;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.DeviceManager;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.patch.model.IBankPatch;
import org.jsynthlib.patch.model.IPatch;
import org.jsynthlib.patch.model.MultiPatchImporter;

public class ImportAllDialog extends JDialog {

    private final transient Logger log = Logger.getLogger(getClass());

    public ImportModel myModel;

    private MultiPatchImporter patchImporter;

    public ImportAllDialog(JFrame Parent, final File file) {
        super(Parent, "Import All Files In Directory", true);

        patchImporter = JSynthLibInjector.getInstance(MultiPatchImporter.class);
        JPanel container = new JPanel();
        container.setLayout(new ColumnLayout());

        myModel = new ImportModel();
        JTable table = new JTable(myModel);
        TableColumn column = null;
        column = table.getColumnModel().getColumn(0);
        column.setPreferredWidth(25);
        column = table.getColumnModel().getColumn(1);
        column.setPreferredWidth(250);
        table.setPreferredScrollableViewportSize(new Dimension(500, 250));
        JScrollPane scrollPane = new JScrollPane(table);
        container.add(scrollPane);

        final ButtonGroup group = new ButtonGroup();
        JRadioButton button1 = new JRadioButton("Nowhere");
        button1.setActionCommand("0");
        JRadioButton button2 = new JRadioButton("in Field 1");
        JRadioButton button3 = new JRadioButton("in Field 2");
        button2.setActionCommand("1");
        button3.setActionCommand("2");
        group.add(button1);
        group.add(button2);
        group.add(button3);
        button1.setSelected(true);
        JPanel radioPanel = new JPanel();
        JLabel myLabel =
                new JLabel("Place the File name for each Patch:          ",
                        JLabel.CENTER);
        radioPanel.setLayout(new FlowLayout());
        radioPanel.add(myLabel, BorderLayout.NORTH);
        radioPanel.add(button1);
        radioPanel.add(button2);
        radioPanel.add(button3);
        container.add(radioPanel);

        final ButtonGroup group2 = new ButtonGroup();
        JRadioButton button4 = new JRadioButton("No");
        button4.setActionCommand("0");
        JRadioButton button5 = new JRadioButton("Yes");

        button5.setActionCommand("1");
        group2.add(button4);
        group2.add(button5);

        button4.setSelected(true);
        JPanel radioPanel2 = new JPanel();
        JLabel myLabel2 =
                new JLabel("Automatically Extract Patches from Banks?   ",
                        JLabel.CENTER);
        radioPanel2.setLayout(new FlowLayout());
        radioPanel2.add(myLabel2, BorderLayout.NORTH);
        radioPanel2.add(button4);
        radioPanel2.add(button5);

        container.add(radioPanel2);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        JButton done = new JButton(" OK ");
        done.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                String command1 = group.getSelection().getActionCommand();
                String command2 = group2.getSelection().getActionCommand();
                boolean extract = (command2 == "1");
                int putName = 0;
                if (command1 == "1")
                    putName = 1;
                if (command1 == "2")
                    putName = 2;
                doImport(putName, extract, file);
            }
        });
        buttonPanel.add(done);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        buttonPanel.add(cancel);

        getRootPane().setDefaultButton(done);

        container.add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(container);
        pack();
        Utility.centerWindow(this);
    }

    public void doImport(int putName, boolean extract, File directory) {
        File[] files = directory.listFiles();

        try {
            for (int i = 0; i < files.length; i++) {
                FileInputStream fileIn = null;
                byte[] buffer = new byte[(int) files[i].length()];
                try {
                    fileIn = new FileInputStream(files[i]);
                    fileIn.read(buffer);
                    fileIn.close();
                } catch (Exception e) {
                    buffer = new byte[1];
                }
                if (buffer.length > 16) {
                    List<IPatch> patarray = patchImporter.createPatches(buffer);
                    if (patarray == null) {
                        ErrorMsg.reportError(
                                "Import All",
                                "Can't import a file \""
                                        + files[i].getCanonicalPath()
                                        + "\". Load a proper synth driver.");
                        continue;
                    }

                    // Loop over all found sub-patches
                    for (IPatch pk : patarray) {
                        if (putName == 1)
                            pk.setDate(pk.getDate() + files[i].getName());
                        if (putName == 2)
                            pk.setAuthor(pk.getAuthor() + files[i].getName());
                        if (myModel.includeDevice[pk.getDriver().getDevice()
                                .getDeviceNum()].booleanValue()) {
                            LibraryFrame frame =
                                    (LibraryFrame) JSLDesktop.Factory
                                            .getDesktop().getSelectedFrame();
                            if (extract && (pk.isBankPatch())) {
                                String[] pn = pk.getDriver().getPatchNumbers();
                                for (int j = 0; j < ((IBankPatch) pk)
                                        .getNumPatches(); j++) {
                                    IPatch q = ((IBankPatch) pk).get(j);
                                    if (putName == 1)
                                        q.setDate(q.getDate()
                                                + files[i].getName() + " "
                                                + pn[j]);
                                    if (putName == 2)
                                        q.setAuthor(q.getAuthor()
                                                + files[i].getName() + " "
                                                + pn[j]);
                                    frame.pastePatch(q);
                                }
                            } else {
                                frame.pastePatch(pk);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            ErrorMsg.reportError("Error", "Unable to Import Patches", e);
            return;
        }
    }

    class ImportModel extends AbstractTableModel {
        private final DeviceManager deviceManager = JSynthLibInjector
                .getInstance(DeviceManager.class);
        private final String[] columnNames = {
                "Include?", "Driver" };
        Boolean[] includeDevice = new Boolean[deviceManager.deviceCount()];

        public ImportModel() {
            super();

            for (int i = 0; i < includeDevice.length; i++)
                includeDevice[i] = new Boolean(true);
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return deviceManager.deviceCount();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            Device myDevice = deviceManager.getDevice(row);
            if (col == 1)
                return myDevice.getManufacturerName() + " "
                        + myDevice.getModelName()/*
                                                  * +" "+myDriver.getPatchType
                                                  * ()
                                                  */;
            else
                return includeDevice[row];
        }

        /*
         * JTable uses this method to determine the default renderer/ editor for
         * each cell. If we didn't implement this method, then the last column
         * would contain text ("true"/"false"), rather than a check box.
         */
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        /*
         * Don't need to implement this method unless your table's editable.
         */
        public boolean isCellEditable(int row, int col) {
            // Note that the data/cell address is constant,
            // no matter where the cell appears onscreen.
            if (col == 0)
                return true;
            else
                return false;
        }

        /*
         * Don't need to implement this method unless your table's data can
         * change.
         */
        public void setValueAt(Object value, int row, int col) {
            includeDevice[row] = (Boolean) value;
            fireTableCellUpdated(row, col);
        }
    }

}
