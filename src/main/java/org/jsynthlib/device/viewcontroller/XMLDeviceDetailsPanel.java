package org.jsynthlib.device.viewcontroller;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.prefs.Preferences;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jsynthlib.device.model.PreferenceUtil;
import org.jsynthlib.xmldevice.PreferenceDefs;
import org.jsynthlib.xmldevice.PreferenceDefs.PreferenceDef;
import org.jsynthlib.xmldevice.PreferenceDefs.PreferenceDef.Type.Enum;
import org.jsynthlib.xmldevice.PreferenceDefs.PreferenceDef.Values;

public class XMLDeviceDetailsPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final JScrollPane scrollPane;
    private final JPanel panel;
    private final Preferences preferences;

    public XMLDeviceDetailsPanel(Preferences preferences,
            PreferenceDefs preferencesDefs) {
        this.preferences = preferences;
        setLayout(new BorderLayout(0, 0));

        scrollPane = new JScrollPane();
        add(scrollPane);

        panel = new JPanel();
        scrollPane.setViewportView(panel);
        GridBagLayout gblPanel = new GridBagLayout();
        panel.setLayout(gblPanel);

        PreferenceDef[] preferenceDefs = preferencesDefs.getPreferenceDefArray();
        for (int i = 0; i < preferenceDefs.length; i++) {
            PreferenceDef preferenceDef = preferenceDefs[i];
            Enum type =
                    preferenceDef.getType();
            switch (type.intValue()) {
            case PreferenceDef.Type.INT_BOOLEAN:
                addBooleanPreference(preferenceDef, i);
                break;
            case PreferenceDef.Type.INT_INTEGER:
                addIntPreference(preferenceDef, i);
                break;
            case PreferenceDef.Type.INT_STRING:
            default:
                if (preferenceDef.getValues() == null) {
                    addStringPreference(preferenceDef, i);
                } else {
                    addValuedPreference(preferenceDef, i);
                }
                break;
            }
        }
    }

    final void addStringPreference(final PreferenceDef pref, int row) {
        JLabel label = new JLabel(pref.getDescription());
        GridBagConstraints gbcLabel = new GridBagConstraints();
        gbcLabel.insets = new Insets(0, 0, 5, 5);
        gbcLabel.anchor = GridBagConstraints.EAST;
        gbcLabel.gridx = 0;
        gbcLabel.gridy = row;
        panel.add(label, gbcLabel);

        final JTextField textField = new JTextField();
        textField.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                preferences.put(pref.getName(), textField.getText());
            }

            @Override
            public void focusGained(FocusEvent e) {
            }
        });
        String startValue = "";
        if (PreferenceUtil.containsPreference(preferences, pref.getName())) {
            startValue = preferences.get(pref.getName(), "");
        } else if (pref.getDefaultValue() != null) {
            startValue = pref.getDefaultValue();
        }
        textField.setText(startValue);

        GridBagConstraints gbcTextField = new GridBagConstraints();
        gbcTextField.insets = new Insets(0, 0, 5, 0);
        gbcTextField.fill = GridBagConstraints.HORIZONTAL;
        gbcTextField.gridx = 1;
        gbcTextField.gridy = row;
        panel.add(textField, gbcTextField);
        textField.setColumns(10);
    }

    final void addIntPreference(final PreferenceDef pref, int row) {
        JLabel label = new JLabel(pref.getDescription());
        GridBagConstraints gbcLabel = new GridBagConstraints();
        gbcLabel.insets = new Insets(0, 0, 5, 5);
        gbcLabel.anchor = GridBagConstraints.EAST;
        gbcLabel.gridx = 0;
        gbcLabel.gridy = row;
        panel.add(label, gbcLabel);

        final JTextField textField = new JTextField() {
            private static final long serialVersionUID = 1L;

            @Override
            public void processKeyEvent(KeyEvent ev) {
                char c = ev.getKeyChar();
                try {
                    // Ignore all non-printable characters. Just check the
                    // printable ones.
                    if (c > 31 && c < 127) {
                        Integer.parseInt(c + "");
                    }
                    super.processKeyEvent(ev);
                } catch (NumberFormatException nfe) {
                    // Do nothing. Character input is not a number, so ignore
                    // it.
                }
            }
        };
        textField.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                preferences.putInt(pref.getName(),
                        Integer.parseInt(textField.getText()));
            }

            @Override
            public void focusGained(FocusEvent e) {
            }
        });
        String startValue = "";
        if (PreferenceUtil.containsPreference(preferences, pref.getName())) {
            startValue = preferences.get(pref.getName(), "");
        } else if (pref.getDefaultValue() != null) {
            startValue = pref.getDefaultValue();
        }
        textField.setText(startValue);
        GridBagConstraints gbcTextField = new GridBagConstraints();
        gbcTextField.insets = new Insets(0, 0, 5, 0);
        gbcTextField.fill = GridBagConstraints.HORIZONTAL;
        gbcTextField.gridx = 1;
        gbcTextField.gridy = row;
        panel.add(textField, gbcTextField);
        textField.setColumns(10);
    }

    final void addValuedPreference(final PreferenceDef pref, int row) {
        JLabel label = new JLabel(pref.getDescription());
        GridBagConstraints gbcLabel = new GridBagConstraints();
        gbcLabel.anchor = GridBagConstraints.EAST;
        gbcLabel.insets = new Insets(0, 0, 5, 5);
        gbcLabel.gridx = 0;
        gbcLabel.gridy = row;
        panel.add(label, gbcLabel);

        final JComboBox<String> comboBox = new JComboBox<String>();
        Values values = pref.getValues();
        String[] valueArray = values.getValueArray();
        for (String value : valueArray) {
            comboBox.addItem(value);
        }
        comboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                preferences.put(pref.getName(), (String) e.getItem());
            }
        });
        String selectedItem = null;
        if (PreferenceUtil.containsPreference(preferences, pref.getName())) {
            selectedItem = preferences.get(pref.getName(), "");
        } else if (pref.getDefaultValue() != null) {
            selectedItem = pref.getDefaultValue();
        }
        comboBox.setSelectedItem(selectedItem);

        GridBagConstraints gbcComboBox = new GridBagConstraints();
        gbcComboBox.insets = new Insets(0, 0, 5, 0);
        gbcComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbcComboBox.gridx = 1;
        gbcComboBox.gridy = row;
        panel.add(comboBox, gbcComboBox);
    }

    final void addBooleanPreference(final PreferenceDef pref, int row) {
        JLabel label = new JLabel(pref.getDescription());
        GridBagConstraints gbcLabel = new GridBagConstraints();
        gbcLabel.insets = new Insets(0, 0, 0, 5);
        gbcLabel.gridx = 0;
        gbcLabel.gridy = row;
        panel.add(label, gbcLabel);

        final JCheckBox checkBox = new JCheckBox();
        checkBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                preferences.putBoolean(pref.getName(), checkBox.isSelected());
            }
        });
        boolean startValue = false;
        if (PreferenceUtil.containsPreference(preferences, pref.getName())) {
            startValue = preferences.getBoolean(pref.getName(), false);
        } else if (pref.getDefaultValue() != null) {
            startValue = Boolean.parseBoolean(pref.getDefaultValue());
        }
        checkBox.setSelected(startValue);
        GridBagConstraints gbcCheckBox = new GridBagConstraints();
        gbcCheckBox.gridx = 1;
        gbcCheckBox.gridy = row;
        panel.add(checkBox, gbcCheckBox);
    }

}
