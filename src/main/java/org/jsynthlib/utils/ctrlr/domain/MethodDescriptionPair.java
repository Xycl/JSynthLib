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

/**
 * @author Pascal Collberg
 *
 */
public class MethodDescriptionPair {

    private String methodName;
    private String description;

    public MethodDescriptionPair() {

    }

    public MethodDescriptionPair(String methodName, String description) {
        super();
        this.methodName = methodName;
        this.description = description;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String string1) {
        this.methodName = string1;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String string2) {
        this.description = string2;
    }
}
