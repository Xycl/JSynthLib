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

import java.util.List;

import javax.swing.JMenuItem;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.data.TableCell;
import org.fest.swing.data.TableCell.TableCellBuilder;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JMenuItemFixture;
import org.fest.swing.fixture.JTableFixture;

import core.TitleFinder;
import core.TitleFinder.FrameWrapper;

public class OpenPatchEditorAction extends AbstractGuiAction {

    private IPopupListener listener;
    private JTableFixture table;
    private FrameWrapper frame;
    private int row;
    private int col;
    private boolean maximize;

    public OpenPatchEditorAction(FrameFixture testFrame, JTableFixture table,
            int row, int col, final IPopupListener listener, boolean maximize) {
        super(testFrame);
        this.table = table;
        this.row = row;
        this.col = col;
        this.listener = listener;
        this.maximize = maximize;
    }

    @Override
    public void perform() {
        String[][] contents = table.contents();
        List<FrameWrapper> before = TitleFinder.getWindowTitles(testFrame);
        if (row == -1) {
            row = contents.length - 1;
        }
        TableCellBuilder cellBuilder = TableCell.row(row);
        TableCell tableCell = cellBuilder.column(col);
        log.info("Selecting driver on " + row + " row " + col + " col.");
        table.cell(tableCell).select();
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
        List<FrameWrapper> after = TitleFinder.getWindowTitles(testFrame);
        frame = getOpenedFrame(before, after);
        if (frame != null && maximize) {
            frame.maximize();
        }
    }

    public FrameWrapper getFrame() {
        return frame;
    }

}
