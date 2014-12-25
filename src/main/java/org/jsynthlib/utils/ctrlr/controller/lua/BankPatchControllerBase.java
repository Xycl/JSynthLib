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
package org.jsynthlib.utils.ctrlr.controller.lua;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Pascal Collberg
 */
public abstract class BankPatchControllerBase extends
EditorLuaMethodControllerBase {

    protected static final String BANK_OFFSET_VAR = "bankOffset";
    protected static final String PATCH_DATA_OFFSET_VAR = "patchDataOffset";
    protected static final String PATCH_DATA_LENGTH_VAR = "patchDataLength";
    protected static final String BANK_OFFSETS_VAR = "bankOffsets";
    protected static final String PATCH_NUM_VAR = "patchNum";
    protected static final String PATCH_DATA_VAR = "patchData";

    public BankPatchControllerBase(String methodName) {
        super(methodName);
    }

    protected String getMethodBegin(AtomicInteger indent) {
        // BankToPatchRelationBean bean = bankOffsets.get(0);
        StringBuilder code = new StringBuilder();
        code.append(indent(indent.getAndIncrement())).append(
                getMethodDecl(PATCH_DATA_VAR, PATCH_NUM_VAR));
        code.append(getPanelInitCheck(indent)).append(newLine());

        // code.append(indent(indent)).append("local ").append(BANK_OFFSETS_VAR)
        // .append(" = { ").append(getBankOffsetsTable()).append(" }")
        // .append(newLine());
        code.append(indent(indent)).append("local ")
        .append(PATCH_DATA_LENGTH_VAR).append(" = 0").append(newLine());
        // .append(bean.getDataSize()).append(newLine());
        code.append(indent(indent)).append("local ")
        .append(PATCH_DATA_OFFSET_VAR).append(" = 0").append(newLine());
        // .append(bean.getSingleDataOffset()).append(newLine());
        code.append(indent(indent)).append("local ").append(BANK_OFFSET_VAR)
        .append(" = ").append(BANK_OFFSETS_VAR).append("[")
        .append(PATCH_NUM_VAR).append("]").append(newLine());
        return code.toString();
    }

}
