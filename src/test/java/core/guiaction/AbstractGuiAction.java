package core.guiaction;

import java.awt.Dialog;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.ContainerFixture;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JInternalFrameFixture;

import core.TitleFinder;

public abstract class AbstractGuiAction {

    protected final Logger log = Logger.getLogger(getClass());
    protected final FrameFixture testFrame;

    public AbstractGuiAction(FrameFixture testFrame) {
        this.testFrame = testFrame;
    }

    public abstract void perform();

    public DialogFixture openPreferencesDialog() {
        testFrame.menuItem(new GenericTypeMatcher<JMenuItem>(JMenuItem.class) {

            @Override
            protected boolean isMatching(JMenuItem component) {
                return component.getActionCommand().equals("Preferences...");
            }
        }).click();

        return testFrame.dialog();
    }

    public void closeDialog(final String dialogName) {
        testFrame.dialog(new GenericTypeMatcher<Dialog>(Dialog.class) {

            @Override
            protected boolean isMatching(Dialog component) {
                return dialogName.equals(component.getTitle())
                        && component.isShowing();
            }
        }).button(new GenericTypeMatcher<JButton>(JButton.class) {

            @Override
            protected boolean isMatching(JButton component) {
                return "OK".equals(component.getText());
            }
        }).click();
    }

    @SuppressWarnings("rawtypes")
    public void closeFrame(ContainerFixture fixture)
            throws InterruptedException {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    testFrame
                            .dialog(new GenericTypeMatcher<JDialog>(
                                    JDialog.class) {

                                @Override
                                protected boolean isMatching(JDialog component) {
                                    return component.getTitle().contains(
                                            "Save Changes?")
                                            && component.isVisible();
                                }
                            })
                            .button(new GenericTypeMatcher<JButton>(
                                    JButton.class) {

                                @Override
                                protected boolean isMatching(JButton component) {
                                    return component.getText().contains(
                                            "Revert");
                                }
                            }).click();
                } catch (Exception e) {
                    // log.warn(e.getMessage(), e);
                }
            }
        }).start();
        if (fixture instanceof JInternalFrameFixture) {
            JInternalFrameFixture type = (JInternalFrameFixture) fixture;
            log.info("Closing JInternalFrameFixture "
                    + fixture.target.getName());
            type.close();
        } else if (fixture instanceof FrameFixture) {
            FrameFixture type = (FrameFixture) fixture;
            log.info("Closing FrameFixture " + fixture.target.getName());
            type.close();
        }
    }

    public interface IPopupListener {
        void onPopupDetected(DialogFixture dialog);
    }

    protected void waitForPopups(final IPopupListener listener) {
        final Semaphore semaphore = new Semaphore(0);

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(1000);
                        DialogFixture patchEditDialog = testFrame.dialog();
                        log.info("Found dialog when opening editor");
                        listener.onPopupDetected(patchEditDialog);
                        log.info("Closing open editor dialog");
                        closeDialog(patchEditDialog.target.getTitle());
                    }
                } catch (Exception e) {
                } finally {
                    semaphore.release();
                }
            }
        }).start();

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
        }
    }

    protected DialogFixture openNewPatchDialogAndSelectDevice(String deviceName) {
        testFrame.menuItem(new GenericTypeMatcher<JMenuItem>(JMenuItem.class) {

            @Override
            protected boolean isMatching(JMenuItem component) {
                return component.getActionCommand().equals("New Patch...");
            }
        }).click();

        DialogFixture dialog = testFrame.dialog();

        JComboBoxFixture comboBox =
                dialog.comboBox(new GenericTypeMatcher<JComboBox>(
                        JComboBox.class) {

                    @Override
                    protected boolean isMatching(JComboBox component) {
                        return "deviceCb".equals(component.getName());
                    }
                });
        JComboBox component = comboBox.component();
        if (component.isEnabled()) {
            for (int i = 0; i < component.getItemCount(); i++) {
                String item = component.getItemAt(i).toString();
                if (item.contains(deviceName)) {
                    comboBox.selectItem(i);
                }
            }
        }

        return dialog;
    }

    @SuppressWarnings("rawtypes")
    protected ContainerFixture findNonLibrarayFrame() {
        Map<String, ContainerFixture> windowTitles =
                TitleFinder.getWindowTitles(testFrame);
        if (windowTitles.size() == 2) {
            Iterator<Entry<String, ContainerFixture>> iterator =
                    windowTitles.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, ContainerFixture> entry = iterator.next();
                if (!entry.getKey().contains("Unsaved Library")) {
                    return entry.getValue();
                }
            }
        } else {
            log.warn("Too many frames detected");
        }

        return null;
    }
    
    protected interface ComboBoxMatcher {
        boolean matches(Object item);
    }

    protected void setComboBoxValue(JComboBoxFixture comboBox, ComboBoxMatcher itemMatcher) {
        final JComboBox component = comboBox.component();

        if (component.isEnabled()) {
            for (int i = 0; i < component.getItemCount(); i++) {
                Object item = component.getItemAt(i);
                if (itemMatcher.matches(item)) {
                    comboBox.selectItem(i);
                    if (component.getSelectedIndex() != i) {
                        final int j = i;
                        GuiActionRunner.execute(new GuiQuery<Object>() {
                            public Object executeInEDT() {
                                component.setSelectedIndex(j);
                                return null;
                            }
                        });
                    }
                    break;
                }
            }
        }
    }
}
