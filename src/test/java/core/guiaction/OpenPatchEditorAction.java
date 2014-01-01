package core.guiaction;

import javax.swing.JMenuItem;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.data.TableCell;
import org.fest.swing.data.TableCell.TableCellBuilder;
import org.fest.swing.fixture.ContainerFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JMenuItemFixture;
import org.fest.swing.fixture.JTableFixture;

public class OpenPatchEditorAction extends AbstractGuiAction {

    private IPopupListener listener;
    private JTableFixture table;
    @SuppressWarnings("rawtypes")
    private ContainerFixture frame;

    public OpenPatchEditorAction(FrameFixture testFrame, JTableFixture table,
            final IPopupListener listener) {
        super(testFrame);
        this.table = table;
        this.listener = listener;
    }

    @Override
    public void perform() {
        String[][] contents = table.contents();
        TableCellBuilder cellBuilder = TableCell.row(contents.length - 1);
        TableCell tableCell = cellBuilder.column(0);
        log.info("Selecting driver on " + (contents.length - 1) + " row");
        table.cell(tableCell).rightClick();

        try {
            JMenuItemFixture menuItem =
                    testFrame.menuItem(new GenericTypeMatcher<JMenuItem>(
                            JMenuItem.class) {

                        @Override
                        protected boolean isMatching(JMenuItem component) {
                            return "Edit...".equals(component
                                    .getActionCommand())
                                    && component.isShowing();
                        }
                    });
            menuItem.click();
        } catch (Exception e) {
            log.info("Driver does not support editing");
            return;
        }

        waitForPopups(listener);
        frame = findNonLibrarayFrame();
    }

    @SuppressWarnings("rawtypes")
    public ContainerFixture getFrame() {
        return frame;
    }

}
