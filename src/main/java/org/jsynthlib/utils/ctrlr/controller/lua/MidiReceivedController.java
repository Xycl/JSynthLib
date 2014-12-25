package org.jsynthlib.utils.ctrlr.controller.lua;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.jsynthlib.utils.ctrlr.domain.CtrlrPanelModel;
import org.jsynthlib.utils.ctrlr.domain.PreConditionsNotMetException;
import org.jsynthlib.utils.ctrlr.service.LuaMethodProvider;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class MidiReceivedController extends LuaMethodControllerBase implements
Observer {

    @Inject
    @Named("root")
    private LuaMethodProvider methodProvider;
    private List<MidiReceivedDriverBean> midiReceivedParts;
    private final CtrlrPanelModel model;

    @Inject
    public MidiReceivedController(
            @Named("midiReceivedMethodName") String midiReceivedMethodName,
            CtrlrPanelModel model) {
        super(midiReceivedMethodName);
        model.addObserver(this);
        this.model = model;
    }

    String getMidiReceivedPart(int indent, MidiReceivedDriverBean part) {
        StringBuilder code = new StringBuilder();
        code.append(indent(indent++)).append("if midiSize == ")
        .append(part.getPatchSize()).append(" then").append(newLine());
        code.append("-------------------- process ").append(
                part.getDriverPrefix());
        if (part.isBankDriver()) {
            code.append(" bank");
        } else {
            code.append(" patch");
        }
        code.append(" data ----------------------------------------").append(
                newLine());
        if (part.isBankDriver()) {
            code.append(indent(indent))
            .append(getMethodCall(part.getMethodName(),
                    "midi:getData()")).append(newLine());
        } else {
            code.append(indent(indent))
            .append(getMethodCall(part.getMethodName(),
                    "midi:getData()", "false")).append(newLine());
        }
        code.append(indent(--indent)).append("end").append(newLine());
        code.append("---------------------------------------------")
        .append("------------------------------------")
        .append(newLine());
        return code.toString();
    }

    @Override
    protected void checkPreconditions() throws PreConditionsNotMetException {
        if (midiReceivedParts == null || midiReceivedParts.isEmpty()) {
            throw new PreConditionsNotMetException();
        }
        super.checkPreconditions();
    }

    @Override
    protected void writeLuaMethodCode() {
        int indent = 0;
        StringBuilder code = new StringBuilder();
        code.append(indent(indent)).append("--").append(newLine());
        code.append(indent(indent)).append(
                "-- Called when a panel receives a midi message ");
        code.append(indent(indent))
        .append("(does not need to match any modulator mask)")
        .append(newLine());
        code.append(indent(indent))
        .append("-- @midi   http://ctrlr.org/api/class_ctrlr_midi_message.html")
        .append(newLine());
        code.append(indent(indent++)).append("midiReceived = function(midi)")
        .append(newLine());
        code.append(indent(indent))
        .append("midiSize = midi:getData():getSize()")
        .append(newLine());
        for (MidiReceivedDriverBean part : midiReceivedParts) {
            code.append(getMidiReceivedPart(indent, part));
        }
        code.append(indent(--indent)).append("end").append(newLine());
        setLuaMethodCode(code.toString());
    }

    @Override
    public void update(Observable o, Object arg) {
        midiReceivedParts = model.getMidiReceivedParts();
        init();
    }

    @Override
    protected LuaMethodProvider getLuaMethodProvider() {
        return methodProvider;
    }

}
