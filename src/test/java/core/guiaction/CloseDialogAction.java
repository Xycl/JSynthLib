package core.guiaction;

import org.fest.swing.fixture.FrameFixture;

public class CloseDialogAction extends AbstractGuiAction {

    private String dialogName;

    public CloseDialogAction(FrameFixture testFrame, String dialogName) {
        super(testFrame);
        this.dialogName = dialogName;
    }

    @Override
    public void perform() {
        closeDialog(dialogName);
    }

}
