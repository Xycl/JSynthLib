package org.jsynthlib.utils.ctrlr;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelDocument;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.factory.CtrlrComponentFactory;
import org.jsynthlib.utils.ctrlr.factory.CtrlrComponentFactoryFactory;
import org.jsynthlib.utils.ctrlr.factory.CtrlrPanelFactory;
import org.jsynthlib.xmldevice.PatchParamGroup;
import org.jsynthlib.xmldevice.PatchParams;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;
import org.jsynthlib.xmldevice.XmlDriverReferences;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference;

public class CtrlrPanelGenerator {

    private static final Logger LOG = Logger
            .getLogger(CtrlrPanelGenerator.class);

    static File createOutDir(String name) throws IOException {
        File outDir = new File(name);
        if (!outDir.exists()) {
            boolean mkdir = outDir.mkdir();
            if (!mkdir) {
                throw new IOException("Failed to create " + name);
            }
        }
        return outDir;
    }

    public static void main(String[] args) {
        try {
            String packageName = System.getProperty("packageName");
            if (packageName == null) {
                packageName = args[0];
            }
            String fileNamePrefix = System.getProperty("fileNamePrefix");
            if (fileNamePrefix == null) {
                fileNamePrefix = args[1];
            }
            String output = System.getProperty("output");
            if (output == null) {
                output = args[2];
            }
            LOG.info("Generating " + packageName + " - " + fileNamePrefix
                    + " into " + output);
            File outDir = createOutDir(output);

            CtrlrPanelGenerator editorGenerator =
                    new CtrlrPanelGenerator(packageName, fileNamePrefix, outDir);
            editorGenerator.generatePanel();
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
        System.exit(0);
    }

    private final String packageName;
    private final String fileNamePrefix;
    private final File outDir;
    private int vstIndex;
    private final ClassLoader classLoader;
    private final PanelDocument panelDocument;
    private PanelType panel;
    private final GroupPositionHandler positionHandler;
    private final CtrlrComponentFactoryFactory componentFactoryFactory;

    CtrlrPanelGenerator(String packageName, String fileNamePrefix, File outDir) {
        this.packageName = packageName;
        this.fileNamePrefix = fileNamePrefix;
        this.outDir = outDir;
        this.vstIndex = 0;
        classLoader = getClass().getClassLoader();
        panelDocument = PanelDocument.Factory.newInstance();
        positionHandler = new GroupPositionHandler(1000);
        componentFactoryFactory = new CtrlrComponentFactoryFactory();
    }

    String getXmlfilePath(String className) {
        return className.replace('.', '/') + ".xml";
    }

    void generatePanel() throws XmlException, IOException {
        InputStream stream =
                classLoader.getResourceAsStream(getXmlfilePath(packageName
                        + "." + fileNamePrefix));
        XmlDeviceDefinitionDocument document =
                XmlDeviceDefinitionDocument.Factory.parse(stream);
        XmlDeviceDefinition xmldevice = document.getXmlDeviceDefinition();
        CtrlrPanelFactory panelFactory = new CtrlrPanelFactory();
        panel = panelFactory.newPanel(panelDocument, xmldevice);

        XmlDriverReferences xmldrivers = xmldevice.getDrivers();
        XmlDriverReference[] xmldriverArray =
                xmldrivers.getXmlDriverReferenceArray();
        for (XmlDriverReference xmldriver : xmldriverArray) {
            switch (xmldriver.getDriverType().intValue()) {
            case XmlDriverReference.DriverType.INT_PATCH:
                String driverClass = xmldriver.getDriverClass();
                XmlDriverEditorParser editorParser =
                        new XmlDriverEditorParser(driverClass, panel);
                editorParser.parseJFX();
                break;
            default:
                LOG.warn("Unsupported driver type: "
                        + xmldriver.getDriverType().toString());
                break;
            }
        }

        saveFile();
    }

    void saveFile() throws IOException {
        XmlOptions options = new XmlOptions();
        options.setUseDefaultNamespace();

        Map<String, String> prefixes = new HashMap<String, String>();
        prefixes.put("", "http://www.ctrlr.org/panel");
        options.setSaveImplicitNamespaces(prefixes);

        options.setSaveNamespacesFirst();
        options.setSavePrettyPrint();
        File outFile = new File(outDir, "panel.panel");
        LOG.info("Saving document to " + outFile.getAbsolutePath());
        panelDocument.save(outFile, options);
    }

    void generateModulatorsRecursive(PatchParams patchParams,
            ModulatorType group) throws XmlException, IOException {
        String query =
                "declare namespace jsl='http://www.jsynthlib.org/xmldevice';"
                        + "$this/*";
        XmlObject[] xmlObjects = patchParams.selectPath(query);

        for (XmlObject xmlObject : xmlObjects) {
            CtrlrComponentFactory<? extends Object> factory =
                    componentFactoryFactory.newFactory(xmlObject);
            if (factory == null) {
                // LOG.debug("Could not find factory for object type "
                // + xmlObject.getClass().getName());
            } else {
                if (xmlObject instanceof PatchParamGroup) {
                    generateModulatorsRecursive((PatchParamGroup) xmlObject,
                            group);
                } else {
                    Rectangle bounds = positionHandler.getNextRectangle();
                    ModulatorType modulator =
                            factory.createComponent(panel, group, vstIndex++,
                                    bounds);
                    // LOG.debug("Added " + modulator.getClass().getName()
                    // + " at " + bounds.toString());

                    positionHandler.addNewModulator(modulator);
                }
            }
        }
    }
}
