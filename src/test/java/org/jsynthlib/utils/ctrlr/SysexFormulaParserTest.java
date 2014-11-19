package org.jsynthlib.utils.ctrlr;

import static org.junit.Assert.assertEquals;

import org.jsynthlib.device.model.DeviceManager;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.utils.ctrlr.impl.SysexFormulaParserImpl;
import org.jsynthlib.xmldevice.HandlerReferenceBase.PropertyValue;
import org.jsynthlib.xmldevice.MidiSenderReference;
import org.junit.Before;
import org.junit.Test;

public class SysexFormulaParserTest {

    private SysexFormulaParser tested;

    @Before
    public void setUp() throws Exception {
        DeviceManager deviceManager =
                JSynthLibInjector.getInstance(DeviceManager.class);
        tested = new SysexFormulaParserImpl(deviceManager);
        tested.setDeviceDefinition(XmlUtils.getD50DeviceDef());
    }

    @Test
    public void test() {
        MidiSenderReference ref = MidiSenderReference.Factory.newInstance();
        ref.setName("D50Sender");
        PropertyValue propertyValue = ref.addNewPropertyValue();
        propertyValue.setKey("offset");
        propertyValue.setValue("409");
        String formula = tested.parseSysexFormula(ref, 0, 127);
        assertEquals("F0 41 00 14 12 00 03 19 xx z4 F7", formula);
    }

}
