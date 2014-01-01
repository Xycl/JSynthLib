package core.guiaction;

import javax.swing.JMenuItem;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;

public class OpenHelpDialogAction extends AbstractGuiAction {

    private DialogFixture dialog;

    public OpenHelpDialogAction(FrameFixture testFrame) {
        super(testFrame);
    }

    @Override
    public void perform() {
        testFrame.menuItem(new GenericTypeMatcher<JMenuItem>(JMenuItem.class) {

            @Override
            protected boolean isMatching(JMenuItem component) {

                return component.getActionCommand().equals("Help")
                        && component.getSubElements().length == 0;
            }
        }).click();

        dialog = testFrame.dialog();
    }

    public DialogFixture getDialog() {
        return dialog;
    }

}
