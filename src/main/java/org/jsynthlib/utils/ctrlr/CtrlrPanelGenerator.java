package org.jsynthlib.utils.ctrlr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.ctrlr.panel.PanelDocument;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.device.model.DeviceException;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.utils.ctrlr.builder.CtrlrPanelBuilder;
import org.jsynthlib.utils.ctrlr.impl.SysexFormulaParserImpl;
import org.jsynthlib.utils.ctrlr.impl.XmlDriverEditorParser;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;
import org.jsynthlib.xmldevice.XmlDriverReferences;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference;

import com.google.inject.Injector;

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
    private final ClassLoader classLoader;
    private final PanelDocument panelDocument;
    private PanelType panel;
    private final Injector injector;
    private final XmlDriverEditorParserFactory parserFactory;
    private final SysexFormulaParser sysexParser;

    CtrlrPanelGenerator(String packageName, String fileNamePrefix, File outDir) {
        this.packageName = packageName;
        this.fileNamePrefix = fileNamePrefix;
        this.outDir = outDir;
        classLoader = getClass().getClassLoader();
        panelDocument = PanelDocument.Factory.newInstance();
        injector =
                JSynthLibInjector.getInjector().createChildInjector(
                        new CtrlrGeneratorModule());
        parserFactory =
                injector.getInstance(XmlDriverEditorParserFactory.class);
        sysexParser = injector.getInstance(SysexFormulaParserImpl.class);
    }

    void generatePanel() throws XmlException, IOException, DeviceException {
        String className = packageName + "." + fileNamePrefix;
        XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setLoadStripWhitespace();
        InputStream stream =
                classLoader.getResourceAsStream(className.replace('.', '/')
                        + ".xml");
        XmlDeviceDefinitionDocument document =
                XmlDeviceDefinitionDocument.Factory.parse(stream, xmlOptions);
        XmlDeviceDefinition xmldevice = document.getXmlDeviceDefinition();
        sysexParser.setDeviceDefinition(xmldevice);
        CtrlrPanelBuilder panelFactory = new CtrlrPanelBuilder();
        panel = panelFactory.newPanel(panelDocument, xmldevice);

        XmlDriverReferences xmldrivers = xmldevice.getDrivers();
        XmlDriverReference[] xmldriverArray =
                xmldrivers.getXmlDriverReferenceArray();
        boolean foundEditor = false;
        for (XmlDriverReference xmldriver : xmldriverArray) {
            switch (xmldriver.getDriverType().intValue()) {
            case XmlDriverReference.DriverType.INT_PATCH:
                String driverClass = xmldriver.getDriverClass();
                XmlDriverEditorParser editorParser =
                        parserFactory.create(driverClass, panel);
                editorParser.parseJFX();
                foundEditor = true;
                break;
            default:
                LOG.warn("Unsupported driver type: "
                        + xmldriver.getDriverType().toString());
                break;
            }
            if (foundEditor) {
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
        File outFile = new File(outDir, fileNamePrefix + ".panel");
        LOG.info("Saving document to " + outFile.getAbsolutePath());
        panelDocument.save(outFile, options);
    }
}
