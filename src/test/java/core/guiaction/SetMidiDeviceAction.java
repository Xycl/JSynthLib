package core.guiaction;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;

public class SetMidiDeviceAction extends AbstractGuiAction {

    private String name;
    private String comboBoxName;

    public SetMidiDeviceAction(FrameFixture testFrame, String comboBoxName, String name) {
        super(testFrame);
        this.comboBoxName = comboBoxName;
        this.name = name;
    }

    @Override
    public void perform() {
        DialogFixture preferencesDialog = openPreferencesDialog();
        JTabbedPaneFixture tabbedPane = preferencesDialog.tabbedPane();
        tabbedPane.selectTab("MIDI");
        preferencesDialog.checkBox(
                new GenericTypeMatcher<JCheckBox>(JCheckBox.class) {

                    @Override
                    protected boolean isMatching(JCheckBox component) {
                        return "Enable MIDI Interface".equals(component
                                .getActionCommand());
                    }
                }).check();
        JComboBoxFixture outBox = preferencesDialog.comboBox(comboBoxName);

        JComboBox outComponent = outBox.component();
        int outCount = outComponent.getItemCount();
        for (int i = 0; i < outCount; i++) {
            String value = outComponent.getItemAt(i).toString();
            if (name.equals(value)) {
                outBox.selectItem(i);
            }
        }

        closeDialog(preferencesDialog.component().getTitle());
    }

}
