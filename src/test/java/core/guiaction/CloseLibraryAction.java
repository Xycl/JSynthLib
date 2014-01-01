package core.guiaction;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.UIManager;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.ContainerFixture;
import org.fest.swing.fixture.FrameFixture;

import core.TitleFinder;

public class CloseLibraryAction extends AbstractGuiAction {

    public CloseLibraryAction(FrameFixture testFrame) {
        super(testFrame);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void perform() {
        final String noOption =
                (String) UIManager.get("OptionPane.noButtonText");
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
                                            "Unsaved Data")
                                            && component.isVisible();
                                }
                            })
                            .button(new GenericTypeMatcher<JButton>(
                                    JButton.class) {

                                @Override
                                protected boolean isMatching(JButton component) {
                                    return noOption.equals(component.getText());
                                }
                            }).click();
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }).start();

        Map<String, ContainerFixture> windowTitles =
                TitleFinder.getWindowTitles(testFrame);
        Iterator<Entry<String, ContainerFixture>> iterator =
                windowTitles.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, ContainerFixture> entry = iterator.next();
            if (entry.getKey().contains("Unsaved Library")) {
                log.info("Closing library frame " + entry.getKey());
                try {
                    closeFrame(entry.getValue());
                } catch (InterruptedException e) {
                }
                break;
            }
        }
    }

}
