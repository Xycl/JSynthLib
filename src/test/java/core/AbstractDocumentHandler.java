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
package core;

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

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.fest.swing.exception.ComponentLookupException;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JCheckBoxFixture;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JSliderFixture;
import org.fest.swing.fixture.JSpinnerFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.jsynthlib.driver.Xmldriver;
import org.jsynthlib.driver.Xmleditor;
import org.jsynthlib.driver.Xmlparam;
import org.jsynthlib.driver.Xmlpatches;
import org.jsynthlib.driver.Xmlstores;
import org.jsynthlib.midi.SingletonMidiDeviceProvider;
import org.jsynthlib.midi.SingletonMidiDeviceProvider.MidiRecordSession;

import synthdrivers.QuasimidiQuasar.MultiWidget;
import synthdrivers.YamahaUB99.IdComboWidget;
import core.EnvelopeWidget.Node;
import core.SysexWidget.IParamModel;
import core.TitleFinder.FrameWrapper;

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

    public abstract Xmleditor handlePatchEditor(Xmldriver driver, boolean editable,
            String editorName, List<PopupContainer> popups);

    public void handleParam(Xmleditor editor, SysexWidget sysexWidget,
            FrameWrapper frame) {
        String uniqueName = getUniqueName(frame, sysexWidget);
        if (sysexWidget instanceof EnvelopeWidget) {
            EnvelopeWidget envWidget = (EnvelopeWidget) sysexWidget;
            handleEnvelopeWidget(envWidget, uniqueName, editor);
            return;
        }

        if (!sysexWidget.isShowing()) {
            handleWidgetNotVisible(uniqueName, editor);
            return;
        }

        Xmlparam param =
                handleParamInternal(editor, sysexWidget, uniqueName);
        int min = param.getMin();
        int max = param.getMax();

        if (sysexWidget instanceof LabelWidget) {
            // Skip...
            return;
        }

        if (sysexWidget instanceof CheckBoxWidget) {
            CheckBoxWidget widget = (CheckBoxWidget) sysexWidget;
            final JCheckBoxFixture fixture =
                    new JCheckBoxFixture(testFrame.robot, widget.cb);
            if (fixture.target.isEnabled()) {
                handleCheckboxWidget(fixture, param, min, max);
            } else {
                handleDisabledWidget(param);
            }
        } else if (sysexWidget instanceof ComboBoxWidget) {
            ComboBoxWidget widget = (ComboBoxWidget) sysexWidget;

            final JComboBoxFixture fixture =
                    new JComboBoxFixture(testFrame.robot, widget.cb);
            if (fixture.target.isEnabled()) {
                handleComboboxWidget(fixture, param, min, max);
            } else {
                handleDisabledWidget(param);
            }
        } else if (sysexWidget instanceof IdComboWidget) {
            IdComboWidget widget = (IdComboWidget) sysexWidget;

            try {
                JComboBox cb = getField("cb", JComboBox.class, widget);
                final JComboBoxFixture fixture =
                        new JComboBoxFixture(testFrame.robot, cb);
                if (fixture.target.isEnabled()) {
                    handleUb99ComboboxWidget(fixture, param, min, max);
                } else {
                    handleDisabledWidget(param);
                }
            } catch (NoSuchFieldException e) {
                log.warn(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                log.warn(e.getMessage(), e);
            }
        } else if (sysexWidget instanceof KnobWidget) {
            KnobWidget widget = (KnobWidget) sysexWidget;
            if (widget.isEnabled()) {
                handleKnobWidget(widget, param, min, max);
            } else {
                handleDisabledWidget(param);
            }
        } else if (sysexWidget instanceof PatchNameWidget) {
            PatchNameWidget widget = (PatchNameWidget) sysexWidget;
            final JTextComponentFixture fixture =
                    new JTextComponentFixture(testFrame.robot, widget.name);
            if (fixture.target.isEnabled()) {
                handlePatchNameWidget(fixture, param, widget);
            } else {
                handleDisabledWidget(param);
            }
        } else if (sysexWidget instanceof ScrollBarWidget) {
            ScrollBarWidget widget = (ScrollBarWidget) sysexWidget;
            final JSliderFixture fixture =
                    new JSliderFixture(testFrame.robot, widget.slider);
            handleScrollbarWidget(fixture, param, min, max);
        } else if (sysexWidget instanceof SpinnerWidget) {
            SpinnerWidget widget = (SpinnerWidget) sysexWidget;
            final JSpinnerFixture fixture =
                    new JSpinnerFixture(testFrame.robot, widget.spinner);

            if (fixture.target.isEnabled()) {
                handleSpinnerWidget(fixture, param, min, max);
            } else {
                handleDisabledWidget(param);
            }
        } else if (sysexWidget instanceof TreeWidget) {
            TreeWidget widget = (TreeWidget) sysexWidget;
            JTreeFixture fixture =
                    new JTreeFixture(testFrame.robot, widget.tree);
            if (fixture.target.isEnabled()) {
                handleTreeWidget(fixture, param);
            } else {
                handleDisabledWidget(param);
            }
        } else if (sysexWidget instanceof ScrollBarLookupWidget) {
            ScrollBarLookupWidget widget = (ScrollBarLookupWidget) sysexWidget;
            final JSliderFixture fixture =
                    new JSliderFixture(testFrame.robot, widget.slider);
            if (fixture.target.isEnabled()) {
                handleScrollbarLookupWidget(fixture, param, min, max);
            } else {
                handleDisabledWidget(param);
            }
        } else if (sysexWidget instanceof MultiWidget) {
            JPanelFixture fixture =
                    new JPanelFixture(testFrame.robot, sysexWidget);
            handleMultiWidget(fixture, param);
        } else {
            log.warn("Could not handle widget "
                    + sysexWidget.getClass().getName());
            System.exit(0);
        }
    }

    protected abstract Xmlparam handleParamInternal(Xmleditor editor,
            SysexWidget sysexWidget, String uniqueName);

    protected abstract void handleMultiWidget(JPanelFixture fixture,
            Xmlparam param);

    protected abstract void handleWidgetNotVisible(String uniqueName,
            Xmleditor editor);

    protected abstract void handleEnvelopeWidget(EnvelopeWidget envWidget,
            String uniqueName, Xmleditor editor);

    protected abstract void handleDisabledWidget(Xmlparam param);

    protected abstract void handleTreeWidget(JTreeFixture fixture,
            Xmlparam param);

    protected abstract void handleCheckboxWidget(JCheckBoxFixture fixture,
            Xmlparam param, int min, int max);

    protected abstract void handleComboboxWidget(JComboBoxFixture fixture,
            Xmlparam param, int min, int max);

    protected abstract void handleUb99ComboboxWidget(JComboBoxFixture fixture,
            Xmlparam param, int min, int max);

    protected abstract void handleKnobWidget(KnobWidget widget, Xmlparam param,
            int min, int max);

    protected abstract void handlePatchNameWidget(
            JTextComponentFixture fixture, Xmlparam param,
            PatchNameWidget widget);

    protected abstract void handleScrollbarWidget(JSliderFixture fixture,
            Xmlparam param, int min, int max);

    protected abstract void handleScrollbarLookupWidget(JSliderFixture fixture,
            Xmlparam param, int min, int max);

    protected abstract void handleSpinnerWidget(JSpinnerFixture fixture,
            Xmlparam param, int min, int max);

    protected abstract void saveDocument() throws IOException;

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
                        ContainerDisplayer.showTableAndGetNameRecursive(
                                frame, sysexWidget);
            } catch (ComponentLookupException e) {
                log.warn("Widget is not visible!");
            }
        } else {
            log.info("Showing container");
            containerName =
                    ContainerDisplayer.showContainerAndGetNameRecursive(
                            frame, sysexWidget);
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

    protected IParamModel getNodeParamModel(Node node, boolean x)
            throws NoSuchFieldException, IllegalAccessException {
        String fieldName = "pmodelY";
        if (x) {
            fieldName = "pmodelX";
        }
        return getField(fieldName, IParamModel.class, node);
    }

    protected String getNodeName(Node node, boolean x)
            throws NoSuchFieldException, IllegalAccessException {
        String fieldName = "nameY";
        if (x) {
            fieldName = "nameX";
        }
        return getField(fieldName, String.class, node);
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
                    MidiRecordSession session = midiDeviceProvider.openSession();
                    String patchNum = patchNumList.get(i);
                    List<PopupContainer> popupList =
                            guiHandler.storePatch(table, bank, patchNum);
                    String sysex =
                            midiDeviceProvider.closeSession(session);
                    handleXmlstore(xmlstores, bank, patchNum, popupList, sysex);
                }
            }
        }
    }

}
