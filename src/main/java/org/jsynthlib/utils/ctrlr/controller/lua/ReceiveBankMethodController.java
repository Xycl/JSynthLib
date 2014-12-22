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

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/**
 * @author Pascal Collberg
 */
public class ReceiveBankMethodController extends ReceiveMethodControllerBase
{

    public interface Factory {
        ReceiveBankMethodController newReceiveBankMethodController(
                @Assisted("midiMessages") List<String> midiMessages,
                @Assisted("popupList") List<String> popupList);
    }

    @Inject
    public ReceiveBankMethodController(@Named("prefix") String prefix) {
        super(prefix + "_ReceiveBank");
    }

    @Override
    @Inject
    public void setReceiveMidiMessages(
            @Assisted("midiMessages") List<String> receiveMidiMessages) {
        super.setReceiveMidiMessages(receiveMidiMessages);
    }

    @Override
    @Inject
    public void setReceivePopupList(
            @Assisted("popupList") List<String> receivePopupList) {
        super.setReceivePopupList(receivePopupList);
    }

    @Override
    protected void writeLuaMethodCode() {
        AtomicInteger indent = new AtomicInteger(0);
        StringBuilder code = new StringBuilder();

        code.append(indent(indent))
        .append("-- This method instructs the synth or user ")
        .append(newLine());
        code.append(indent(indent)).append("-- to perform a single patch dump")
        .append(newLine());
        code.append(getReceiveMethodBase(indent)).append(newLine());

        setLuaMethodCode(code.toString());
    }
}
