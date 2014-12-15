package org.jsynthlib.utils.ctrlr.controller.lua;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.jsynthlib.utils.SysexUtils;
import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.utils.ctrlr.domain.PreConditionsNotMetException;
import org.jsynthlib.utils.ctrlr.domain.WritePatchMessage;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public abstract class WriteMethodControllerBase extends
EditorLuaMethodControllerBase implements Observer {

    public interface Factory {
        WriteMethodControllerBase newWriteMethodController(
                @Assisted("variableBanks") boolean variableBanks,
                @Assisted("variablePatches") boolean variablePatches,
                List<WritePatchMessage> writeMsgList, List<String> popups);
    }

    private static final String PATCH_NBR = "patchNbr";
    private static final String BANK_NBR = "bankNbr";
    private final DriverModel model;
    private final boolean variableBanks;
    private final boolean variablePatches;
    private final List<WritePatchMessage> writeMsgList;
    private String assembleValuesMethodName;
    private final List<String> popups;

    @Inject
    public WriteMethodControllerBase(
            @Assisted("variableBanks") boolean variableBanks,
            @Assisted("variablePatches") boolean variablePatches,
            @Assisted List<WritePatchMessage> writeMsgList,
            @Assisted List<String> popups, @Named("prefix") String prefix,
            DriverModel model) {
        super(prefix + "_WritePatch");
        this.model = model;
        this.variableBanks = variableBanks;
        this.variablePatches = variablePatches;
        this.writeMsgList = writeMsgList;
        this.popups = popups;
        model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        assembleValuesMethodName = model.getAssembleValuesMethodName();
        initialize();
    }

    @Override
    protected void checkPreconditions() throws PreConditionsNotMetException {
        if (assembleValuesMethodName == null) {
            throw new PreConditionsNotMetException();
        }
        super.checkPreconditions();
    }

    @Override
    protected void initialize() {
        super.initialize();
        model.deleteObserver(this);
        model.setWriteMenuName(getMethodName());
    }

    @Override
    protected void writeLuaMethodCode() {
        int indent = 0;
        StringBuilder code = new StringBuilder();
        String[] args = new String[0];
        if (variableBanks) {
            if (variablePatches) {
                args = new String[2];
                args[0] = BANK_NBR;
                args[1] = PATCH_NBR;
            } else {
                args = new String[1];
                args[0] = BANK_NBR;
            }
        } else if (variablePatches) {
            args = new String[1];
            args[0] = PATCH_NBR;
        }
        code.append(indent(indent))
        .append("-- This method instructs the user or synth to ")
        .append(newLine());
        code.append(indent(indent)).append("-- store the current patch")
        .append(newLine());
        code.append(indent(indent++)).append(getMethodDecl(args));
        for (WritePatchMessage msg : writeMsgList) {
            byte[] bytes = SysexUtils.stringToSysex(msg.getMessage());
            if (msg.isPatchDataMsg()) {
                code.append(indent(indent))
                .append("PatchDataCurrent = MemoryBlock()")
                .append(newLine());
                code.append(indent(indent))
                .append(getMethodCall(assembleValuesMethodName,
                        "PatchDataCurrent")).append(newLine());
                code.append(indent(indent))
                .append("m = CtrlrMidiMessage(PatchDataCurrent)")
                .append(newLine());

            } else {
                String string = SysexUtils.byteToHexStringArray(bytes);
                code.append(indent(indent)).append("m = CtrlrMidiMessage({")
                .append(string).append("})").append(newLine());
            }

            if (variableBanks && msg.containsBankNbr()) {
                code.append(indent(indent)).append("PatchDataCurrent:setByte(")
                .append(msg.getBankNbrOffset()).append(", ")
                .append(BANK_NBR).append(")").append(newLine());
            }

            if (variablePatches && msg.containsPatchNbr()) {
                code.append(indent(indent)).append("PatchDataCurrent:setByte(")
                .append(msg.getPatchNbrOffset()).append(", ")
                .append(PATCH_NBR).append(")").append(newLine());
            }
            code.append(indent(indent)).append("panel:sendMidiMessageNow(m)")
            .append(newLine());
        }

        for (String popString : popups) {
            code.append(indent(indent)).append(getInfoMessageCall(popString))
            .append(newLine());
        }

        code.append(indent(--indent)).append("end").append(newLine());

        setLuaMethodCode(code.toString());
    }
}
