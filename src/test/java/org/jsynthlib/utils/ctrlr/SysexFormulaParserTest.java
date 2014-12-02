package org.jsynthlib.utils.ctrlr;

import static org.easymock.EasyMock.createMock;
//import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import org.jsynthlib.utils.ctrlr.driverContext.ConverterDeviceFactory;
import org.jsynthlib.utils.ctrlr.driverContext.SysexFormulaParser;
import org.jsynthlib.xmldevice.HandlerReferenceBase.PropertyValue;
import org.jsynthlib.xmldevice.MidiSenderReference;
import org.junit.Before;
import org.junit.Test;

public class SysexFormulaParserTest {

    private SysexFormulaParser tested;
    // private DriverContext driverContextMock;
    private ConverterDeviceFactory handlerReferenceFactoryMock;

    @Before
    public void setUp() throws Exception {
        handlerReferenceFactoryMock =
                createMock(ConverterDeviceFactory.class);
        // driverContextMock = createMock(DriverContext.class);
    }

    void replayAll() {
        // replay(driverContextMock);
        replay(handlerReferenceFactoryMock);
    }

    void verifyAll() {
        // verify(driverContextMock);
        verify(handlerReferenceFactoryMock);
    }

    @Test
    public void testParseSysexFormula() throws Exception {

        replayAll();
        // expect(driverContextMock.getDeviceDefinition()).andReturn(
        // XmlUtils.getRolandD50DeviceDef());
        // tested =
        // new SysexFormulaParserImpl(handlerReferenceFactoryMock,
        // driverContextMock);
        MidiSenderReference ref = MidiSenderReference.Factory.newInstance();
        ref.setName("D50Sender");
        PropertyValue propertyValue = ref.addNewPropertyValue();
        propertyValue.setKey("offset");
        propertyValue.setValue("409");
        String formula = tested.parseSysexFormula(ref, 0, 127);
        assertEquals("F0 41 00 14 12 00 03 19 xx z4 F7", formula);

        verifyAll();
    }
}
