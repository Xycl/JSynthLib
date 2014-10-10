package org.jsynthlib.core;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTableFixture;
import org.jsynthlib.core.SynthDriversManager.ManufacturerDriverPair;
import org.jsynthlib.core.TitleFinder.FrameWrapper;
import org.jsynthlib.core.guiaction.AbstractGuiAction.IPopupListener;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.device.viewcontroller.widgets.AbstractPatchHandler;
import org.jsynthlib.device.viewcontroller.widgets.SysexWidget;
import org.jsynthlib.patch.model.impl.PatchEdit;
import org.jsynthlib.patchtest.PatchFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PatchLoadingTest {

    private static final Logger LOG = Logger.getLogger(PatchLoadingTest.class);
    private static final File OUT_DIR = new File("src/test/resources/patchLoading");

    public static void main(String[] args) {
        try {
            if (!OUT_DIR.exists()) {
                OUT_DIR.mkdir();
            }
            System.setProperty(PatchEditorTest.TEST_LEVEL,
                    Integer.toString(PatchEditorTest.TESTLEVEL_HIGH));
            PatchLoadingTest driverTest = new PatchLoadingTest(null);
            driverTest.setUp();

            // uninstall all previously installed drivers.
            driverTest.guiHandler.uninstallDevice(null);
            driverTest.setupMidi();
            List<ManufacturerDriverPair> deviceList =
                    SynthDriversManager.getDeviceList();
            Iterator<ManufacturerDriverPair> iterator = deviceList.iterator();
            while (iterator.hasNext()) {
                ManufacturerDriverPair entry = iterator.next();
                String driverName = entry.getDriverName();
                String manufacturer = entry.getManufacturer();
                LOG.info("Generating " + manufacturer + "/" + driverName);

                File outputFile = PatchEditorTest.getXmlFile(driverName);
                File file = new File(OUT_DIR, outputFile.getName());
                if (file.exists()) {
                    LOG.info("Skipping already parsed device: " + driverName);
                    continue;
                }

                String deviceName = driverName.replace(" Driver", "");

                driverTest.documentHandler =
                        new CreatePatchHandler(file, driverTest.testFrame);
                driverTest.handleXmlFile(manufacturer, deviceName);
            }
            System.exit(0);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    @Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return PatchEditorTest.data();
    }
    
    private FrameFixture testFrame;
    private GuiHandler guiHandler;
    private AbstractPatchHandler documentHandler;
    private final String xmlFile;
    private String deviceNameField;
    private final File patchTestFolder;

    public PatchLoadingTest(String xmlFile) {
        TestInitializer testInitializer = TestInitializer.getInstance();
        patchTestFolder = testInitializer.getPatchTestFolder();
        this.xmlFile = xmlFile;
    }

    void setupMidi() {
        guiHandler.setTestMidiDevices();
    }

    void copyFile(InputStream is, String filename, File outdir)
            throws IOException {
        OutputStream os = null;
        try {

            os = new FileOutputStream(new File(outdir, filename));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = is.read(bytes)) != -1) {
                os.write(bytes, 0, read);
            }
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        testFrame = new FrameFixture(PatchEdit.getInstance());
        testFrame.show();
        testFrame.maximize();
        guiHandler = new GuiHandler(testFrame);
    }

    @After
    public void tearDown() throws Exception {
        testFrame.cleanUp();
    }

    void setupPreferences(String manufacturer, String deviceName) {
        DialogFixture preferencesDialog = guiHandler.openPreferencesDialog();

        JTabbedPaneFixture tabbedPane = preferencesDialog.tabbedPane();
        tabbedPane.selectTab("File & Directories");
        preferencesDialog.button("SysexFilePath").click();

        JFileChooserFixture fileChooser = testFrame.fileChooser();
        fileChooser.selectFile(patchTestFolder.getAbsoluteFile());
        fileChooser.approve();

        guiHandler.closeDialog(preferencesDialog);

        documentHandler.handleDocument(manufacturer, deviceName);

        guiHandler.installDevice(manufacturer, deviceName);
    }

    void handleXmlFile(String manufacturer, String deviceName)
            throws InterruptedException, IOException {
        try {
            setupPreferences(manufacturer, deviceName);

            FrameWrapper library = guiHandler.openLibrary();

            LOG.info("Receiving drivers for device " + deviceName);
            List<Class<? extends IDriver>> driversForDevice =
                    guiHandler.getDriversForDevice(deviceName);

            for (Class<?> driverClass : driversForDevice) {
                PatchFile patchFile =
                        documentHandler.handleDriver(driverClass.getName());
                if (patchFile == null) {
                    continue;
                }

                String filename = patchFile.getFileName();
                InputStream is =
                        getClass().getResourceAsStream("/patchLoading/" + filename);
                if (is == null) {
                    continue;
                }
                copyFile(is, filename, patchTestFolder);

                guiHandler.openDialog("Import...");

                JFileChooserFixture syxChooser = testFrame.fileChooser();
                File currDir = syxChooser.target.getCurrentDirectory();
                assertEquals("Check selected folder",
                        patchTestFolder.getName(), currDir.getName());

                File syxFile =
                        new File(patchTestFolder, patchFile.getFileName());
                syxChooser.selectFile(syxFile.getAbsoluteFile());
                syxChooser.approve();

                JTableFixture table = library.table();
                documentHandler.handlePatchRow(table, patchFile);

                FrameWrapper patchEditor =
                        guiHandler.openPatchEditor(library.table(), -1, 0,
                                new IPopupListener() {

                                    @Override
                                    public void onPopupDetected(
                                            DialogFixture dialog) {
                                    }
                                }, false);
                if (patchEditor == null) {
                    LOG.warn("Patch Editor is null!");
                } else {
                    List<SysexWidget> sysexWidgets =
                            SysexWidgetFinder.findSysexWidgets(patchEditor);
                    if (sysexWidgets.isEmpty()) {
                        // Patch Bank
                        JTableFixture bankTable = patchEditor.table();
                        documentHandler.handleBankEditor(bankTable, patchFile);
                    } else {
                        // Single editor
                        for (int i = 0; i < sysexWidgets.size(); i++) {
                            SysexWidget sysexWidget = sysexWidgets.get(i);
                            documentHandler.handleParam(patchEditor,
                                    sysexWidget, i, patchFile);
                        }
                    }

                    guiHandler.closeFrame(patchEditor, false);
                }
            }

            guiHandler.closeLibrary(library);

            documentHandler.saveDocument();
        } finally {
            LOG.info("Uninstall device " + deviceName);
            guiHandler.uninstallDevice(deviceName);
        }
    }

    @Test
    public void testPatchLoading() throws Exception {
        URL resource = getClass().getResource("/patchLoading/" + xmlFile);
        if (resource == null) {
            return;
        }
        File outputFile = new File(resource.toURI());
        guiHandler.uninstallDevice(null);
        TestPatchHandler testPatchHandler =
                new TestPatchHandler(outputFile, testFrame);
        documentHandler = testPatchHandler;
        deviceNameField = testPatchHandler.getDeviceName();
        if (!testPatchHandler.isEmpty()) {
            handleXmlFile(testPatchHandler.getManufacturer(), deviceNameField);
        }
    }

}
