package core.guiaction;

import javax.swing.JMenuItem;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;

public class OpenDialogAction extends AbstractGuiAction {

    private String menuName;
    private DialogFixture dialog;

    public OpenDialogAction(FrameFixture testFrame, String menuName) {
        super(testFrame);
        this.menuName = menuName;
    }

    @Override
    public void perform() {
        testFrame.menuItem(new GenericTypeMatcher<JMenuItem>(JMenuItem.class) {

            @Override
            protected boolean isMatching(JMenuItem component) {
                return component.getActionCommand().equals(menuName);
            }
        }).click();

        dialog = testFrame.dialog();

    }

    public DialogFixture getDialog() {
        return dialog;
    }

}
