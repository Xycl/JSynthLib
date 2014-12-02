package org.jsynthlib.utils.ctrlr.lua.decorator;

import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.indent;
import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.newLine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jsynthlib.utils.SysexUtils;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public class DefaultGetMethodDecorator extends DriverLuaHandlerDecoratorBase {

    public interface Factory {
        DefaultGetMethodDecorator newDefaultGetMethodDecorator(
                DriverLuaHandler decoratedHandler);
    }

    private final List<String> midiMessages;
    private final List<String> popupList;

    @Inject
    public DefaultGetMethodDecorator(@Named("prefix") String driverPrefix,
            @Assisted DriverLuaHandler decoratedHandler) {
        super(driverPrefix, decoratedHandler);
        midiMessages = new ArrayList<String>();
        popupList = new ArrayList<String>();
    }

    @Override
    protected String getGetMethodBody() {
        int indent = 1;
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(super.getGetMethodBody());
        codeBuilder.append(indent(indent++))
                .append("if panel_loaded == 1 then").append(newLine());
        // codeBuilder.append(indent(indent++)).append("if newValue == 1 then")
        // .append(newLine());

        for (String msg : midiMessages) {
            byte[] sysex = SysexUtils.stringToSysex(msg);
            String hexStringArray = SysexUtils.byteToHexStringArray(sysex);
            codeBuilder.append(indent(indent)).append("m = CtrlrMidiMessage({")
                    .append(hexStringArray).append("})").append(newLine());
            codeBuilder.append(indent(indent))
                    .append("panel:sendMidiMessageNow(m)").append(newLine());
        }

        for (String popString : popupList) {
            codeBuilder.append(getInfoMessageCall(popString, indent));
        }

        codeBuilder.append(indent(indent)).append("dump_send = 1")
                .append(newLine());
        codeBuilder.append(indent(indent))
                .append("modulator:setValue(0,false)").append(newLine());
        // codeBuilder.append(indent(--indent)).append("end").append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        return codeBuilder.toString();
    }

    public boolean addMidiMessages(Collection<? extends String> arg0) {
        return midiMessages.addAll(arg0);
    }

    public boolean addPopups(List<String> popups) {
        return popupList.addAll(popups);
    }

}
