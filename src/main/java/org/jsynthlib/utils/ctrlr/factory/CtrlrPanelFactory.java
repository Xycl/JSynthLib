package org.jsynthlib.utils.ctrlr.factory;

import org.ctrlr.panel.PanelDocument;
import org.ctrlr.panel.PanelType;
import org.ctrlr.panel.UiPanelCanvasLayerType;
import org.ctrlr.panel.UiPanelEditorType;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;

public class CtrlrPanelFactory {

    public PanelType newPanel(PanelDocument panelDocument,
            XmlDeviceDefinition xmldevice) {
        PanelType panel = panelDocument.addNewPanel();
        panel.setName("Oberheim - Matrix 1000");
        panel.setPanelShowDialogs(1);
        panel.setPanelMessageTime(10000);
        panel.setPanelAuthorName("Roman Kubiak");
        panel.setPanelAuthorEmail("kubiak.roman@gmail.com");
        panel.setPanelAuthorUrl("http://ctrlr.org");
        panel.setPanelAuthorDesc("Skeleton panel for Oberheim Matrix 1000");
        panel.setPanelVersionMajor(1);
        panel.setPanelVersionMinor(0);
        panel.setPanelVersionName("Hell-O-Kitty");
        panel.setPanelVendor("");
        panel.setPanelDevice("");
        panel.setPanelMidiSnapshotAfterLoad(0);
        panel.setPanelMidiSnapshotAfterProgramChange(0);
        panel.setPanelMidiSnapshotDelay(10);
        panel.setPanelMidiInputChannelDevice(1);
        panel.setPanelMidiInputDevice("-- None");
        panel.setPanelMidiControllerChannelDevice(1);
        panel.setPanelMidiControllerDevice("-- None");
        panel.setPanelMidiOutputChannelDevice(1);
        panel.setPanelMidiOutputDevice("-- None");
        panel.setPanelMidiInputFromHost(0);
        panel.setPanelMidiInputChannelHost(1);
        panel.setPanelMidiOutputToHost(0);
        panel.setPanelMidiOutputChannelHost(1);
        panel.setPanelMidiThruH2H(0);
        panel.setPanelMidiThruH2HChannelize(0);
        panel.setPanelMidiThruH2D(0);
        panel.setPanelMidiThruH2DChannelize(0);
        panel.setPanelMidiThruD2D(0);
        panel.setPanelMidiThruD2DChannelize(0);
        panel.setPanelMidiThruD2H(0);
        panel.setPanelMidiThruD2HChannelize(0);
        panel.setPanelMidiRealtimeIgnore(1);
        panel.setPanelMidiInputThreadPriority(7);
        panel.setPanelMidiProgram(0);
        panel.setPanelMidiBankLsb(0);
        panel.setPanelMidiBankMsb(0);
        panel.setPanelMidiSendProgramChangeOnLoad(0);
        panel.setPanelMidiProgramCalloutOnprogramChange(0);
        panel.setPanelMidiMatchCacheSize(32);
        panel.setPanelMidiGlobalDelay(50);
        panel.setLuaPanelMidiChannelChanged("-- None");
        panel.setLuaPanelMidiReceived("-- None");
        panel.setLuaPanelLoaded("-- None");
        panel.setLuaPanelBeforeLoad("-- None");
        panel.setLuaPanelSaved("-- None");
        panel.setLuaPanelResourcesLoaded("-- None");
        panel.setLuaPanelProgramChanged("-- None");
        panel.setLuaPanelGlobalChanged("-- None");
        panel.setLuaPanelMessageHandler("-- None");
        panel.setLuaPanelModulatorValueChanged("-- None");
        panel.setPanelFilePath("/home/r.kubiak/devel/ctrlr/panels/Oberheim - Matrix 1000.z");
        panel.setPanelUID("8.I4U4PO.mNLH");
        panel.setPanelInstanceUID("HryD");
        panel.setPanelInstanceManufacturerID("sbza");
        panel.setPanelModulatorListColumns("<TABLELAYOUT sortedCol=\"560\" sortForwards=\"1\"><COLUMN id=\"560\" visible=\"1\" width=\"330\"/><COLUMN id=\"1\" visible=\"1\" width=\"290\"/><COLUMN id=\"559\" visible=\"1\" width=\"290\"/><COLUMN id=\"561\" visible=\"1\" width=\"157\"/><COLUMN id=\"507\" visible=\"1\" width=\"504\"/></TABLELAYOUT>");
        panel.setPanelModulatorListCsvDelimiter(",");
        panel.setPanelModulatorListXmlRoot("ctrlrModulatorList");
        panel.setPanelModulatorListXmlModulator("ctrlrModulator");
        panel.setPanelModulatorListSortOption(1);
        panel.setPanelGlobalVariables("0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:2:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0");
        panel.setPanelResources("");
        panel.setPanelPropertyDisplayIDs(0);
        panel.setCtrlrMenuItemBackgroundColour("ffffffff");
        panel.setCtrlrMenuItemTextColour("ff000000");
        panel.setCtrlrMenuItemHighlightedTextColour("ffffffff");
        panel.setCtrlrMenuItemHighlightColour("ff4364ff");
        panel.setCtrlrMenuItemFont("<Sans-Serif>;13;0;0;0;0;1;3");
        panel.setCtrlrMenuItemSeparatorColour("44000000");
        panel.setCtrlrMenuItemHeaderColour("ff000000");
        panel.setCtrlrMenuBarBackgroundColour1("fff7f7f7");
        panel.setCtrlrMenuBarBackgroundColour2("ffcccccc");
        panel.setCtrlrMenuBarTextColour("ff000000");
        panel.setCtrlrMenuBarHighlightedTextColour("ffffffff");
        panel.setCtrlrMenuBarHighlightColour("ff4364ff");
        panel.setCtrlrMenuBarFont("<Sans-Serif>;13;0;0;0;0;1;3");
        panel.setCtrlrUseEditorWrapper(0);
        panel.setPanelIndex(1);
        panel.setUiPanelModulatorListViewTree(0);

        addWindowManager(panel);
        addMidiLibrary(panel);
        addLuaManager(panel);
        addPanelResources(panel);
        addUiPanelEditor(panel);

        return panel;
    }

    // TODO: add libraries and midi handling
    void addWindowManager(PanelType panel) {
        panel.addNewUiWindowManager(); // UiWindowManagerType windowManager = a
        // windowManager.
    }

    void addMidiLibrary(PanelType panel) {
        panel.addNewMidiLibrary();
    }

    void addLuaManager(PanelType panel) {
        panel.addNewLuaManager();
    }

    void addPanelResources(PanelType panel) {
        // panel.add
    }

    void addUiPanelEditor(PanelType panel) {
        UiPanelEditorType editor = panel.addNewUiPanelEditor();
        editor.setUiPanelSnapSize(8);
        editor.setUiPanelBackgroundColour("0xffffffff");
        editor.setUiPanelBackgroundColour1("0xffffffff");
        editor.setUiPanelBackgroundColour2("0xffffffff");
        editor.setUiPanelBackgroundGradientType(1);
        editor.setUiPanelImageResource("-- None");
        editor.setUiPanelEditMode(1);
        editor.setUiPanelViewPortSize(1272);
        editor.setUiPanelPropertiesSize(318);
        editor.setUiPanelLock(0);
        editor.setUiPanelDisabledOnEdit(0);
        editor.setUiPanelWidth(400);
        editor.setUiPanelHeight(400);
        editor.setName("Ctrlr Panel");
        editor.setUiPanelImageAlpha(255);
        editor.setUiPanelImageLayout(64);
        editor.setUiPanelSnapActive(1);
        editor.setUiPanelPropertiesOnRight(0);
        editor.setLuaPanelPaintBackground("-- None");
        editor.setLuaPanelResized("-- None");
        editor.setLuaPanelFileDragDropHandler("-- None");
        editor.setLuaPanelFileDragEnterHandler("-- None");
        editor.setLuaPanelFileDragExitHandler("-- None");
        editor.setUiPanelInvisibleComponentAlpha((float) 0.5);
        editor.setUiPanelMidiToolbarVisible(0);
        editor.setUiPanelTooltipBackgroundColour("0xffeeeebb");
        editor.setUiPanelTooltipOutlineColour("0xff000000");
        editor.setUiPanelTooltipColour("0xff000000");
        editor.setUiPanelTooltipCornerRound(1);
        editor.setUiPanelTooltipPlacement(2);
        editor.setUiPanelTooltipFont("<Sans-Serif>;15;1;0;0;0;1;3");
        editor.setUiPanelZoom(1);

        UiPanelCanvasLayerType layer = editor.addNewUiPanelCanvasLayer();
        layer.setUiPanelCanvasLayerName("New layer");
        layer.setUiPanelCanvasLayerUid("6fafe60984010000f0f63200a8000000");
        layer.setUiPanelCanvasLayerColour("0x000000");
        layer.setUiPanelCanvasLayerVisibility(1);
        layer.setUiPanelCanvasLayerIndex(0);
    }
}
