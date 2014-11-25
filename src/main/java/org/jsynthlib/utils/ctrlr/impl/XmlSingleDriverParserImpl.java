package org.jsynthlib.utils.ctrlr.impl;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.ctrlr.panel.UiPanelEditorType;
import org.jsynthlib.utils.ctrlr.builder.BuilderFactoryFacade;
import org.jsynthlib.utils.ctrlr.builder.CtrlrLuaManagerBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.CtrlrComponentBuilderBase;
import org.jsynthlib.utils.ctrlr.builder.component.GlobalGroupBuilder;
import org.jsynthlib.utils.ctrlr.builder.method.MidiReceivedMethodBuilder;
import org.jsynthlib.utils.ctrlr.driverContext.XmlDriverParser;
import org.jsynthlib.xmldevice.CombinedGroup;
import org.jsynthlib.xmldevice.CombinedIntPatchParam;
import org.jsynthlib.xmldevice.EnvelopeSpec;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamGroup;
import org.jsynthlib.xmldevice.StringParamSpec;
import org.jsynthlib.xmldevice.XmlDriverDefinition;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class XmlSingleDriverParserImpl extends JFrame implements
XmlDriverParser {

    private static final int DRIVER_GLOBAL_CONTROLS = 40;

    private static final long serialVersionUID = 1L;

    private final transient Logger log = Logger.getLogger(getClass());

    private final XmlSingleDriverDefinition xmlDriverDef;

    private Scene scene;

    private ObservableMap<String, Object> namespace;

    private final JFXPanel jfxPanel;

    private int vstIndex;

    @Inject
    private PanelType panel;

    @Inject
    @Named("prefix")
    private String driverPrefix;

    @Inject
    @Named("className")
    private String driverClassName;

    @Inject
    private GlobalGroupBuilder globalGroupBuilder;

    @Inject
    private BuilderFactoryFacade builderFacade;

    private ModulatorType globalGroup;

    @Inject
    private CtrlrLuaManagerBuilder luaManagerBuilder;

    private final HashSet<CombinedGroup> handledCombinedGroups;

    @Inject
    public XmlSingleDriverParserImpl(
            Provider<XmlDriverDefinition> driverDefProvider) {
        xmlDriverDef = (XmlSingleDriverDefinition) driverDefProvider.get();
        this.vstIndex = 0;
        jfxPanel = new JFXPanel();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        handledCombinedGroups = new HashSet<CombinedGroup>();
    }

    @Override
    public void extractDriverToPanel() {
        luaManagerBuilder.addMethodGroup(driverPrefix);
        MidiReceivedMethodBuilder midiReceivedBuilder =
                luaManagerBuilder.getMidiReceivedBuilder();

        midiReceivedBuilder.addNewDriver(xmlDriverDef);

        final Semaphore semaphore = new Semaphore(0);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX();
                log.info("Scene initialized");
                semaphore.release();
            }
        });

        try {
            log.info("Waiting for scene");
            semaphore.acquire();
            log.info("Scene done adding jfxpanel");
            double width = scene.getWidth();
            double height = scene.getHeight();
            jfxPanel.setSize((int) width, (int) height);
            add(jfxPanel);
            // 4. Size the frame.
            pack();

            // 5. Show it.
            setVisible(true);
        } catch (InterruptedException e) {
            log.warn(e.getMessage(), e);
        }

        setPanelBounds();
        Parent root = scene.getRoot();
        Bounds bounds = root.getBoundsInParent();

        initNodeRecursive(root, null, bounds);
        addDriverGlobalControls();
    }

    void addDriverGlobalControls() {
        Rectangle rect =
                new Rectangle(0, (int) scene.getHeight(),
                        (int) scene.getWidth(), DRIVER_GLOBAL_CONTROLS);
        globalGroup = globalGroupBuilder.createComponent(panel, null, 0, rect);
    }

    void setPanelBounds() {
        UiPanelEditorType editor = panel.getUiPanelEditor();
        StringBuilder sb = new StringBuilder();

        int panelWidth = getPanelWidth();
        int panelHeight = getPanelHeight();
        int width = (int) scene.getWidth();
        int height = (int) scene.getHeight() + DRIVER_GLOBAL_CONTROLS;

        sb.append("0 0 ");

        if (width > panelWidth) {
            sb.append(width);
        } else {
            sb.append(panelWidth);
        }

        sb.append(" ");

        if (height > panelHeight) {
            sb.append(height);
        } else {
            sb.append(panelHeight);
        }

        editor.setUiPanelCanvasRectangle(sb.toString());
    }

    // TODO: Implement
    int getPanelWidth() {
        return 0;
    }

    // TODO: Implement
    int getPanelHeight() {
        return 0;
    }

    String getXmlfilePath(String name) {
        return name.replace('.', '/') + ".xml";
    }

    void initFX() {
        // This method is invoked on JavaFX thread
        try {
            String fxmlName = driverClassName.replace('.', '/') + "Editor.fxml";
            log.info("Loading fxml: " + fxmlName);
            FXMLLoader fxmlLoader =
                    new FXMLLoader(getClass().getClassLoader().getResource(
                            fxmlName));
            Parent root = (Parent) fxmlLoader.load();

            namespace = fxmlLoader.getNamespace();

            scene = new Scene(root);
            scene.getStylesheets().add("application.css");

            jfxPanel.setScene(scene);
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    final void initNodeRecursive(Parent parent, ModulatorType group,
            Bounds groupAbsBounds) {
        ObservableList<Node> children = parent.getChildrenUnmodifiable();
        for (Node node : children) {
            if (node instanceof TitledPane) {
                TitledPane pane = (TitledPane) node;
                String title = pane.getText();
                XmlObject xmlObject = getXmlobjectByTitle(title);
                ModulatorType component =
                        addComponent(xmlObject, node, group, groupAbsBounds);
                initNodeRecursive(pane, component, getAbsoluteBounds(pane));
            } else if (node instanceof TabPane) {
                TabPane tabPane = (TabPane) node;
                ObservableList<Tab> tabs = tabPane.getTabs();
                PatchParamGroup[] array = new PatchParamGroup[tabs.size()];
                for (int i = 0; i < tabs.size(); i++) {
                    Tab tab = tabs.get(i);
                    array[i] =
                            (PatchParamGroup) getXmlobjectByTitle(tab.getText());
                }
                ModulatorType tabType =
                        addComponent(array, node, group, groupAbsBounds);

                for (int i = 0; i < tabs.size(); i++) {
                    Tab tab = tabs.get(i);
                    tabType.getComponent().setUiTabsCurrentTab(i);
                    Node content = tab.getContent();
                    if (content instanceof Parent) {
                        Parent p = (Parent) content;
                        initNodeRecursive(p, tabType,
                                getAbsoluteBounds(tabPane));
                    } else {
                        log.warn("Weird tab: " + tab.getText());
                    }
                }
            } else if (node.getId() != null && node.getId().length() == 32) {
                log.info("Node " + node.getClass().getName());
                XmlObject xmlObject = getXmlobjectByUuid(node.getId());
                addComponent(xmlObject, node, group, groupAbsBounds);
            } else if (node instanceof Parent) {
                Parent p = (Parent) node;
                log.info("Parent " + p.getClass().getName());
                initNodeRecursive(p, group, groupAbsBounds);
            } else {
                log.info("Unsupported control " + node.getClass().getName());
            }
        }
    }

    ModulatorType addComponent(Object xmlObject, Node node,
            ModulatorType group, Bounds groupAbsBounds) {
        CtrlrComponentBuilderBase<? extends Object> builder =
                newBuilder(xmlObject);
        if (builder == null) {
            log.debug("Could not find factory for object type "
                    + xmlObject.getClass().getName());
            return null;
        } else {
            builder.setParentAbsoluteBounds(groupAbsBounds);
            Bounds bounds = getAbsoluteBounds(node);
            return builder.createModulator(panel, group, vstIndex++, bounds);
        }
    }

    public CtrlrComponentBuilderBase<? extends Object> newBuilder(Object object) {
        if (object instanceof IntParamSpec) {
            IntParamSpec paramSpec = (IntParamSpec) object;
            if (paramSpec.isSetPatchParamResources()) {
                return builderFacade.newUiImageButtonBuilder(paramSpec);
            } else if (paramSpec.isSetPatchParamValues()) {
                // Choose which factory to use.
                // return newUiComboFactory(paramSpec);
                return builderFacade.newUiButtonBuilder(paramSpec);
            } else if (paramSpec.getMin() == 0 && paramSpec.getMax() == 1) {
                return builderFacade.newUiButtonBuilder(paramSpec);
            } else {
                return builderFacade.newUiKnobBuilder(paramSpec);
            }
        } else if (object instanceof PatchParamGroup) {
            PatchParamGroup group = (PatchParamGroup) object;
            return builderFacade.newUiGroupBuilder(group);
        } else if (object instanceof PatchParamGroup[]) {
            PatchParamGroup[] groups = (PatchParamGroup[]) object;
            return builderFacade.newUiTabBuilder(groups);
        } else if (object instanceof StringParamSpec) {
            StringParamSpec paramSpec = (StringParamSpec) object;
            return builderFacade.newPatchNameBuilder(paramSpec);
        } else if (object instanceof EnvelopeSpec) {
            EnvelopeSpec envelopeSpec = (EnvelopeSpec) object;
            return builderFacade.newUiEnvelopeBuilder(envelopeSpec);
        } else if (object instanceof CombinedIntPatchParam) {
            CombinedIntPatchParam param = (CombinedIntPatchParam) object;
            XmlCursor cursor = param.newCursor();
            cursor.toParent();
            CombinedGroup combGroup = (CombinedGroup) cursor.getObject();
            cursor.dispose();
            if (handledCombinedGroups.contains(combGroup)) {
                log.debug("Skipping handled combined group");
            } else {
                handledCombinedGroups.add(combGroup);
                return builderFacade.newUiCombinedGroupBuilder(combGroup);
            }
        } else {
            log.warn("Unsupported xml type: " + object.getClass().getName());
        }
        return null;
    }

    Bounds getAbsoluteBounds(Node node) {
        return node.localToScene(node.getBoundsInLocal());
    }

    XmlObject getXmlobjectByUuid(String uuid) {
        String query =
                "declare namespace jsl='http://www.jsynthlib.org/xmldevice';"
                        + "//*[jsl:uuid='" + uuid + "']";
        XmlObject[] xmlObjects = xmlDriverDef.selectPath(query);
        if (xmlObjects == null || xmlObjects.length == 0) {
            throw new IllegalStateException("Could not find uuid " + uuid);
        }
        return xmlObjects[0];
    }

    XmlObject getXmlobjectByTitle(String title) {
        String query =
                "declare namespace jsl='http://www.jsynthlib.org/xmldevice';"
                        + "//*[@name='" + title + "']";
        XmlObject[] xmlObjects = xmlDriverDef.selectPath(query);
        if (xmlObjects == null || xmlObjects.length == 0) {
            throw new IllegalStateException("Could not find title " + title);
        }
        return xmlObjects[0];
    }
}
