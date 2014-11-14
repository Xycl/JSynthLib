package org.jsynthlib.utils.ctrlr;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.apache.xmlbeans.XmlObject;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamGroup;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;
import org.junit.Before;
import org.junit.Test;

public class XmlDriverEditorParserTest {

    private XmlDriverEditorParser tested;

    @Before
    public void setUp() throws Exception {
        String resName =
                "org/jsynthlib/synthdrivers/RolandD50/D50SingleDriver.xml";
        tested = new XmlDriverEditorParser("", null);
        InputStream stream =
                getClass().getClassLoader().getResourceAsStream(resName);
        XmlSingleDriverDefinitionDocument doc =
                XmlSingleDriverDefinitionDocument.Factory.parse(stream);
        XmlSingleDriverDefinition definition =
                doc.getXmlSingleDriverDefinition();
        tested.setXmlDriverDef(definition);
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
