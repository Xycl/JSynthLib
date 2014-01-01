package core.guiaction;

import org.fest.swing.fixture.ContainerFixture;
import org.fest.swing.fixture.FrameFixture;

public class CloseFrameAction extends AbstractGuiAction {

    @SuppressWarnings("rawtypes")
    private ContainerFixture fixture;

    @SuppressWarnings("rawtypes")
    public CloseFrameAction(FrameFixture testFrame, ContainerFixture fixture) {
        super(testFrame);
        this.fixture = fixture;
    }

    @Override
    public void perform() {
        try {
            closeFrame(fixture);
        } catch (InterruptedException e) {
        }
    }

}
