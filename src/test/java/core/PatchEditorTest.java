/*
 * Copyright 2014 Pascal Collberg
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTableFixture;
import org.jsynthlib.driver.Xmldriver;
import org.jsynthlib.driver.Xmleditor;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import core.SynthDriversManager.ManufacturerDriverPair;
import core.TitleFinder.FrameWrapper;

/**
 * This class tests all drivers based on the XML files in the
 * src/test/resources/patchEditor folder. The class also contains a main method
 * that is used to generate new XML files based on added device drivers.
 * @author Pascal Collberg
 */
@RunWith(Parameterized.class)
public class PatchEditorTest {

    public static final String TEST_LEVEL = "org.jsynthlib.testlevel";
    public static final String TEST_DEVICE = "org.jsynthlib.testDevice";
    public static final int TESTLEVEL_LOW = 1;
    public static final int TESTLEVEL_MEDIUM = 2;
    public static final int TESTLEVEL_HIGH = 3;
    private static final Logger LOG = Logger.getLogger(PatchEditorTest.class);
    private static final File OUT_DIR = new File(
            "src/test/resources/patchEditor");

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
        GuiActionRunner.execute(new GuiQuery<PatchEdit>() {
            protected PatchEdit executeInEDT() {
                return new PatchEdit(new ArrayList<String>(), 2);
            }
        });
    }

    @Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        String testLevelStr = System.getProperty(TEST_LEVEL);
        String testDeviceStr = System.getProperty(TEST_DEVICE);
        if (testDeviceStr != null) {
            System.setProperty(TEST_LEVEL, Integer.toString(TESTLEVEL_HIGH));
            testLevelStr = Integer.toString(TESTLEVEL_HIGH);
        } else if (testLevelStr == null) {
            System.setProperty(TEST_LEVEL, Integer.toString(TESTLEVEL_LOW));
            testLevelStr = Integer.toString(TESTLEVEL_LOW);
        }
        List<String> list = new ArrayList<String>();
        try {
            List<ManufacturerDriverPair> deviceList =
                    SynthDriversManager.getDeviceList();
            Iterator<ManufacturerDriverPair> iterator = deviceList.iterator();
            while (iterator.hasNext()) {
                ManufacturerDriverPair entry = iterator.next();
                String driverName = entry.getDriverName();
                String filename = getXmlFile(driverName).getName();
                if (testDeviceStr == null || filename.contains(testDeviceStr)) {
                    list.add(filename);
                }
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }

        List<String> shrinkedList = getShrinkedList(list);
        LOG.info(shrinkedList.toString());
        Object[][] objects = new Object[shrinkedList.size()][1];
        for (int i = 0; i < shrinkedList.size(); i++) {
            String s = shrinkedList.get(i);
            objects[i] = new Object[] {
                s };
        }
        return Arrays.asList(objects);
    }

    public static <T> List<T> getShrinkedList(List<T> list) {
        String testLevelStr = System.getProperty(TEST_LEVEL);
        int testLevel = Integer.parseInt(testLevelStr);
        List<T> arrayList = new ArrayList<T>();
        switch (testLevel) {
        case TESTLEVEL_LOW:
            Collections.shuffle(list);
            int size = list.size() / 4;
            if (size > 4) {
                size = 4;
            }
            arrayList = list.subList(0, size);
            break;
        case TESTLEVEL_MEDIUM:
            Collections.shuffle(list);
            size = list.size() / 2;
            if (size <= 2) {
                size = list.size();
            }
            arrayList = list.subList(0, size);
            break;
        case TESTLEVEL_HIGH:
        default:
            // Leave list intact
            arrayList.addAll(list);
            break;
        }
        return arrayList;
    }

    /**
     * Generate xml files
     * @param args
     */
    public static void main(String[] args) {
        try {
            if (!OUT_DIR.exists()) {
                OUT_DIR.mkdir();
            }
            System.setProperty(TEST_LEVEL, Integer.toString(TESTLEVEL_HIGH));
            setUpOnce();
            PatchEditorTest driverTest = new PatchEditorTest(null);
            driverTest.setUp();

            // uninstall all previously installed drivers.
            driverTest.guiHandler.uninstallDevice(null);
            driverTest.guiHandler.setTestMidiDevices();
            List<ManufacturerDriverPair> deviceList =
                    SynthDriversManager.getDeviceList();
            Iterator<ManufacturerDriverPair> iterator = deviceList.iterator();
            while (iterator.hasNext()) {
                ManufacturerDriverPair entry = iterator.next();
                String driverName = entry.getDriverName();
                String manufacturer = entry.getManufacturer();
                LOG.info("Generating " + manufacturer + "/" + driverName);

                File outputFile = getXmlFile(driverName);
                File file = new File(OUT_DIR, outputFile.getName());
                if (file.exists()) {
                    LOG.info("Skipping already parsed device: " + driverName);
                    continue;
                }

                String deviceName = driverName.replace(" Driver", "");

                driverTest.documentHandler =
                        new CreateDocumentHandler(file, driverTest.testFrame);
                driverTest.handleXmlFile(manufacturer, deviceName);
            }
            System.exit(0);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    public static File getXmlFile(String driverName) {
        String deviceName = driverName.replace(" Driver", "");
        return new File(deviceName.replace('/', ' ') + ".xml");
    }

    private FrameFixture testFrame;
    private GuiHandler guiHandler;
    private String xmlFile;
    private AbstractDocumentHandler documentHandler;
    private String deviceNameField;

    public PatchEditorTest(String xmlFile) {
        this.xmlFile = xmlFile;
    }

    @Before
    public void setUp() throws Exception {
        testFrame = new FrameFixture(PatchEdit.getInstance());
        testFrame.show();
        // testFrame.resizeHeightTo(600);
        // testFrame.resizeWidthTo(800);
        testFrame.maximize();
        guiHandler = new GuiHandler(testFrame);
    }

    @After
    public void tearDown() throws Exception {
        testFrame.cleanUp();
    }

    Properties setupXmlFile(String manufacturer, String deviceName)
            throws InterruptedException, IOException {
        Properties excludedDrivers = new Properties();
        excludedDrivers.load(getClass().getResourceAsStream(
                "/excludeddrivers.properties"));

        documentHandler.handleDocument(manufacturer, deviceName);

        LOG.info("Installing " + manufacturer + "/" + deviceName);
        guiHandler.installDevice(manufacturer, deviceName);
        return excludedDrivers;
    }

    void handleXmlFile(String manufacturer, String deviceName)
            throws InterruptedException, IOException {
        FrameWrapper library = null;
        try {
            deviceNameField = deviceName;
            Properties excludedDrivers = setupXmlFile(manufacturer, deviceName);

            LOG.info("Opening library");
            library = guiHandler.openLibrary();
            JTableFixture table = library.table();

            LOG.info("Receiving drivers for device " + deviceName);
            List<Class<?>> driversForDevice =
                    guiHandler.getDriversForDevice(deviceName);
            for (Class<?> driverClass : driversForDevice) {
                if (excludedDrivers.containsKey(driverClass.getName())) {
                    LOG.info("Skipping excluded driver "
                            + driverClass.getName());
                    continue;
                }

                LOG.info("Using driver: " + driverClass.getName());
                PopupListener driverPopupListener = new PopupListener();
                guiHandler.newPatch(library, deviceName, driverClass,
                        driverPopupListener);

                Xmldriver xmldriver =
                        documentHandler.handleDriver(driverClass.getName());

                handlePatchStore(library, xmldriver);

                LOG.info("Opening patch editor");
                PopupListener editorPopupListener = new PopupListener();
                FrameWrapper patchEditor =
                        guiHandler.openPatchEditor(table, -1, 0,
                                editorPopupListener, true);
                List<PopupContainer> popups = driverPopupListener.getPopups();

                try {
                    if (patchEditor == null) {
                        LOG.warn("Patch Editor is null!");
                        documentHandler.handlePatchEditor(xmldriver, false,
                                null, popups);
                        continue;
                    } else {
                        String title = findEditorTitle(deviceName);

                        if (title == null) {
                            LOG.warn("Could not find editor name!");
                            documentHandler.handlePatchEditor(xmldriver, false,
                                    null, popups);
                            continue;
                        }
                        Xmleditor editor =
                                documentHandler.handlePatchEditor(xmldriver,
                                        true, title, popups);

                        List<SysexWidget> sysexWidgets =
                                SysexWidgetFinder.findSysexWidgets(patchEditor);
                        if (sysexWidgets.isEmpty()) {
                            // Patch Bank
                            JTableFixture bankTable = patchEditor.table();

                            documentHandler.handleBankEditor(editor, bankTable);
                        } else {
                            // Single editor
                            for (final SysexWidget sysexWidget : sysexWidgets) {
                                documentHandler.handleParam(editor,
                                        sysexWidget, patchEditor);
                            }
                        }
                    }
                } finally {
                    if (patchEditor != null) {
                        LOG.info("Close patch editor frame");
                        guiHandler.closeFrame(patchEditor, false);
                    }

                    if (library != null) {
                        LOG.info("Selecting library frame");
                        guiHandler.selectLibraryFrame(library);
                    }
                }
            }
            documentHandler.saveDocument();
        } finally {
            tearDownXmlFile(deviceNameField, library);
        }
    }

    void tearDownXmlFile(String deviceName, FrameWrapper library)
            throws InterruptedException, IOException {
        if (library != null) {
            LOG.info("Closing library");
            guiHandler.closeLibrary(library);
        }

        LOG.info("Uninstall device " + deviceName);
        guiHandler.uninstallDevice(deviceName);
    }

    void handlePatchStore(FrameWrapper library, Xmldriver xmldriver) {
        LOG.info("Handle patch store");
        try {
            JTableFixture table = library.table();
            Map<String, List<String>> bankMap =
                    guiHandler.getPatchStoreOptions(table);

            LOG.debug("Got " + bankMap.keySet().size() + " banks to store.");
            documentHandler.handleStore(xmldriver, table, bankMap);

            guiHandler.restorePatchStoreDialog(table);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        } finally {
            LOG.info("Selecting library frame");
            guiHandler.selectLibraryFrame(library);
        }
    }

    String findEditorTitle(String deviceName) {
        List<FrameWrapper> windowTitles =
                TitleFinder.getWindowTitles(testFrame);
        Iterator<FrameWrapper> iterator = windowTitles.iterator();
        while (iterator.hasNext()) {
            FrameWrapper frame = iterator.next();
            String title = frame.getTitle();
            if (!title.contains("Unsaved Library")) {
                return title;
            }
        }
        return null;
    }

    int getTabIndexByName(JTabbedPane tabbedPane, String tabName) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals(tabName)) {
                return i;
            }
        }
        return -1;
    }

    @Test
    public void testEditPatch() throws Exception {
        URL resource = getClass().getResource("/patchEditor/" + xmlFile);
        if (resource == null) {
            return;
        }
        File outputFile = new File(resource.toURI());
        guiHandler.uninstallDevice(null);
        guiHandler.setTestMidiDevices();
        TestDocumentHandler testDocumentHandler =
                new TestDocumentHandler(outputFile, testFrame);
        documentHandler = testDocumentHandler;
        deviceNameField = testDocumentHandler.getDeviceName();
        handleXmlFile(testDocumentHandler.getManufacturer(), deviceNameField);
    }
}
