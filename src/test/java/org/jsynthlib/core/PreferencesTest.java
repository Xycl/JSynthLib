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
package org.jsynthlib.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Dialog;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.apache.log4j.Logger;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JLabelFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JSliderFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.jsynthlib.core.TitleFinder.FrameWrapper;
import org.jsynthlib.core.guiaction.AbstractGuiAction.IPopupListener;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.midi.domain.MidiSettings;
import org.jsynthlib.patch.model.PatchEditFactory;
import org.jsynthlib.patch.model.impl.PatchEdit;
import org.jsynthlib.synthdrivers.RolandD50.D50SingleDriver;
import org.jsynthlib.synthdrivers.WaldorfPulse.WaldorfPulseSingleDriver;
import org.jsynthlib.utils.SingletonMidiDeviceProvider;
import org.jsynthlib.utils.SingletonMidiDeviceProvider.MidiRecordSession;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class PreferencesTest {

    private final transient Logger log = Logger.getLogger(getClass());

    private final IPopupListener listener = new IPopupListener() {

        @Override
        public void onPopupDetected(DialogFixture dialog) {
        }
    };

    @BeforeClass
    public static void setUpOnce() {
        Assume.assumeTrue(OsUtil.isWindows());
        FailOnThreadViolationRepaintManager.install();
        GuiActionRunner.execute(new GuiQuery<PatchEdit>() {
            @Override
            protected PatchEdit executeInEDT() {
                PatchEditFactory patchEditFactory =
                        JSynthLibInjector.getInstance(PatchEditFactory.class);
                return patchEditFactory
                        .newPatchEdit(new ArrayList<String>(), 2);
            }
        });
    }

    private FrameFixture testFrame;
    private GuiHandler guiHandler;
    private SingletonMidiDeviceProvider midiDeviceProvider;

    private DialogFixture fixture;

    @Before
    public void setUp() throws Exception {
        testFrame = new FrameFixture(PatchEdit.getInstance());
        testFrame.show();
        testFrame.maximize();
        guiHandler = new GuiHandler(testFrame);
        midiDeviceProvider = SingletonMidiDeviceProvider.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        if (fixture != null) {
            guiHandler.closeDialog(fixture);
            fixture = null;
        }
        guiHandler.uninstallDevice(null);
        testFrame.cleanUp();
    }

    @Test
    public void testInstallDriver() throws InterruptedException {
        guiHandler.installDevice("Roland", "Roland D-50");
        boolean removeDriver = guiHandler.uninstallDevice("Roland D-50");

        assertTrue(removeDriver);

        Thread.sleep(1000);
    }

    @Test
    public void testOpenAndCloseEditor() throws Exception {
        guiHandler.installDevice("Roland", "Roland D-50");
        FrameWrapper library = guiHandler.openLibrary();
        JTableFixture table = library.table();
        Thread.sleep(100);

        guiHandler.newPatch(library, "Roland D-50", D50SingleDriver.class,
                listener);
        FrameWrapper d50Fixture =
                guiHandler.openPatchEditor(table, -1, 0, listener, false);

        assertNotNull(d50Fixture);

        Thread.sleep(1000);

        guiHandler.closeFrame(d50Fixture, false);

        guiHandler.closeLibrary(library);
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

            MidiSettings midiSettings =
                    JSynthLibInjector.getInstance(MidiSettings.class);

            if (midiDevice.getMaxReceivers() != 0) {
                guiHandler.setMidiOutDevice(name);
                int initPortOut = midiSettings.getInitPortOut();
                assertEquals(outIndex, initPortOut);
                outIndex++;
            } else {
                guiHandler.setMidiInDevice(name);
                int initPortIn = midiSettings.getInitPortIn();
                assertEquals(inIndex, initPortIn);
                inIndex++;
            }
        }
    }

    @Test
    public void testLicense() {
        fixture = guiHandler.openDialog("License");
        Dialog dialog = fixture.target;
        String title = dialog.getTitle();
        assertEquals("JSynthLib Documentation Viewer", title);
        JTextComponentFixture textBoxFixture = fixture.textBox();
        assertTrue(textBoxFixture.target.getText().contains(
                "GNU GENERAL PUBLIC LICENSE"));
    }

    @Test
    public void testHelp() throws BadLocationException {
        fixture = guiHandler.openHelpDialog();
        Dialog dialog = fixture.target;
        String title = dialog.getTitle();
        assertEquals("JSynthLib Documentation Viewer", title);
        JTextComponentFixture textBoxFixture = fixture.textBox();
        Document document = textBoxFixture.target.getDocument();
        String text = document.getText(0, document.getLength());
        log.info("Help text: '" + text + "'");
        assertTrue(text.contains(
                "The Main Windows"));
    }

    @Test
    public void testAbout() {
        fixture = guiHandler.openDialog("About");
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
    public void testPlayNote() throws Exception {
        guiHandler.setTestMidiDevices();
        guiHandler.installDevice("Roland", "Roland D-50");

        fixture = guiHandler.openPreferencesDialog();

        JTabbedPaneFixture tabbedPane = fixture.tabbedPane();
        tabbedPane.selectTab("Play Note");
        Dialog dialog = fixture.target;
        JSliderFixture noteSlider = fixture.slider("midiNote");
        int noteMaxValue = noteSlider.target.getMaximum();
        int noteMinValue = noteSlider.target.getMinimum();
        int noteIncr = (noteMaxValue - noteMinValue) / 4;

        JSliderFixture velocity = fixture.slider("velocity");
        int velocityMaxValue = velocity.target.getMaximum();
        int velocityMinValue = velocity.target.getMinimum();
        int velocityIncr = (velocityMaxValue - velocityMinValue) / 4;

        JSliderFixture duration = fixture.slider("duration");
        int durationMaxValue = duration.target.getMaximum();
        int durationMinValue = duration.target.getMinimum();
        int durationIncr = (durationMaxValue - durationMinValue) / 4;

        guiHandler.closeDialog(fixture);

        FrameWrapper library = guiHandler.openLibrary();
        JTableFixture table = library.table();
        Thread.sleep(100);

        guiHandler.newPatch(library, "Roland D-50", D50SingleDriver.class,
                listener);

        for (int i = 0; i < 4; i++) {
            MidiRecordSession session = midiDeviceProvider.openSession();
            int noteValue = noteMinValue + i * noteIncr;
            int velocityValue = velocityMinValue + i * velocityIncr;
            int durationValue = durationMinValue + i * durationIncr;
            guiHandler.setPlayNoteValues(noteValue, velocityValue,
                    durationValue);
            guiHandler.playNote(table);
            String midiMessage = midiDeviceProvider.closeSession(session);
            String[] split = midiMessage.split(";");
            assertEquals(3, split.length);
            String noteOn = split[1];
            String noteOff = split[2];
            Pattern pattern = Pattern.compile("[0-9A-F]{2}");
            Matcher matcher = pattern.matcher(noteOn);
            assertTrue(matcher.find());
            assertEquals("90", matcher.group());
            assertTrue(matcher.find());
            int decimal = Integer.parseInt(matcher.group(), 16);
            assertEquals(noteValue, decimal);
            log.info("Note value is " + decimal + " " + matcher.group());
            assertTrue(matcher.find());
            decimal = Integer.parseInt(matcher.group(), 16);
            assertEquals(velocityValue, decimal);
            log.info("Velocity value is " + decimal + " " + matcher.group());

            matcher.reset(noteOff);
            assertTrue(matcher.find());
            assertEquals("90", matcher.group());
            assertTrue(matcher.find());
            decimal = Integer.parseInt(matcher.group(), 16);
            assertEquals(noteValue, decimal);
            assertTrue(matcher.find());
            decimal = Integer.parseInt(matcher.group(), 16);
            assertEquals(0, decimal);
        }

        guiHandler.closeLibrary(library);
        guiHandler.uninstallDevice("Roland D-50");
    }

    @Ignore
    @Test
    public void testFaderBox() throws Exception {
        guiHandler.setTestMidiDevices();
        guiHandler.installDevice("Waldorf", "Waldorf Pulse/Pulse+");

        fixture = guiHandler.openPreferencesDialog();

        JTabbedPaneFixture tabbedPane = fixture.tabbedPane();
        tabbedPane.selectTab("Fader Box");
        fixture.checkBox(new GenericTypeMatcher<JCheckBox>(JCheckBox.class) {

            @Override
            protected boolean isMatching(JCheckBox component) {
                return component.isShowing();
            }
        }).check();
        fixture.button(new GenericTypeMatcher<JButton>(JButton.class) {

            @Override
            protected boolean isMatching(JButton component) {
                return component.getText().contains("Peavey")
                        && component.isShowing();
            }
        }).click();
        guiHandler.closeDialog(fixture);

        FrameWrapper library = guiHandler.openLibrary();
        guiHandler.newPatch(library, "Waldorf Pulse/Pulse+",
                WaldorfPulseSingleDriver.class, listener);
        FrameWrapper patchEditor =
                guiHandler.openPatchEditor(library.table(), -1, 0, listener,
                        false);

        ShortMessage shortMessage = new ShortMessage();
        shortMessage.setMessage(ShortMessage.CONTROL_CHANGE, 1, 24, 30);
        midiDeviceProvider.sendMidi(shortMessage);

        Thread.sleep(500);

        shortMessage.setMessage(ShortMessage.CONTROL_CHANGE, 1, 24, 80);
        midiDeviceProvider.sendMidi(shortMessage);
    }
}
