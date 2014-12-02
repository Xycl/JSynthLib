package org.jsynthlib.utils.ctrlr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.ctrlr.panel.PanelDocument;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.core.impl.PopupHandlerProvider;
import org.jsynthlib.device.model.DeviceException;
import org.jsynthlib.utils.ctrlr.builder.CtrlrPanelBuilder;
import org.jsynthlib.utils.ctrlr.builder.PanelLuaManagerBuilder;
import org.jsynthlib.utils.ctrlr.driverContext.XmlDriverParser;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;
import org.jsynthlib.xmldevice.XmlDriverReferences;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

public class CtrlrSynthGenerator {

    private static final Logger LOG = Logger
            .getLogger(CtrlrSynthGenerator.class);

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

            Injector injector = CtrlrGeneratorModule.getInjector();
            CtrlrSynthGeneratorFactory factory =
                    injector.getInstance(CtrlrSynthGeneratorFactory.class);
            CtrlrSynthGenerator editorGenerator =
                    factory.newPanelGenerator(packageName, fileNamePrefix,
                            outDir);
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

    @Inject
    private PanelLuaManagerBuilder luaManagerBuilder;

    @Inject
    private CtrlrPanelBuilder panelBuilder;

    @Inject
    private DriverInjectorFactory driverInjectorFactory;

    @Inject
    public CtrlrSynthGenerator(@Assisted("packageName") String packageName,
            @Assisted("fileNamePrefix") String fileNamePrefix,
            @Assisted File outDir) {
        this.packageName = packageName;
        this.fileNamePrefix = fileNamePrefix;
        this.outDir = outDir;
        classLoader = getClass().getClassLoader();
        panelDocument = PanelDocument.Factory.newInstance();

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
        PanelType panel = panelBuilder.newPanel(panelDocument, xmldevice);

        XmlDriverReferences xmldrivers = xmldevice.getDrivers();
        XmlDriverReference[] xmldriverArray =
                xmldrivers.getXmlDriverReferenceArray();

        for (XmlDriverReference xmldriver : xmldriverArray) {
            Injector childInjector =
                    driverInjectorFactory.newDriverinjector(xmldevice,
                            xmldriver, panel,
                            CtrlrGeneratorModule.getInjector());
            PopupHandlerProvider.setInjector(childInjector);
            XmlDriverParser driverParser =
                    childInjector.getInstance(XmlDriverParser.class);
            driverParser.parseDriverAndGeneratePanel(panel);
            break;
        }

        luaManagerBuilder.createLuaManager(panel);

        saveFile();
    }

    void saveFile() throws IOException, XmlException {
        XmlOptions options = new XmlOptions();
        options.setUseDefaultNamespace();

        Map<String, String> prefixes = new HashMap<String, String>();
        prefixes.put("", "http://www.ctrlr.org/panel");
        options.setSaveImplicitNamespaces(prefixes);

        options.setSaveNamespacesFirst();
        options.setSavePrettyPrint();
        String xmlText = panelDocument.xmlText(options);
        xmlText = xmlText.replaceAll("&amp;#13;", "&#13;");
        xmlText = xmlText.replaceAll("&amp;#10;", "&#10;");
        xmlText = xmlText.replaceAll("&amp;#9;", "&#9;");
        xmlText = xmlText.replaceAll("&amp;quot;", "&quot;");
        File outFile = new File(outDir, fileNamePrefix + ".panel");
        LOG.info("Saving document to " + outFile.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(outFile);
        fos.write(xmlText.getBytes());
        fos.flush();
        fos.close();

    }
}
