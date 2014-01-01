package core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Dialog;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.swing.JLabel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.apache.log4j.Logger;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.ContainerFixture;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JLabelFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import synthdrivers.RolandD50.D50SingleDriver;
import core.guiaction.AbstractGuiAction.IPopupListener;

@SuppressWarnings("rawtypes")
public class GuiTest {

    private final Logger log = Logger.getLogger(getClass());

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
        GuiActionRunner.execute(new GuiQuery<PatchEdit>() {
            protected PatchEdit executeInEDT() {
                return new PatchEdit(new ArrayList<String>(), 2);
            }
        });
    }

    private FrameFixture testFrame;
    private GuiHandler guiHandler;

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

    @Test
    public void testInstallDriver() throws InterruptedException {
        guiHandler.installDevice("Roland", "Roland D-50 Driver");
        boolean removeDriver = guiHandler.uninstallDevice("Roland D-50");

        assertTrue(removeDriver);

        Thread.sleep(1000);
    }

    @Test
    public void testOpenAndCloseEditor() throws Exception {
        guiHandler.installDevice("Roland", "Roland D-50 Driver");
        JTableFixture table = guiHandler.openLibrary();
        Thread.sleep(100);

        IPopupListener listener = new IPopupListener() {

            @Override
            public void onPopupDetected(DialogFixture dialog) {
            }
        };
        guiHandler.newPatch("Roland D-50", D50SingleDriver.class, listener);
        guiHandler.openPatchEditor(table, listener);

        Map<String, ContainerFixture> windowTitles =
                TitleFinder.getWindowTitles(testFrame);
        Thread.sleep(500);
        ContainerFixture d50Fixture = null;
        boolean foundLibraryTitle = false;
        for (Entry<String, ContainerFixture> windowTitle : windowTitles
                .entrySet()) {
            log.info(windowTitle.getKey());
            if (windowTitle.getKey().contains("Unsaved Library")) {
                foundLibraryTitle = true;
            } else if (windowTitle.getKey().contains("D-50")) {
                d50Fixture = windowTitle.getValue();
            }
        }
        assertTrue(foundLibraryTitle);
        assertNotNull(d50Fixture);

        Thread.sleep(1000);

        guiHandler.closeFrame(d50Fixture);

        boolean removeDriver = guiHandler.uninstallDevice("Roland D-50");

        assertTrue(removeDriver);
    }

    @Test
    public void testMidiSettings() throws MidiUnavailableException {
        Info[] infos = MidiSystem.getMidiDeviceInfo();

        int outIndex = 0;
        int inIndex = 0;
        for (int i = 0; i < infos.length; i++) {
            Info info = infos[i];
            MidiDevice midiDevice = MidiSystem.getMidiDevice(info);
            if (midiDevice instanceof Synthesizer
                    || midiDevice instanceof Sequencer) {
                continue;
            }
            String name = midiDevice.getDeviceInfo().getName();

            if (midiDevice.getMaxReceivers() != 0) {
                guiHandler.setMidiOutDevice(name);
                int initPortOut = AppConfig.getInitPortOut();
                assertEquals(outIndex, initPortOut);
                outIndex++;
            } else {
                guiHandler.setMidiInDevice(name);
                int initPortIn = AppConfig.getInitPortIn();
                assertEquals(inIndex, initPortIn);
                inIndex++;
            }
        }
    }

    @Test
    public void testLicense() {
        DialogFixture fixture = guiHandler.openDialog("License");
        Dialog dialog = fixture.target;
        String title = dialog.getTitle();
        assertEquals("JSynthLib Documentation Viewer", title);
        JTextComponentFixture textBoxFixture = fixture.textBox();
        assertTrue(textBoxFixture.target.getText().contains(
                "GNU GENERAL PUBLIC LICENSE"));
    }

    @Test
    public void testHelp() throws BadLocationException {
        DialogFixture fixture = guiHandler.openHelpDialog();
        Dialog dialog = fixture.target;
        String title = dialog.getTitle();
        assertEquals("JSynthLib Documentation Viewer", title);
        JTextComponentFixture textBoxFixture = fixture.textBox();
        Document document = textBoxFixture.target.getDocument();
        assertTrue(document.getText(0, document.getLength()).contains(
                "JSynthLib runs under Windows, GNU/Linux, and Mac OS X."));
    }

    @Test
    public void testAbout() {
        DialogFixture fixture = guiHandler.openDialog("About");
        Dialog dialog = fixture.target;
        String title = dialog.getTitle();
        assertEquals("About JSynthLib", title);
        JPanelFixture panel = fixture.panel("OptionPane.body");
        JLabelFixture label =
                panel.label(new GenericTypeMatcher<JLabel>(JLabel.class) {

                    @Override
                    protected boolean isMatching(JLabel component) {
                        return component.getText()
                                .contains("JSynthLib Version");
                    }
                });
        assertNotNull(label);

        label = panel.label(new GenericTypeMatcher<JLabel>(JLabel.class) {

            @Override
            protected boolean isMatching(JLabel component) {
                return component.getText().contains("Copyright (C)");
            }
        });
        assertNotNull(label);

        label = panel.label(new GenericTypeMatcher<JLabel>(JLabel.class) {

            @Override
            protected boolean isMatching(JLabel component) {
                return component.getText().contains("'Help -> License'");
            }
        });
        assertNotNull(label);
    }

    @Test
    public void testPlayNote() {
        DialogFixture fixture = guiHandler.openPreferencesDialog();

        JTabbedPaneFixture tabbedPane = fixture.tabbedPane();
        tabbedPane.selectTab("Play Note");
        Dialog dialog = fixture.target;
        // fixture.
    }

    @Test
    public void testDivision() {
        int[] values = {
                0, 3, 4, 6, 7, 10, 14, 15, 16, 19, 20 };
        for (int i : values) {
            int percent = i % 4;
            int dvision = i / 4;
            System.out.println(i + " div " + dvision + " per " + percent);
        }
    }
}
