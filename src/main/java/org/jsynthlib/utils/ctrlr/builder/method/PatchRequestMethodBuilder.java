package org.jsynthlib.utils.ctrlr.builder.method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jsynthlib.utils.SysexUtils;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class PatchRequestMethodBuilder extends MethodBuilder {

    private static final String CODE_BEGIN =
            "--&#13;&#10;"
                    + "-- Called when a modulator value changes&#13;&#10;"
                    + "-- @modulator   http://ctrlr.org/api/class_ctrlr_modulator.html&#13;&#10;"
                    + "-- @newValue    new numeric value of the modulator&#13;&#10;"
                    + "--&#13;&#10;"
                    + "__DRIVER_PREFIX___GetPatch = function(modulator, newValue)&#13;&#10;"
                    + "&#9;if panel_loaded == 1 then&#13;&#10;"
                    + "&#9;&#9;if newValue == 1 then&#13;&#10;";
    private static final String MSG_STUB =
            "&#9;&#9;&#9;m = CtrlrMidiMessage({__MSG__})&#13;&#10;"
                    + "&#9;&#9;&#9;panel:sendMidiMessageNow(m)&#13;&#10;";
    private static final String CODE_END =
            "&#9;&#9;&#9;dump_send = 1&#13;&#10;"
                    + "&#9;&#9;&#9;modulator:setValue(0,false)&#13;&#10;"
                    + "&#9;&#9;end&#13;&#10;" + "&#9;end&#13;&#10;end";
    private final List<String> midiMessages;
    private final String prefix;

    @Inject
    public PatchRequestMethodBuilder(@Named("prefix") String prefix) {
        super(prefix + "_GetPatch");
        this.prefix = prefix;
        midiMessages = new ArrayList<String>();
    }

    @Override
    public String getCode() {
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(CODE_BEGIN.replace("__DRIVER_PREFIX__", prefix));
        for (String msg : midiMessages) {
            byte[] sysex = SysexUtils.stringToSysex(msg);
            String hexStringArray = SysexUtils.byteToHexStringArray(sysex);
            codeBuilder.append(MSG_STUB.replace("__MSG__", hexStringArray));
        }
        codeBuilder.append(CODE_END);
        return codeBuilder.toString();
    }

    public boolean add(String arg0) {
        return midiMessages.add(arg0);
    }

    public boolean addAll(Collection<? extends String> arg0) {
        return midiMessages.addAll(arg0);
    }
}
