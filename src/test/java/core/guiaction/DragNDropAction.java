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

import org.fest.swing.data.TableCell;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTableCellFixture;
import org.fest.swing.fixture.JTableFixture;

import core.TitleFinder.FrameWrapper;

public class DragNDropAction extends AbstractGuiAction {

    private FrameWrapper source;
    private int row;
    private int col;
    private int destRow;
    private int destCol;

    public DragNDropAction(FrameFixture testFrame, FrameWrapper source,
            int row, int col, int destRow, int destCol) {
        super(testFrame);
        this.source = source;
        this.row = row;
        this.col = col;
        this.destRow = destRow;
        this.destCol = destCol;
    }

    @Override
    public void perform() {
        log.info("Bringing " + source.toString() + " to front");
        source.moveToFront();
        JTableFixture table = source.table();

        JTableCellFixture cell = table.cell(TableCell.row(row).column(col));
        cell.drag();

        table.cell(TableCell.row(destRow).column(destCol)).drop();
    }

}
