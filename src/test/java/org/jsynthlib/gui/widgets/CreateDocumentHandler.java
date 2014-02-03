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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeModel;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JCheckBoxFixture;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JSliderFixture;
import org.fest.swing.fixture.JSpinnerFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.jsynthlib.driver.XmlPopup;
import org.jsynthlib.driver.XmlPopups;
import org.jsynthlib.driver.XmldeviceDocument;
import org.jsynthlib.driver.XmldeviceDocument.Xmldevice;
import org.jsynthlib.driver.Xmldriver;
import org.jsynthlib.driver.Xmldrivers;
import org.jsynthlib.driver.Xmleditor;
import org.jsynthlib.driver.XmlenvelopeParam;
import org.jsynthlib.driver.Xmlinput;
import org.jsynthlib.driver.Xmlmessage;
import org.jsynthlib.driver.Xmlmessages;
import org.jsynthlib.driver.Xmlparam;
import org.jsynthlib.driver.Xmlparams;
import org.jsynthlib.driver.Xmlpatch;
import org.jsynthlib.driver.Xmlpatches;
import org.jsynthlib.driver.Xmlstore;
import org.jsynthlib.driver.Xmlstores;
import org.jsynthlib.gui.widgets.EnvelopeWidget;
import org.jsynthlib.gui.widgets.KnobWidget;
import org.jsynthlib.gui.widgets.PatchNameWidget;
import org.jsynthlib.gui.widgets.SysexWidget;
import org.jsynthlib.gui.widgets.EnvelopeWidget.Node;
import org.jsynthlib.gui.widgets.SysexWidget.IParamModel;
import org.jsynthlib.midi.SingletonMidiDeviceProvider.MidiRecordSession;

import core.EnvelopeValueSetter;
import core.PopupContainer;
import core.valuesetter.CheckBoxValueSetter;
import core.valuesetter.ComboBoxValueSetter;
import core.valuesetter.IValueSetter;
import core.valuesetter.KnobValueSetter;
import core.valuesetter.SliderValueSetter;
import core.valuesetter.SpinnerValueSetter;

public class CreateDocumentHandler extends AbstractDocumentHandler {

    private XmldeviceDocument deviceDocument;
    private Xmldevice device;
    private Xmldrivers drivers;

    public CreateDocumentHandler(File outputFile, FrameFixture testFrame) {
        super(outputFile, testFrame);
        deviceDocument = XmldeviceDocument.Factory.newInstance();
        device = deviceDocument.addNewXmldevice();
    }

    @Override
    public void handleDocument(String manufacturer, String deviceName) {
        device.setManufacturer(manufacturer);
        device.setName(deviceName);
        drivers = device.addNewXmldrivers();
    }

    Xmlparams getParams(Xmleditor editor) {
        int numparams = editor.getNumparams();
        editor.setNumparams(numparams + 1);
        Xmlparams params = editor.getXmlparams();
        if (params == null) {
            params = editor.addNewXmlparams();
        }
        return params;
    }

    @Override
    public Xmlparam handleParamInternal(Xmleditor editor,
            SysexWidget sysexWidget, String uniqueName) {
        Xmlparams params = getParams(editor);
        Xmlparam param = params.addNewXmlparam();
        int max = sysexWidget.getValueMax();
        int min = sysexWidget.getValueMin();

        param.setMin(min);
        param.setMax(max);
        param.setLabel(uniqueName);

        log.info("Saving param " + uniqueName + " -> "
                + sysexWidget.getClass().getName());
        return param;
    }

    @Override
    public void saveDocument() throws IOException {
        deviceDocument.save(outputFile);
    }

    @Override
    public Xmleditor handlePatchEditor(Xmldriver driver,
            boolean editable, String editorName, List<PopupContainer> popups) {
        driver.setEditable(editable);
        Xmleditor editor = driver.addNewXmleditor();
        editor.setName(editorName);
        if (!popups.isEmpty()) {
            XmlPopups editorPopups = editor.addNewXmlPopups();
            for (PopupContainer popup : popups) {
                XmlPopup editorPopup = editorPopups.addNewXmlPopup();
                editorPopup.setTitle(popup.getTitle());
                editorPopup.setContent(popup.getContents());
            }
        }
        return editor;
    }

    void storeTreeWidgetRecursive(JTreeFixture fixture, Object node,
            String parentPath, Xmlmessages messages) {
        TreeModel model = fixture.target.getModel();
        int childCount = model.getChildCount(node);
        int i = 0;
        while (i < childCount) {
            MidiRecordSession session = midiDeviceProvider.openSession();
            Object child = model.getChild(node, i);
            String childPath =
                    parentPath + fixture.separator() + child.toString();
            if (model.isLeaf(child)) {
                fixture.selectPath(childPath);
                fixture.clickPath(childPath);
                String sysex = midiDeviceProvider.closeSession(session);
                if (sysex == null || sysex.isEmpty()) {
                    log.warn("Value is empty for tree param.");
                }
                Xmlmessage message = messages.addNewXmlmessage();
                Xmlinput input = message.addNewXmlinput();
                input.setValue(i);
                message.setSysex(sysex);
                i += childCount / 4;
            } else {
                storeTreeWidgetRecursive(fixture, child, childPath, messages);
                i++;
            }
        }
    }

    void storeSliderWidget(final JSliderFixture fixture, Xmlparam param,
            int min, int max) {
        int value = fixture.target.getValue();
        storeMessages(param, new SliderValueSetter(fixture), value, min, max);
    }

    void storeMessages(Xmlparam param, IValueSetter setter, int value, int min,
            int max) {
        Xmlmessages messages = param.addNewXmlmessages();
        if (value == min) {
            setter.setValue(max);
        }

        int incr = (max / 4);
        if (incr == 0 || incr == 1) {
            incr++;
        }
        for (int i = min; i <= max; i += incr) {
            MidiRecordSession session = midiDeviceProvider.openSession();
            if (i > max) {
                i = max;
            }
            log.debug("Sliding to: " + i);
            setter.setValue(i);
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
            }
            String sysex = midiDeviceProvider.closeSession(session);
            if (sysex == null || sysex.isEmpty()) {
                log.warn("Value is empty for param " + param.getLabel());
                // if (!param.getLabel().equals("Corresponding Voice?")) {
                // System.exit(0);
                // }
            }
            Xmlmessage message = messages.addNewXmlmessage();
            Xmlinput input = message.addNewXmlinput();
            input.setValue(i);
            message.setSysex(sysex);
        }
    }

    @Override
    public Xmldriver handleDriver(String driverName) {
        Xmldriver driver = drivers.addNewXmldriver();
        driver.setName(driverName);
        return driver;
    }

    @Override
    protected void handleWidgetNotVisible(String uniqueName, Xmleditor editor) {
        log.warn("Widget is not visible! " + uniqueName);
        // Increment params index
        getParams(editor);
    }

    @Override
    protected void handleEnvelopeWidget(EnvelopeWidget widget,
            String uniqueName, Xmleditor editor) {
        try {
            Xmlparams params = getParams(editor);
            XmlenvelopeParam envelopeParam = params.addNewXmlenvelopeParam();

            Node[] nodes = widget.nodes;
            int numFaders = 0;
            for (int i = 0; i < nodes.length; i++) {
                Node node = nodes[i];

                final IParamModel modelX = getNodeParamModel(node, true);
                if (modelX != null) {
                    final int faderX = widget.getSliderNum() + numFaders++;
                    int minX = getField("minX", Integer.class, node);
                    int maxX = getField("maxX", Integer.class, node);

                    int valueX = modelX.get();
                    Xmlparam paramX = envelopeParam.addNewXmlparam();
                    paramX.setType(Xmlparam.Type.ENVELOPE_WIDGET);
                    paramX.setLabel(getNodeName(node, true));
                    paramX.setMax(maxX);
                    paramX.setMin(minX);

                    storeMessages(paramX, new EnvelopeValueSetter(widget,
                            faderX), valueX, minX, maxX);
                }

                final IParamModel modelY = getNodeParamModel(node, false);
                if (modelY != null) {
                    final int faderY = widget.getSliderNum() + numFaders++;
                    int minY = getField("minY", Integer.class, node);
                    int maxY = getField("maxY", Integer.class, node);
                    int valueY = modelY.get();
                    Xmlparam paramY = envelopeParam.addNewXmlparam();
                    paramY.setType(Xmlparam.Type.ENVELOPE_WIDGET);
                    paramY.setLabel(getNodeName(node, false));
                    paramY.setMax(maxY);
                    paramY.setMin(minY);

                    storeMessages(paramY, new EnvelopeValueSetter(widget,
                            faderY), valueY, minY, maxY);
                }
            }
        } catch (NoSuchFieldException e) {
            log.warn(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    protected void handleDisabledWidget(Xmlparam param) {
        param.setType(Xmlparam.Type.DISABLED_WIDGET);
        log.warn("Disabled widget: " + param.getLabel());
    }

    @Override
    protected void handleTreeWidget(JTreeFixture fixture, Xmlparam param) {
        param.setType(Xmlparam.Type.TREE_WIDGET);
        Xmlmessages messages = param.addNewXmlmessages();
        TreeModel model = fixture.target.getModel();
        fixture.separator(";");
        Object root = model.getRoot();
        String parentPath = root.toString();
        storeTreeWidgetRecursive(fixture, root, parentPath, messages);
    }

    @Override
    protected void handleCheckboxWidget(JCheckBoxFixture fixture,
            Xmlparam param, int min, int max) {
        param.setType(Xmlparam.Type.CHECK_BOX_WIDGET);
        int value = fixture.target.isSelected() ? max : min;
        storeMessages(param, new CheckBoxValueSetter(fixture, min), value, min,
                max);
    }

    @Override
    protected void handleComboboxWidget(JComboBoxFixture fixture,
            Xmlparam param, int min, int max) {
        int value = fixture.target.getSelectedIndex();
        param.setType(Xmlparam.Type.COMBO_BOX_WIDGET);

        storeMessages(param, new ComboBoxValueSetter(fixture, min), value, min,
                max);
    }

    @Override
    protected void handleUb99ComboboxWidget(JComboBoxFixture fixture,
            Xmlparam param, int min, int max) {
        int value = fixture.target.getSelectedIndex();
        param.setType(Xmlparam.Type.UB_99_ID_COMBO_WIDGET);
        storeMessages(param, new ComboBoxValueSetter(fixture, min), value, min,
                max);
    }

    @Override
    protected void handleKnobWidget(KnobWidget widget, Xmlparam param, int min,
            int max) {
        param.setType(Xmlparam.Type.KNOB_WIDGET);
        int value = widget.getValue();
        storeMessages(param, new KnobValueSetter(widget), value, min, max);
    }

    @Override
    protected void handlePatchNameWidget(JTextComponentFixture fixture,
            Xmlparam param, PatchNameWidget widget) {
        String[] values = {
                "a", "A", "Apa", "apa", "Text T", "t t tt" };
        param.setType(Xmlparam.Type.PATCH_NAME_WIDGET);

        fixture.deleteText();
        IClickable clickable = getClickableParentRecursive(widget.getParent());
        clickable.click();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }

        Xmlmessages messages = param.addNewXmlmessages();

        for (String value : values) {
            MidiRecordSession session = midiDeviceProvider.openSession();
            fixture.setText(value);
            clickable.click();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            String sysex = midiDeviceProvider.closeSession(session);
            Xmlmessage message = messages.addNewXmlmessage();
            message.setSysex(sysex);
            Xmlinput input = message.addNewXmlinput();
            input.setString(value);
        }
    }

    @Override
    protected void handleSpinnerWidget(JSpinnerFixture fixture, Xmlparam param,
            int min, int max) {
        param.setType(Xmlparam.Type.SPINNER_WIDGET);
        Integer value = (Integer) fixture.target.getValue();
        storeMessages(param, new SpinnerValueSetter(fixture), value, min, max);
    }

    @Override
    protected void handleScrollbarWidget(JSliderFixture fixture,
            Xmlparam param, int min, int max) {
        param.setType(Xmlparam.Type.SCROLL_BAR_WIDGET);
        storeSliderWidget(fixture, param, min, max);
    }

    @Override
    protected void handleScrollbarLookupWidget(JSliderFixture fixture,
            Xmlparam param, int min, int max) {
        param.setType(Xmlparam.Type.SCROLL_BAR_LOOKUP_WIDGET);
        storeSliderWidget(fixture, param, min, max);
    }

    @Override
    protected void handleMultiWidget(JPanelFixture fixture, Xmlparam param) {
        param.setType(Xmlparam.Type.MULTI_WIDGET);
        int fixtureIndex = 0;
        List<JComboBoxFixture> comboBoxFixtures =
                guiHandler.getComboBoxFixtures(fixture);
        for (JComboBoxFixture jComboBoxFixture : comboBoxFixtures) {
            int itemCount = jComboBoxFixture.target.getItemCount();
            int increment = itemCount / 4;
            for (int i = 0; i < 4; i++) {
                int value = increment * i;
                jComboBoxFixture.selectItem(value);
                storeMultiMessage(param, fixtureIndex + ":" + value);
            }

            fixtureIndex++;
        }

        List<JCheckBoxFixture> checkBoxFixtures =
                guiHandler.getCheckBoxFixtures(fixture);
        for (JCheckBoxFixture jCheckBoxFixture : checkBoxFixtures) {
            int value = 0;
            if (jCheckBoxFixture.target.isSelected()) {
                jCheckBoxFixture.uncheck();
            } else {
                jCheckBoxFixture.check();
                value = 1;
            }

            storeMultiMessage(param, fixtureIndex + ":" + value);
            fixtureIndex++;
        }
    }

    void storeMultiMessage(Xmlparam param, String string) {
        MidiRecordSession session = midiDeviceProvider.openSession();
        Xmlmessages messages = param.addNewXmlmessages();
        Xmlmessage message = messages.addNewXmlmessage();
        Xmlinput input = message.addNewXmlinput();
        input.setString(string);
        log.debug("Sliding to: " + string);
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
        }
        String sysex = midiDeviceProvider.closeSession(session);
        message.setSysex(sysex);
    }

    @Override
    protected Xmlstores getXmlstores(Xmldriver driver,
            Map<String, List<String>> bankMap) {
        log.debug("Storing patch for driver " + driver.getName());
        return driver.addNewXmlstores();
    }

    @Override
    protected void handleXmlstore(Xmlstores xmlstores, String bank,
            String patchNum, List<PopupContainer> popupList, String sysex) {
        Xmlstore xmlstore = xmlstores.addNewXmlstore();
        xmlstore.setBank(bank);
        xmlstore.setPatch(patchNum);
        xmlstore.setSysex(sysex);

        if (!popupList.isEmpty()) {
            XmlPopups xmlPopups = xmlstore.addNewXmlPopups();
            for (PopupContainer popupContainer : popupList) {
                XmlPopup xmlPopup = xmlPopups.addNewXmlPopup();
                xmlPopup.setTitle(popupContainer.getTitle());
                xmlPopup.setContent(popupContainer.getContents());
            }
        }
    }

    @Override
    protected Xmlpatches getXmlpatches(Xmleditor editor, String[][] contents) {
        Xmlpatches patches = editor.addNewXmlpatches();
        patches.setNumCols(contents[0].length);
        patches.setNumRows(contents.length);
        return patches;
    }

    @Override
    protected void handlePatch(Xmlpatches xmlpatches, String name,
            String sysex, List<PopupContainer> popups, String[][] contents) {
        Xmlpatch patch = xmlpatches.addNewXmlpatch();
        patch.setName(name);
        boolean first = true;
        XmlPopups xmlPopups = null;
        for (PopupContainer popup : popups) {
            if (first) {
                first = false;
                xmlPopups = patch.addNewXmlPopups();
            }
            XmlPopup xmlPopup = xmlPopups.addNewXmlPopup();
            xmlPopup.setTitle(popup.getTitle());
            xmlPopup.setContent(popup.getContents());
        }

        patch.setSendSysex(sysex);
    }
}
