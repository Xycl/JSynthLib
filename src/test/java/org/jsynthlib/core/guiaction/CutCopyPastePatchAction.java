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
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTableCellFixture;
import org.fest.swing.fixture.JTableFixture;
import org.jsynthlib.core.TitleFinder.FrameWrapper;

public class CutCopyPastePatchAction extends AbstractGuiAction {
    private FrameWrapper source;
    private int row;
    private int col;
    private FrameWrapper destination;
    private boolean copy;

    public CutCopyPastePatchAction(FrameFixture testFrame, FrameWrapper source,
            int row, int col, FrameWrapper destination, boolean copy) {
        super(testFrame);
        this.source = source;
        this.row = row;
        this.col = col;
        this.destination = destination;
        this.copy = copy;
    }

    @Override
    public void perform() {
        log.info("Bringing " + source.toString() + " to front");
        source.moveToFront();
        JTableFixture table = source.table();

        JTableCellFixture cell = table.cell(TableCell.row(row).column(col));
        cell.select();
        if (copy) {
            clickMenuItem("Copy");
        } else {
            clickMenuItem("Cut");
        }

        log.info("Bringing " + destination.toString() + " to front");
        destination.moveToFront();
        JTableFixture destTable = destination.table();
        destTable.click();
        try {
            destTable.cell(TableCell.row(row).column(col)).select();
        } catch (Exception e) {
            // Skip if table doesn't contain any cells
        }

        clickMenuItem("Paste");
    }

}
