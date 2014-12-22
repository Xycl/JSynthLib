package org.jsynthlib.utils.ctrlr.controller.lua;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public class ReceivePatchMethodController extends ReceiveMethodControllerBase {

    public interface Factory {
        ReceivePatchMethodController newReceivePatchMethodController(
                @Assisted("midiMessages") List<String> midiMessages,
                @Assisted("popupList") List<String> popupList);
    }

    @Inject
    public ReceivePatchMethodController(@Named("prefix") String prefix) {
        super(prefix + "_ReceivePatch");
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
