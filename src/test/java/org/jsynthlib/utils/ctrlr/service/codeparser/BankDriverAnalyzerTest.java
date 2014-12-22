/*
 * Copyright 2014 Pascal Collberg
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package org.jsynthlib.utils.ctrlr.service.codeparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.jsynthlib.synthdrivers.YamahaDX7.YamahaDX7VoiceBankDriver;
import org.jsynthlib.utils.ctrlr.XmlUtils;
import org.jsynthlib.utils.ctrlr.controller.lua.JavaParsedMethodController;
import org.jsynthlib.utils.ctrlr.domain.CtrlrPanelModel;
import org.jsynthlib.xmldevice.XmlBankDriverDefinitionDocument.XmlBankDriverDefinition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Pascal Collberg
 */
public class BankDriverAnalyzerTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCompleteParse() throws Exception {
        BankDriverAnalyzer tested = new BankDriverAnalyzer();
        CtrlrPanelModel panelModel = new CtrlrPanelModel();
        TestLuaFactoryFacade luaFactoryFacade = new TestLuaFactoryFacade();
        XmlBankDriverDefinition bankDriverDef =
                XmlUtils.getBankDriverDef(YamahaDX7VoiceBankDriver.class);
        VisitorFactoryFacade visitorFactoryFacade =
                new TestVisitorFactoryFacade(luaFactoryFacade, bankDriverDef,
                        panelModel);

        BankDriverVisitor bankDriverVisitor =
                new BankDriverVisitor(visitorFactoryFacade);
        tested.setVisitor(bankDriverVisitor);
        tested.setPanelModel(panelModel);

        tested.parseBankDriver(YamahaDX7VoiceBankDriver.class);
        List<JavaParsedMethodController> list = luaFactoryFacade.getList();
        for (JavaParsedMethodController controllerBase : list) {
            System.out.println(controllerBase.toString());
        }

        Iterator<Entry<String, String>> iterator =
                panelModel.getGlobalVariableEntries().iterator();
        System.out.println("--------Global variables-------");
        while (iterator.hasNext()) {
            Entry<String, String> entry = iterator.next();
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }

    @Test
    public void testGetFilesAsList() {
        BankDriverAnalyzer tested = new BankDriverAnalyzer();
        List<File> result =
                tested.getFilesAsList(YamahaDX7VoiceBankDriver.class);
        assertEquals(5, result.size());
        assertTrue(result
                .get(0)
                .getAbsolutePath()
                .endsWith(
                        "src\\main\\java\\org\\jsynthlib\\synthdrivers\\YamahaDX7\\YamahaDX7VoiceBankDriver.java"));
        assertTrue(result
                .get(1)
                .getAbsolutePath()
                .endsWith(
                        "src\\main\\java\\org\\jsynthlib\\synthdrivers\\YamahaDX7\\common\\DX7FamilyXmlVoiceBankDriver.java"));
        assertTrue(result
                .get(4)
                .getAbsolutePath()
                .endsWith(
                        "src\\main\\java\\org\\jsynthlib\\device\\model\\AbstractDriver.java"));
    }

}
