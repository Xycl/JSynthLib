package org.jsynthlib.utils.ctrlr.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.jsynthlib.utils.ctrlr.XmlUtils;
import org.jsynthlib.utils.ctrlr.service.ResourceContainer;
import org.jsynthlib.utils.ctrlr.service.impl.CtrlrImageConverterImpl;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamGroup;
import org.jsynthlib.xmldevice.PatchParams;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;
import org.junit.Test;

public class CtrlrImageConverterImplTest {

    @Test
    public void test() throws Exception {
        XmlSingleDriverDefinition driverDefinition =
                XmlUtils.getD50SingleDriverDef();
        IntParamSpec paramSpec = null;
        PatchParams patchParams = driverDefinition.getPatchParams();
        for (PatchParamGroup patchParamGroup : patchParams
                .getPatchParamGroupArray()) {
            if (patchParamGroup.getName().equals("Common Upper")) {
                for (IntParamSpec intParamSpec : patchParamGroup
                        .getIntParamSpecArray()) {
                    if (intParamSpec.getName().equals("Structure")) {
                        paramSpec = intParamSpec;
                        break;
                    }
                }
                break;
            }
        }

        assertNotNull(paramSpec);
        CtrlrImageConverterImpl tested = new CtrlrImageConverterImpl();
        ResourceContainer result =
                tested.convertJslImageResources(paramSpec, new File(""));
        assertNotNull(result);
        File file = result.getFile();
        assertNotNull(file);
        assertTrue(file.exists());
        assertTrue(file.isFile());
        assertTrue(file.delete());
    }

}
