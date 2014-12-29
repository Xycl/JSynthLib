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

import java.util.Observable;

/**
 * @author Pascal Collberg
 *
 */
public class DriverTypeModel extends Observable {

    private int singlePatchSize;
    private int singleCsStart;
    private int singleCsEnd;
    private int singleCsOfs;
    private String calculateChecksumMethodName;

    public int getSinglePatchSize() {
        return singlePatchSize;
    }

    public void setSinglePatchSize(int singlePatchSize) {
        this.singlePatchSize = singlePatchSize;
        setChanged();
        notifyObservers(singlePatchSize);
    }

    public void driverParseComplete() {
        setChanged();
        notifyObservers();
    }

    public int getSingleCsStart() {
        return singleCsStart;
    }

    public int getSingleCsEnd() {
        return singleCsEnd;
    }

    public int getSingleCsOfs() {
        return singleCsOfs;
    }

    public void setSingleCsStart(int singleCsStart) {
        this.singleCsStart = singleCsStart;
        setChanged();
        notifyObservers(singleCsStart);
    }

    public void setSingleCsEnd(int singleCsEnd) {
        this.singleCsEnd = singleCsEnd;
        setChanged();
        notifyObservers(singleCsEnd);
    }

    public void setSingleCsOfs(int singleCsOfs) {
        this.singleCsOfs = singleCsOfs;
        setChanged();
        notifyObservers(singleCsOfs);
    }

    public String getCalculateChecksumMethodName() {
        return calculateChecksumMethodName;
    }

    public void setCalculateChecksumMethodName(
            String calculateChecksumMethodName) {
        this.calculateChecksumMethodName = calculateChecksumMethodName;
        setChanged();
        notifyObservers(calculateChecksumMethodName);
    }

}
