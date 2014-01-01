package core.guiaction;

import javax.swing.JMenuItem;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.data.TableCell;
import org.fest.swing.data.TableCell.TableCellBuilder;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JMenuItemFixture;
import org.fest.swing.fixture.JTableFixture;

public class SendPatchAction extends AbstractGuiAction {

    private JTableFixture table;
    private int col;
    private int row;
    
    public SendPatchAction(FrameFixture testFrame, final JTableFixture table, final int col,
            final int row) {
        super(testFrame);
        this.table = table;
        this.row = row;
        this.col = col;
    }

    @Override
    public void perform() {
        log.info("Selecting driver on " + row + " row");
        TableCellBuilder cellBuilder = TableCell.row(row);
        TableCell tableCell = cellBuilder.column(col);
        table.cell(tableCell).rightClick();

        try {
            JMenuItemFixture menuItem =
                    testFrame.menuItem(new GenericTypeMatcher<JMenuItem>(
                            JMenuItem.class) {

                        @Override
                        protected boolean isMatching(JMenuItem component) {
                            return "Send".equals(component.getActionCommand())
                                    && component.isShowing();
                        }
                    });
            menuItem.click();
        } catch (Exception e) {
            log.info("Driver does not support single patch sending");
        }
    }

}
