package core.guiaction;

import javax.swing.JMenuItem;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JMenuItemFixture;
import org.fest.swing.fixture.JTableFixture;

public class OpenLibraryAction extends AbstractGuiAction {

    public OpenLibraryAction(FrameFixture testFrame) {
        super(testFrame);
    }

    @Override
    public void perform() {
        JMenuItemFixture menuItem =
                testFrame.menuItem(new GenericTypeMatcher<JMenuItem>(
                        JMenuItem.class) {

                    @Override
                    protected boolean isMatching(JMenuItem component) {
                        return component.getActionCommand().equals(
                                "New Library");
                    }
                });
        menuItem.click();
    }

    public JTableFixture getTableFixture() {
        return testFrame.table();
    }
}
