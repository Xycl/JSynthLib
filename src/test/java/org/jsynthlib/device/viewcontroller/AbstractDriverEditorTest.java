package org.jsynthlib.device.viewcontroller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.SwingUtilities;

import org.apache.xmlbeans.XmlException;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamSpec;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AbstractDriverEditorTest {

    private AbstractDriverEditor tested;

    @SuppressWarnings("serial")
    @Before
    public void setUp() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                tested = new AbstractDriverEditor(null, null, null, null) {
                };
            }
        });
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetControlById() throws XmlException, IOException {
        InputStream inputStream =
                getClass()
                        .getClassLoader()
                        .getResourceAsStream(
                                "org/jsynthlib/synthdrivers/RolandD50/D50SingleDriver.xml");
        XmlSingleDriverDefinitionDocument document =
                XmlSingleDriverDefinitionDocument.Factory.parse(inputStream);
        XmlSingleDriverDefinition xmlDriverSpec = document.getXmlSingleDriverDefinition();
        PatchParamSpec patchParamSpec =
                tested.getPatchParamSpec(xmlDriverSpec,
                        "b49d914c57e0497faa1421c4e1016887");
        assertNotNull(patchParamSpec);
        assertTrue(patchParamSpec instanceof IntParamSpec);
        assertEquals("b49d914c57e0497faa1421c4e1016887",
                patchParamSpec.getUuid());
    }

}
