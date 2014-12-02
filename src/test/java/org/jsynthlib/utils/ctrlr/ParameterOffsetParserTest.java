package org.jsynthlib.utils.ctrlr;

//import static org.easymock.EasyMock.createMock;
//import static org.easymock.EasyMock.replay;
//import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jsynthlib.utils.ctrlr.driverContext.ConverterDeviceFactory;
import org.jsynthlib.utils.ctrlr.driverContext.ParameterOffsetParser;
import org.jsynthlib.xmldevice.HandlerReferenceBase.PropertyValue;
import org.jsynthlib.xmldevice.ParamModelReference;
import org.junit.Before;
import org.junit.Test;

public class ParameterOffsetParserTest {

    private ParameterOffsetParser tested;
    // private DriverContext driverContextMock;
    private ConverterDeviceFactory handlerReferenceFactoryMock;

    @Before
    public void setUp() throws Exception {
        // driverContextMock = createMock(DriverContext.class);
        // handlerReferenceFactoryMock =
        // createMock(CtrlrConverterDeviceFactory.class);
    }

    void replayAll() {
        // replay(driverContextMock);
        // replay(handlerReferenceFactoryMock);
    }

    void verifyAll() {
        // verify(driverContextMock);
        // verify(handlerReferenceFactoryMock);
    }

    @Test
    public void testParseParameterOffsetRolandD50() throws Exception {
        replayAll();
        // tested =
        // new ParameterOffsetParserImpl(handlerReferenceFactoryMock,
        // driverContextMock);

        ParamModelReference ref = ParamModelReference.Factory.newInstance();
        ref.setName("defaultParamModel");
        PropertyValue propertyValue = ref.addNewPropertyValue();
        propertyValue.setKey("offset");
        propertyValue.setValue("409");
        int[] offsets = tested.parseParameterOffset(ref);
        assertNotNull(offsets);
        assertEquals(1, offsets.length);
        assertEquals(409, offsets[0]);

        verifyAll();
    }

    @Test
    public void testParseParameterOffsetEmuProteus2() throws Exception {
        replayAll();
        // tested =
        // new ParameterOffsetParserImpl(handlerReferenceFactoryMock,
        // driverContextMock);
        ParamModelReference ref = ParamModelReference.Factory.newInstance();
        ref.setName("EmuParamModel");
        PropertyValue propertyValue = ref.addNewPropertyValue();
        propertyValue.setKey("offset");
        propertyValue.setValue("409");
        int[] offsets = tested.parseParameterOffset(ref);
        assertNotNull(offsets);
        assertEquals(2, offsets.length);
        assertEquals(409, offsets[0]);
        assertEquals(410, offsets[1]);

        verifyAll();
    }

}
