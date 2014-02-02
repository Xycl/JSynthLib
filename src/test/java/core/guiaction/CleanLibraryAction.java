/*
 * Copyright 2014 Pascal Collberg
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
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
