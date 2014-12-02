package org.jsynthlib.device.viewcontroller.widgets;

import java.awt.Container;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

import javax.swing.ImageIcon;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
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
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.IBankDriver;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.device.model.IPatchDriver;
import org.jsynthlib.device.model.handler.AbstractSender;
import org.jsynthlib.device.model.handler.IParamModel;
import org.jsynthlib.device.model.handler.IPatchStringSender;
import org.jsynthlib.device.model.handler.ISender;
import org.jsynthlib.device.model.handler.ParamModel;
import org.jsynthlib.device.viewcontroller.BankEditorFrame;
import org.jsynthlib.device.viewcontroller.PatchEditorFrame;
import org.jsynthlib.device.viewcontroller.widgets.EnvelopeWidget.Node;
import org.jsynthlib.patch.model.impl.PatchEdit;
import org.jsynthlib.xmldevice.DeviceConfiguration;
import org.jsynthlib.xmldevice.DeviceConfiguration.MidiSenderDefinitions;
import org.jsynthlib.xmldevice.DeviceConfiguration.ParamModelDefinitions;
import org.jsynthlib.xmldevice.EnvelopeNodeSpec;
import org.jsynthlib.xmldevice.EnvelopeSpec;
import org.jsynthlib.xmldevice.HandlerDefinitionBase;
import org.jsynthlib.xmldevice.HandlerDefinitionBase.Property;
import org.jsynthlib.xmldevice.HandlerReferenceBase;
import org.jsynthlib.xmldevice.HandlerReferenceBase.PropertyValue;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.MidiSenderDefinition;
import org.jsynthlib.xmldevice.MidiSenderReference;
import org.jsynthlib.xmldevice.ParamModelDefinition;
import org.jsynthlib.xmldevice.ParamModelReference;
import org.jsynthlib.xmldevice.PatchParamGroup;
import org.jsynthlib.xmldevice.PatchParamValues;
import org.jsynthlib.xmldevice.PatchParams;
import org.jsynthlib.xmldevice.SingleParamSpec;
import org.jsynthlib.xmldevice.StringArray;
import org.jsynthlib.xmldevice.StringParamSpec;
import org.jsynthlib.xmldevice.StringSenderReference;
import org.jsynthlib.xmldevice.UuidSingleParamSpec;
import org.jsynthlib.xmldevice.XEnvelopeParamSpec;
import org.jsynthlib.xmldevice.XmlBankDriverDefinitionDocument;
import org.jsynthlib.xmldevice.XmlBankDriverDefinitionDocument.XmlBankDriverDefinition;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;
import org.jsynthlib.xmldevice.XmlDriverDefinition;
import org.jsynthlib.xmldevice.XmlDriverReferences;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;
import org.jsynthlib.xmldevice.YEnvelopeParamSpec;

public class XMLExtractor {

    private static final Logger LOG = Logger.getLogger(XMLExtractor.class);

    public static void main(String[] args) {
        try {
            String manufacturer = System.getProperty("manufacturer");
            if (manufacturer == null) {
                manufacturer = args[0];
            }
            String device = System.getProperty("device");
            if (device == null) {
                device = args[1];
            }
            String output = System.getProperty("output");
            if (output == null) {
                output = args[2];
            }
            LOG.info("Extracting " + manufacturer + " - " + device + " into "
                    + output);
            File outDir = new File(output);
            if (!outDir.exists()) {
                boolean mkdir = outDir.mkdir();
                if (!mkdir) {
                    throw new IOException("Failed to create " + output);
                }
            }
            XMLExtractor extractor =
                    new XMLExtractor(manufacturer, device, outDir);
            extractor.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private FrameFixture testFrame;
    private GuiHandler guiHandler;

    private final String manufacturer;

    private final String deviceName;

    private XmlDeviceDefinitionDocument deviceSpecDocument;

    private XmlDeviceDefinition deviceSpec;

    private final File outDir;
    private final Properties properties;
    private int driverIndex = 0;

    XMLExtractor(String manufacturer, String device, File outDir) {
        this.manufacturer = manufacturer;
        this.deviceName = device;
        this.outDir = outDir;
        properties = new Properties();
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

        LOG.info("Installing " + manufacturer + "/" + deviceName);
        String infoText = guiHandler.installDevice(manufacturer, deviceName);

        FrameWrapper library = guiHandler.openLibrary();
        JTableFixture table = library.table();

        LOG.info("Receiving drivers for device " + deviceName);
        List<Class<? extends IDriver>> driversForDevice =
                guiHandler.getDriversForDevice(deviceName);

        boolean deviceCreated = false;
        Device device = null;
        try {
            for (Class<? extends IDriver> driverClass : driversForDevice) {
                StringBuilder popupBuilder = new StringBuilder();

                LOG.info("Using driver: " + driverClass.getName());
                PopupListener driverPopupListener = new PopupListener();
                guiHandler.newPatch(library, deviceName, driverClass,
                        driverPopupListener);
                List<PopupContainer> driverPopups =
                        driverPopupListener.getPopups();
                for (PopupContainer popup : driverPopups) {
                    popupBuilder.append(popup.getContents());
                    popupBuilder.append("\n\n--------------------------\n\n");
                }

                LOG.info("Opening patch editor");
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
                                device = driver.getDevice();
                                createDevice(device, infoText);
                                deviceCreated = true;
                            }
                            SingleDriverGenerator generator =
                                    new SingleDriverGenerator(
                                            (IPatchDriver) driver);
                            generator.createPatchDriver(patchEditor, infoText);
                            generator.save();
                        } else if (jslFrame instanceof BankEditorFrame) {
                            BankEditorFrame frame = (BankEditorFrame) jslFrame;
                            IDriver driver = frame.getBankData().getDriver();
                            if (!deviceCreated) {
                                device = driver.getDevice();
                                createDevice(device, infoText);
                                deviceCreated = true;
                            }
                            BankDriverGenerator generator =
                                    new BankDriverGenerator(
                                            (IBankDriver) driver);
                            generator.createBankDriver(infoText, patchEditor);
                            generator.save();
                        }
                    }

                } finally {
                    if (patchEditor != null) {
                        LOG.info("Close patch editor frame");
                        guiHandler.closeFrame(patchEditor, false);
                    }

                    if (library != null) {
                        LOG.info("Selecting library frame");
                        guiHandler.selectLibraryFrame(library);
                    }
                }
            }
        } finally {
            if (!deviceCreated) {
                throw new IllegalStateException("Device was never created");
            }
            properties.store(new FileOutputStream(new File(outDir, device
                    .getClass().getSimpleName() + ".properties")), null);
            deviceSpecDocument.save(new File(outDir, device.getClass()
                    .getSimpleName() + ".xml"));

            if (library != null) {
                LOG.info("Closing library");
                guiHandler.closeLibrary(library);
            }

            LOG.info("Uninstall device " + deviceName);
            guiHandler.uninstallDevice(deviceName);

            testFrame.cleanUp();
        }
    }

    abstract class DriverGenerator {

        protected DriverGenerator() {
        }

        String newDriverKey() {
            return "driver" + driverIndex++;
        }

        protected void addGenericFields(IDriver driver,
                XmlDriverDefinition driverSpec, String infoText)
                throws IllegalAccessException, NoSuchFieldException {
            driverSpec.setInfoText(infoText);
            driverSpec.setName(deviceName + " " + driver.getPatchType());
            driverSpec.setAuthors(driver.getAuthors());
            driverSpec.setPatchType(driver.getPatchType());

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
                driverSpec.setChecksumEnd(getField("checksumEnd", int.class,
                        d2, AbstractDriver.class));
                driverSpec.setChecksumStart(getField("checksumStart",
                        int.class, d2, AbstractDriver.class));
                driverSpec.setChecksumOffset(getField("checksumOffset",
                        int.class, d2, AbstractDriver.class));
                driverSpec.setSysexID(getField("sysexID", String.class, d2,
                        AbstractDriver.class));
                driverSpec.setDeviceIDoffset(getField("deviceIDoffset",
                        int.class, d2, AbstractDriver.class));
                driverSpec.setPatchNameStart(getField("patchNameStart",
                        int.class, d2, AbstractDriver.class));
            }
        }

        @SuppressWarnings("unchecked")
        protected <T> T getField(String fieldName, Class<T> fieldClass,
                Object object, Class<?> objectClass)
                throws IllegalAccessException, NoSuchFieldException {
            Class<?> tmpClass = object.getClass();
            while (!tmpClass.equals(objectClass)) {
                tmpClass = tmpClass.getSuperclass();
            }
            Field f = tmpClass.getDeclaredField(fieldName);
            f.setAccessible(true);
            return (T) f.get(object);
        }

        public void save() throws IOException {
            properties.put(newDriverKey(), getDriver().getClass()
                    .getSimpleName());
            getDocument().save(
                    new File(outDir, getDriver().getClass().getSimpleName()
                            + ".xml"));
        }

        protected abstract XmlObject getDocument();

        protected abstract IDriver getDriver();
    }

    class BankDriverGenerator extends DriverGenerator {

        private final XmlBankDriverDefinitionDocument document;
        private final IBankDriver driver;

        protected BankDriverGenerator(IBankDriver driver) {
            super();
            this.driver = driver;
            document = XmlBankDriverDefinitionDocument.Factory.newInstance();
        }

        void createBankDriver(String infoText, FrameWrapper patchEditor)
                throws IllegalAccessException, NoSuchFieldException,
                IOException {
            XmlBankDriverDefinition driverSpec =
                    document.addNewXmlBankDriverDefinition();

            XmlDriverReferences drivers = deviceSpec.getDrivers();
            XmlDriverReference driverDef = drivers.addNewXmlDriverReference();
            driverDef.setDriverClass(driver.getClass().getName());
            driverDef.setDriverType(XmlDriverReference.DriverType.BANK);

            addGenericFields(driver, driverSpec, infoText);

            if (driver instanceof AbstractBankDriver) {
                AbstractBankDriver bankDriver = (AbstractBankDriver) driver;
                driverSpec.setNumPatches(getField("numPatches", int.class,
                        bankDriver, AbstractBankDriver.class));
                driverSpec.setNumColumns(getField("numColumns", int.class,
                        bankDriver, AbstractBankDriver.class));
                driverSpec.setSingleSysexID(getField("singleSysexID",
                        String.class, bankDriver, AbstractBankDriver.class));
                driverSpec.setSingleSize(getField("singleSize", int.class,
                        bankDriver, AbstractBankDriver.class));
            }

        }

        @Override
        protected XmlObject getDocument() {
            return document;
        }

        @Override
        protected IDriver getDriver() {
            return driver;
        }
    }

    class SingleDriverGenerator extends DriverGenerator {

        private final String[] IGNORED_PROPERTIES = {
                "class", "patch", "channel" };

        private final XmlSingleDriverDefinitionDocument document;
        private final IPatchDriver driver;
        private final HashMap<String, PatchParams> groupMap;
        private final XmlSingleDriverDefinition driverSpec;
        private final Map<String, HandlerDefinitionBase> handlerDefinitionMap;

        private final List<String> ignoredProperties;

        public SingleDriverGenerator(IPatchDriver driver) {
            super();
            this.ignoredProperties = Arrays.asList(IGNORED_PROPERTIES);
            groupMap = new HashMap<String, PatchParams>();
            handlerDefinitionMap = new HashMap<String, HandlerDefinitionBase>();
            this.driver = driver;
            document = XmlSingleDriverDefinitionDocument.Factory.newInstance();
            driverSpec = document.addNewXmlSingleDriverDefinition();
        }

        void createPatchDriver(FrameWrapper patchEditor, String infoText)
                throws IllegalAccessException, NoSuchFieldException,
                IOException {
            XmlDriverReferences drivers = deviceSpec.getDrivers();
            XmlDriverReference deviceDriverDef =
                    drivers.addNewXmlDriverReference();
            deviceDriverDef.setDriverClass(driver.getClass().getName());
            deviceDriverDef.setDriverType(XmlDriverReference.DriverType.PATCH);

            addGenericFields(driver, driverSpec, infoText);

            PatchParams patchParams = driverSpec.addNewPatchParams();
            groupMap.put("", patchParams);

            List<SysexWidget> sysexWidgets =
                    SysexWidgetFinder.findSysexWidgets(patchEditor);
            if (sysexWidgets.size() > 0) {
                for (SysexWidget sysexWidget : sysexWidgets) {
                    handleSysexWidget(patchEditor, sysexWidget);
                }
            }
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
            LOG.info("Looking for group " + path);
            if (groupMap.containsKey(path)) {
                patchParams = groupMap.get(path);
                LOG.info("Found group " + path);
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
                // CheckBoxWidget widget = (CheckBoxWidget) sysexWidget;
                handleDefaultIntParam(patchParams, valueMax, valueMin, name,
                        sender, paramModel, 0);
            } else if (sysexWidget instanceof ComboBoxWidget) {
                ComboBoxWidget widget = (ComboBoxWidget) sysexWidget;
                int itemCount = widget.cb.getItemCount();
                String[] values = new String[itemCount];

                for (int i = 0; i < itemCount; i++) {
                    String item = null;
                    try {
                        item = (String) widget.cb.getItemAt(i);
                    } catch (ClassCastException e) {
                        ImageIcon icon = (ImageIcon) widget.cb.getItemAt(i);
                        item = icon.getAccessibleContext().getAccessibleName();
                    }
                    values[i] = item;
                }
                handleIntParamWValues(values, patchParams, valueMax, valueMin,
                        name, sender, paramModel);
            } else if (sysexWidget instanceof KnobWidget) {
                KnobWidget widget = (KnobWidget) sysexWidget;
                int base =
                        getField("mBase", int.class, widget, KnobWidget.class);
                handleDefaultIntParam(patchParams, valueMax, valueMin, name,
                        sender, paramModel, base);
            } else if (sysexWidget instanceof PatchNameWidget) {
                PatchNameWidget widget = (PatchNameWidget) sysexWidget;
                handlePatchName(widget, patchParams, name, sender, paramModel);
            } else if (sysexWidget instanceof ScrollBarWidget) {
                ScrollBarWidget widget = (ScrollBarWidget) sysexWidget;
                int base =
                        getField("base", int.class, widget,
                                ScrollBarWidget.class);
                handleDefaultIntParam(patchParams, valueMax, valueMin, name,
                        sender, paramModel, base);
            } else if (sysexWidget instanceof SpinnerWidget) {
                SpinnerWidget widget = (SpinnerWidget) sysexWidget;
                int base =
                        getField("base", int.class, widget, SpinnerWidget.class);
                handleDefaultIntParam(patchParams, valueMax, valueMin, name,
                        sender, paramModel, base);
            } else if (sysexWidget instanceof ScrollBarLookupWidget) {
                ScrollBarLookupWidget widget =
                        (ScrollBarLookupWidget) sysexWidget;
                String[] strings =
                        getField("options", String[].class, widget,
                                ScrollBarLookupWidget.class);

                handleIntParamWValues(strings, patchParams, valueMax, valueMin,
                        name, sender, paramModel);
            } else if (sysexWidget instanceof EnvelopeWidget) {
                EnvelopeWidget envWidget = (EnvelopeWidget) sysexWidget;
                handleEnvelope(patchParams, envWidget);
            } else {
                LOG.warn("Could not handle widget "
                        + sysexWidget.getClass().getName());
            }
        }

        private PatchParams createNewGroup(String path) {
            LOG.info("Creating group " + path);
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

        void addSender(Object handler, HandlerReferenceBase midiSender) {
            if (handler == null) {
                LOG.warn("Found param w/o sender");
                org.w3c.dom.Node parentNode =
                        midiSender.getDomNode().getParentNode();
                if (parentNode instanceof SingleParamSpec) {
                    SingleParamSpec singleParamSpec =
                            (SingleParamSpec) parentNode;
                    singleParamSpec.unsetMidiSender();
                } else if (parentNode instanceof UuidSingleParamSpec) {
                    UuidSingleParamSpec singleParamSpec =
                            (UuidSingleParamSpec) parentNode;
                    singleParamSpec.unsetMidiSender();
                }
                return;
            }
            Class<? extends Object> handlerClass = handler.getClass();
            String simpleName = handlerClass.getSimpleName();
            DeviceConfiguration config = deviceSpec.getConfiguration();

            if (!handlerDefinitionMap.containsKey(simpleName)) {
                MidiSenderDefinitions midiSenderDefinitions =
                        config.getMidiSenderDefinitions();
                if (midiSenderDefinitions == null) {
                    midiSenderDefinitions =
                            config.addNewMidiSenderDefinitions();
                }
                MidiSenderDefinition midiSenderDefinition =
                        midiSenderDefinitions.addNewMidiSenderDefinition();
                midiSenderDefinition.setHandlerClass(handlerClass.getName());
                copyProperties(handler, midiSenderDefinition);

                if (handler instanceof AbstractSender) {
                    Property property = midiSenderDefinition.addNewProperty();
                    property.setKey("offset");
                }
                handlerDefinitionMap.put(simpleName, midiSenderDefinition);
            }

            midiSender.setName(simpleName);
            copyPropertyValues(handler, midiSender);

            if (handler instanceof AbstractSender) {
                AbstractSender aSender = (AbstractSender) handler;
                PropertyValue property = midiSender.addNewPropertyValue();
                property.setKey("offset");
                property.setValue(Integer.toString(aSender.getOffset()));
            }
        }

        void addParamModel(Object handler, ParamModelReference paramModel) {
            Class<? extends Object> handlerClass = handler.getClass();
            String simpleName = handlerClass.getSimpleName();
            DeviceConfiguration config = deviceSpec.getConfiguration();

            if (!handlerDefinitionMap.containsKey(simpleName)) {
                ParamModelDefinitions paramModelDefinitions =
                        config.getParamModelDefinitions();
                if (paramModelDefinitions == null) {
                    paramModelDefinitions =
                            config.addNewParamModelDefinitions();
                }
                ParamModelDefinition paramModelDefinition =
                        paramModelDefinitions.addNewParamModelDefinition();
                paramModelDefinition.setHandlerClass(handlerClass.getName());
                copyProperties(handler, paramModelDefinition);

                if (paramModel instanceof ParamModel) {
                    Property property = paramModelDefinition.addNewProperty();
                    property.setKey("offset");
                }
                handlerDefinitionMap.put(simpleName, paramModelDefinition);
            }

            paramModel.setName(simpleName);
            copyPropertyValues(handler, paramModel);

            if (paramModel instanceof ParamModel) {
                ParamModel concreteParamModel = (ParamModel) handler;
                PropertyValue property = paramModel.addNewPropertyValue();
                property.setKey("offset");
                property.setValue(Integer.toString(concreteParamModel
                        .getOffset()));
            }
        }

        @SuppressWarnings("unchecked")
        void copyProperties(Object handler, HandlerDefinitionBase definition) {
            try {
                Map<String, String> description = BeanUtils.describe(handler);
                Iterator<Entry<String, String>> iterator =
                        description.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, String> entry = iterator.next();
                    if (ignoredProperties.contains(entry.getKey())) {
                        continue;
                    }
                    Property property = definition.addNewProperty();
                    property.setKey(entry.getKey());
                }
            } catch (InvocationTargetException e) {
                LOG.warn(e.getMessage(), e);
            } catch (NoSuchMethodException e) {
                LOG.warn(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                LOG.warn(e.getMessage(), e);
            }
        }

        @SuppressWarnings("unchecked")
        void copyPropertyValues(Object handler, HandlerReferenceBase reference) {
            try {
                Map<String, String> description = BeanUtils.describe(handler);
                Iterator<Entry<String, String>> iterator =
                        description.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, String> entry = iterator.next();
                    if (ignoredProperties.contains(entry.getKey())) {
                        continue;
                    }
                    PropertyValue property = reference.addNewPropertyValue();
                    property.setKey(entry.getKey());
                    property.setValue(entry.getValue());
                }
            } catch (InvocationTargetException e) {
                LOG.warn(e.getMessage(), e);
            } catch (NoSuchMethodException e) {
                LOG.warn(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                LOG.warn(e.getMessage(), e);
            }
        }

        private void handlePatchName(PatchNameWidget widget,
                PatchParams patchParams, String name, ISender sender,
                IParamModel paramModel) throws IllegalAccessException,
                NoSuchFieldException {
            StringParamSpec paramSpec = patchParams.addNewStringParamSpec();
            paramSpec.setName(name);
            Integer field =
                    getField("patchNameSize", int.class, widget,
                            PatchNameWidget.class);
            IPatchStringSender patchNameSender =
                    getField("sender", IPatchStringSender.class, widget,
                            PatchNameWidget.class);
            paramSpec.setLength(field.intValue());
            paramSpec.setUuid(generateUuid());
            StringSenderReference xmlSender = paramSpec.addNewStringSender();
            addSender(patchNameSender, xmlSender);
        }

        void handleEnvelope(PatchParams patchParams, EnvelopeWidget envWidget)
                throws IllegalAccessException, NoSuchFieldException {
            EnvelopeSpec envelopeSpec = patchParams.addNewEnvelopeSpec();
            envelopeSpec.setName(envWidget.getLabel());
            envelopeSpec.setUuid(generateUuid());

            Node[] nodes = envWidget.getNodes();
            for (Node node : nodes) {
                EnvelopeNodeSpec nodeSpec =
                        envelopeSpec.addNewEnvelopeNodeSpec();
                XEnvelopeParamSpec xParam = nodeSpec.addNewXParam();
                YEnvelopeParamSpec yParam = nodeSpec.addNewYParam();
                xParam.setInvert(node.isInvertX());
                xParam.setMax(node.getMaxX());
                if (node.getSenderX() != null) {
                    MidiSenderReference midiSenderX = xParam.addNewMidiSender();
                    addSender(node.getSenderX(), midiSenderX);
                }
                xParam.setMin(node.getMinX());
                xParam.setName(node.getNameX());
                if (node.getPmodelX() != null) {
                    ParamModelReference paramModelX = xParam.addNewParamModel();
                    addParamModel(node.getPmodelX(), paramModelX);
                }
                xParam.setUuid(generateUuid());

                yParam.setBase(node.getBaseY());
                yParam.setMax(node.getMaxY());
                if (node.getSenderY() != null) {
                    MidiSenderReference midiSenderY = yParam.addNewMidiSender();
                    addSender(node.getSenderY(), midiSenderY);
                }
                yParam.setMin(node.getMinY());
                yParam.setName(node.getNameY());
                if (node.getPmodelY() != null) {
                    ParamModelReference paramModelY = yParam.addNewParamModel();
                    addParamModel(node.getPmodelY(), paramModelY);
                }
                yParam.setUuid(generateUuid());
            }
        }

        void handleDefaultIntParam(PatchParams patchParams, int valueMax,
                int valueMin, String name, ISender sender,
                IParamModel paramModel, int base)
                throws IllegalAccessException, NoSuchFieldException {
            IntParamSpec intParamSpec = patchParams.addNewIntParamSpec();
            intParamSpec.setMax(valueMax);
            intParamSpec.setMin(valueMin);
            intParamSpec.setName(name);
            intParamSpec.setUuid(generateUuid());
            if (base != 0) {
                intParamSpec.setBase(base);
            }
            MidiSenderReference midiSender = intParamSpec.addNewMidiSender();
            addSender(sender, midiSender);
            ParamModelReference newParamModel = intParamSpec.addNewParamModel();
            addParamModel(paramModel, newParamModel);
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
            MidiSenderReference midiSender = intParamSpec.addNewMidiSender();
            addSender(sender, midiSender);
            ParamModelReference newParamModel = intParamSpec.addNewParamModel();
            addParamModel(paramModel, newParamModel);
            PatchParamValues paramValues =
                    intParamSpec.addNewPatchParamValues();
            for (String string : values) {
                XmlString paramValue = paramValues.addNewPatchParamValue();
                paramValue.setStringValue(string);
            }
        }

        String generateUuid() {
            UUID uuid = UUID.randomUUID();
            return uuid.toString().replaceAll("\\-", "");
        }

        @Override
        protected XmlObject getDocument() {
            return document;
        }

        @Override
        protected IDriver getDriver() {
            return driver;
        }
    }

    /**
     * @param device
     * @param infoText
     */
    void createDevice(Device device, String infoText) {
        deviceSpecDocument = XmlDeviceDefinitionDocument.Factory.newInstance();

        deviceSpec = deviceSpecDocument.addNewXmlDeviceDefinition();
        deviceSpec.setAuthors(device.getAuthors());
        deviceSpec.addNewDrivers();
        deviceSpec.setInfoText(infoText);
        deviceSpec.setManufacturer(device.getManufacturerName());
        deviceSpec.setModelName(device.getModelName());
        deviceSpec.setInquiryId(device.getInquiryID());
        deviceSpec.addNewConfiguration();

        properties.put("packageName", device.getClass().getPackage().getName());
    }

}
