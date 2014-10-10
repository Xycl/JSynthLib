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
package org.jsynthlib.core.guiaction;

import org.fest.swing.data.TableCell;
import org.fest.swing.data.TableCell.TableCellBuilder;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTableFixture;

public class SetTableCellValueAction extends AbstractGuiAction {

    private String data;
    private int col;
    private JTableFixture table;
    private int row;

    public SetTableCellValueAction(FrameFixture testFrame,
            JTableFixture table, int row, int col, String data) {
        super(testFrame);
        this.table = table;
        this.row = row;
        this.col = col;
        this.data = data;
    }

    @Override
    public void perform() {
        TableCellBuilder cellBuilder = TableCell.row(row);
        TableCell tableCell = cellBuilder.column(col);
        log.info("Adding meta data in field " + col + ": " + data);
        table.cell(tableCell).enterValue(data);
    }

}
