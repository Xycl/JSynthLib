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

import org.jsynthlib.patch.model.IPatch;

/**
 * Refactored from PerformanceListModel
 * @author Gerrit Gehnen
 */
public class PatchListModel extends PatchTableModel {

    private static final long serialVersionUID = 1L;

    @Override
    public int getColumnCount() {
        return LibraryColumns.values().length;
    }

    @Override
    public String getColumnName(int col) {
        return LibraryColumns.values()[col].getName();
    }

    @Override
    public Object getValueAt(int row, int col) {
        IPatch myPatch = getList().get(row);
        LibraryColumns column = LibraryColumns.values()[col];
        try {
            switch (column) {
            case SYNTH:
                return myPatch.getDevice().getSynthName();
            case TYPE:
                return myPatch.getType();
            case PATCH_NAME:
                return myPatch.getName();
            case FIELD_1:
                return myPatch.getDate();
            case FIELD_2:
                return myPatch.getAuthor();
            case COMMENT:
                return myPatch.getComment();
            default:
                log.info("LibraryFrame.getValueAt: internal error.");
                return null;
            }
        } catch (NullPointerException e) {
            log.info("LibraryFrame.getValueAt: row=" + row + ", col=" + col
                    + ", Patch=" + myPatch + " row count =" + getRowCount(), e);
            return null;
        }
    }

    /*
     * JTable uses this method to determine the default renderer/ editor for
     * each cell. If we didn't implement this method, then the last column would
     * contain text ("true"/"false"), rather than a check box.
     */
    @Override
    public Class<?> getColumnClass(int c) {
        return String.class;
    }

    /*
     * Don't need to implement this method unless your table's editable.
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        return (col > LibraryColumns.TYPE.ordinal());
    }

    /*
     * Don't need to implement this method unless your table's data can change.
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        LibraryColumns column = LibraryColumns.values()[col];
        IPatch myPatch = getList().get(row);
        switch (column) {
        case PATCH_NAME:
            myPatch.setName((String) value);
            break;
        case FIELD_1:
            myPatch.setDate((String) value);
            break;
        case FIELD_2:
            myPatch.setAuthor((String) value);
            break;
        case COMMENT:
            myPatch.setComment((String) value);
            break;
        default:
            log.info("LibraryFrame.setValueAt: internal error.");
        }
        fireTableCellUpdated(row, col);
    }
}
