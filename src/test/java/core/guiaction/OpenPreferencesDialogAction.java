package core.guiaction;

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;

public class OpenPreferencesDialogAction extends AbstractGuiAction {

    private DialogFixture dialog;

    public OpenPreferencesDialogAction(FrameFixture testFrame) {
        super(testFrame);
    }

    @Override
    public void perform() {
        dialog = openPreferencesDialog();
    }

    public DialogFixture getDialog() {
        return dialog;
    }

}
