package org.jsynthlib.utils.ctrlr.driverContext.impl;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import org.ctrlr.panel.PanelType;
import org.ctrlr.panel.UiPanelEditorType;
import org.jsynthlib.utils.ctrlr.builder.BuilderFactoryFacade;
import org.jsynthlib.utils.ctrlr.builder.component.CtrlrComponentBuilderBase;
import org.jsynthlib.utils.ctrlr.builder.component.GlobalGroupBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.GroupBuilderBase;
import org.jsynthlib.utils.ctrlr.builder.component.UiTabBuilder;
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
public class XmlSingleDriverParserImpl extends XmlDriverParser {

    private static final int DRIVER_GLOBAL_CONTROLS = 100;

    private final transient Logger log = Logger.getLogger(getClass());

    private final XmlSingleDriverDefinition xmlDriverDef;

    private int numStringParamSpecs;

    @Inject
    private PanelType panel;

    @Inject
    @Named("className")
    private String driverClassName;

    @Inject
    private GlobalGroupBuilder globalGroupBuilder;

    @Inject
    private BuilderFactoryFacade builderFacade;

    private final HashSet<CombinedGroup> handledCombinedGroups;

    private final EditorParser editorFrame;

    @Inject
    public XmlSingleDriverParserImpl(
            Provider<XmlDriverDefinition> driverDefProvider) {
        xmlDriverDef = (XmlSingleDriverDefinition) driverDefProvider.get();
        handledCombinedGroups = new HashSet<CombinedGroup>();
        editorFrame = new EditorParser();
    }

    @Override
    protected List<CtrlrComponentBuilderBase<?>> parseDriverGui() {
        ArrayList<CtrlrComponentBuilderBase<?>> list =
                new ArrayList<CtrlrComponentBuilderBase<?>>();
        editorFrame.launchEditor();
        editorFrame.initNodeRecursive(editorFrame.root, null,
                editorFrame.bounds);
        Rectangle rect =
                new Rectangle(0, editorFrame.height + 40, editorFrame.width,
                        DRIVER_GLOBAL_CONTROLS - 40);
        globalGroupBuilder.setRect(rect);

        list.addAll(editorFrame.list);
        list.add(globalGroupBuilder);
        return list;
    }

    void setPanelBounds() {
        UiPanelEditorType editor = panel.getUiPanelEditor();
        StringBuilder sb = new StringBuilder();

        int panelWidth = getPanelWidth();
        int panelHeight = getPanelHeight();
        int width = editorFrame.width;
        int height = editorFrame.height + DRIVER_GLOBAL_CONTROLS;

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

    CtrlrComponentBuilderBase<? extends Object> newBuilder(Object object) {
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
            numStringParamSpecs++;
            if (numStringParamSpecs == 1) {
                return builderFacade.newPatchNameBuilder(paramSpec);
            } else {
                return builderFacade.newUiLabelBuilder(paramSpec.getName());
            }
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

    class EditorParser extends JFrame {
        private static final long serialVersionUID = 1L;

        private Scene scene;

        private ObservableMap<String, Object> namespace;

        private final JFXPanel jfxPanel;

        private Parent root;

        private Bounds bounds;

        private int width;

        private int height;

        private final ArrayList<CtrlrComponentBuilderBase<?>> list;

        public EditorParser() {
            jfxPanel = new JFXPanel();
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            list = new ArrayList<CtrlrComponentBuilderBase<?>>();
        }

        void initFX() {
            // This method is invoked on JavaFX thread
            try {
                String fxmlName =
                        driverClassName.replace('.', '/') + "Editor.fxml";
                log.info("Loading fxml: " + fxmlName);
                FXMLLoader fxmlLoader =
                        new FXMLLoader(getClass().getClassLoader().getResource(
                                fxmlName));
                Parent r = (Parent) fxmlLoader.load();

                namespace = fxmlLoader.getNamespace();

                scene = new Scene(r);
                scene.getStylesheets().add("application.css");

                jfxPanel.setScene(scene);
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            }
        }

        void launchEditor() {
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
                width = (int) scene.getWidth();
                height = (int) scene.getHeight();
                jfxPanel.setSize(width, height);
                add(jfxPanel);
                // 4. Size the frame.
                pack();

                // 5. Show it.
                setVisible(true);
            } catch (InterruptedException e) {
                log.warn(e.getMessage(), e);
            }

            setPanelBounds();
            root = scene.getRoot();
            bounds = root.getBoundsInParent();
        }

        final void initNodeRecursive(Parent parent,
                GroupBuilderBase<?> groupBuilder, Bounds groupAbsBounds) {
            ObservableList<Node> children = parent.getChildrenUnmodifiable();
            for (Node node : children) {
                if (node instanceof TitledPane) {
                    TitledPane pane = (TitledPane) node;
                    String title = pane.getText();
                    XmlObject xmlObject = getXmlobjectByTitle(title);
                    CtrlrComponentBuilderBase<? extends Object> subGroupBuilder =
                            addComponent(xmlObject, node, groupBuilder,
                                    groupAbsBounds);
                    initNodeRecursive(pane,
                            (GroupBuilderBase<?>) subGroupBuilder,
                            getAbsoluteBounds(pane));
                } else if (node instanceof TabPane) {
                    TabPane tabPane = (TabPane) node;
                    ObservableList<Tab> tabs = tabPane.getTabs();
                    PatchParamGroup[] array = new PatchParamGroup[tabs.size()];
                    for (int i = 0; i < tabs.size(); i++) {
                        Tab tab = tabs.get(i);
                        array[i] =
                                (PatchParamGroup) getXmlobjectByTitle(tab
                                        .getText());
                    }
                    UiTabBuilder tabBuilder =
                            (UiTabBuilder) addComponent(array, node,
                                    groupBuilder, groupAbsBounds);

                    for (int i = 0; i < tabs.size(); i++) {
                        Tab tab = tabs.get(i);
                        Node content = tab.getContent();
                        if (content instanceof Parent) {
                            Parent p = (Parent) content;
                            initNodeRecursive(p, tabBuilder.getTabGroup(i),
                                    getAbsoluteBounds(tabPane));
                        } else {
                            log.warn("Weird tab: " + tab.getText());
                        }
                    }
                } else if (node.getId() != null && node.getId().length() == 32) {
                    log.info("Node " + node.getClass().getName());
                    XmlObject xmlObject = getXmlobjectByUuid(node.getId());
                    addComponent(xmlObject, node, groupBuilder, groupAbsBounds);
                } else if (node instanceof Parent) {
                    Parent p = (Parent) node;
                    log.info("Parent " + p.getClass().getName());
                    initNodeRecursive(p, groupBuilder, groupAbsBounds);
                } else {
                    log.info("Unsupported control " + node.getClass().getName());
                }
            }
        }

        CtrlrComponentBuilderBase<? extends Object> addComponent(
                Object xmlObject, Node node, GroupBuilderBase<?> groupBuilder,
                Bounds groupAbsBounds) {
            CtrlrComponentBuilderBase<? extends Object> builder =
                    newBuilder(xmlObject);
            if (builder == null) {
                log.debug("Could not find factory for object type "
                        + xmlObject.getClass().getName());
                return null;
            } else {
                builder.setParentAbsoluteBounds(groupAbsBounds);
                builder.setRect(getAbsoluteBounds(node));
                if (groupBuilder == null) {
                    list.add(builder);
                } else {
                    groupBuilder.add(builder);
                }
                return builder;
            }
        }

    }
}
