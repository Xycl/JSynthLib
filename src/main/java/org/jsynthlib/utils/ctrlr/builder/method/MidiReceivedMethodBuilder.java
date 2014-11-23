package org.jsynthlib.utils.ctrlr.builder.method;

import java.util.ArrayList;
import java.util.List;

import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;

import com.google.inject.Singleton;

@Singleton
public class MidiReceivedMethodBuilder extends MethodBuilder {

    private static final String CODE_START =
            "--&#13;"
                    + "-- Called when a panel receives a midi message "
                    + "(does not need to match any modulator mask)&#13;&#10;"
                    + "-- @midi   http://ctrlr.org/api/class_ctrlr_midi_message.html&#13;&#10;"
                    + "--&#13;midiReceived = function(midi)&#13;&#10;"
                    + "&#9;midiSize = midi:getData():getSize()&#13;&#10;";
    private static final String CODE_END = "end&#13;&#10;";
    private static final String DRIVER_STUB =
            "&#9;if midiSize == __PATCH_SIZE__ then&#13;&#10;"
                    + "-------------------- process __PREFIX__ data ----------------------------------------&#13;&#10;"
                    + "&#9;&#9;for i = 0,midiSize do -- gets the voice parameter values&#13;&#10;"
                    + "&#9;&#9;&#9;midiParam = midi:getData():getByte(i)&#13;&#10;"
                    + "&#9;&#9;&#9;name = &quot;__PREFIX__&quot;..j&#13;&#10;"
                    + "&#9;&#9;&#9;panel:getModulatorByName(name):setModulatorValue(midiParam,false,false,false)&#13;&#10;"
                    + "&#9;&#9;end&#13;&#10;"
                    + "&#9;&#9;v_char = getPatchName(midi)&#13;&#10;"
                    + "&#9;&#9;panel:getComponent(&quot;__PATCH_NAME_MOD_NAME__&quot;):setText(v_char)&#13;&#10;"
                    + "&#9;end&#13;&#10;"
                    + "---------------------------------------------------------------------------------&#13;&#10;";
    private final List<Driver> drivers;

    public MidiReceivedMethodBuilder() {
        super("midiReceived");
        drivers = new ArrayList<Driver>();
    }

    public void addNewDriver(XmlSingleDriverDefinition xmlDriverDef) {
        Driver driver = new Driver();
        drivers.add(driver);
        driver.patchNameLength = xmlDriverDef.getPatchNameSize();
        driver.patchNameOffset = xmlDriverDef.getPatchNameStart();
        driver.patchSize = xmlDriverDef.getPatchSize();
        driver.prefix = xmlDriverDef.getName().replaceAll("[^A-Za-z0-9]", "");
    }

    @Override
    public String getCode() {
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(CODE_START);

        for (Driver driver : drivers) {
            String s = DRIVER_STUB.replaceAll("__PREFIX__", driver.prefix);
            s =
                    s.replaceAll("__PATCH_SIZE__",
                            Integer.toString(driver.patchSize));
            s =
                    s.replaceAll("__PATCH_NAME_START__",
                            Integer.toString(driver.patchNameOffset));
            s =
                    s.replaceAll(
                            "__PATCH_NAME_END__",
                            Integer.toString(driver.patchNameOffset
                                    + driver.patchNameLength - 1));
            s =
                    s.replaceAll("__PATCH_NAME_MOD_NAME__", driver.prefix
                            + "PatchName");
            codeBuilder.append(s);
        }
        codeBuilder.append(CODE_END);
        return codeBuilder.toString();
    }

    public static class Driver {
        private String prefix;
        private int patchSize;
        private int patchNameOffset;
        private int patchNameLength;
    }

}
