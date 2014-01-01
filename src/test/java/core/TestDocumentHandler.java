package core;

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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JCheckBoxFixture;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JSliderFixture;
import org.fest.swing.fixture.JSpinnerFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.fixture.JTextComponentFixture;
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
import org.jsynthlib.midi.TestMidiDeviceProvider;

import synthdrivers.YamahaUB99.IdComboWidget;
import core.EnvelopeWidget.Node;
import core.SysexWidget.IParamModel;
import core.valuesetter.CheckBoxValueSetter;
import core.valuesetter.ComboBoxValueSetter;
import core.valuesetter.IValueSetter;
import core.valuesetter.KnobValueSetter;
import core.valuesetter.SpinnerValueSetter;

public class TestDocumentHandler extends AbstractDocumentHandler {

    private XmldeviceDocument deviceDocument;
    private Xmldevice device;
    private TestMidiDeviceProvider midiDeviceProvider;

    private Map<Xmldriver, Set<Xmlparam>> testedItems;
    private int envelopeIndex;
    private HashMap<String, Integer> editorParamsMap;

    TestDocumentHandler(File outputFile, FrameFixture testFrame)
            throws XmlException, IOException {
        super(outputFile, testFrame);
        log.info("Using file " + outputFile.getAbsolutePath());
        deviceDocument = XmldeviceDocument.Factory.parse(outputFile);
        device = deviceDocument.getXmldevice();
        midiDeviceProvider = TestMidiDeviceProvider.getInstance();
        testedItems = new HashMap<Xmldriver, Set<Xmlparam>>();
    }

    @Override
    public void handleDocument(String manufacturer, String deviceName) {
        assertEquals("Check manufacturer " + deviceName,
                device.getManufacturer(), manufacturer);
        assertEquals("Check device " + deviceName, device.getName(), deviceName);
        midiDeviceProvider.getAndClearReceivedMessages();
        editorParamsMap = new HashMap<String, Integer>();
    }

    @Override
    public void handleParam(Xmleditor editor, SysexWidget sysexWidget,
            JPanel jPanel) {
        XmlCursor cursor = editor.newCursor();
        cursor.toParent();
        Xmldriver driver = (Xmldriver) cursor.getObject();
        cursor.dispose();
        Integer numParams = editorParamsMap.get(editor.getName());
        if (numParams == null) {
            numParams = new Integer(1);
            editorParamsMap.put(editor.getName(), numParams);
        } else {
            editorParamsMap.put(editor.getName(), new Integer(numParams + 1));
        }

        if (sysexWidget instanceof LabelWidget) {
            // Skip...
            return;
        }

        Xmlparams params = editor.getXmlparams();

        assertTrue("Check driver " + driver.getName(),
                testedItems.containsKey(driver));
        Set<Xmlparam> testedParams = testedItems.get(driver);
        String uniqueName = getUniqueName(sysexWidget, jPanel);
        if (!sysexWidget.isShowing()) {
            Xmlparam param = getParamByLabel(params, uniqueName);
            assertNull("Check param exists " + uniqueName, param);
            return;
        }

        if (sysexWidget instanceof EnvelopeWidget) {
            EnvelopeWidget envWidget = (EnvelopeWidget) sysexWidget;
            XmlenvelopeParam[] xmlenvelopeParamArray =
                    params.getXmlenvelopeParamArray();
            XmlenvelopeParam xmlenvelopeParam =
                    xmlenvelopeParamArray[envelopeIndex];
            try {
                verifyEnvelopeWidget(envWidget, xmlenvelopeParam);
            } catch (NoSuchFieldException e) {
                fail("Check param class " + uniqueName + ": " + e.getMessage());
            } catch (IllegalAccessException e) {
                fail("Check param class " + uniqueName + ": " + e.getMessage());
            } catch (InterruptedException e) {
                fail("Check param class " + uniqueName + ": " + e.getMessage());
            }
            envelopeIndex++;
            return;
        }

        final Xmlparam param = getParamByLabel(params, uniqueName);
        log.info("Testing param " + uniqueName);
        assertNotNull("Testing param " + uniqueName, param);
        assertFalse("Check param exists: " + uniqueName,
                testedParams.contains(param));
        testedParams.add(param);
        assertEquals("Check param max: " + uniqueName, param.getMax(),
                sysexWidget.getValueMax());
        assertEquals("Check param min: " + uniqueName, param.getMin(),
                sysexWidget.getValueMin());

        switch (param.getType().intValue()) {
        case Xmlparam.Type.INT_DISABLED_WIDGET:
            assertFalse("Check param class " + uniqueName,
                    sysexWidget.isEnabled());
            break;
        case Xmlparam.Type.INT_TREE_WIDGET:
            break;
        case Xmlparam.Type.INT_CHECK_BOX_WIDGET:
            if (!(sysexWidget instanceof CheckBoxWidget)) {
                fail("Check param class " + uniqueName + ": "
                        + sysexWidget.getClass().getName()
                        + " is not subclass to "
                        + CheckBoxWidget.class.getName());
            }
            CheckBoxWidget cbWidget = (CheckBoxWidget) sysexWidget;

            final JCheckBoxFixture cbFixture =
                    new JCheckBoxFixture(testFrame.robot, cbWidget.cb);
            int value =
                    cbFixture.target.isSelected() ? param.getMax() : param
                            .getMin();
            testMessages(param,
                    new CheckBoxValueSetter(cbFixture, param.getMin()), value,
                    param.getMin(), param.getMax());
            break;
        case Xmlparam.Type.INT_COMBO_BOX_WIDGET:
            if (!(sysexWidget instanceof ComboBoxWidget)) {
                fail("Check param class " + uniqueName + ": "
                        + sysexWidget.getClass().getName()
                        + " is not subclass to "
                        + ComboBoxWidget.class.getName());
            }
            ComboBoxWidget comboWidget = (ComboBoxWidget) sysexWidget;
            verifyComboboxWidget(new JComboBoxFixture(testFrame.robot,
                    comboWidget.cb), param);
            break;
        case Xmlparam.Type.INT_UB_99_ID_COMBO_WIDGET:
            assertEquals("Check param class " + uniqueName,
                    IdComboWidget.class, sysexWidget.getClass());
            try {
                IdComboWidget idComboWidget = (IdComboWidget) sysexWidget;
                JComboBox cb = getField("cb", JComboBox.class, idComboWidget);
                verifyComboboxWidget(new JComboBoxFixture(testFrame.robot, cb),
                        param);
            } catch (IllegalAccessException e1) {
                fail("Check param class " + uniqueName + ": " + e1.getMessage());
            } catch (NoSuchFieldException e1) {
                fail("Check param class " + uniqueName + ": " + e1.getMessage());
            }
            break;
        case Xmlparam.Type.INT_KNOB_WIDGET:
            if (!(sysexWidget instanceof KnobWidget)) {
                fail("Check param class " + uniqueName + ": "
                        + sysexWidget.getClass().getName() + " is not a "
                        + KnobWidget.class.getName());
            }
            final KnobWidget kWidget = (KnobWidget) sysexWidget;
            value = kWidget.getValue();

            testMessages(param, new KnobValueSetter(kWidget), value,
                    param.getMin(), param.getMax());
            break;
        case Xmlparam.Type.INT_PATCH_NAME_WIDGET:
            if (!(sysexWidget instanceof PatchNameWidget)) {
                fail("Check param class " + uniqueName + ": "
                        + sysexWidget.getClass().getName()
                        + " is not subclass to "
                        + PatchNameWidget.class.getName());
            }
            PatchNameWidget pnWidget = (PatchNameWidget) sysexWidget;
            verifyPatchNameWidget(pnWidget, param);
            break;
        case Xmlparam.Type.INT_SCROLL_BAR_WIDGET:
            if (!(sysexWidget instanceof ScrollBarWidget)) {
                fail("Check param class " + uniqueName + ": "
                        + sysexWidget.getClass().getName()
                        + " is not subclass to "
                        + ScrollBarWidget.class.getName());
            }
            ScrollBarWidget sbWidget = (ScrollBarWidget) sysexWidget;
            verifyScrollbarWidget(new JSliderFixture(testFrame.robot,
                    sbWidget.slider), param);
            break;
        case Xmlparam.Type.INT_SCROLL_BAR_LOOKUP_WIDGET:
            if (!(sysexWidget instanceof ScrollBarLookupWidget)) {
                fail("Check param class " + uniqueName + ": "
                        + sysexWidget.getClass().getName()
                        + " is not subclass to "
                        + ScrollBarLookupWidget.class.getName());
            }
            ScrollBarLookupWidget sblWidget =
                    (ScrollBarLookupWidget) sysexWidget;
            verifyScrollbarWidget(new JSliderFixture(testFrame.robot,
                    sblWidget.slider), param);
            break;
        case Xmlparam.Type.INT_SPINNER_WIDGET:
            assertEquals("Check param class " + uniqueName,
                    SpinnerWidget.class, sysexWidget.getClass());
            SpinnerWidget sWidget = (SpinnerWidget) sysexWidget;
            final JSpinnerFixture sFixture =
                    new JSpinnerFixture(testFrame.robot, sWidget.spinner);

            value = (Integer) sFixture.target.getValue();
            testMessages(param, new SpinnerValueSetter(sFixture), value,
                    param.getMin(), param.getMax());
            break;
        default:
            fail("Weird widget type " + uniqueName);
            break;
        }
    }

    Xmlparam getParamByLabel(Xmlparams params, String label) {
        Xmlparam[] paramArray = params.getXmlparamArray();
        for (final Xmlparam param : paramArray) {
            if (param.getLabel().equals(label)) {
                return param;
            }
        }
        return null;
    }

    void verifyComboboxWidget(final JComboBoxFixture comboFixture,
            Xmlparam param) {
        int value = comboFixture.target.getSelectedIndex();
        testMessages(param, new ComboBoxValueSetter(comboFixture), value,
                param.getMin(), param.getMax());
    }

    void verifyScrollbarWidget(final JSliderFixture fixture, Xmlparam param) {
        int value = fixture.target.getValue();
        testMessages(param, new SliderValueSetter(fixture), value,
                param.getMin(), param.getMax());
    }

    void verifyPatchNameWidget(PatchNameWidget pnWidget, Xmlparam param) {
        final JTextComponentFixture fixture =
                new JTextComponentFixture(testFrame.robot, pnWidget.name);
        fixture.deleteText();
        IClickable clickable =
                getClickableParentRecursive(pnWidget.getParent());
        clickable.click();
        midiDeviceProvider.getAndClearReceivedMessages();

        Xmlmessages messages = param.getXmlmessages();
        Xmlmessage[] messageArray = messages.getXmlmessageArray();
        for (Xmlmessage message : messageArray) {
            String string = message.getXmlinput().getString();
            fixture.setText(string);
            clickable.click();
            String sysex = midiDeviceProvider.getAndClearReceivedMessages();
            assertEquals("Check param sysex: " + param.getLabel(),
                    message.getSysex(), sysex);
        }
    }

    void verifyEnvelopeWidget(final EnvelopeWidget widget,
            XmlenvelopeParam xmlenvelopeParam) throws NoSuchFieldException,
            IllegalAccessException, InterruptedException {
        Xmlparam[] xmlparamArray = xmlenvelopeParam.getXmlparamArray();
        Node[] nodes = widget.nodes;
        int numFaders = 0;
        for (int i = 0; i < nodes.length; i++) {
            Node node = nodes[i];

            final IParamModel modelX = getNodeParamModel(node, true);
            if (modelX != null) {
                Xmlparam paramX = xmlparamArray[numFaders];
                final int faderX = widget.getSliderNum() + numFaders++;
                int minX = getField("minX", Integer.class, node);
                int maxX = getField("maxX", Integer.class, node);

                int valueX = modelX.get();

                assertEquals("Check paramX label: " + paramX.getLabel(),
                        paramX.getLabel(), getNodeName(node, true));
                assertEquals("Check paramX max: " + paramX.getLabel(),
                        paramX.getMax(), maxX);
                assertEquals("Check paramX min: " + paramX.getLabel(),
                        paramX.getMin(), minX);

                testMessages(paramX, new EnvelopeValueSetter(widget, faderX),
                        valueX, minX, maxX);
            }

            final IParamModel modelY = getNodeParamModel(node, false);
            if (modelY != null) {
                Xmlparam paramY = xmlparamArray[numFaders];
                final int faderY = widget.getSliderNum() + numFaders++;
                int minY = getField("minY", Integer.class, node);
                int maxY = getField("maxY", Integer.class, node);
                int valueY = modelY.get();

                assertEquals("Check paramY label: " + paramY.getLabel(),
                        paramY.getLabel(), getNodeName(node, false));
                assertEquals("Check paramY max: " + paramY.getLabel(),
                        paramY.getMax(), maxY);
                assertEquals("Check paramY min: " + paramY.getLabel(),
                        paramY.getMin(), minY);

                testMessages(paramY, new EnvelopeValueSetter(widget, faderY),
                        valueY, minY, maxY);
            }
        }
        assertEquals("Check param array length", xmlparamArray.length,
                numFaders);
    }

    void testMessages(Xmlparam param, IValueSetter setter, int value, int min,
            int max) {
        if (value == min) {
            setter.setValue(max);
            midiDeviceProvider.getAndClearReceivedMessages();
        }

        Xmlmessages messages = param.getXmlmessages();
        for (Xmlmessage message : messages.getXmlmessageArray()) {
            setter.setValue(message.getXmlinput().getValue());
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
            }
            String sysex = midiDeviceProvider.getAndClearReceivedMessages();
            assertEquals("Check param " + param.getContainerName() + "/"
                    + param.getLabel() + " sysex", message.getSysex(), sysex);
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
                    Integer integer = editorParamsMap.get(editor.getName());
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
    public Xmleditor handleEditor(Xmldriver driver, boolean editable,
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
                    editor.getName(), editorName);
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
    public void handlePatch(Xmleditor editor, JTableFixture table) {
        String[][] contents = table.contents();
        Xmlpatches patches = editor.getXmlpatches();
        assertEquals("Check " + editor.getName() + " patch cols",
                patches.getNumCols(), contents[0].length);
        assertEquals("Check " + editor.getName() + " patch rows",
                patches.getNumRows(), contents.length);

        Xmlpatch[] patchArray = patches.getXmlpatchArray();
        int k = 0;
        for (int i = 0; i < contents.length; i++) {
            for (int j = 0; j < contents[i].length; j++) {
                Xmlpatch patch = patchArray[k];
                if (patch.getSendSysex() == null) {
                    continue;
                }

                String name = (String) table.target.getModel().getValueAt(i, j);
                assertEquals("Check " + editor.getName() + " patch name",
                        patch.getName(), name);
                List<PopupContainer> popups = guiHandler.sendPatch(table, j, i);
                XmlPopups xmlPopups = patch.getXmlPopups();
                if (xmlPopups == null) {
                    assertTrue("Num popups check", popups.isEmpty());
                } else {
                    XmlPopup[] popupArray = xmlPopups.getXmlPopupArray();
                    assertEquals("Num popups check", popupArray.length,
                            popups.size());
                    for (int l = 0; l < popupArray.length; l++) {
                        XmlPopup xmlPopup = popupArray[l];
                        PopupContainer popupContainer = popups.get(l);
                        assertEquals("Popup title check", xmlPopup.getTitle(),
                                popupContainer.getTitle());
                        assertEquals("Popup content check",
                                xmlPopup.getContent(),
                                popupContainer.getContents());
                    }
                }

                String sysex = midiDeviceProvider.getAndClearReceivedMessages();
                assertEquals("Check " + editor.getName() + " patch sysex",
                        patch.getSendSysex(), sysex);
                // assertFalse("Check " + editor.getName() + " sysex not empty",
                // sysex.isEmpty());
                log.info("Sysex for col " + j + " row " + i + " matched");
                k++;
            }
        }
    }

    @Override
    public void handleStore(Xmldriver driver, JTableFixture table,
            Map<String, List<String>> bankMap) {
        Xmlstores xmlstores = driver.getXmlstores();
        Xmlstore[] xmlstoreArray = xmlstores.getXmlstoreArray();
        Set<String> testedBanks = new HashSet<String>();
        for (Xmlstore xmlstore : xmlstoreArray) {
            String bank = xmlstore.getBank();
            assertTrue(
                    "Bank " + bank + " is in bank map for driver "
                            + driver.getName(), bankMap.containsKey(bank));
            testedBanks.add(bank);
            List<String> patchNumList = bankMap.get(bank);
            String patch = xmlstore.getPatch();
            assertTrue("Patch num " + patch + " is in bank list for driver "
                    + driver.getName(), patchNumList.contains(patch));
            guiHandler.storePatch(table, bank, patch);
            String sysex = midiDeviceProvider.getAndClearReceivedMessages();
            assertEquals("Sysex check for bank " + bank + " and patch " + patch
                    + " with driver " + driver.getName(), xmlstore.getSysex(),
                    sysex);
        }
        assertEquals(testedBanks, bankMap.keySet());
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
}
