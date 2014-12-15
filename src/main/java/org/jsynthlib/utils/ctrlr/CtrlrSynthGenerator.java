package org.jsynthlib.utils.ctrlr;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.ctrlr.panel.PanelDocument;
import org.ctrlr.panel.PanelType;
import org.ctrlr.panel.UiPanelEditorType;
import org.jsynthlib.device.model.DeviceException;
import org.jsynthlib.utils.ctrlr.config.CtrlrGeneratorModule;
import org.jsynthlib.utils.ctrlr.config.DriverInjectorFactory;
import org.jsynthlib.utils.ctrlr.controller.GroupController;
import org.jsynthlib.utils.ctrlr.controller.PanelController;
import org.jsynthlib.utils.ctrlr.controller.PanelLuaManagerController;
import org.jsynthlib.utils.ctrlr.controller.modulator.ModulatorControllerBase;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiTabController;
import org.jsynthlib.utils.ctrlr.domain.CtrlrPanelModel;
import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.utils.ctrlr.service.XmlDriverParser;
import org.jsynthlib.xmldevice.PatchParamGroup;
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
    private PanelLuaManagerController.Factory luaManagerFactory;

    @Inject
    private PanelController.Factory panelControllerFactory;

    @Inject
    private DriverInjectorFactory driverInjectorFactory;

    @Inject
    private CtrlrPanelModel model;
    private PanelController panelController;

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
        panelController =
                panelControllerFactory.newPanelController(panelDocument);
        PanelType panel = panelController.getPanel();
        PanelLuaManagerController luaManagerController =
                luaManagerFactory.newPanelLuaManagerController(panel);
        luaManagerController.init();
        model.setPanel(panel);
        model.setXmldevice(xmldevice);

        XmlDriverReferences xmldrivers = xmldevice.getDrivers();
        XmlDriverReference[] xmldriverArray =
                xmldrivers.getXmlDriverReferenceArray();

        int width = 0;
        int height = 0;
        HashMap<String, List<ModulatorControllerBase>> modMap =
                new HashMap<String, List<ModulatorControllerBase>>();
        for (XmlDriverReference xmldriver : xmldriverArray) {
            try {
                Injector childInjector =
                        driverInjectorFactory.newDriverinjector(xmldevice,
                                xmldriver, CtrlrGeneratorModule.getInjector());
                XmlDriverParser driverParser =
                        childInjector.getInstance(XmlDriverParser.class);
                driverParser.parse();
                DriverModel driverModel = driverParser.getModel();

                if (driverModel.getEditorHeight() > height) {
                    height = driverModel.getEditorHeight();
                }

                if (driverModel.getEditorWidth() > width) {
                    width = driverModel.getEditorWidth();
                }

                if (modMap.containsKey(driverModel.getPrefix())) {
                    modMap.get(driverModel.getPrefix()).addAll(
                            driverModel.getRootModulators());
                } else {
                    modMap.put(driverModel.getPrefix(),
                            driverModel.getRootModulators());
                }
            } catch (DriverParseException e) {
                LOG.info("Skipping driver", e);
            }
        }

        UiTabController editorTabBuilder = newTabBuilder(modMap, panel);
        if (editorTabBuilder != null) {
            width += 10;
            height += 10;
            editorTabBuilder.setRect(new Rectangle(0, 0, width, height));
            editorTabBuilder.setTabsOrientation(1);
        }

        setPanelBounds(panel, width + 5, height + 5);

        saveFile();
    }

    UiTabController newTabBuilder(
            HashMap<String, List<ModulatorControllerBase>> modMap,
            PanelType panel) {
        if (modMap.size() < 2) {
            return null;
        }
        PatchParamGroup[] array = new PatchParamGroup[modMap.size()];
        List<List<ModulatorControllerBase>> compList =
                new ArrayList<List<ModulatorControllerBase>>();
        int i = 0;
        for (Entry<String, List<ModulatorControllerBase>> entry : modMap
                .entrySet()) {
            PatchParamGroup group = PatchParamGroup.Factory.newInstance();
            group.setName(entry.getKey());
            array[i] = group;
            compList.add(entry.getValue());
            i++;
        }

        UiTabController tabBuilder = new UiTabController(array);
        tabBuilder.setPanel(panel);
        tabBuilder.init();
        for (int j = 0; j < compList.size(); j++) {
            List<ModulatorControllerBase> list = compList.get(j);
            GroupController tabGroup = tabBuilder.getTabGroup(j);
            tabGroup.addAll(list);
        }

        return tabBuilder;
    }

    void setPanelBounds(PanelType panel, int width, int height) {
        UiPanelEditorType editor = panel.getUiPanelEditor();
        StringBuilder sb = new StringBuilder();

        sb.append("0 0 ").append(width).append(" ").append(height);
        editor.setUiPanelCanvasRectangle(sb.toString());
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
