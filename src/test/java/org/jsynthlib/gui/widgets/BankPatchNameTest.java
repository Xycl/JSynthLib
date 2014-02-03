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
package org.jsynthlib.gui.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.fest.swing.data.TableCell;
import org.fest.swing.data.TableCell.TableCellBuilder;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTableFixture;
import org.jsynthlib.driver.XmldeviceDocument;
import org.jsynthlib.driver.XmldeviceDocument.Xmldevice;
import org.jsynthlib.driver.Xmldriver;
import org.jsynthlib.driver.Xmleditor;
import org.jsynthlib.driver.Xmlpatches;
import org.jsynthlib.gui.widgets.PatchNameWidget;
import org.jsynthlib.gui.widgets.SysexWidget;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import core.GuiHandler;
import core.PatchEdit;
import core.PatchEditorTest;
import core.PopupListener;
import core.SysexWidgetFinder;
import core.TitleFinder;
import core.TitleFinder.FrameWrapper;

@RunWith(Parameterized.class)
public class BankPatchNameTest {

    public static final String TESTLEVEL = "org.jsynthlib.testlevel";
    public static final int TESTLEVEL_LOW = 1;
    public static final int TESTLEVEL_MEDIUM = 2;
    public static final int TESTLEVEL_HIGH = 3;

    private static final Logger LOG = Logger.getLogger(BankPatchNameTest.class);

    @BeforeClass
    public static void setUpOnce() {
        PatchEditorTest.setUpOnce();
    }

    @Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return PatchEditorTest.data();
    }

    private FrameFixture testFrame;
    private GuiHandler guiHandler;
    private String xmlFile;

    public BankPatchNameTest(String xmlFile) {
        this.xmlFile = xmlFile;
    }

    @Before
    public void setUp() throws Exception {
        testFrame = new FrameFixture(PatchEdit.getInstance());
        testFrame.show();
        testFrame.maximize();
        guiHandler = new GuiHandler(testFrame);
    }

    @After
    public void tearDown() throws Exception {
        testFrame.cleanUp();
    }

    @Test
    public void testEditPatchNameInBank() throws Exception {
        URL resource = getClass().getResource("/" + xmlFile);
        if (resource == null) {
            return;
        }
        File outputFile = new File(resource.toURI());
        guiHandler.uninstallDevice(null);
        PopupListener popupListener = new PopupListener();

        XmldeviceDocument deviceDocument =
                XmldeviceDocument.Factory.parse(outputFile);
        Xmldevice device = deviceDocument.getXmldevice();

        guiHandler.installDevice(device.getManufacturer(), device.getName());
        FrameWrapper library = guiHandler.openLibrary();
        try {
            Xmldriver[] xmldriverArray = device.getXmldrivers().getXmldriverArray();
            for (Xmldriver xmldriver : xmldriverArray) {
                if (xmldriver.getEditable()) {
                    Xmleditor xmleditor = xmldriver.getXmleditor();
                    Xmlpatches xmlpatches = xmleditor.getXmlpatches();
                    if (xmlpatches != null) {
                        // Bank editor
                        Class<?> driverClass = Class.forName(xmldriver.getName());
                        guiHandler.newPatch(library, device.getName(), driverClass,
                                popupListener);
                        FrameWrapper bankEditor = null;
                        try {
                            bankEditor =
                                    guiHandler.openPatchEditor(library.table(), -1,
                                            0, popupListener, false);
                            verifyBankEditorNames(bankEditor);
                        } finally {
                            if (bankEditor != null) {
                                guiHandler.closeFrame(bankEditor, false);
                            }
                        }
                    }
                }
            }
        } finally {
            guiHandler.closeLibrary(library);
        }
    }

    void verifyBankEditorNames(FrameWrapper bankEditor) {
        JTableFixture table = bankEditor.table();
        String[][] contents = table.contents();
        int[] testedRows = {
                0, contents.length / 2 };
        int[] testedCols = {
                0, contents[0].length / 2 };
        HashSet<TableCell> testedCells = new HashSet<TableCell>();
        for (int row : testedRows) {
            for (int col : testedCols) {
                String patchName = "PN " + row + " " + col;
                TableCellBuilder cellBuilder = TableCell.row(row);
                TableCell tableCell = cellBuilder.column(col);
                if (testedCells.contains(tableCell)) {
                    LOG.info("Skipping already tested patch on " + row
                            + " row " + col + " col.");
                    continue;
                }

                testedCells.add(tableCell);
                LOG.info("Selecting patch on " + row + " row " + col + " col.");
                table.cell(tableCell).enterValue(patchName);
                table.cell(tableCell).requireValue(
                        "[\\w\\-]+\\s+" + patchName + "\\s*");
                FrameWrapper patchEditor = null;
                try {
                    patchEditor =
                            guiHandler.openPatchEditor(table, row, col,
                                    new PopupListener(), false);
                    if (patchEditor != null) {
                        List<SysexWidget> sysexWidgets =
                                SysexWidgetFinder.findSysexWidgets(patchEditor);
                        ArrayList<String> patchNameWidgetValues =
                                new ArrayList<String>();
                        for (SysexWidget sysexWidget : sysexWidgets) {
                            if (sysexWidget instanceof PatchNameWidget) {
                                PatchNameWidget widget =
                                        (PatchNameWidget) sysexWidget;
                                patchNameWidgetValues
                                        .add(widget.name.getText());
                            }
                        }
                        if (patchNameWidgetValues.size() == 1) {
                            assertEquals(patchName, patchNameWidgetValues
                                    .get(0).trim());
                        } else if (!patchNameWidgetValues.isEmpty()) {
                            boolean foundName = false;
                            for (String string : patchNameWidgetValues) {
                                if (string.trim().equals(patchName)) {
                                    foundName = true;
                                    break;
                                }
                            }
                            assertTrue(foundName);
                        }
                    }
                } finally {
                    if (patchEditor != null) {
                        guiHandler.closeFrame(patchEditor, false);
                    }
                }
            }
        }
    }

}
