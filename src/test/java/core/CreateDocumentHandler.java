package core;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.tree.TreeModel;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JCheckBoxFixture;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JSliderFixture;
import org.fest.swing.fixture.JSpinnerFixture;
import org.fest.swing.fixture.JTableFixture;
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
import org.jsynthlib.midi.TestMidiDeviceProvider;

import synthdrivers.YamahaUB99.IdComboWidget;
import core.EnvelopeWidget.Node;
import core.SysexWidget.IParamModel;
import core.valuesetter.CheckBoxValueSetter;
import core.valuesetter.ComboBoxValueSetter;
import core.valuesetter.IValueSetter;
import core.valuesetter.KnobValueSetter;
import core.valuesetter.SpinnerValueSetter;

public class CreateDocumentHandler extends AbstractDocumentHandler {

    private XmldeviceDocument deviceDocument;
    private Xmldevice device;
    private Xmldrivers drivers;

    private TestMidiDeviceProvider midiDeviceProvider;

    CreateDocumentHandler(File outputFile, FrameFixture testFrame) {
        super(outputFile, testFrame);
        deviceDocument = XmldeviceDocument.Factory.newInstance();
        device = deviceDocument.addNewXmldevice();
        midiDeviceProvider = TestMidiDeviceProvider.getInstance();
    }

    @Override
    public void handleDocument(String manufacturer, String deviceName) {
        device.setManufacturer(manufacturer);
        device.setName(deviceName);
        drivers = device.addNewXmldrivers();
        midiDeviceProvider.getAndClearReceivedMessages();
    }

    @Override
    public void handleParam(Xmleditor editor, SysexWidget sysexWidget,
            JPanel jPanel) {
        int numparams = editor.getNumparams();
        editor.setNumparams(numparams + 1);
        if (sysexWidget instanceof LabelWidget) {
            // Skip...
            return;
        }
        Xmlparams params = editor.getXmlparams();
        if (params == null) {
            params = editor.addNewXmlparams();
        }

        String uniqueName = getUniqueName(sysexWidget, jPanel);
        if (!sysexWidget.isShowing()) {
            log.warn("Widget is not visible!");
            return;
        }

        if (sysexWidget instanceof EnvelopeWidget) {
            EnvelopeWidget widget = (EnvelopeWidget) sysexWidget;
            try {
                storeEnvelopeWidget(widget, params);
            } catch (NoSuchFieldException e) {
                log.warn(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                log.warn(e.getMessage(), e);
            } catch (InterruptedException e) {
                log.warn(e.getMessage(), e);
            }
            return;
        }

        Xmlparam param = params.addNewXmlparam();
        int max = sysexWidget.getValueMax();
        int min = sysexWidget.getValueMin();

        param.setMin(min);
        param.setMax(max);
        param.setLabel(uniqueName);

        log.info("Saving param " + uniqueName + " -> "
                + sysexWidget.getClass().getName());

        if (sysexWidget instanceof CheckBoxWidget) {
            CheckBoxWidget widget = (CheckBoxWidget) sysexWidget;
            storeCheckBoxWidget(widget, param, min, max);
        } else if (sysexWidget instanceof ComboBoxWidget) {
            ComboBoxWidget widget = (ComboBoxWidget) sysexWidget;

            param.setType(Xmlparam.Type.COMBO_BOX_WIDGET);

            final JComboBoxFixture fixture =
                    new JComboBoxFixture(testFrame.robot, widget.cb);

            storeComboBoxWidget(fixture, param, min, max);
        } else if (sysexWidget instanceof IdComboWidget) {
            IdComboWidget widget = (IdComboWidget) sysexWidget;
            param.setType(Xmlparam.Type.COMBO_BOX_WIDGET);

            try {
                JComboBox cb = getField("cb", JComboBox.class, widget);
                final JComboBoxFixture fixture =
                        new JComboBoxFixture(testFrame.robot, cb);

                storeComboBoxWidget(fixture, param, min, max);
            } catch (NoSuchFieldException e) {
                log.warn(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                log.warn(e.getMessage(), e);
            }
        } else if (sysexWidget instanceof KnobWidget) {
            KnobWidget widget = (KnobWidget) sysexWidget;
            storeKnobWidget(widget, param, min, max);
        } else if (sysexWidget instanceof LabelWidget) {
            log.info("LabelWidget");
        } else if (sysexWidget instanceof PatchNameWidget) {
            PatchNameWidget widget = (PatchNameWidget) sysexWidget;
            storePatchNameWidget(widget, param);
        } else if (sysexWidget instanceof ScrollBarWidget) {
            ScrollBarWidget widget = (ScrollBarWidget) sysexWidget;
            param.setType(Xmlparam.Type.SCROLL_BAR_WIDGET);

            final JSliderFixture fixture =
                    new JSliderFixture(testFrame.robot, widget.slider);

            storeSliderWidget(fixture, param, min, max);
        } else if (sysexWidget instanceof SpinnerWidget) {
            SpinnerWidget widget = (SpinnerWidget) sysexWidget;
            storeSpinnerWidget(widget, param, min, max);
        } else if (sysexWidget instanceof TreeWidget) {
            TreeWidget widget = (TreeWidget) sysexWidget;
            JTreeFixture fixture =
                    new JTreeFixture(testFrame.robot, widget.tree);
            storeTreeWidget(fixture, param);
        } else if (sysexWidget instanceof ScrollBarLookupWidget) {
            ScrollBarLookupWidget widget = (ScrollBarLookupWidget) sysexWidget;
            param.setType(Xmlparam.Type.SCROLL_BAR_LOOKUP_WIDGET);

            final JSliderFixture fixture =
                    new JSliderFixture(testFrame.robot, widget.slider);

            storeSliderWidget(fixture, param, min, max);
        } else {
            log.warn("Could not handle widget "
                    + sysexWidget.getClass().getName());
            System.exit(0);
        }
    }

    @Override
    public void saveDocument() throws IOException {
        deviceDocument.save(outputFile);
    }

    @Override
    public Xmleditor handleEditor(Xmldriver driver, boolean editable,
            String editorName, List<PopupContainer> popups) {
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

    boolean handleDisabled(Component component, Xmlparam param) {
        if (!component.isEnabled()) {
            param.setType(Xmlparam.Type.DISABLED_WIDGET);
            log.warn("Disabled widget: " + param.getLabel());
            return true;
        }
        return false;
    }

    void storeTreeWidget(final JTreeFixture fixture, Xmlparam param) {
        if (handleDisabled(fixture.target, param)) {
            return;
        }

        param.setType(Xmlparam.Type.TREE_WIDGET);
        Xmlmessages messages = param.addNewXmlmessages();
        TreeModel model = fixture.target.getModel();
        fixture.separator(";");
        Object root = model.getRoot();
        String parentPath = root.toString();
        storeTreeWidgetRecursive(fixture, root, parentPath, messages);
    }

    void storeTreeWidgetRecursive(JTreeFixture fixture, Object node,
            String parentPath, Xmlmessages messages) {
        TreeModel model = fixture.target.getModel();
        int childCount = model.getChildCount(node);
        int i = 0;
        while (i < childCount) {
            Object child = model.getChild(node, i);
            String childPath =
                    parentPath + fixture.separator() + child.toString();
            if (model.isLeaf(child)) {
                fixture.selectPath(childPath);
                fixture.clickPath(childPath);
                String sysex = midiDeviceProvider.getAndClearReceivedMessages();
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
        if (handleDisabled(fixture.target, param)) {
            return;
        }
        int value = fixture.target.getValue();
        storeMessages(param, new SliderValueSetter(fixture), value, min, max);
    }

    void storeComboBoxWidget(final JComboBoxFixture fixture, Xmlparam param,
            int min, int max) {
        if (handleDisabled(fixture.target, param)) {
            return;
        }
        int value = fixture.target.getSelectedIndex();
        storeMessages(param, new ComboBoxValueSetter(fixture), value, min, max);
    }

    void storeCheckBoxWidget(CheckBoxWidget widget, Xmlparam param,
            final int min, int max) {
        param.setType(Xmlparam.Type.CHECK_BOX_WIDGET);

        final JCheckBoxFixture fixture =
                new JCheckBoxFixture(testFrame.robot, widget.cb);
        if (handleDisabled(widget, param)) {
            return;
        }
        int value = fixture.target.isSelected() ? max : min;
        storeMessages(param, new CheckBoxValueSetter(fixture, min), value, min,
                max);
    }

    void storePatchNameWidget(PatchNameWidget widget, Xmlparam param) {
        String[] values = {
                "a", "A", "Apa", "apa", "Text T", "t t tt" };
        param.setType(Xmlparam.Type.PATCH_NAME_WIDGET);

        final JTextComponentFixture fixture =
                new JTextComponentFixture(testFrame.robot, widget.name);
        if (handleDisabled(widget, param)) {
            return;
        }
        fixture.deleteText();
        IClickable clickable = getClickableParentRecursive(widget.getParent());
        clickable.click();
        midiDeviceProvider.getAndClearReceivedMessages();

        Xmlmessages messages = param.addNewXmlmessages();

        for (String value : values) {
            fixture.setText(value);
            clickable.click();
            String sysex = midiDeviceProvider.getAndClearReceivedMessages();
            Xmlmessage message = messages.addNewXmlmessage();
            message.setSysex(sysex);
            Xmlinput input = message.addNewXmlinput();
            input.setString(value);
        }
    }

    void storeSpinnerWidget(SpinnerWidget widget, Xmlparam param, int min,
            int max) {
        param.setType(Xmlparam.Type.SPINNER_WIDGET);

        final JSpinnerFixture fixture =
                new JSpinnerFixture(testFrame.robot, widget.spinner);
        if (handleDisabled(widget, param)) {
            return;
        }
        Integer value = (Integer) fixture.target.getValue();
        storeMessages(param, new SpinnerValueSetter(fixture), value, min, max);
    }

    void storeKnobWidget(final KnobWidget widget, Xmlparam param, int min,
            int max) {
        param.setType(Xmlparam.Type.KNOB_WIDGET);

        int value = widget.getValue();
        if (handleDisabled(widget, param)) {
            return;
        }
        storeMessages(param, new KnobValueSetter(widget), value, min, max);
    }

    void storeEnvelopeWidget(final EnvelopeWidget widget, Xmlparams params)
            throws NoSuchFieldException, IllegalAccessException,
            InterruptedException {
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

                storeMessages(paramX, new EnvelopeValueSetter(widget, faderX),
                        valueX, minX, maxX);
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

                storeMessages(paramY, new EnvelopeValueSetter(widget, faderY),
                        valueY, minY, maxY);
            }
        }
    }

    void storeMessages(Xmlparam param, IValueSetter setter, int value, int min,
            int max) {
        Xmlmessages messages = param.addNewXmlmessages();
        if (value == min) {
            setter.setValue(max);
            midiDeviceProvider.getAndClearReceivedMessages();
        }

        int incr = (max / 4);
        if (incr == 0 || incr == 1) {
            incr++;
        }
        for (int i = min; i <= max; i += incr) {
            if (i > max) {
                i = max;
            }
            log.debug("Sliding to: " + i);
            setter.setValue(i);
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
            }
            String sysex = midiDeviceProvider.getAndClearReceivedMessages();
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
    public void handlePatch(Xmleditor editor, JTableFixture table) {
        String[][] contents = table.contents();
        Xmlpatches patches = editor.addNewXmlpatches();
        patches.setNumCols(contents[0].length);
        patches.setNumRows(contents.length);

        for (int i = 0; i < contents.length; i++) {
            for (int j = 0; j < contents[i].length; j++) {
                Xmlpatch patch = patches.addNewXmlpatch();
                String name = (String) table.target.getModel().getValueAt(i, j);
                patch.setName(name);
                List<PopupContainer> popups = guiHandler.sendPatch(table, j, i);
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

                String sysex = midiDeviceProvider.getAndClearReceivedMessages();
                patch.setSendSysex(sysex);
            }
        }
    }

    @Override
    public void handleStore(Xmldriver driver, JTableFixture table,
            Map<String, List<String>> bankMap) {
        Iterator<Entry<String, List<String>>> iterator =
                bankMap.entrySet().iterator();
        if (bankMap.isEmpty()) {
            log.info("No banks to store...");
            return;
        }
        log.debug("Storing patch for driver " + driver.getName());
        Xmlstores xmlstores = driver.addNewXmlstores();
        while (iterator.hasNext()) {
            Entry<String, List<String>> entry = iterator.next();
            String bank = entry.getKey();
            List<String> patchNumList = entry.getValue();

            if (patchNumList.isEmpty()) {
                Xmlstore xmlstore = xmlstores.addNewXmlstore();
                xmlstore.setBank(bank);
                xmlstore.setPatch(null);
                List<PopupContainer> popupList = guiHandler.storePatch(table, bank, null);
                if (!popupList.isEmpty()) {
                    XmlPopups xmlPopups = xmlstore.addNewXmlPopups();
                    for (PopupContainer popupContainer : popupList) {
                        XmlPopup xmlPopup = xmlPopups.addNewXmlPopup();
                        xmlPopup.setTitle(popupContainer.getTitle());
                        xmlPopup.setContent(popupContainer.getContents());
                    }
                }
                String sysex = midiDeviceProvider.getAndClearReceivedMessages();
                xmlstore.setSysex(sysex);
            } else {
                int incr = (patchNumList.size() / 4) + 1;
                for (int i = 0; i < patchNumList.size(); i += incr) {
                    Xmlstore xmlstore = xmlstores.addNewXmlstore();
                    xmlstore.setBank(bank);
                    String patchNum = patchNumList.get(i);
                    xmlstore.setPatch(patchNum);
                    guiHandler.storePatch(table, bank, patchNum);
                    String sysex =
                            midiDeviceProvider.getAndClearReceivedMessages();
                    xmlstore.setSysex(sysex);
                }
            }
        }
    }

    @Override
    public Xmldriver handleDriver(String driverName) {
        Xmldriver driver = drivers.addNewXmldriver();
        driver.setName(driverName);
        return driver;
    }
}
