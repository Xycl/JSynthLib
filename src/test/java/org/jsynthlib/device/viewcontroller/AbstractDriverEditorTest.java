package org.jsynthlib.device.viewcontroller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.apache.xmlbeans.XmlException;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamSpec;
import org.jsynthlib.xmldevice.XmlPatchDriverSpecDocument;
import org.jsynthlib.xmldevice.XmlPatchDriverSpecDocument.XmlPatchDriverSpec;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AbstractDriverEditorTest {

    private AbstractDriverEditor tested;

    @Before
    public void setUp() throws Exception {
        tested = new AbstractDriverEditor(null, null) {
        };
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws XmlException, IOException {
        InputStream inputStream =
                getClass()
                        .getClassLoader()
                        .getResourceAsStream(
                                "org/jsynthlib/synthdrivers/RolandD50/D50SingleDriver.xml");
        XmlPatchDriverSpecDocument document =
                XmlPatchDriverSpecDocument.Factory.parse(inputStream);
        XmlPatchDriverSpec xmlDriverSpec = document.getXmlPatchDriverSpec();
        PatchParamSpec patchParamSpec =
                tested.getPatchParamSpec(xmlDriverSpec,
                        "D80ED8B0-B90A-11E3-A5E2-0800200C9A66");
        assertNotNull(patchParamSpec);
        assertTrue(patchParamSpec instanceof IntParamSpec);
        assertEquals("D80ED8B0-B90A-11E3-A5E2-0800200C9A66",
                patchParamSpec.getUuid());
    }

}
