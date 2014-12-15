package org.jsynthlib.utils.ctrlr.controller.lua;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsynthlib.utils.ctrlr.domain.DriverModel;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public class ReceivePatchMethodController extends ReceiveMethodControllerBase
implements Observer {

    public interface Factory {
        ReceivePatchMethodController newReceivePatchMethodController(
                @Assisted("midiMessages") List<String> midiMessages,
                @Assisted("popupList") List<String> popupList);
    }

    private final DriverModel model;

    @Inject
    public ReceivePatchMethodController(@Named("prefix") String prefix,
            DriverModel model) {
        super(prefix + "_ReceivePatch");
        model.addObserver(this);
        this.model = model;
    }

    @Override
    protected void initialize() {
        super.initialize();
        model.deleteObserver(this);
        if (model.getReceiveMenuName() == null) {
            model.setReceiveMenuName(getMethodName());
        }
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

    @Override
    public void update(Observable o, Object arg) {
        setBankDataVar(model.getBankDataVarName());
        init();
    }

}
