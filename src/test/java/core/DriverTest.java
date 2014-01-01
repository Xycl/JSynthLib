package core;

import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.ContainerFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
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

@SuppressWarnings("rawtypes")
@RunWith(Parameterized.class)
public class DriverTest {

    private static final Logger LOG = Logger.getLogger(DriverTest.class);
    private static final File OUT_DIR = new File("output");

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
        ArrayList<String> list = new ArrayList<String>();
        try {
            Properties properties = new Properties();
            properties.load(DriverTest.class
                    .getResourceAsStream("/synthdrivers.properties"));
            Iterator<Entry<Object, Object>> iterator =
                    properties.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<Object, Object> entry = iterator.next();
                String key = (String) entry.getKey();
                if (key.startsWith("deviceName.")) {
                    String driverName = (String) entry.getValue();
                    String filename = getXmlFile(driverName).getName();
                    list.add(filename);
                }
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }
        LOG.info(list.toString());
        Object[][] objects = new Object[list.size()][1];
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            objects[i] = new Object[] {
                s };

        }
        return Arrays.asList(objects);
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
            setUpOnce();
            DriverTest driverTest = new DriverTest(null);
            driverTest.setUp();

            // uninstall all previously installed drivers.
            driverTest.guiHandler.uninstallDevice(null);
            driverTest.setupMidi();
            Properties properties = new Properties();
            properties.load(DriverTest.class
                    .getResourceAsStream("/synthdrivers.properties"));
            Iterator<Entry<Object, Object>> iterator =
                    properties.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<Object, Object> entry = iterator.next();
                String key = (String) entry.getKey();
                if (key.startsWith("deviceName.")) {
                    String driverName = properties.getProperty(key);
                    String manufacturer =
                            properties.getProperty("manufacturer."
                                    + key.substring("deviceName.".length()));
                    LOG.info("Generating " + manufacturer + "/" + driverName);
                    if ("Generic Unknown Driver".equals(driverName)) {
                        LOG.info("Skipping generic driver...");
                        continue;
                    }

                    File outputFile = getXmlFile(driverName);
                    File file = new File(OUT_DIR, outputFile.getName());
                    if (file.exists()) {
                        LOG.info("Skipping already parsed device: "
                                + driverName);
                        continue;
                    }

                    driverTest.documentHandler =
                            new CreateDocumentHandler(file,
                                    driverTest.testFrame);
                    String deviceName = driverName.replace(" Driver", "");
                    driverTest.handleXmlFile(manufacturer, deviceName);
                }
            }
            System.exit(0);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    static File getXmlFile(String driverName) {
        String deviceName = driverName.replace(" Driver", "");
        return new File(deviceName.replace('/', ' ') + ".xml");
    }

    private FrameFixture testFrame;
    private GuiHandler guiHandler;
    private String xmlFile;
    private AbstractDocumentHandler documentHandler;
    private String deviceNameField;

    public DriverTest(String xmlFile) {
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

    void setupMidi() {
        guiHandler.setTestMidiDevices();
    }

    Properties setupXmlFile(String manufacturer, String deviceName)
            throws InterruptedException, IOException {
        Properties excludedDrivers = new Properties();
        excludedDrivers.load(getClass().getResourceAsStream(
                "/excludeddrivers.properties"));

        documentHandler.handleDocument(manufacturer, deviceName);

        LOG.info("Installing " + manufacturer + "/" + deviceName);
        guiHandler.installDevice(manufacturer, deviceName + " Driver");
        return excludedDrivers;
    }

    void handleXmlFile(String manufacturer, String deviceName)
            throws InterruptedException, IOException {
        try {
            deviceNameField = deviceName;
            Properties excludedDrivers = setupXmlFile(manufacturer, deviceName);

            LOG.info("Opening library");
            JTableFixture table = guiHandler.openLibrary();

            LOG.info("Receiving drivers for device " + deviceName);
            List<Class> driversForDevice =
                    guiHandler.getDriversForDevice(deviceName);
            for (Class driverClass : driversForDevice) {
                if (excludedDrivers.containsKey(driverClass.getName())) {
                    LOG.info("Skipping excluded driver "
                            + driverClass.getName());
                    continue;
                }

                LOG.info("Using driver: " + driverClass.getName());
                PopupListener driverPopupListener = new PopupListener();
                guiHandler.newPatch(deviceName, driverClass,
                        driverPopupListener);

                Xmldriver xmldriver =
                        documentHandler.handleDriver(driverClass.getName());

                handlePatchStore(table, xmldriver);

                LOG.info("Opening patch editor");
                PopupListener editorPopupListener = new PopupListener();
                ContainerFixture patchEditor =
                        guiHandler.openPatchEditor(table, editorPopupListener);
                List<PopupContainer> popups = driverPopupListener.getPopups();

                try {
                    if (patchEditor == null) {
                        LOG.warn("Patch Editor is null!");
                        documentHandler.handleEditor(xmldriver, false, null,
                                popups);
                        continue;
                    } else {
                        String title = findEditorTitle(deviceName);

                        if (title == null) {
                            LOG.warn("Could not find editor name!");
                            documentHandler.handleEditor(xmldriver,
                                    false, null, popups);
                            continue;
                        }
                        Xmleditor editor =
                                documentHandler.handleEditor(xmldriver, true, title,
                                        popups);

                        Container cont = (Container) patchEditor.component();
                        JPanel jPanel =
                                guiHandler.findContentPanelRecursive(cont);
                        List<SysexWidget> sysexWidgets =
                                SysexWidgetFinder.findSysexWidgets(jPanel);
                        if (sysexWidgets.isEmpty()) {
                            // Patch Bank
                            JPanelFixture fixture =
                                    new JPanelFixture(testFrame.robot, jPanel);
                            JTableFixture bankTable = fixture.table();

                            documentHandler.handlePatch(editor, bankTable);
                        } else {
                            // Single editor
                            handleSingleEditor(editor, sysexWidgets, jPanel);
                        }
                    }
                } finally {
                    if (patchEditor != null) {
                        LOG.info("Close patch editor frame");
                        guiHandler.closeFrame(patchEditor);

                        LOG.info("Selecting library frame");
                        guiHandler.selectLibraryFrame();
                    }
                }
            }
            documentHandler.saveDocument();
        } finally {
            tearDownXmlFile(deviceNameField);
        }
    }

    void tearDownXmlFile(String deviceName) throws InterruptedException,
            IOException {
        LOG.info("Closing library");
        guiHandler.closeLibrary();

        LOG.info("Uninstall device " + deviceName);
        guiHandler.uninstallDevice(deviceName);
    }

    void handlePatchStore(JTableFixture table, Xmldriver xmldriver) {
        LOG.info("Handle patch store");
        try {
            Map<String, List<String>> bankMap =
                    guiHandler.getPatchStoreOptions(table);

            LOG.debug("Got " + bankMap.keySet().size() + " banks to store.");
            documentHandler.handleStore(xmldriver, table, bankMap);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        } finally {
            LOG.info("Selecting library frame");
            guiHandler.selectLibraryFrame();
        }
    }

    void handleSingleEditor(Xmleditor editor, List<SysexWidget> sysexWidgets,
            JPanel jPanel) {

        for (final SysexWidget sysexWidget : sysexWidgets) {
            documentHandler.handleParam(editor, sysexWidget, jPanel);
        }
    }

    String findEditorTitle(String deviceName) {
        Map<String, ContainerFixture> windowTitles =
                TitleFinder.getWindowTitles(testFrame);
        Iterator<String> iterator = windowTitles.keySet().iterator();
        while (iterator.hasNext()) {
            String title = iterator.next();
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
        URL resource = getClass().getResource("/" + xmlFile);
        if (resource == null) {
            return;
        }
        File outputFile = new File(resource.toURI());
        guiHandler.uninstallDevice(null);
        TestDocumentHandler testDocumentHandler =
                new TestDocumentHandler(outputFile, testFrame);
        documentHandler = testDocumentHandler;
        deviceNameField = testDocumentHandler.getDeviceName();
        handleXmlFile(testDocumentHandler.getManufacturer(), deviceNameField);
    }
}
