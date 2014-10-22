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
package org.jsynthlib.device.viewcontroller.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.fest.swing.fixture.FrameFixture;
import org.jsynthlib.core.PopupContainer;
import org.jsynthlib.device.model.EnvelopeNode;
import org.jsynthlib.device.model.IParamModel;
import org.jsynthlib.driver.XmlPopup;
import org.jsynthlib.driver.XmlPopups;
import org.jsynthlib.driver.XmldeviceDocument;
import org.jsynthlib.driver.XmldeviceDocument.Xmldevice;
import org.jsynthlib.driver.Xmldriver;
import org.jsynthlib.driver.Xmldrivers;
import org.jsynthlib.driver.Xmleditor;
import org.jsynthlib.driver.XmlenvelopeParam;
import org.jsynthlib.driver.Xmlmessage;
import org.jsynthlib.driver.Xmlmessages;
import org.jsynthlib.driver.Xmlparam;
import org.jsynthlib.driver.Xmlparams;
import org.jsynthlib.driver.Xmlpatch;
import org.jsynthlib.driver.Xmlpatches;
import org.jsynthlib.driver.Xmlstore;
import org.jsynthlib.driver.Xmlstores;
import org.jsynthlib.midi.SingletonMidiDeviceProvider.MidiRecordSession;
import org.jsynthlib.test.adapter.WidgetAdapter;

public class TestDocumentHandler extends AbstractDocumentHandler {

    private final XmldeviceDocument deviceDocument;
    private final Xmldevice device;

    private final Map<Xmldriver, Set<Xmlparam>> testedItems;
    private int envelopeIndex;
    private HashMap<Xmleditor, Integer> editorParamsMap;

    public TestDocumentHandler(File outputFile, FrameFixture testFrame)
            throws XmlException, IOException {
        super(outputFile, testFrame);
        log.info("Using file " + outputFile.getAbsolutePath());
        XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setLoadStripWhitespace();
        deviceDocument = XmldeviceDocument.Factory.parse(outputFile, xmlOptions);
        device = deviceDocument.getXmldevice();
        testedItems = new HashMap<Xmldriver, Set<Xmlparam>>();
    }

    @Override
    public void handleDocument(String manufacturer, String deviceName) {
        assertEquals("Check manufacturer " + deviceName,
                device.getManufacturer(), manufacturer);
        assertEquals("Check device " + deviceName, device.getName(), deviceName);
        editorParamsMap = new HashMap<Xmleditor, Integer>();
    }

    Xmlparams getParams(Xmleditor editor) {
        Integer numParams = editorParamsMap.get(editor);
        if (numParams == null) {
            numParams = new Integer(1);
            editorParamsMap.put(editor, numParams);
        } else {
            editorParamsMap.put(editor, new Integer(numParams + 1));
        }
        log.info("Num params " + numParams);
        return editor.getXmlparams();
    }

    @Override
    public Xmlparam handleParamInternal(Xmleditor editor,
            WidgetAdapter adapter, String uniqueName) {
        XmlCursor cursor = editor.newCursor();
        cursor.toParent();
        Xmldriver driver = (Xmldriver) cursor.getObject();
        cursor.dispose();
        assertTrue("Check driver " + driver.getName(),
                testedItems.containsKey(driver));

        Xmlparams params = getParams(editor);
        final Xmlparam param = getParamByLabel(params, uniqueName);
        log.info("Testing param " + uniqueName);
        assertNotNull("Testing param " + uniqueName, param);

        Set<Xmlparam> testedParams = testedItems.get(driver);
        assertFalse("Check param exists: " + uniqueName,
                testedParams.contains(param));
        testedParams.add(param);

        assertEquals("Check param max: " + uniqueName, param.getMax(),
                adapter.getMax());
        assertEquals("Check param min: " + uniqueName, param.getMin(),
                adapter.getMin());
        return param;

    }

    Xmlparam getParamByLabel(Xmlparams params, String label) {
        Xmlparam[] paramArray = params.getXmlparamArray();
        for (final Xmlparam param : paramArray) {
            if (param.getLabel().equals(label.trim())) {
                return param;
            }
        }
        return null;
    }

    void verifyScrollbarWidget(WidgetAdapter sysexWidget, Xmlparam param) {
        testMessages(param, sysexWidget);
    }

    void testMessages(Xmlparam param, WidgetAdapter sysexWidget) {
        if (sysexWidget.getValue() == sysexWidget.getMin()) {
            sysexWidget.getValueSetter().setValue(sysexWidget.getMax());
        }

        Xmlmessages messages = param.getXmlmessages();
        for (Xmlmessage message : messages.getXmlmessageArray()) {
            MidiRecordSession session = midiDeviceProvider.openSession();
            sysexWidget.getValueSetter().setValue(
                    message.getXmlinput().getValue());
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
            String sysex = midiDeviceProvider.closeSession(session);
            assertEquals("Check param " + param.getContainerName() + "/"
                    + param.getLabel() + " sysex for value "
                    + message.getXmlinput().getValue(), message.getSysex(),
                    sysex);
        }
    }

    @Override
    public void saveDocument() {
        Xmldrivers drivers = device.getXmldrivers();
        Xmldriver[] driverArray = drivers.getXmldriverArray();
        assertEquals("Check driver array " + device.getName(),
                driverArray.length, testedItems.size());
        for (Xmldriver driver : driverArray) {
            if (driver.getEditable()) {
                Set<Xmlparam> set = testedItems.get(driver);
                Xmleditor editor = driver.getXmleditor();
                Xmlparams params = editor.getXmlparams();
                if (params != null) {
                    Integer integer = editorParamsMap.get(editor);
                    assertNotNull(
                            "Check editor all params " + editor.getName(),
                            integer);
                    assertEquals(
                            "Check editor num all params " + editor.getName(),
                            editor.getNumparams(), integer.intValue());
                    Xmlparam[] paramArray = params.getXmlparamArray();
                    assertEquals("Check editor num params " + editor.getName(),
                            paramArray.length, set.size());
                }
            }
        }
    }

    @Override
    public Xmleditor handlePatchEditor(Xmldriver driver, boolean editable,
            String editorName, List<PopupContainer> popups) {
        String driverName = driver.getName();
        envelopeIndex = 0;
        log.info("Testing driver " + driverName);
        assertFalse("Check driver " + driverName,
                testedItems.containsKey(driver));
        testedItems.put(driver, new HashSet<Xmlparam>());
        assertEquals("Check " + driverName + " is editable",
                driver.getEditable(), editable);
        if (driver.getEditable()) {
            Xmleditor editor = driver.getXmleditor();
            assertEquals("Check " + driverName + " editor " + editorName,
                    editor.getName(), editorName.trim());
            XmlPopups editorPopups = editor.getXmlPopups();
            if (editorPopups == null) {
                assertTrue("Check " + driverName + " no popup",
                        popups.isEmpty());
            } else {
                XmlPopup[] editorPopupArray = editorPopups.getXmlPopupArray();
                log.info("Driver has " + editorPopupArray.length + " popups");
                assertEquals("Check " + driverName + " popups",
                        editorPopupArray.length, popups.size());
                if (!popups.isEmpty()) {
                    for (XmlPopup editorPopup : editorPopupArray) {
                        PopupContainer popup =
                                new PopupContainer(editorPopup.getTitle(),
                                        editorPopup.getContent());
                        assertTrue("Check " + driverName + " popup",
                                popups.contains(popup));
                    }
                }
            }
            return editor;
        } else {
            return null;
        }
    }

    public String getManufacturer() {
        return device.getManufacturer();
    }

    public String getDeviceName() {
        return device.getName();
    }

    @Override
    public Xmldriver handleDriver(String driverName) {
        Xmldrivers drivers = device.getXmldrivers();
        Xmldriver[] driverArray = drivers.getXmldriverArray();
        for (Xmldriver driver : driverArray) {
            if (driver.getName().equals(driverName)) {
                return driver;
            }
        }
        fail("Could not find driver " + driverName);
        return null;
    }

    @Override
    protected void handleWidgetNotVisible(String uniqueName, Xmleditor editor) {
        Xmlparams params = getParams(editor);
        final Xmlparam param = getParamByLabel(params, uniqueName);
        assertNull("Check param exists " + uniqueName, param);
    }

    @Override
    protected void handleEnvelopeWidget(AbstractEnvelopeWidgetAdapter widget,
            String uniqueName, Xmleditor editor) {
        Xmlparams params = getParams(editor);
        XmlenvelopeParam xmlenvelopeParam = null;
        try {
            xmlenvelopeParam = getEnvelopeParamByUniqueName(params, uniqueName);
        } catch (IllegalArgumentException e) {
            log.debug(e.getMessage());
            XmlenvelopeParam[] xmlenvelopeParamArray =
                    params.getXmlenvelopeParamArray();
            xmlenvelopeParam = xmlenvelopeParamArray[envelopeIndex];
        }

        Xmlparam[] xmlparamArray = xmlenvelopeParam.getXmlparamArray();
        EnvelopeNode[] nodes = widget.getNodes();
        int numFaders = 0;
        for (int i = 0; i < nodes.length; i++) {
            EnvelopeNode node = nodes[i];

            final IParamModel modelX = widget.getParamModel(node, true);
            if (modelX != null) {
                Xmlparam paramX = xmlparamArray[numFaders];
                final int faderX = widget.getSliderNum() + numFaders++;
                int minX = node.getMinX();
                int maxX = node.getMaxX();

                assertEquals("Check paramX label: " + paramX.getLabel(),
                        paramX.getLabel(), node.getNameX().trim());
                assertEquals("Check paramX max: " + paramX.getLabel(),
                        paramX.getMax(), maxX);
                assertEquals("Check paramX min: " + paramX.getLabel(),
                        paramX.getMin(), minX);

                widget.setFader(faderX);
                testMessages(paramX, widget);
            }

            final IParamModel modelY = widget.getParamModel(node, false);
            if (modelY != null) {
                Xmlparam paramY = xmlparamArray[numFaders];
                final int faderY = widget.getSliderNum() + numFaders++;
                int minY = node.getMinY();
                int maxY = node.getMaxY();

                assertEquals("Check paramY label: " + paramY.getLabel(),
                        paramY.getLabel(), node.getNameY().trim());
                assertEquals("Check paramY max: " + paramY.getLabel(),
                        paramY.getMax(), maxY);
                assertEquals("Check paramY min: " + paramY.getLabel(),
                        paramY.getMin(), minY);

                widget.setFader(faderY);
                testMessages(paramY, widget);
            }
        }
        assertEquals("Check param array length", xmlparamArray.length,
                numFaders);
        envelopeIndex++;
    }

    @Override
    protected void handleDisabledWidget(Xmlparam param) {
        assertEquals("Check param " + param.getLabel() + " type: "
                + Xmlparam.Type.DISABLED_WIDGET.toString(),
                Xmlparam.Type.INT_DISABLED_WIDGET, param.getType().intValue());
    }

    // @Override
    // protected void handleTreeWidget(WidgetAdapter sysexWidget, Xmlparam
    // param) {
    // assertEquals("Check param " + param.getLabel() + " type: "
    // + Xmlparam.Type.TREE_WIDGET.toString(),
    // Xmlparam.Type.INT_TREE_WIDGET, param.getType().intValue());
    // }

    @Override
    protected void handleCheckboxWidget(WidgetAdapter sysexWidget,
            Xmlparam param, int min, int max) {
        assertEquals("Check param " + param.getLabel() + " type: "
                + Xmlparam.Type.CHECK_BOX_WIDGET.toString(),
                Xmlparam.Type.INT_CHECK_BOX_WIDGET, param.getType().intValue());
        testMessages(param, sysexWidget);
    }

    @Override
    protected void handleComboboxWidget(WidgetAdapter sysexWidget,
            Xmlparam param, int min, int max) {
        assertEquals("Check param " + param.getLabel() + " type: "
                + Xmlparam.Type.COMBO_BOX_WIDGET.toString(),
                Xmlparam.Type.INT_COMBO_BOX_WIDGET, param.getType().intValue());
        testMessages(param, sysexWidget);
    }

    @Override
    protected void handleUb99ComboboxWidget(WidgetAdapter sysexWidget,
            Xmlparam param, int min, int max) {
        assertEquals("Check param " + param.getLabel() + " type: "
                + Xmlparam.Type.UB_99_ID_COMBO_WIDGET.toString(),
                Xmlparam.Type.INT_UB_99_ID_COMBO_WIDGET, param.getType()
                        .intValue());
        testMessages(param, sysexWidget);
    }

    @Override
    protected void handleKnobWidget(WidgetAdapter sysexWidget, Xmlparam param,
            int min, int max) {
        // assertEquals("Check param " + param.getLabel() + " type: "
        // + Xmlparam.Type.KNOB_WIDGET.toString(),
        // Xmlparam.Type.INT_KNOB_WIDGET, param.getType().intValue());
        testMessages(param, sysexWidget);
    }

    @Override
    protected void handlePatchNameWidget(WidgetAdapter sysexWidget,
            Xmlparam param) {
        assertEquals("Check param " + param.getLabel() + " type: "
                + Xmlparam.Type.PATCH_NAME_WIDGET.toString(),
                Xmlparam.Type.INT_PATCH_NAME_WIDGET, param.getType().intValue());

        Xmlmessages messages = param.getXmlmessages();
        Xmlmessage[] messageArray = messages.getXmlmessageArray();
        for (Xmlmessage message : messageArray) {
            MidiRecordSession session = midiDeviceProvider.openSession();
            int value = message.getXmlinput().getValue();
            sysexWidget.getValueSetter().setValue(value);
            String sysex = midiDeviceProvider.closeSession(session);
            assertEquals("Check param sysex: " + param.getLabel(),
                    message.getSysex(), sysex);
        }
    }

    @Override
    protected void handleSpinnerWidget(WidgetAdapter sysexWidget,
            Xmlparam param, int min, int max) {
        assertEquals("Check param type", Xmlparam.Type.INT_SPINNER_WIDGET,
                param.getType().intValue());
        testMessages(param, sysexWidget);
    }

    @Override
    protected void handleScrollbarWidget(WidgetAdapter sysexWidget,
            Xmlparam param, int min, int max) {
        assertEquals("Check param type", Xmlparam.Type.INT_SCROLL_BAR_WIDGET,
                param.getType().intValue());
        verifyScrollbarWidget(sysexWidget, param);
    }

    @Override
    protected void handleScrollbarLookupWidget(WidgetAdapter sysexWidget,
            Xmlparam param, int min, int max) {
        assertEquals("Check param type",
                Xmlparam.Type.INT_SCROLL_BAR_LOOKUP_WIDGET, param.getType()
                        .intValue());
        verifyScrollbarWidget(sysexWidget, param);
    }

    // @Override
    // protected void handleMultiWidget(JPanelFixture fixture, Xmlparam param) {
    // // TODO Auto-generated method stub
    //
    // }

    @Override
    protected Xmlstores getXmlstores(Xmldriver driver,
            Map<String, List<String>> bankMap) {
        log.debug("Testing patch for driver " + driver.getName());
        Xmlstores xmlstores = driver.getXmlstores();
        if (xmlstores == null) {
            assertTrue(bankMap.isEmpty());
        } else {
            Iterator<Entry<String, List<String>>> iterator =
                    bankMap.entrySet().iterator();
            int storesSize = 0;
            while (iterator.hasNext()) {
                Entry<String, List<String>> entry = iterator.next();
                List<String> value = entry.getValue();
                if (value.isEmpty()) {
                    storesSize++;
                } else {
                    storesSize += value.size();
                }
            }
            // assertEquals("Check patch stores size",
            // xmlstores.getXmlstoreArray().length, storesSize);
        }
        return xmlstores;
    }

    Xmlstore getStore(Xmlstores xmlstores, String bank, String patchNum) {
        Xmlstore[] xmlstoreArray = xmlstores.getXmlstoreArray();
        for (Xmlstore xmlstore : xmlstoreArray) {
            boolean bankEquals = false;
            boolean patchEquals = false;
            if (bank == null) {
                bankEquals = xmlstore.getBank() == null;
            } else {
                bankEquals = bank.equals(xmlstore.getBank());
            }

            if (patchNum == null) {
                patchEquals = xmlstore.getPatch() == null;
            } else {
                patchEquals = patchNum.equals(xmlstore.getPatch());
            }
            if (bankEquals && patchEquals) {
                return xmlstore;
            }
        }
        return null;
    }

    @Override
    protected void handleXmlstore(Xmlstores xmlstores, String bank,
            String patchNum, List<PopupContainer> popupList, String sysex) {
        Xmlstore xmlstore = getStore(xmlstores, bank, patchNum);
        assertNotNull("Patch store is in XML bank " + bank + " patch num: " + patchNum, xmlstore);
        assertEquals("Sysex check for bank " + bank + " and patch " + patchNum,
                xmlstore.getSysex(), sysex);
        if (popupList.isEmpty()) {
            assertNull("Check patch store popup list empty",
                    xmlstore.getXmlPopups());
        } else {
            XmlPopups xmlPopups = xmlstore.getXmlPopups();
            if (xmlPopups == null) {
                fail("Expected " + popupList.size() + " popups for bank" + bank
                        + " patch " + patchNum);
            }
            XmlPopup[] xmlPopupArray = xmlPopups.getXmlPopupArray();
            assertEquals("Check patch store popup number",
                    xmlPopupArray.length, popupList.size());
            for (int i = 0; i < xmlPopupArray.length; i++) {
                XmlPopup xmlPopup = xmlPopupArray[i];
                PopupContainer popupContainer = popupList.get(i);
                assertEquals("Check patch store popup title",
                        xmlPopup.getTitle(), popupContainer.getTitle());
                assertEquals("Check patch store popup content",
                        xmlPopup.getContent(), popupContainer.getContents());
            }
        }
    }

    @Override
    protected Xmlpatches getXmlpatches(Xmleditor editor, String[][] contents) {
        Xmlpatches patches = editor.getXmlpatches();
        assertEquals("Check " + editor.getName() + " patch cols",
                patches.getNumCols(), contents[0].length);
        assertEquals("Check " + editor.getName() + " patch rows",
                patches.getNumRows(), contents.length);
        assertEquals("Check sent patch length",
                patches.getXmlpatchArray().length, contents.length
                        * contents[0].length);
        return patches;
    }

    Xmlpatch getXmlpatch(Xmlpatches xmlpatches, String name) {
        Xmlpatch[] xmlpatchArray = xmlpatches.getXmlpatchArray();
        for (Xmlpatch xmlpatch : xmlpatchArray) {
            if (name.equals(xmlpatch.getName())) {
                return xmlpatch;
            }
        }
        return null;
    }

    @Override
    protected void handlePatch(Xmlpatches xmlpatches, String name,
            String sysex, List<PopupContainer> popups, String[][] contents) {
        Xmlpatch xmlpatch = getXmlpatch(xmlpatches, name);
        assertNotNull("Check " + name + " patch exists", xmlpatch);
        assertEquals("Check " + name + " patch sysex", xmlpatch.getSendSysex(),
                sysex);

        XmlPopups xmlPopups = xmlpatch.getXmlPopups();
        if (xmlPopups == null) {
            assertTrue("Num popups check", popups.isEmpty());
        } else {
            XmlPopup[] popupArray = xmlPopups.getXmlPopupArray();
            assertEquals("Num popups check", popupArray.length, popups.size());
            for (int l = 0; l < popupArray.length; l++) {
                XmlPopup xmlPopup = popupArray[l];
                PopupContainer popupContainer = popups.get(l);
                assertEquals("Popup title check", xmlPopup.getTitle(),
                        popupContainer.getTitle());
                assertEquals("Popup content check", xmlPopup.getContent(),
                        popupContainer.getContents());
            }
        }
    }
}
