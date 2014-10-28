
package org.jsynthlib.device.viewcontroller;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jsynthlib.core.Utility;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.DeviceDescriptor;
import org.jsynthlib.device.model.DeviceManager;
import org.jsynthlib.device.model.DeviceSelectionTree;
import org.jsynthlib.inject.JSynthLibInjector;

public class DeviceAddDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final DeviceSelectionTree availableDeviceList;

    // DevicesConfig devConf = null;

    public DeviceAddDialog(JFrame parent) {
        super(parent, "Synthesizer Device Install", true);
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());

        availableDeviceList = new DeviceSelectionTree();
        JScrollPane scrollpane = new JScrollPane(availableDeviceList);
        container.add(scrollpane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // The following code catches double-clicks on leafs and treats them
        // like pressing "OK"
        MouseListener ml = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selRow =
                        availableDeviceList.getRowForLocation(e.getX(),
                                e.getY());
                TreePath tp = availableDeviceList.getPathForRow(selRow);
                // Did they even click on a tree item
                if (tp != null) {
                    if (e.getClickCount() == 2) {
                        // User double-clicked. What did they click on?
                        DefaultMutableTreeNode o =
                                (DefaultMutableTreeNode) tp
                                        .getLastPathComponent();
                        if (o.isLeaf()) {
                            // User double-clicked on a leaf. Treat it like "OK"
                            okPressed();
                        }
                    }
                }
            }
        };
        availableDeviceList.addMouseListener(ml);

        JButton ok = new JButton("OK");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okPressed();
            }
        });
        buttonPanel.add(ok);
        JButton cancel = new JButton("Cancel");

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelPressed();
            }
        });
        buttonPanel.add(cancel);

        getRootPane().setDefaultButton(ok);

        container.add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(container);
        setSize(400, 300);

        Utility.centerWindow(this);
    }

    void okPressed() {
        this.setVisible(false);
        String s = availableDeviceList.getSelectedValue();
        if (s == null) {
            return;
        }

        DeviceManager deviceManager = JSynthLibInjector.getInstance(DeviceManager.class);
        DeviceDescriptor descriptor = deviceManager.getDescriptorForDeviceName(s);
        Device device = deviceManager.addDevice(descriptor);
        if (device == null) {
            return;
        }

        String info = device.getInfoText();
        if (info != null && info.length() > 0) {
            JTextArea jta = new JTextArea(info, 15, 40);
            jta.setEditable(false);
            jta.setLineWrap(true);
            jta.setWrapStyleWord(true);
            jta.setCaretPosition(0);
            JScrollPane jasp = new JScrollPane(jta);
            JOptionPane.showMessageDialog(null, jasp, "Device Information",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    void cancelPressed() {
        this.setVisible(false);
    }
}
