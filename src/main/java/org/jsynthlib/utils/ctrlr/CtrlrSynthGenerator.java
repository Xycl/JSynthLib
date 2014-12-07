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
import org.jsynthlib.utils.ctrlr.builder.CtrlrPanelBuilder;
import org.jsynthlib.utils.ctrlr.builder.PanelLuaManagerBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.CtrlrComponentBuilderBase;
import org.jsynthlib.utils.ctrlr.builder.component.GroupBuilderBase;
import org.jsynthlib.utils.ctrlr.builder.component.UiTabBuilder;
import org.jsynthlib.utils.ctrlr.driverContext.XmlDriverParser;
import org.jsynthlib.utils.ctrlr.lua.DriverLuaBean;
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

        int width = 0;
        int height = 0;
        HashMap<String, List<CtrlrComponentBuilderBase<?>>> modMap =
                new HashMap<String, List<CtrlrComponentBuilderBase<?>>>();
        for (XmlDriverReference xmldriver : xmldriverArray) {
            try {
                Injector childInjector =
                        driverInjectorFactory.newDriverinjector(xmldevice,
                                xmldriver, panel,
                                CtrlrGeneratorModule.getInjector());
                XmlDriverParser driverParser =
                        childInjector.getInstance(XmlDriverParser.class);
                List<CtrlrComponentBuilderBase<?>> modBuilders =
                        driverParser.parseDriver();
                DriverLuaBean luaBean = driverParser.getLuaBean();
                if (modMap.containsKey(luaBean.getDriverPrefix())) {
                    modMap.get(luaBean.getDriverPrefix()).addAll(modBuilders);
                } else {
                    modMap.put(luaBean.getDriverPrefix(), modBuilders);
                }

                if (luaBean.getPanelHeight() > height) {
                    height = luaBean.getPanelHeight();
                }

                if (luaBean.getPanelWidth() > width) {
                    width = luaBean.getPanelWidth();
                }
            } catch (IllegalArgumentException e) {
                LOG.info("Skipping driver", e);
            }
        }

        UiTabBuilder editorTabBuilder = newTabBuilder(modMap);
        if (editorTabBuilder == null) {
            int vstIndex = 0;
            for (List<CtrlrComponentBuilderBase<?>> list : modMap.values()) {
                for (CtrlrComponentBuilderBase<?> ctrlrComponentBuilderBase : list) {
                    ctrlrComponentBuilderBase.createModulator(panel, null,
                            vstIndex);
                }
            }
        } else {
            width += 10;
            height += 10;
            editorTabBuilder.setRect(new Rectangle(0, 0, width, height));
            editorTabBuilder.setTabsOrientation(1);
            editorTabBuilder.createModulator(panel, null, 0);
        }

        setPanelBounds(panel, width + 5, height + 5);

        luaManagerBuilder.createLuaManager(panel);

        saveFile();
    }

    UiTabBuilder newTabBuilder(
            HashMap<String, List<CtrlrComponentBuilderBase<?>>> modMap) {
        if (modMap.size() < 2) {
            return null;
        }
        PatchParamGroup[] array = new PatchParamGroup[modMap.size()];
        List<List<CtrlrComponentBuilderBase<?>>> compList =
                new ArrayList<List<CtrlrComponentBuilderBase<?>>>();
        int i = 0;
        for (Entry<String, List<CtrlrComponentBuilderBase<?>>> entry : modMap
                .entrySet()) {
            PatchParamGroup group = PatchParamGroup.Factory.newInstance();
            group.setName(entry.getKey());
            array[i] = group;
            compList.add(entry.getValue());
            i++;
        }

        UiTabBuilder tabBuilder = new UiTabBuilder(array);
        for (int j = 0; j < compList.size(); j++) {
            List<CtrlrComponentBuilderBase<?>> list = compList.get(j);
            GroupBuilderBase<?> tabGroup = tabBuilder.getTabGroup(j);
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
