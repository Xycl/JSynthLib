package core.guiaction;

import java.awt.Dialog;

import javax.swing.JButton;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTreeFixture;

import core.DeviceAddDialog;

public class InstallDeviceAction extends AbstractGuiAction {

    private String driverName;
    private String manufacturer;

    public InstallDeviceAction(FrameFixture testFrame, String manufacturer, String driverName) {
        super(testFrame);
        this.manufacturer = manufacturer;
        this.driverName = driverName;
    }

    @Override
    public void perform() {
        DialogFixture prefsDialog = openPreferencesDialog();
        JTabbedPaneFixture tabbedPane = prefsDialog.tabbedPane();
        tabbedPane.selectTab("Synth Driver");

        prefsDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

            @Override
            protected boolean isMatching(JButton component) {
                return "Add Device...".equals(component.getText());
            }
        }).click();

        DialogFixture driverDialog =
                testFrame.dialog(new GenericTypeMatcher<Dialog>(Dialog.class) {

                    @Override
                    protected boolean isMatching(Dialog component) {
                        if (component instanceof DeviceAddDialog
                                && component.isVisible()) {
                            return true;
                        }
                        return false;
                    }
                });
        JTreeFixture tree = driverDialog.tree();

        String separator = "/";
        if (driverName.contains(separator)) {
            separator = ";";
            tree.separator(separator);
        }

        tree.selectPath("Manufacturers" + separator + manufacturer + separator
                + driverName);

        closeDialog(driverDialog.component().getTitle());

        closeDialog("Device Information");

        closeDialog(prefsDialog.component().getTitle());
    }

}
