package org.jsynthlib.utils.ctrlr.service.impl;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Semaphore;

import javafx.application.Platform;
import javafx.collections.ObservableList;
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

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.utils.SingletonMidiDeviceProvider.MidiRecordSession;
import org.jsynthlib.utils.SysexUtils;
import org.jsynthlib.utils.ctrlr.CtrlrMidiService;
import org.jsynthlib.utils.ctrlr.controller.GroupController;
import org.jsynthlib.utils.ctrlr.controller.LuaFactoryFacade;
import org.jsynthlib.utils.ctrlr.controller.ModulatorFactoryFacade;
import org.jsynthlib.utils.ctrlr.controller.lua.LoadPatchMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.ReceivePatchMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.SavePatchMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.WritePatchMethodController;
import org.jsynthlib.utils.ctrlr.controller.modulator.GlobalGroupController;
import org.jsynthlib.utils.ctrlr.controller.modulator.GroupControllerBase;
import org.jsynthlib.utils.ctrlr.controller.modulator.ModulatorControllerBase;
import org.jsynthlib.utils.ctrlr.controller.modulator.PatchNameController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiTabController;
import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.utils.ctrlr.domain.DriverTypeModel;
import org.jsynthlib.utils.ctrlr.domain.MethodDescriptionPair;
import org.jsynthlib.utils.ctrlr.domain.WritePatchMessage;
import org.jsynthlib.utils.ctrlr.service.LuaMethodProvider;
import org.jsynthlib.utils.ctrlr.service.PopupManager.PopupSession;
import org.jsynthlib.utils.ctrlr.service.XmlDriverParser;
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
public class XmlSingleDriverParser extends XmlDriverParser {

    public interface Factory {

        XmlDriverParser newSingleDriverParser(
                XmlSingleDriverDefinition driverDef);
    }

    private static final int DRIVER_GLOBAL_CONTROLS = 100;

    private final transient Logger log = Logger.getLogger(getClass());

    private final XmlSingleDriverDefinition xmlDriverDef;

    private int numStringParamSpecs;

    @Inject
    @Named("editor")
    private LuaMethodProvider luaMethodProvider;

    @Inject
    @Named("className")
    private String driverClassName;

    @Inject
    private GlobalGroupController globalGroupBuilder;

    @Inject
    private ModulatorFactoryFacade modulatorFacade;

    @Inject
    private LuaFactoryFacade luaFacade;

    private final HashSet<CombinedGroup> handledCombinedGroups;

    private final EditorParser editorFrame;

    @Inject
    private CtrlrMidiService midiService;

    @Inject
    private DriverTypeModel driverTypeModel;

    @Inject
    private DriverModel model;

    @Inject
    public XmlSingleDriverParser(Provider<XmlDriverDefinition> driverDefProvider) {
        xmlDriverDef = (XmlSingleDriverDefinition) driverDefProvider.get();
        handledCombinedGroups = new HashSet<CombinedGroup>();
        editorFrame = new EditorParser();
    }

    @Override
    protected void parseDriver() {
        globalGroupBuilder.init();

        driverTypeModel.setSingleCsEnd(xmlDriverDef.getChecksumEnd());
        driverTypeModel.setSingleCsOfs(xmlDriverDef.getChecksumOffset());
        driverTypeModel.setSingleCsStart(xmlDriverDef.getChecksumStart());

        driverTypeModel.setSinglePatchSize(xmlDriverDef.getPatchSize());

        editorFrame.launchEditor();
        editorFrame.initNodeRecursive(editorFrame.root, null,
                editorFrame.bounds);
        Rectangle rect =
                new Rectangle(0, editorFrame.height + 40, editorFrame.width,
                        DRIVER_GLOBAL_CONTROLS - 40);
        globalGroupBuilder.setRect(rect);
        model.addRootModulator(globalGroupBuilder);

        String description = "Single Patch";

        ReceivePatchMethodController receivePatchController =
                parsePatchDumpMethod();
        luaMethodProvider.addReceiveMenuOption(new MethodDescriptionPair(
                receivePatchController.getMethodName(), description));

        WritePatchMethodController writePatchController =
                parseStorePatchMethod();
        luaMethodProvider.addWriteMenuOption(new MethodDescriptionPair(
                writePatchController.getMethodName(), description));
        parseGetSetPatchNameMethods();
        SavePatchMethodController savePatchController =
                luaFacade.newSavePatchMethodController();

        luaMethodProvider.addSaveMenuOption(new MethodDescriptionPair(
                savePatchController.getMethodName(), description));
        LoadPatchMethodController loadPatchController =
                luaFacade.newLoadPatchMethodController();
        luaMethodProvider.addLoadMenuOption(new MethodDescriptionPair(
                loadPatchController.getMethodName(), description));
        luaFacade.newAssembleValuesController();
        luaFacade.newAssignValuesController();
        luaFacade.newSelectPatchMethodController();
    }

    String getXmlfilePath(String name) {
        return name.replace('.', '/') + ".xml";
    }

    ModulatorControllerBase newBuilder(Object object) {
        if (object instanceof IntParamSpec) {
            IntParamSpec paramSpec = (IntParamSpec) object;
            if (paramSpec.isSetPatchParamResources()) {
                return modulatorFacade.newUiImageButtonController(paramSpec);
            } else if (paramSpec.isSetPatchParamValues()) {
                // Choose which factory to use.
                // return newUiComboFactory(paramSpec);
                return modulatorFacade.newUiButtonController(paramSpec);
            } else if (paramSpec.getMin() == 0 && paramSpec.getMax() == 1) {
                return modulatorFacade.newUiButtonController(paramSpec);
            } else {
                return modulatorFacade.newUiKnobController(paramSpec);
            }
        } else if (object instanceof PatchParamGroup) {
            PatchParamGroup group = (PatchParamGroup) object;
            return modulatorFacade.newUiGroupController(group);
        } else if (object instanceof PatchParamGroup[]) {
            PatchParamGroup[] groups = (PatchParamGroup[]) object;
            return modulatorFacade.newUiTabController(groups);
        } else if (object instanceof StringParamSpec) {
            StringParamSpec paramSpec = (StringParamSpec) object;
            numStringParamSpecs++;
            if (numStringParamSpecs == 1) {
                return modulatorFacade.newPatchNameController(paramSpec);
            } else {
                return modulatorFacade
                        .newUiLabelController(paramSpec.getName());
            }
        } else if (object instanceof EnvelopeSpec) {
            EnvelopeSpec envelopeSpec = (EnvelopeSpec) object;
            return modulatorFacade.newUiEnvelopeController(envelopeSpec);
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
                return modulatorFacade.newUiCombinedGroupController(combGroup);
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

        private final JFXPanel jfxPanel;

        private Parent root;

        private Bounds bounds;

        private int width;

        private int height;

        public EditorParser() {
            jfxPanel = new JFXPanel();
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

            model.setEditorHeight(height + DRIVER_GLOBAL_CONTROLS);
            model.setEditorWidth(width);
            root = scene.getRoot();
            bounds = root.getBoundsInParent();
        }

        final void initNodeRecursive(Parent parent,
                GroupController groupBuilder, Bounds groupAbsBounds) {
            ObservableList<Node> children = parent.getChildrenUnmodifiable();
            for (Node node : children) {
                if (node instanceof TitledPane) {
                    TitledPane pane = (TitledPane) node;
                    String title = pane.getText();
                    XmlObject xmlObject = getXmlobjectByTitle(title);
                    ModulatorControllerBase subGroupBuilder =
                            addComponent(xmlObject, node, groupBuilder,
                                    groupAbsBounds);
                    initNodeRecursive(pane,
                            (GroupControllerBase) subGroupBuilder,
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
                    UiTabController tabBuilder =
                            (UiTabController) addComponent(array, node,
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
                    log.debug("Node " + node.getClass().getName());
                    XmlObject xmlObject = getXmlobjectByUuid(node.getId());
                    addComponent(xmlObject, node, groupBuilder, groupAbsBounds);
                } else if (node instanceof Parent) {
                    Parent p = (Parent) node;
                    log.debug("Parent " + p.getClass().getName());
                    initNodeRecursive(p, groupBuilder, groupAbsBounds);
                } else {
                    log.debug("Unsupported control "
                            + node.getClass().getName());
                }
            }
        }

        ModulatorControllerBase addComponent(Object xmlObject, Node node,
                GroupController groupBuilder, Bounds groupAbsBounds) {
            ModulatorControllerBase builder = newBuilder(xmlObject);
            if (builder == null) {
                log.debug("Could not find factory for object type "
                        + xmlObject.getClass().getName());
                return null;
            } else {
                builder.setParentAbsoluteBounds(groupAbsBounds);
                builder.setRect(getAbsoluteBounds(node));
                if (builder instanceof PatchNameController) {
                    globalGroupBuilder.add(builder);
                } else if (groupBuilder == null) {
                    model.addRootModulator(builder);
                } else {
                    groupBuilder.add(builder);
                }
                return builder;
            }
        }

    }

    ReceivePatchMethodController parsePatchDumpMethod() {
        MidiRecordSession midiRecordSession = midiService.openSession();
        PopupSession popupSession = getPopupManager().openSession();
        getDriver().requestPatchDump(0, 0);
        String midiMessages = midiService.closeSession(midiRecordSession);
        List<String> popups = getPopupManager().closeSession(popupSession);
        List<String> msgList = new ArrayList<String>();

        String[] split = midiMessages.split(";");
        if (split.length > 1) {
            msgList.addAll(Arrays.asList(split));
            msgList.remove("");
        }

        return luaFacade.newReceivePatchMethodController(msgList, popups);
    }

    WritePatchMethodController parseStorePatchMethod() {
        String sysexID = getDriver().getSysexID().replaceAll("\\*{2}", "FF");
        byte[] sysexIdBytes = SysexUtils.stringToSysex(sysexID);

        IDriver iDriver = getDriver();

        List<WritePatchMessage> msgList = new ArrayList<WritePatchMessage>();

        Patch patch = iDriver.createPatch();
        PopupSession popupSession = getPopupManager().openSession();
        MidiRecordSession session = midiService.openSession();
        iDriver.storePatch(patch, 0, 0);
        String sentMessages = midiService.closeSession(session);
        List<String> popups = getPopupManager().closeSession(popupSession);
        List<String> temp = Arrays.asList(sentMessages.split(";"));
        ArrayList<String> sentMsgList = new ArrayList<String>();
        sentMsgList.addAll(temp);
        sentMsgList.remove("");

        for (String sentMessage : sentMsgList) {
            byte[] msgBytes = SysexUtils.stringToSysex(sentMessage);
            boolean patchDataMsg = true;
            for (int i = 0; i < sysexIdBytes.length; i++) {
                byte b = sysexIdBytes[i];
                if (b != (byte) 0xFF && b != msgBytes[i]) {
                    log.info("Message did not match sysex id: " + sentMessage);
                    patchDataMsg = false;
                    break;
                }
            }
            if (!patchDataMsg && msgBytes.length != iDriver.getPatchSize()) {
                patchDataMsg = false;
            }
            msgList.add(new WritePatchMessage(sentMessage, patchDataMsg));
        }

        String[] bankNumbers = iDriver.getBankNumbers();
        boolean variableBanks = false;
        if (bankNumbers != null && bankNumbers.length > 1) {
            parseWriteMsgDiffs(msgList, bankNumbers, true);
            variableBanks = true;
        }

        boolean variablePatches = false;
        String[] patchNumbers = iDriver.getPatchNumbers();
        if (patchNumbers != null && patchNumbers.length > 1) {
            parseWriteMsgDiffs(msgList, patchNumbers, true);
            variablePatches = true;
        }

        return luaFacade.newWriteMethodController(variableBanks,
                variablePatches, msgList, popups);

    }

    void parseWriteMsgDiffs(List<WritePatchMessage> writeMsgList,
            String[] numbers, boolean findBankDiff) {

        IDriver iDriver = getDriver();
        Patch patch = iDriver.createPatch();
        MidiRecordSession session = midiService.openSession();
        iDriver.storePatch(patch, 0, 0);
        String sentMessages = midiService.closeSession(session);
        List<String> temp = Arrays.asList(sentMessages.split(";"));
        ArrayList<String> sentMsgList0 = new ArrayList<String>();
        sentMsgList0.addAll(temp);
        sentMsgList0.remove("");

        patch = iDriver.createPatch();
        session = midiService.openSession();
        iDriver.storePatch(patch, numbers.length - 1, 0);
        sentMessages = midiService.closeSession(session);
        temp = Arrays.asList(sentMessages.split(";"));
        ArrayList<String> sentMsgListLast = new ArrayList<String>();
        sentMsgListLast.addAll(temp);
        sentMsgListLast.remove("");

        if (sentMsgList0.size() != sentMsgListLast.size()
                || sentMsgList0.size() != writeMsgList.size()) {
            throw new IllegalArgumentException("Lists didn't match!");
        }

        for (int i = 0; i < writeMsgList.size(); i++) {
            String sent0 = sentMsgList0.get(i);
            String sentLast = sentMsgListLast.get(i);
            if (!sent0.equals(sentLast)) {
                byte[] msg0Bytes = SysexUtils.stringToSysex(sent0);
                byte[] msgLastBytes = SysexUtils.stringToSysex(sentLast);
                for (int j = 0; j < msg0Bytes.length; j++) {
                    if (msg0Bytes[j] != msgLastBytes[j]) {
                        WritePatchMessage writePatchMessage =
                                writeMsgList.get(i);
                        if (findBankDiff) {
                            writePatchMessage.setBankNbrOffset(j);
                        } else {
                            writePatchMessage.setPatchNbrOffset(j);
                        }
                        break;
                    }
                }
                // TODO: improve to check if several differences occurred.
                break;
            }
        }
    }

    private static final int CHAR_START = 32;
    private static final int CHAR_END = 122;

    void parseGetSetPatchNameMethods() {
        if (getDriver().getPatchNameSize() > 0) {
            Patch patch = new Patch();
            HashMap<Integer, String> charMap = new HashMap<Integer, String>();
            char[] c = new char[getDriver().getPatchNameSize()];
            for (int i = CHAR_START; i <= CHAR_END; i++) {
                Arrays.fill(c, (char) 32);
                c[0] = (char) i;
                patch.sysex = new byte[getDriver().getPatchSize()];
                String string = new String(c);
                getDriver().setPatchName(patch, string);
                int key = patch.sysex[getDriver().getPatchNameStart()];
                String value =
                        StringEscapeUtils.escapeJava(Character.toString(c[0]));
                if (!charMap.containsKey(key)) {
                    charMap.put(key, value);
                }
            }

            ArrayList<Integer> keys = new ArrayList<Integer>(charMap.keySet());
            if (keys.size() == 1 && keys.get(0) == 0) {
                model.setPatchNameCharMax(127);
                luaFacade.newGetNameMethodController();
                luaFacade.newSetNameMethodController();
            } else {
                Collections.sort(keys);

                model.setPatchNameCharMax(keys.get(keys.size() - 1));

                String[] array = new String[keys.get(keys.size() - 1) + 1];
                Arrays.fill(array, "|");
                for (Integer key : keys) {
                    array[key] = charMap.get(key);
                }

                luaFacade.newGetNameMethodController(array);
                luaFacade.newSetNameMethodController(array);
            }
        } else if (getDriver().canCreatePatch()) {
            Patch patch = getDriver().createPatch();
            String name = patch.getName();
            luaFacade.newGetNameMethodController(name);
        } else {
            log.warn("Could not provide any getter/setter for patch name");
        }
    }
}
