package org.jsynthlib.utils.ctrlr.lua.decorator;

import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.indent;
import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.newLine;

import java.util.ArrayList;
import java.util.List;

import org.jsynthlib.utils.SysexUtils;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public class DefaultSendMethodDecorator extends DriverLuaHandlerDecoratorBase {

    public interface Factory {
        DefaultSendMethodDecorator newDefaultSendMethodDecorator(
                DriverLuaHandler decoratedHandler);
    }

    private final List<SendPatchMessage> msgList;

    @Inject
    public DefaultSendMethodDecorator(@Named("prefix") String driverPrefix,
            @Assisted DriverLuaHandler decoratedHandler) {
        super(driverPrefix, decoratedHandler);
        msgList = new ArrayList<SendPatchMessage>();
    }

    @Override
    protected String getSendMethodBody() {
        int indent = 1;
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(super.getAssembleValuesMethodBody());
        for (SendPatchMessage msg : msgList) {
            byte[] bytes = SysexUtils.stringToSysex(msg.getMessage());
            if (msg.isStaticMsg()) {
                String string = SysexUtils.byteToHexStringArray(bytes);
                codeBuilder.append(indent(indent))
                        .append("m = CtrlrMidiMessage({").append(string)
                        .append("})").append(newLine());
                codeBuilder.append(indent(indent))
                .append("panel:sendMidiMessageNow(m)")
                .append(newLine());
            } else {

            }
        }
        return codeBuilder.toString();
    }

    public void addMessage(SendPatchMessage msg) {
        msgList.add(msg);
    }

    public static class SendPatchMessage {
        private final String message;
        private final boolean staticMsg;

        public SendPatchMessage(String message, boolean staticMsg) {
            super();
            this.message = message;
            this.staticMsg = staticMsg;
        }

        public String getMessage() {
            return message;
        }

        public boolean isStaticMsg() {
            return staticMsg;
        }
    }

}
