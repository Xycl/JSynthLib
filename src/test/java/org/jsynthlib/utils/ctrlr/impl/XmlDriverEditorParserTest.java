package org.jsynthlib.utils.ctrlr.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.xmlbeans.XmlObject;
import org.jsynthlib.utils.ctrlr.XmlUtils;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamGroup;
import org.junit.Before;
import org.junit.Test;

public class XmlDriverEditorParserTest {

    private XmlDriverEditorParser tested;

    @Before
    public void setUp() throws Exception {
        tested = new XmlDriverEditorParser(null, "", null);
        tested.setXmlDriverDef(XmlUtils.getD50SingleDriverDef());
    }

    @Test
    public void testGetXmlobjectByUuid() {
        XmlObject result =
                tested.getXmlobjectByUuid("b49d914c57e0497faa1421c4e1016887");
        assertNotNull(result);
        assertTrue(IntParamSpec.class.isAssignableFrom(result.getClass()));
    }

    @Test
    public void testGetXmlobjectByTitle() {
        XmlObject result = tested.getXmlobjectByTitle("Patch");
        assertNotNull(result);
        assertTrue(PatchParamGroup.class.isAssignableFrom(result.getClass()));
    }
}
