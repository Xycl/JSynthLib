package org.jsynthlib.utils.ctrlr.driverContext.impl;

//import static org.easymock.EasyMock.createMock;
//import static org.easymock.EasyMock.expect;
//import static org.easymock.EasyMock.replay;
//import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.xmlbeans.XmlObject;
import org.jsynthlib.utils.ctrlr.driverContext.impl.XmlSingleDriverParserImpl;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamGroup;
import org.jsynthlib.xmldevice.XmlDriverDefinition;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Provider;

public class XmlDriverEditorParserTest {

    private XmlSingleDriverParserImpl tested;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGetXmlobjectByUuid() throws Exception {
        // expect(driverContextMock.getDriverDefinition()).andReturn(
        // XmlUtils.getD50SingleDriverDef());
        //
        // replay(driverContextMock);

        tested =
                new XmlSingleDriverParserImpl(
                        new Provider<XmlDriverDefinition>() {

                            @Override
                            public XmlDriverDefinition get() {
                                // TODO Auto-generated method stub
                                return null;
                            }
                        });
        XmlObject result =
                tested.getXmlobjectByUuid("b49d914c57e0497faa1421c4e1016887");
        assertNotNull(result);
        assertTrue(IntParamSpec.class.isAssignableFrom(result.getClass()));
        // verify(driverContextMock);
    }

    @Test
    public void testGetXmlobjectByTitle() {
        XmlObject result = tested.getXmlobjectByTitle("Patch");
        assertNotNull(result);
        assertTrue(PatchParamGroup.class.isAssignableFrom(result.getClass()));
    }
}
