package core.guiaction;

import javax.swing.JMenuItem;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.data.TableCell;
import org.fest.swing.data.TableCell.TableCellBuilder;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTableFixture;

public class CleanLibraryAction extends AbstractGuiAction {

    private JTableFixture table;

    public CleanLibraryAction(FrameFixture testFrame, JTableFixture table) {
        super(testFrame);
        this.table = table;
    }

    @Override
    public void perform() {
        String[][] contents = table.contents();
        for (int i = 0; i < contents.length; i++) {
            TableCellBuilder cellBuilder = TableCell.row(i);
            TableCell tableCell = cellBuilder.column(0);
            table.cell(tableCell).rightClick();
            testFrame.menuItem(
                    new GenericTypeMatcher<JMenuItem>(JMenuItem.class) {

                        @Override
                        protected boolean isMatching(JMenuItem component) {
                            return "Delete".equals(component.getActionCommand())
                                    && component.isShowing();
                        }
                    }).click();
        }
    }

}
