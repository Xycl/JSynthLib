package core;

// import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.*;
import java.awt.*;

/**
 * Dialog to create a new Patch of the loaded Devices respective Drivers.
 * Any 'Generic' device and 'Converter' driver are skipped.
 *
 * @author  unascribed
 * @author  Torsten Tittmann
 * @version $Id$
 */
public class NewPatchDialog extends JDialog
{
    private JComboBox deviceComboBox;
    private JComboBox driverComboBox;
    private IPatch p;

    public NewPatchDialog(JFrame parent)
    {
        super(parent,"Create New Patch",true);

	JPanel container=new JPanel(new BorderLayout(5,5));

        JLabel myLabel=new JLabel("Please select a Patch Type to Create.",JLabel.CENTER);
	container.add(myLabel, BorderLayout.NORTH);

	deviceComboBox =new JComboBox();
	deviceComboBox.addActionListener(new DeviceActionListener());
	driverComboBox =new JComboBox();

	// First Populate the Device/Driver List with
	// Device/Driver. which supports the "createNewPatch" method
// 	try {
	// Skipping the generic device (i == 0)
	for (int i=1; i < PatchEdit.appConfig.deviceCount(); i++) {
	    Device device = (Device) AppConfig.getDevice(i);
	    for (int j=0; j < device.driverCount(); j++) {
		IDriver driver = device.getDriver(j);
		//XXX:Should be IPatchDriver
		if (driver instanceof IPatchDriver) { // Skipping a converter
		    if (((IPatchDriver)driver).canCreatePatch())
			deviceComboBox.addItem(device);
		}
	    }
	}
	deviceComboBox.setEnabled(deviceComboBox.getItemCount() > 1);

	//----- Layout the labels in a panel.
	JPanel labelPanel = new JPanel(new GridLayout(0, 1, 5, 5));
	labelPanel.add(new JLabel("Device:", JLabel.LEFT));
	labelPanel.add(new JLabel("Driver:", JLabel.LEFT));

	//----- Layout the fields in a panel
	JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
	fieldPanel.add(deviceComboBox);
	fieldPanel.add(driverComboBox);

	//----- Create the comboPanel, labels on left, fields on right
	JPanel comboPanel = new JPanel(new BorderLayout());
	comboPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	comboPanel.add(labelPanel, BorderLayout.CENTER);
	comboPanel.add(fieldPanel, BorderLayout.EAST);
	container.add(comboPanel, BorderLayout.CENTER);

	//----- Create "Create" button
	JPanel buttonPanel=new JPanel();
	JButton create = new JButton(" Create ");
	create.addActionListener(new ActionListener()
	    {
		public void actionPerformed(ActionEvent e)
		{
		    IPatchDriver driver = (IPatchDriver) driverComboBox.getSelectedItem();

		    p = driver.createNewPatch();
		    if (p != null) {
			ErrorMsg.reportStatus("Bingo " + driver.toString());
		    } else {
			// If a driver does not override
			// createNewPatch method unnecessary, this
			// error never occurs.
			ErrorMsg.reportError("New Patch Error", "The driver does not support `New Patch' function.");
		    }
		    setVisible(false);
		    dispose();
		}
	    });
	buttonPanel.add( create );

	//----- Create "Cancel" button
	JButton cancel = new JButton("Cancel");
	cancel.addActionListener(new ActionListener()
	    {
		public void actionPerformed(ActionEvent e)
		{
		    setVisible(false);
		    dispose();
		}
	    });
	buttonPanel.add( cancel );

	getRootPane().setDefaultButton(create);

	container.add(buttonPanel, BorderLayout.SOUTH);
	getContentPane().add(container);
	pack();
	Utility.centerDialog(this);
// 	} catch(Exception e) {
// 	    ErrorMsg.reportStatus(e);
// 	}
    }

    public IPatch getNewPatch() { return p; }

    /**
     * Repopulate the Driver ComboBox with valid drivers after a Device change
     */
    public class DeviceActionListener implements ActionListener
    {
	public void actionPerformed (ActionEvent evt)
	{
	    driverComboBox.removeAllItems();

	    Device device = (Device) deviceComboBox.getSelectedItem();
	    for (int i = 0; i < device.driverCount(); i++) {
		IDriver driver = device.getDriver(i);
		if (driver instanceof IPatchDriver) {
		    try {
			// If the actual driver doesn't override the
			// method "createNewPatch" this command will
			// throw an exception.  This means, that the
			// driver doesn't support the creation of a
			// new patch.
			driver.getClass().getDeclaredMethod("createNewPatch", null);
			driverComboBox.addItem (driver);
		    } catch (Exception ex) {
			// This is normal.  Simply do nothing....
		    }
		}
	    }
	    driverComboBox.setEnabled(driverComboBox.getItemCount() > 1);
	}
    }
}
