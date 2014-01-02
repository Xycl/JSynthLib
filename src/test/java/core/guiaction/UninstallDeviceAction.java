package core.guiaction;

import java.awt.Dialog;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTableFixture;

public class UninstallDeviceAction extends AbstractGuiAction {

    private String deviceName;
    private boolean uninstalledDriver;

    public UninstallDeviceAction(FrameFixture testFrame, String deviceName) {
        super(testFrame);
        this.deviceName = deviceName;
    }

    @Override
    public void perform() {
        if (deviceName == null) {
            log.info("uninstall all drivers.");
        } else {
            log.info("uninstall driver " + deviceName);
        }
        DialogFixture prefsDialog = openPreferencesDialog();
        JTabbedPaneFixture tabbedPane = prefsDialog.tabbedPane();
        tabbedPane.selectTab("Synth Driver");
        JTableFixture table =
                prefsDialog.panel(new GenericTypeMatcher<JPanel>(JPanel.class) {

                    @Override
                    protected boolean isMatching(JPanel component) {
//                        if (component instanceof SynthConfigPanel) {
//                            return true;
//                        }
                        return false;
                    }
                }).table();
        String[][] contents = table.contents();
        uninstalledDriver = false;
        for (int i = 0; i < contents.length; i++) {
            if (deviceName == null || deviceName.equals(contents[i][1])) {
                if ("Generic Unknown".equals(contents[i][1])) {
                    continue;
                }
                uninstalledDriver = true;
                table.selectCell(table.cell(contents[i][1])).rightClick();
                prefsDialog.menuItem(
                        new GenericTypeMatcher<JMenuItem>(JMenuItem.class) {

                            @Override
                            protected boolean isMatching(JMenuItem component) {
                                return "Delete".equals(component
                                        .getActionCommand());
                            }
                        }).click();

                final String yesOption =
                        (String) UIManager.get("OptionPane.yesButtonText");
                prefsDialog
                        .dialog(new GenericTypeMatcher<Dialog>(Dialog.class) {

                            @Override
                            protected boolean isMatching(Dialog component) {
                                return "Remove Device?".equals(component
                                        .getTitle()) && component.isVisible();
                            }
                        })
                        .button(new GenericTypeMatcher<JButton>(JButton.class) {

                            @Override
                            protected boolean isMatching(JButton component) {
                                return yesOption.equals(component.getText());
                            }
                        }).click();
                if (deviceName != null) {
                    break;
                }
            }
        }

        closeDialog(prefsDialog.component().getTitle());
    }

    public boolean isUninstalledDriver() {
        return uninstalledDriver;
    }

}
