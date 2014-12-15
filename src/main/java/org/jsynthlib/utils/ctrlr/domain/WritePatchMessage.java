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
package org.jsynthlib.utils.ctrlr.domain;

public class WritePatchMessage {
    private final String message;
    private final boolean patchDataMsg;
    private int bankNbrOffset = -1;
    private int patchNbrOffset = -1;

    public WritePatchMessage(String message, boolean patchDataMsg) {
        super();
        this.message = message;
        this.patchDataMsg = patchDataMsg;
    }

    public String getMessage() {
        return message;
    }

    public boolean isPatchDataMsg() {
        return patchDataMsg;
    }

    public boolean containsBankNbr() {
        return bankNbrOffset != -1;
    }

    public boolean containsPatchNbr() {
        return patchNbrOffset != -1;
    }

    public int getBankNbrOffset() {
        return bankNbrOffset;
    }

    public void setBankNbrOffset(int bankNbrOffset) {
        this.bankNbrOffset = bankNbrOffset;
    }

    public int getPatchNbrOffset() {
        return patchNbrOffset;
    }

    public void setPatchNbrOffset(int patchNbrOffset) {
        this.patchNbrOffset = patchNbrOffset;
    }
}