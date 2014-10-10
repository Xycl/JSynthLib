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
package org.jsynthlib.patch.model.impl;

import javax.swing.JComponent;
import javax.swing.JTable;

import org.jsynthlib.patch.model.IPatch;

public class PatchListTransferHandler extends PatchTransferHandler {

    protected boolean storePatch(IPatch p, JComponent c) {
        PatchTableModel model = (PatchTableModel) ((JTable) c).getModel();
        model.addPatch(p);
        // TODO This method shouldn't have to worry about calling
        // fireTableDataChanged(). Find a better way.
        model.fireTableDataChanged();
        return true;
    }

    // only for debugging
    // protected void exportDone(JComponent source, Transferable data, int
    // action) {
    // log.info("PatchListTransferHandler.exportDone " + data);
    // }
}