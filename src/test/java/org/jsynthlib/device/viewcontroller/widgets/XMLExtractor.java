package org.jsynthlib.device.viewcontroller.widgets;

import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlString;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTableFixture;
import org.jsynthlib.core.ContainerDisplayer;
import org.jsynthlib.core.GuiHandler;
import org.jsynthlib.core.PatchEditorTest;
import org.jsynthlib.core.PopupContainer;
import org.jsynthlib.core.PopupListener;
import org.jsynthlib.core.SysexWidgetFinder;
import org.jsynthlib.core.TitleFinder.FrameWrapper;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.core.viewcontroller.desktop.mdi.MDIFrameProxy;
import org.jsynthlib.device.model.AbstractBankDriver;
import org.jsynthlib.device.model.AbstractDriver;
import org.jsynthlib.device.model.AbstractSender;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.IBankDriver;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.device.model.IParamModel;
import org.jsynthlib.device.model.IPatchDriver;
import org.jsynthlib.device.model.IPatchStringSender;
import org.jsynthlib.device.model.ISender;
import org.jsynthlib.device.viewcontroller.BankEditorFrame;
import org.jsynthlib.device.viewcontroller.PatchEditorFrame;
import org.jsynthlib.device.viewcontroller.widgets.EnvelopeWidget.Node;
import org.jsynthlib.patch.model.impl.PatchEdit;
import org.jsynthlib.xmldevice.EnvelopeNodeSpec;
import org.jsynthlib.xmldevice.EnvelopeSpec;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.MidiSender;
import org.jsynthlib.xmldevice.ParamModel;
import org.jsynthlib.xmldevice.PatchParamGroup;
import org.jsynthlib.xmldevice.PatchParamValues;
import org.jsynthlib.xmldevice.PatchParams;
import org.jsynthlib.xmldevice.PropertySpec;
import org.jsynthlib.xmldevice.StringArray;
import org.jsynthlib.xmldevice.StringParamSpec;
import org.jsynthlib.xmldevice.StringSenderSpec;
import org.jsynthlib.xmldevice.XEnvelopeParamSpec;
import org.jsynthlib.xmldevice.XmlBankDriverSpecDocument;
import org.jsynthlib.xmldevice.XmlBankDriverSpecDocument.XmlBankDriverSpec;
import org.jsynthlib.xmldevice.XmlDeviceSpecDocument;
import org.jsynthlib.xmldevice.XmlDeviceSpecDocument.XmlDeviceSpec;
import org.jsynthlib.xmldevice.XmlDriverDefs;
import org.jsynthlib.xmldevice.XmlDriverDefs.XmlDriverDef;
import org.jsynthlib.xmldevice.XmlDriverSpec;
import org.jsynthlib.xmldevice.XmlPatchDriverSpecDocument;
import org.jsynthlib.xmldevice.XmlPatchDriverSpecDocument.XmlPatchDriverSpec;
import org.jsynthlib.xmldevice.YEnvelopeParamSpec;

public class XMLExtractor {

    public static void main(String[] args) {
        try {
            String manufacturer = System.getProperty("manufacturer");
            String device = System.getProperty("device");
            XMLExtractor extractor = new XMLExtractor(manufacturer, device);
            extractor.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private final transient Logger log = Logger.getLogger(getClass());

    private FrameFixture testFrame;
    private GuiHandler guiHandler;

    private final HashMap<String, PatchParams> groupMap;

    private final String manufacturer;

    private final String deviceName;

    private XmlDeviceSpecDocument deviceSpecDocument;

    private XmlDeviceSpec deviceSpec;

    XMLExtractor(String manufacturer, String device) {
        groupMap = new HashMap<String, PatchParams>();
        this.manufacturer = manufacturer;
        this.deviceName = device;
    }

    void run() throws Exception {
        PatchEditorTest.setUpOnce();
        testFrame = new FrameFixture(PatchEdit.getInstance());
        testFrame.show();
        testFrame.resizeHeightTo(600);
        testFrame.resizeWidthTo(800);
        // testFrame.maximize();
        guiHandler = new GuiHandler(testFrame);

        // uninstall all previously installed drivers.
        guiHandler.uninstallDevice(null);

        log.info("Installing " + manufacturer + "/" + deviceName);
        String infoText = guiHandler.installDevice(manufacturer, deviceName);

        FrameWrapper library = guiHandler.openLibrary();
        JTableFixture table = library.table();

        log.info("Receiving drivers for device " + deviceName);
        List<Class<? extends IDriver>> driversForDevice =
                guiHandler.getDriversForDevice(deviceName);

        boolean deviceCreated = false;
        try {
            for (Class<? extends IDriver> driverClass : driversForDevice) {
                StringBuilder popupBuilder = new StringBuilder();

                log.info("Using driver: " + driverClass.getName());
                PopupListener driverPopupListener = new PopupListener();
                guiHandler.newPatch(library, deviceName, driverClass,
                        driverPopupListener);
                List<PopupContainer> driverPopups =
                        driverPopupListener.getPopups();
                for (PopupContainer popup : driverPopups) {
                    popupBuilder.append(popup.getContents());
                    popupBuilder.append("\n\n--------------------------\n\n");
                }

                log.info("Opening patch editor");
                PopupListener editorPopupListener = new PopupListener();
                FrameWrapper patchEditor =
                        guiHandler.openPatchEditor(table, -1, 0,
                                editorPopupListener, true);
                List<PopupContainer> editorPopups =
                        editorPopupListener.getPopups();
                for (PopupContainer popup : editorPopups) {
                    popupBuilder.append(popup.getContents());
                    popupBuilder.append("\n\n--------------------------\n\n");
                }

                try {
                    if (patchEditor == null) {
                        // No editor at all showed up.
                        continue;
                    }

                    Container component = patchEditor.component();
                    if (component instanceof MDIFrameProxy) {
                        MDIFrameProxy proxy = (MDIFrameProxy) component;
                        JSLFrame jslFrame = proxy.getJSLFrame();
                        if (jslFrame instanceof PatchEditorFrame) {
                            PatchEditorFrame frame =
                                    (PatchEditorFrame) jslFrame;
                            IDriver driver = frame.getPatch().getDriver();
                            if (!deviceCreated) {
                                createDevice(driver.getDevice(), infoText);
                                deviceCreated = true;
                            }
                            createPatchDriver(patchEditor, infoText,
                                    (IPatchDriver) driver);
                        } else if (jslFrame instanceof BankEditorFrame) {
                            BankEditorFrame frame = (BankEditorFrame) jslFrame;
                            IDriver driver = frame.getBankData().getDriver();
                            if (!deviceCreated) {
                                createDevice(driver.getDevice(), infoText);
                                deviceCreated = true;
                            }
                            createBankDriver(infoText, patchEditor,
                                    (IBankDriver) driver);
                        }
                    }

                } finally {
                    if (patchEditor != null) {
                        log.info("Close patch editor frame");
                        guiHandler.closeFrame(patchEditor, false);
                    }

                    if (library != null) {
                        log.info("Selecting library frame");
                        guiHandler.selectLibraryFrame(library);
                    }
                }
            }
        } finally {
            deviceSpecDocument.save(new File(deviceName + ".xml"));

            if (library != null) {
                log.info("Closing library");
                guiHandler.closeLibrary(library);
            }

            log.info("Uninstall device " + deviceName);
            guiHandler.uninstallDevice(deviceName);

            testFrame.cleanUp();
        }
    }

    void createBankDriver(String infoText, FrameWrapper patchEditor,
            IBankDriver driver) throws IllegalAccessException, NoSuchFieldException, IOException {
        XmlBankDriverSpecDocument document =
                XmlBankDriverSpecDocument.Factory.newInstance();
        XmlBankDriverSpec driverSpec = document.addNewXmlBankDriverSpec();

        XmlDriverDefs drivers = deviceSpec.getDrivers();
        XmlDriverDef driverDef = drivers.addNewXmlDriverDef();
        driverDef.setDriverClass(driver.getClass().getName());
        driverDef.setDriverType(XmlDriverDef.DriverType.BANK);

        addGenericFields(driver, driverSpec, infoText);

        if (driver instanceof AbstractBankDriver) {
            AbstractBankDriver bankDriver = (AbstractBankDriver) driver;
            driverSpec.setNumPatches(getField("numPatches", int.class, bankDriver,
                    AbstractBankDriver.class));
            driverSpec.setNumColumns(getField("numColumns", int.class, bankDriver,
                    AbstractBankDriver.class));
            driverSpec.setSingleSysexID(getField("singleSysexID", String.class, bankDriver,
                    AbstractBankDriver.class));
            driverSpec.setSingleSize(getField("singleSize", int.class, bankDriver,
                    AbstractBankDriver.class));
        }

        document.save(new File(driver.getClass().getSimpleName() + ".xml"));
    }

    void createPatchDriver(FrameWrapper patchEditor, String infoText,
            IPatchDriver driver) throws IllegalAccessException,
            NoSuchFieldException, IOException {
        XmlPatchDriverSpecDocument document =
                XmlPatchDriverSpecDocument.Factory.newInstance();
        XmlPatchDriverSpec driverSpec = document.addNewXmlPatchDriverSpec();

        XmlDriverDefs drivers = deviceSpec.getDrivers();
        XmlDriverDef deviceDriverSpec = drivers.addNewXmlDriverDef();
        deviceDriverSpec.setDriverClass(driver.getClass().getName());
        deviceDriverSpec.setDriverType(XmlDriverDef.DriverType.PATCH);

        addGenericFields(driver, driverSpec, infoText);

        PatchParams patchParams = driverSpec.addNewPatchParams();
        groupMap.put("", patchParams);

        List<SysexWidget> sysexWidgets =
                SysexWidgetFinder.findSysexWidgets(patchEditor);
        if (sysexWidgets.size() > 0) {
            for (SysexWidget sysexWidget : sysexWidgets) {
                handleSysexWidget(patchEditor, sysexWidget);
            }
            document.save(new File(driver.getClass().getSimpleName() + ".xml"));
        }
    }

    void addGenericFields(IDriver driver, XmlDriverSpec driverSpec,
            String infoText) throws IllegalAccessException,
            NoSuchFieldException {
        driverSpec.setInfoText(infoText);
        driverSpec.setName(deviceName + " " + driver.getPatchType());
        driverSpec.setAuthors(driver.getAuthors());

        String[] origBankNumbers = driver.getBankNumbers();
        if (origBankNumbers != null) {
            StringArray bankNumbers = driverSpec.addNewBankNumbers();
            for (String bankNumber : origBankNumbers) {
                bankNumbers.addString(bankNumber);
            }
        }

        String[] origPatchNumbers = driver.getPatchNumbers();
        if (origPatchNumbers != null) {
            StringArray patchNumbers = driverSpec.addNewPatchNumbers();
            for (String patchNumber : origPatchNumbers) {
                patchNumbers.addString(patchNumber);
            }
        }

        driverSpec.setPatchNameSize(driver.getPatchNameSize());
        driverSpec.setPatchSize(driver.getPatchSize());

        if (driver instanceof AbstractDriver) {
            AbstractDriver d2 = (AbstractDriver) driver;
            driverSpec.setChecksumEnd(getField("checksumEnd", int.class, d2,
                    AbstractDriver.class));
            driverSpec.setChecksumStart(getField("checksumStart", int.class,
                    d2, AbstractDriver.class));
            driverSpec.setChecksumOffset(getField("checksumOffset", int.class,
                    d2, AbstractDriver.class));
            driverSpec.setSysexID(getField("sysexID", String.class, d2,
                    AbstractDriver.class));
            driverSpec.setDeviceIDoffset(getField("deviceIDoffset", int.class,
                    d2, AbstractDriver.class));
            driverSpec.setPatchNameStart(getField("patchNameStart", int.class,
                    d2, AbstractDriver.class));
        }

    }

    /**
     * @param device
     * @param infoText
     */
    void createDevice(Device device, String infoText) {
        deviceSpecDocument = XmlDeviceSpecDocument.Factory.newInstance();

        deviceSpec = deviceSpecDocument.addNewXmlDeviceSpec();
        deviceSpec.setAuthors(device.getAuthors());
        deviceSpec.addNewDrivers();
        deviceSpec.setInfoText(infoText);
        deviceSpec.setManufacturer(device.getManufacturerName());
        deviceSpec.setModelName(device.getModelName());
        deviceSpec.setInquiryId(device.getInquiryID());
    }

    void handleSysexWidget(FrameWrapper patchEditor, SysexWidget sysexWidget)
            throws IllegalAccessException, NoSuchFieldException {
        if (sysexWidget instanceof LabelWidget) {
            // Skip...
            return;
        }

        String path =
                ContainerDisplayer.showContainerAndGetNameRecursive(
                        patchEditor, sysexWidget);
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        PatchParams patchParams = null;
        log.info("Looking for group " + path);
        if (groupMap.containsKey(path)) {
            patchParams = groupMap.get(path);
            log.info("Found group " + path);
        } else {
            patchParams = createNewGroup(path);
        }

        int valueMax = sysexWidget.getValueMax();
        int valueMin = sysexWidget.getValueMin();
        String name = sysexWidget.getLabel();
        ISender sender =
                getField("sender", ISender.class, sysexWidget,
                        SysexWidget.class);
        IParamModel paramModel =
                getField("paramModel", IParamModel.class, sysexWidget,
                        SysexWidget.class);

        if (sysexWidget instanceof CheckBoxWidget) {
            CheckBoxWidget widget = (CheckBoxWidget) sysexWidget;
            handleDefaultIntParam(patchParams, valueMax, valueMin, name,
                    sender, paramModel, 0);
        } else if (sysexWidget instanceof ComboBoxWidget) {
            ComboBoxWidget widget = (ComboBoxWidget) sysexWidget;
            int itemCount = widget.cb.getItemCount();
            String[] values = new String[itemCount];

            for (int i = 0; i < itemCount; i++) {
                String item = (String) widget.cb.getItemAt(i);
                values[i] = item;
            }
            handleIntParamWValues(values, patchParams, valueMax, valueMin,
                    name, sender, paramModel);
        } else if (sysexWidget instanceof KnobWidget) {
            KnobWidget widget = (KnobWidget) sysexWidget;
            int base = getField("mBase", int.class, widget, KnobWidget.class);
            handleDefaultIntParam(patchParams, valueMax, valueMin, name,
                    sender, paramModel, base);
        } else if (sysexWidget instanceof PatchNameWidget) {
            PatchNameWidget widget = (PatchNameWidget) sysexWidget;
            handlePatchName(widget, patchParams, name, sender, paramModel);
        } else if (sysexWidget instanceof ScrollBarWidget) {
            ScrollBarWidget widget = (ScrollBarWidget) sysexWidget;
            int base = getField("base", int.class, widget, ScrollBarWidget.class);
            handleDefaultIntParam(patchParams, valueMax, valueMin, name,
                    sender, paramModel, base);
        } else if (sysexWidget instanceof SpinnerWidget) {
            SpinnerWidget widget = (SpinnerWidget) sysexWidget;
            int base = getField("base", int.class, widget, SpinnerWidget.class);
            handleDefaultIntParam(patchParams, valueMax, valueMin, name,
                    sender, paramModel, base);
        } else if (sysexWidget instanceof ScrollBarLookupWidget) {
            ScrollBarLookupWidget widget = (ScrollBarLookupWidget) sysexWidget;
            String[] strings =
                    getField("options", String[].class, widget,
                            ScrollBarLookupWidget.class);

            handleIntParamWValues(strings, patchParams, valueMax, valueMin,
                    name, sender, paramModel);
        } else if (sysexWidget instanceof EnvelopeWidget) {
            EnvelopeWidget envWidget = (EnvelopeWidget) sysexWidget;
            handleEnvelope(patchParams, envWidget);
        } else {
            log.warn("Could not handle widget "
                    + sysexWidget.getClass().getName());
        }
    }

    private PatchParams createNewGroup(String path) {
        log.info("Creating group " + path);
        String[] split = path.split("/");
        String tempPath = null;
        PatchParams parentGroup = null;

        for (int i = 0; i < split.length - 1; i++) {
            String groupName = split[i];
            if (tempPath == null) {
                tempPath = groupName;
            } else {
                tempPath = tempPath + "/" + groupName;
            }

            if (groupMap.containsKey(tempPath)) {
                parentGroup = groupMap.get(tempPath);
            } else {
                parentGroup = createNewGroup(tempPath);
            }
        }

        PatchParamGroup paramGroup = parentGroup.addNewPatchParamGroup();
        paramGroup.setName(split[split.length - 1]);
        groupMap.put(path, paramGroup);
        return paramGroup;
    }

    private void handlePatchName(PatchNameWidget widget,
            PatchParams patchParams, String name, ISender sender,
            IParamModel paramModel) throws IllegalAccessException,
            NoSuchFieldException {
        StringParamSpec stringParamSpec = patchParams.addNewStringParamSpec();
        stringParamSpec.setName(name);
        Integer field =
                getField("patchNameSize", int.class, widget,
                        PatchNameWidget.class);
        IPatchStringSender patchNameSender =
                getField("sender", IPatchStringSender.class, widget,
                        PatchNameWidget.class);
        stringParamSpec.setLength(field.intValue());
        stringParamSpec.setUuid(generateUuid());
        if (sender != null) {
            StringSenderSpec xmlSender = stringParamSpec.addNewStringSender();
            xmlSender.setStringSenderClass(patchNameSender.getClass().getName());
            try {
                Map<String, String> description =
                        BeanUtils.describe(patchNameSender);
                Iterator<Entry<String, String>> iterator =
                        description.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, String> entry = iterator.next();
                    PropertySpec property = xmlSender.addNewProperty();
                    property.setName(entry.getKey());
                    property.setValue(entry.getValue());
                }
            } catch (InvocationTargetException e) {
                log.warn(e.getMessage(), e);
            } catch (NoSuchMethodException e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    void handleEnvelope(PatchParams patchParams, EnvelopeWidget envWidget)
            throws IllegalAccessException, NoSuchFieldException {
        EnvelopeSpec envelopeSpec = patchParams.addNewEnvelopeSpec();
        envelopeSpec.setName(envWidget.getLabel());
        envelopeSpec.setUuid(generateUuid());

        Node[] nodes = envWidget.getNodes();
        for (Node node : nodes) {
            EnvelopeNodeSpec nodeSpec = envelopeSpec.addNewEnvelopeNodeSpec();
            XEnvelopeParamSpec xParam = nodeSpec.addNewXParam();
            YEnvelopeParamSpec yParam = nodeSpec.addNewYParam();
            xParam.setInvert(node.isInvertX());
            xParam.setMax(node.getMaxX());
            if (node.getSenderX() != null) {
                MidiSender midiSenderX = xParam.addNewMidiSender();
                setSender(midiSenderX, node.getSenderX());
            }
            xParam.setMin(node.getMinX());
            xParam.setName(node.getNameX());
            if (node.getPmodelX() != null) {
                ParamModel paramModelX = xParam.addNewParamModel();
                setParamModel(paramModelX, node.getPmodelX());
            }
            xParam.setUuid(generateUuid());

            yParam.setBase(node.getBaseY());
            yParam.setMax(node.getMaxY());
            if (node.getSenderY() != null) {
                MidiSender midiSenderY = yParam.addNewMidiSender();
                setSender(midiSenderY, node.getSenderY());
            }
            yParam.setMin(node.getMinY());
            yParam.setName(node.getNameY());
            if (node.getPmodelY() != null) {
                ParamModel paramModelY = yParam.addNewParamModel();
                setParamModel(paramModelY, node.getPmodelY());
            }
            yParam.setUuid(generateUuid());
        }
    }

    void handleDefaultIntParam(PatchParams patchParams, int valueMax,
            int valueMin, String name, ISender sender, IParamModel paramModel, int base)
            throws IllegalAccessException, NoSuchFieldException {
        IntParamSpec intParamSpec = patchParams.addNewIntParamSpec();
        intParamSpec.setMax(valueMax);
        intParamSpec.setMin(valueMin);
        intParamSpec.setName(name);
        intParamSpec.setUuid(generateUuid());
        if (base != 0) {
            intParamSpec.setBase(base);
        }
        MidiSender midiSender = intParamSpec.addNewMidiSender();
        setSender(midiSender, sender);
        ParamModel newParamModel = intParamSpec.addNewParamModel();
        setParamModel(newParamModel, paramModel);
    }

    void handleIntParamWValues(String[] values, PatchParams patchParams,
            int valueMax, int valueMin, String name, ISender sender,
            IParamModel paramModel) throws IllegalAccessException,
            NoSuchFieldException {

        IntParamSpec intParamSpec = patchParams.addNewIntParamSpec();
        intParamSpec.setMax(valueMax);
        intParamSpec.setMin(valueMin);
        intParamSpec.setName(name);
        intParamSpec.setUuid(generateUuid());
        MidiSender midiSender = intParamSpec.addNewMidiSender();
        if (sender == null) {
            log.warn("Found param w/o sender " + name);
        } else {
            setSender(midiSender, sender);
        }
        ParamModel newParamModel = intParamSpec.addNewParamModel();
        setParamModel(newParamModel, paramModel);
        PatchParamValues paramValues = intParamSpec.addNewPatchParamValues();
        for (String string : values) {
            XmlString paramValue = paramValues.addNewPatchParamValue();
            paramValue.setStringValue(string);
        }
    }

    @SuppressWarnings("unchecked")
    void setSender(MidiSender midiSenderSpec, ISender sender) {
        midiSenderSpec.setSenderClass(sender.getClass().getName());
        if (sender instanceof AbstractSender) {
            AbstractSender aSender = (AbstractSender) sender;
            PropertySpec property = midiSenderSpec.addNewProperty();
            property.setName("offset");
            property.setValue(Integer.toString(aSender.getOffset()));
        } else {
            try {
                Map<String, String> map =
                        BeanUtilsBean.getInstance().describe(sender);
                Iterator<Entry<String, String>> iterator =
                        map.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, String> entry = iterator.next();
                    if (entry.getKey().equals("class")) {
                        continue;
                    }
                    PropertySpec propertySpec = midiSenderSpec.addNewProperty();
                    propertySpec.setName(entry.getKey());
                    propertySpec.setValue(entry.getValue());
                }
            } catch (IllegalAccessException e) {
                log.warn(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                log.warn(e.getMessage(), e);
            } catch (NoSuchMethodException e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    void setParamModel(ParamModel paramModelSpec, IParamModel paramModel) {
        Class<? extends IParamModel> class1 = paramModel.getClass();
        paramModelSpec.setModelClass(class1.getName());
        if (paramModel instanceof org.jsynthlib.device.model.ParamModel) {
            org.jsynthlib.device.model.ParamModel concreteParamModel =
                    (org.jsynthlib.device.model.ParamModel) paramModel;
            PropertySpec property = paramModelSpec.addNewProperty();
            property.setName("offset");
            property.setValue(Integer.toString(concreteParamModel.getOffset()));
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T getField(String fieldName, Class<T> fieldClass,
            Object object, Class<?> objectClass) throws IllegalAccessException,
            NoSuchFieldException {
        Class<?> tmpClass = object.getClass();
        while (!tmpClass.equals(objectClass)) {
            tmpClass = tmpClass.getSuperclass();
        }
        Field f = tmpClass.getDeclaredField(fieldName);
        f.setAccessible(true);
        return (T) f.get(object);
    }

    String generateUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("\\-", "");
    }
}
