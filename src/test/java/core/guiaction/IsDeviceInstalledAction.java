package core.guiaction;

import javax.swing.JPanel;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTableFixture;

import core.SynthConfigPanel;

public class IsDeviceInstalledAction extends AbstractGuiAction {

    private String deviceName;
    private boolean foundDevice;

    public IsDeviceInstalledAction(FrameFixture testFrame, String deviceName) {
        super(testFrame);
        this.deviceName = deviceName;
    }

    @Override
    public void perform() {
        log.info("Check driver " + deviceName);
        DialogFixture prefsDialog = openPreferencesDialog();
        JTabbedPaneFixture tabbedPane = prefsDialog.tabbedPane();
        tabbedPane.selectTab("Synth Driver");

        JTableFixture table =
                prefsDialog.panel(new GenericTypeMatcher<JPanel>(JPanel.class) {

                    @Override
                    protected boolean isMatching(JPanel component) {
                        if (component instanceof SynthConfigPanel) {
                            return true;
                        }
                        return false;
                    }
                }).table();

        foundDevice = false;
        String[][] contents = table.contents();
        for (int i = 0; i < contents.length; i++) {
            if (deviceName.contains(contents[i][1])) {
                foundDevice = true;
                break;
            }
        }

        closeDialog(prefsDialog.component().getTitle());
    }

    public boolean isFoundDevice() {
        return foundDevice;
    }

}
