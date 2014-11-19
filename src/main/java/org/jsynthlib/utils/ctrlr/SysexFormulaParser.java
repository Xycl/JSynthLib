package org.jsynthlib.utils.ctrlr;

import org.jsynthlib.device.model.DeviceException;
import org.jsynthlib.xmldevice.MidiSenderReference;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;

public interface SysexFormulaParser {

    String parseSysexFormula(MidiSenderReference ref, int min, int max);

    XmlDeviceDefinition getDeviceDefinition();

    void setDeviceDefinition(XmlDeviceDefinition deviceDefinition)
            throws DeviceException;

}
