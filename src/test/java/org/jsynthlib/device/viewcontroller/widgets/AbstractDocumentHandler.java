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

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.fest.swing.exception.ComponentLookupException;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTableFixture;
import org.jsynthlib.core.ContainerDisplayer;
import org.jsynthlib.core.GuiHandler;
import org.jsynthlib.core.PatchEditorTest;
import org.jsynthlib.core.PopupContainer;
import org.jsynthlib.core.TitleFinder.FrameWrapper;
import org.jsynthlib.driver.Xmldriver;
import org.jsynthlib.driver.Xmleditor;
import org.jsynthlib.driver.XmlenvelopeParam;
import org.jsynthlib.driver.Xmlparam;
import org.jsynthlib.driver.Xmlparams;
import org.jsynthlib.driver.Xmlpatches;
import org.jsynthlib.driver.Xmlstores;
import org.jsynthlib.midi.SingletonMidiDeviceProvider;
import org.jsynthlib.midi.SingletonMidiDeviceProvider.MidiRecordSession;
import org.jsynthlib.test.adapter.WidgetAdapter;
import org.jsynthlib.test.adapter.WidgetAdapter.Type;

public abstract class AbstractDocumentHandler {

    protected final transient Logger log = Logger.getLogger(getClass());
    protected final File outputFile;
    protected final FrameFixture testFrame;
    private final List<String> uniqueNames;
    protected GuiHandler guiHandler;
    protected final SingletonMidiDeviceProvider midiDeviceProvider;

    public AbstractDocumentHandler(File outputFile, FrameFixture testFrame) {
        this.testFrame = testFrame;
        this.outputFile = outputFile;
        uniqueNames = new ArrayList<String>();
        guiHandler = new GuiHandler(testFrame);
        midiDeviceProvider = SingletonMidiDeviceProvider.getInstance();
    }

    public abstract Xmldriver handleDriver(String driverName);

    public abstract void handleDocument(String manufacturer, String deviceName);

    public abstract Xmleditor handlePatchEditor(Xmldriver driver,
            boolean editable, String editorName, List<PopupContainer> popups);

    public void handleParam(Xmleditor editor, WidgetAdapter sysexWidget,
            FrameWrapper frame) {
        String uniqueName = sysexWidget.getUniqueName(frame);
        if (Type.ENVELOPE.equals(sysexWidget.getType())) {
            handleEnvelopeWidget((AbstractEnvelopeWidgetAdapter) sysexWidget,
                    uniqueName, editor);
            return;
        }

        if (!sysexWidget.isShowing()) {
            handleWidgetNotVisible(uniqueName, editor);
            return;
        }

        Xmlparam param = handleParamInternal(editor, sysexWidget, uniqueName);
        int min = param.getMin();
        int max = param.getMax();

        if (Type.LABEL.equals(sysexWidget.getType())) {
            // Skip...
            return;
        }

        if (Type.CHECKBOX.equals(sysexWidget.getType())) {
            if (sysexWidget.isEnabled()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                handleCheckboxWidget(sysexWidget, param, min, max);
            } else {
                handleDisabledWidget(param);
            }
        } else if (Type.COMBOBOX.equals(sysexWidget.getType())) {
            if (sysexWidget.isEnabled()) {
                handleComboboxWidget(sysexWidget, param, min, max);
            } else {
                handleDisabledWidget(param);
            }
        } else if (Type.ID_COMBOBOX.equals(sysexWidget.getType())) {
            if (sysexWidget.isEnabled()) {
                handleUb99ComboboxWidget(sysexWidget, param, min, max);
            } else {
                handleDisabledWidget(param);
            }
        } else if (Type.KNOB.equals(sysexWidget.getType())) {
            if (sysexWidget.isEnabled()) {
                handleKnobWidget(sysexWidget, param, min, max);
            } else {
                handleDisabledWidget(param);
            }
        } else if (Type.PATCH_NAME.equals(sysexWidget.getType())) {
            if (sysexWidget.isEnabled()) {
                handlePatchNameWidget(sysexWidget, param);
            } else {
                handleDisabledWidget(param);
            }
        } else if (Type.SCROLLBAR.equals(sysexWidget.getType())) {
            handleScrollbarWidget(sysexWidget, param, min, max);
        } else if (Type.SPINNER.equals(sysexWidget.getType())) {
            if (sysexWidget.isEnabled()) {
                handleSpinnerWidget(sysexWidget, param, min, max);
            } else {
                handleDisabledWidget(param);
            }
        } else if (Type.TREE.equals(sysexWidget.getType())) {
            // TreeWidget widget = (TreeWidget) sysexWidget;
            // JTreeFixture fixture =
            // new JTreeFixture(testFrame.robot, widget.tree);
            if (sysexWidget.isEnabled()) {
                // handleTreeWidget(sysexWidget, param);
            } else {
                handleDisabledWidget(param);
            }
        } else if (Type.SCROLLBAR_LOOKUP.equals(sysexWidget.getType())) {
            if (sysexWidget.isEnabled()) {
                handleScrollbarLookupWidget(sysexWidget, param, min, max);
            } else {
                handleDisabledWidget(param);
            }
        } else if (Type.MULTI.equals(sysexWidget.getType())) {
            // JPanelFixture fixture =
            // new JPanelFixture(testFrame.robot, sysexWidget);
            // handleMultiWidget(sysexWidget, param);
        } else {
            log.warn("Could not handle widget " + sysexWidget.getType().name());
            System.exit(0);
        }
    }

    protected abstract Xmlparam handleParamInternal(Xmleditor editor,
            WidgetAdapter sysexWidget, String uniqueName);

    // protected abstract void handleMultiWidget(WidgetAdapter sysexWidget,
    // Xmlparam param);

    protected abstract void handleWidgetNotVisible(String uniqueName,
            Xmleditor editor);

    protected abstract void handleEnvelopeWidget(
            AbstractEnvelopeWidgetAdapter sysexWidget, String uniqueName,
            Xmleditor editor);

    protected abstract void handleDisabledWidget(Xmlparam param);

    // protected abstract void handleTreeWidget(WidgetAdapter sysexWidget,
    // Xmlparam param);

    protected abstract void handleCheckboxWidget(WidgetAdapter sysexWidget,
            Xmlparam param, int min, int max);

    protected abstract void handleComboboxWidget(WidgetAdapter sysexWidget,
            Xmlparam param, int min, int max);

    protected abstract void handleUb99ComboboxWidget(WidgetAdapter sysexWidget,
            Xmlparam param, int min, int max);

    protected abstract void handleKnobWidget(WidgetAdapter sysexWidget,
            Xmlparam param, int min, int max);

    protected abstract void handlePatchNameWidget(WidgetAdapter sysexWidget,
            Xmlparam param);

    protected abstract void handleScrollbarWidget(WidgetAdapter sysexWidget,
            Xmlparam param, int min, int max);

    protected abstract void handleScrollbarLookupWidget(
            WidgetAdapter sysexWidget, Xmlparam param, int min, int max);

    protected abstract void handleSpinnerWidget(WidgetAdapter sysexWidget,
            Xmlparam param, int min, int max);

    public abstract void saveDocument() throws IOException;

    protected abstract Xmlpatches getXmlpatches(Xmleditor editor,
            String[][] contents);

    protected abstract void handlePatch(Xmlpatches xmlpatches, String name,
            String sysex, List<PopupContainer> popups, String[][] contents);

    protected int getBankIncrement(int length) {
        String testLevelStr = System.getProperty(PatchEditorTest.TEST_LEVEL);
        int testLevel = Integer.parseInt(testLevelStr);
        int retval = 0;
        switch (testLevel) {
        case PatchEditorTest.TESTLEVEL_LOW:
            retval = length / 2;
        case PatchEditorTest.TESTLEVEL_MEDIUM:
            retval = length / 4;
        case PatchEditorTest.TESTLEVEL_HIGH:
        default:
            retval = 1;
        }
        if (retval < 1) {
            retval = 1;
        }
        return retval;
    }

    public void handleBankEditor(Xmleditor editor, JTableFixture table) {
        String[][] contents = table.contents();
        Xmlpatches xmlpatches = getXmlpatches(editor, contents);

        int rowIncr = getBankIncrement(contents.length);
        for (int i = 0; i < contents.length; i += rowIncr) {
            int colIncr = getBankIncrement(contents[i].length);
            for (int j = 0; j < contents[i].length; j += colIncr) {
                MidiRecordSession session = midiDeviceProvider.openSession();

                // TODO:Workaround for Roland MT32
                char c = 21;
                String name =
                        table.target.getModel().getValueAt(i, j).toString()
                                .trim().replace(c, ' ');
                List<PopupContainer> popups = guiHandler.sendPatch(table, j, i);
                String sysex = midiDeviceProvider.closeSession(session);
                handlePatch(xmlpatches, name, sysex, popups, contents);
            }
        }
    }

    String getUniqueName(FrameWrapper frame, SysexWidget sysexWidget) {
        JComponent parent = (JComponent) sysexWidget.getParent();
        String containerName = null;
        if (parent == null) {
            try {
                log.info("Showing table");
                containerName =
                        ContainerDisplayer.showTableAndGetNameRecursive(frame,
                                sysexWidget);
            } catch (ComponentLookupException e) {
                log.warn("Widget is not visible!");
            }
        } else {
            log.info("Showing container");
            containerName =
                    ContainerDisplayer.showContainerAndGetNameRecursive(frame,
                            sysexWidget);
        }

        String label = sysexWidget.getLabel();
        if (label == null || label.isEmpty()) {
            if (sysexWidget instanceof EnvelopeWidget) {
                label = "Envelope";
            } else if (sysexWidget.getParent() != null) {
                label = findNearestLabelRecursive(sysexWidget.getParent(), 0);
            }
        }
        if (label == null || label.isEmpty()) {
            log.warn("Label is not valid!");
        }
        String uniqueName = containerName + label;
        int index = 1;
        while (uniqueNames.contains(uniqueName)) {
            log.warn("Editor has duplicate widgets! " + uniqueName);
            if (index == 1) {
                uniqueName = uniqueName + "-id" + index;
            } else {
                uniqueName =
                        uniqueName.replace("-id" + (index - 1), "-id" + index);
            }
            index++;
        }
        uniqueNames.add(uniqueName);
        return uniqueName;
    }

    String findNearestLabelRecursive(Container parent, int index) {
        if (index >= 3) {
            log.warn("Could not find label!");
            return null;
        }
        Component[] components = parent.getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                String text = ((JLabel) component).getText();
                if (text != null && !text.isEmpty()) {
                    return text;
                }
            }
        }
        return findNearestLabelRecursive(parent.getParent(), index + 1);
    }

    protected interface IClickable {
        void click();
    }

    protected IClickable getClickableParentRecursive(Container container) {
        if (container instanceof JFrame) {
            JFrame frame = (JFrame) container;
            final FrameFixture fixture = new FrameFixture(frame);
            return new IClickable() {
                @Override
                public void click() {
                    fixture.click();
                }
            };
        } else if (container instanceof JTabbedPane) {
            JTabbedPane pane = (JTabbedPane) container;
            final JTabbedPaneFixture fixture =
                    new JTabbedPaneFixture(testFrame.robot, pane);
            return new IClickable() {
                @Override
                public void click() {
                    fixture.click();
                }
            };
        } else if (container instanceof JPanel) {
            JPanel pane = (JPanel) container;
            final JPanelFixture fixture =
                    new JPanelFixture(testFrame.robot, pane);
            return new IClickable() {
                @Override
                public void click() {
                    fixture.focus();
                }
            };
        } else {
            return getClickableParentRecursive(container.getParent());
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T getField(String fieldName, Class<T> klass, Object object)
            throws IllegalAccessException, NoSuchFieldException {
        Field f = object.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return (T) f.get(object);
    }

    protected abstract Xmlstores getXmlstores(Xmldriver driver,
            Map<String, List<String>> bankMap);

    protected abstract void handleXmlstore(Xmlstores xmlstores, String bank,
            String patchNum, List<PopupContainer> popupList, String sysex);

    public void handleStore(Xmldriver driver, JTableFixture table,
            Map<String, List<String>> bankMap) {
        Iterator<Entry<String, List<String>>> iterator =
                bankMap.entrySet().iterator();
        if (bankMap.isEmpty()) {
            log.info("No banks to handle...");
            return;
        }

        Xmlstores xmlstores = getXmlstores(driver, bankMap);
        while (iterator.hasNext()) {
            Entry<String, List<String>> entry = iterator.next();
            String bank = entry.getKey();
            List<String> patchNumList = entry.getValue();

            if (patchNumList.isEmpty()) {
                MidiRecordSession session = midiDeviceProvider.openSession();
                List<PopupContainer> popupList =
                        guiHandler.storePatch(table, bank, null);
                String sysex = midiDeviceProvider.closeSession(session);
                handleXmlstore(xmlstores, bank, null, popupList, sysex);
            } else {
                int incr = (patchNumList.size() / 4) + 1;
                for (int i = 0; i < patchNumList.size(); i += incr) {
                    MidiRecordSession session =
                            midiDeviceProvider.openSession();
                    String patchNum = patchNumList.get(i);
                    List<PopupContainer> popupList =
                            guiHandler.storePatch(table, bank, patchNum);
                    String sysex = midiDeviceProvider.closeSession(session);
                    handleXmlstore(xmlstores, bank, patchNum, popupList, sysex);
                }
            }
        }
    }

    protected XmlenvelopeParam getEnvelopeParamByUniqueName(Xmlparams params,
            String uniqueName) {
        XmlenvelopeParam[] envelopeParams = params.getXmlenvelopeParamArray();
        for (XmlenvelopeParam xmlenvelopeParam : envelopeParams) {
            if (xmlenvelopeParam.getLabel() != null
                    && xmlenvelopeParam.getLabel().equals(uniqueName)) {
                return xmlenvelopeParam;
            }
        }
        throw new IllegalArgumentException("Could not find envelope param "
                + uniqueName);
    }
}
