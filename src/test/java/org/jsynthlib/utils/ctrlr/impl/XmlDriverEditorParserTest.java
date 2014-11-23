package org.jsynthlib.utils.ctrlr.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.xmlbeans.XmlObject;
import org.jsynthlib.utils.ctrlr.XmlUtils;
import org.jsynthlib.utils.ctrlr.driverContext.DriverContext;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamGroup;
import org.junit.Before;
import org.junit.Test;

public class XmlDriverEditorParserTest {

    private XmlSingleDriverParserImpl tested;
    private DriverContext driverContextMock;

    @Before
    public void setUp() throws Exception {
        driverContextMock = createMock(DriverContext.class);
    }

    @Test
    public void testGetXmlobjectByUuid() throws Exception {
        expect(driverContextMock.getDriverDefinition()).andReturn(
                XmlUtils.getD50SingleDriverDef());

        replay(driverContextMock);

        tested = new XmlSingleDriverParserImpl(driverContextMock);
        XmlObject result =
                tested.getXmlobjectByUuid("b49d914c57e0497faa1421c4e1016887");
        assertNotNull(result);
        assertTrue(IntParamSpec.class.isAssignableFrom(result.getClass()));
        verify(driverContextMock);
    }

    @Test
    public void testGetXmlobjectByTitle() {
        XmlObject result = tested.getXmlobjectByTitle("Patch");
        assertNotNull(result);
        assertTrue(PatchParamGroup.class.isAssignableFrom(result.getClass()));
    }
}
