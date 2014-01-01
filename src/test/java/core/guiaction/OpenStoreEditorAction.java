package core.guiaction;

import javax.swing.JDialog;
import javax.swing.JMenuItem;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.data.TableCell;
import org.fest.swing.data.TableCell.TableCellBuilder;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JMenuItemFixture;
import org.fest.swing.fixture.JTableFixture;

public class OpenStoreEditorAction extends AbstractGuiAction {

    private JTableFixture table;
    private DialogFixture dialog;

    public OpenStoreEditorAction(FrameFixture testFrame, JTableFixture table) {
        super(testFrame);
        this.table = table;
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
                            return "Store...".equals(component
                                    .getActionCommand())
                                    && component.isShowing();
                        }
                    });
            menuItem.click();
        } catch (Exception e) {
            log.info("Driver does not support storing");
            return;
        }

        try {
            dialog =
                    testFrame.dialog(new GenericTypeMatcher<JDialog>(
                            JDialog.class) {

                        @Override
                        protected boolean isMatching(JDialog component) {
                            return "Store Sysex Data".equals(component
                                    .getTitle()) && component.isShowing();
                        }
                    });
        } catch (Exception e) {
            log.warn("Failed to find patch store dialog!");
        }
    }

    public DialogFixture getFrame() {
        return dialog;
    }

}
