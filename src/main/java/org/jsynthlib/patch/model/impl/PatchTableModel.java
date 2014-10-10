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

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.jsynthlib.patch.model.IPatch;

/**
 * @author Pascal Collberg
 *
 */
/**
 * This is the general interface to unify the handling of the LibraryTable and
 * SceneTable.
 * @author Gerrit
 */
public abstract class PatchTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    protected final transient Logger log = Logger.getLogger(getClass());

    private List<IPatch> list = new ArrayList<IPatch>();

    @Override
    public int getRowCount() {
        return list.size();
    }

    /**
     * Add a patch to the end of the internal list.
     * @param p
     *            The patch to add
     * @return the position the patch occupies
     */
    public int addPatch(IPatch p) {
        log.info("LibraryFrame.addPatch: Patch=" + p);
        list.add(p);
        return list.size() - 1;
    }

    /**
     * Add a patch to the end of the internal list. and sets bank and patch
     * numbers
     * @param p
     *            The patch to add
     * @return the position the patch occupies
     */
    public int addPatch(IPatch p, int bankNum, int patchNum) { // wirski@op.pl
        log.info("LibraryFrame.addPatch: Patch=" + p);
        list.add(p);
        return list.size() - 1;
    }

    /**
     * Set (and replace) the patch at the specified row of the list.
     * @param p
     *            The patch to set
     * @param row
     *            The row of the table.
     * @param bankNum
     *            patch bank number
     * @param patchNum
     *            patch number
     */
    public void setPatchAt(IPatch p, int row, int bankNum, int patchNum) { // wirski@op.pl
        log.info("LibraryFrame.setPatchAt: row=" + row + ", Patch=" + p);
        list.set(row, p);
    }

    /**
     * Set (and replace) the patch at the specified row of the list.
     * @param p
     *            The patch to set
     * @param row
     *            The row of the table.
     */
    public void setPatchAt(IPatch p, int row) {
        log.info("LibraryFrame.setPatchAt: row=" + row + ", Patch=" + p);
        list.set(row, p);
    }

    /**
     * Get the patch at the specified row.
     * @param row
     *            The row specified
     * @return The patch
     */
    public IPatch getPatchAt(int row) {
        return list.get(row);
    }

    /**
     * Get the comment at the specified row.
     * @param row
     *            The row specified
     * @return The comment.
     */
    public String getCommentAt(int row) {
        return list.get(row).getComment();
    }

    public void removeAt(int row) {
        this.list.remove(row);
    }

    public List<IPatch> getList() {
        return this.list;
    }

    public void setList(List<IPatch> newList) {
        this.list = newList;
    }
}
