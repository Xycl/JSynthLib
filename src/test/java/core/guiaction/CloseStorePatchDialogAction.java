package core.guiaction;

import javax.swing.JButton;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;

public class CloseStorePatchDialogAction extends AbstractGuiAction {

    private boolean send;
    private DialogFixture fixture;

    public CloseStorePatchDialogAction(FrameFixture testFrame,
            DialogFixture fixture, boolean send) {
        super(testFrame);
        this.fixture = fixture;
        this.send = send;
    }

    @Override
    public void perform() {
        fixture.button(new GenericTypeMatcher<JButton>(JButton.class) {

            @Override
            protected boolean isMatching(JButton component) {
                if (send) {
                    return "Store...".equals(component.getText());
                } else {
                    return "Cancel".equals(component.getText());
                }
            }
        }).click();
    }
}
