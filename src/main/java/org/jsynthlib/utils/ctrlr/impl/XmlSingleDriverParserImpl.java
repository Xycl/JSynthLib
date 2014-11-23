package org.jsynthlib.utils.ctrlr.impl;

import java.io.IOException;
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
import org.apache.xmlbeans.XmlObject;
import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.ctrlr.panel.UiPanelEditorType;
import org.jsynthlib.utils.ctrlr.builder.CtrlrComponentBuilderFactory;
import org.jsynthlib.utils.ctrlr.builder.CtrlrLuaManagerBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.CtrlrComponentBuilderBase;
import org.jsynthlib.utils.ctrlr.builder.method.MidiReceivedMethodBuilder;
import org.jsynthlib.utils.ctrlr.driverContext.DriverContext;
import org.jsynthlib.utils.ctrlr.driverContext.XmlDriverParser;
import org.jsynthlib.xmldevice.PatchParamGroup;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;

import com.google.inject.Inject;

public class XmlSingleDriverParserImpl extends JFrame implements
XmlDriverParser {

    private static final long serialVersionUID = 1L;

    private final transient Logger log = Logger.getLogger(getClass());

    private final XmlSingleDriverDefinition xmlDriverDef;

    private Scene scene;

    private ObservableMap<String, Object> namespace;

    private final JFXPanel jfxPanel;

    private int vstIndex;

    private final PanelType panel;

    private final DriverContext context;

    @Inject
    public XmlSingleDriverParserImpl(DriverContext context) {
        xmlDriverDef =
                (XmlSingleDriverDefinition) context.getDriverDefinition();
        this.context = context;
        this.panel = context.getPanel();
        this.vstIndex = 0;
        jfxPanel = new JFXPanel();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void extractDriverToPanel() {
        CtrlrLuaManagerBuilder luaManagerBuilder =
                context.getInstance(CtrlrLuaManagerBuilder.class);
        luaManagerBuilder.addMethodGroup(context.getDriverPrefix());
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
    }

    void setPanelBounds() {
        UiPanelEditorType editor = panel.getUiPanelEditor();
        StringBuilder sb = new StringBuilder();

        int width = (int) scene.getWidth();
        int height = (int) scene.getHeight();
        sb.append("0 0 ").append(width).append(" ").append(height);
        editor.setUiPanelCanvasRectangle(sb.toString());
    }

    String getXmlfilePath(String name) {
        return name.replace('.', '/') + ".xml";
    }

    void initFX() {
        // This method is invoked on JavaFX thread
        try {
            String fxmlName =
                    context.getDriverClassName().replace('.', '/')
                    + "Editor.fxml";
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
                context.getInstance(CtrlrComponentBuilderFactory.class)
                        .newFactory(xmlObject);
        if (builder == null) {
            log.debug("Could not find factory for object type "
                    + xmlObject.getClass().getName());
            return null;
        } else {
            builder.setParentAbsoluteBounds(groupAbsBounds);
            Bounds bounds = getAbsoluteBounds(node);
            return builder.createModulator(panel, group, vstIndex++, bounds);
            // LOG.debug("Added " + modulator.getClass().getName()
            // + " at " + bounds.toString());
        }
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
