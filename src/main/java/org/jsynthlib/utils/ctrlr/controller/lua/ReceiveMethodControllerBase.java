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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsynthlib.utils.SysexUtils;
import org.jsynthlib.utils.ctrlr.domain.DriverModel;

import com.google.inject.Inject;

/**
 * @author Pascal Collberg
 */
public abstract class ReceiveMethodControllerBase extends
EditorLuaMethodControllerBase {

    @Inject
    private DriverModel model;

    private List<String> receiveMidiMessages;
    private List<String> receivePopupList;

    public ReceiveMethodControllerBase(String methodName) {
        super(methodName);
    }

    protected String getReceiveMethodBase(AtomicInteger indent) {
        StringBuilder code = new StringBuilder();
        code.append(indent(indent.getAndIncrement())).append(
                getMethodDecl("modulator", "newValue"));

        code.append(getPanelInitCheck(indent)).append(newLine());
        code.append(
                getSaveCurrentWorkPrompt(model.getBankDataVarName(), indent))
                .append(
                newLine());

        for (String msg : receiveMidiMessages) {
            byte[] sysex = SysexUtils.stringToSysex(msg);
            String hexStringArray = SysexUtils.byteToHexStringArray(sysex);
            code.append(indent(indent)).append("m = CtrlrMidiMessage({")
            .append(hexStringArray).append("})").append(newLine());
            code.append(indent(indent)).append("panel:sendMidiMessageNow(m)")
            .append(newLine());
        }

        for (String popString : receivePopupList) {
            code.append(indent(indent)).append(getInfoMessageCall(popString))
            .append(newLine());
        }

        code.append(indent(indent)).append("dump_send = 1").append(newLine());

        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());

        return code.toString();
    }

    public void setReceiveMidiMessages(List<String> receiveMidiMessages) {
        this.receiveMidiMessages = receiveMidiMessages;
    }

    public void setReceivePopupList(List<String> receivePopupList) {
        this.receivePopupList = receivePopupList;
    }
}
