package org.jsynthlib.midi.service.impl;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

import org.apache.log4j.Logger;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.midi.domain.MidiSettings;
import org.jsynthlib.midi.service.MidiLoopbackService;
import org.jsynthlib.midi.service.MidiMessageFormatter;
import org.jsynthlib.midi.service.MidiService;

@Singleton
public class MidiLoopbackServiceImpl implements MidiLoopbackService {

    /** Max data size of system exclusive message test. */
    private static final int MAX_SYSEX_SIZE = 500;

    private static final String ERROR_MSG_STRING =
            "Data Compare Error:\nReceived data: %s\nexpected data: %s";

    private final transient Logger log = Logger.getLogger(getClass());

    private MidiSettings midiSettings;

    private MidiService midiService;

    @Override
    public boolean runLoopbackTest(int inport, int outport) {
        ErrorMsg.reportError(
                "Before we start",
                "This process is designed to test the communication of\n"
                        + "your MIDI ports. Please connect a MIDI cable from the\n"
                        + "selected \"IN\" port to the selected \"OUT\" port.\n"
                        + "When you press \"OK\", a variety of MIDI messages will\n"
                        + "be sent from the OUT to the IN to make sure that they\n"
                        + "transferred properly. This process usually takes about\n"
                        + "5 to 10 seconds, so be patient.\n");

        try {
            String outputName = midiSettings.getOutputName(outport);
            List<MidiMessage> msgList = getMidiMessages();

            /*
             * This section is for creating/driving a progress bar. However,
             * since this method is currently getting run by the UI thread, all
             * UI updates stop while the test is running, so the progress bar
             * never gets updated. I probably need to execute this thing in a
             * separate thread in order to make this work. - emenaker 2003.03.26
             */
            /*
             * JDialog dialog = new JDialog(); JPanel panel = new JPanel();
             * JProgressBar pbar = new JProgressBar(); panel.add(pbar);
             * dialog.getContentPane().add(panel); dialog.pack(); dialog.show();
             * pbar.setMinimum(0); pbar.setMaximum(msgList.size());
             */
            for (int i = 0; i < msgList.size(); i++) {
                // pbar.setValue(i);
                runLoopbackTest(outputName, inport, outport, msgList.get(i));
            }

            // If we get this far, then things must have gone okay....
            ErrorMsg.reportError(
                    "Congratulations!",
                    "The test appears to have completed successfully.\n"
                            + "Don't forget to hook your MIDI cables back up to your devices.");
        } catch (Exception e) {
            ErrorMsg.reportError(
                    "Warning",
                    "The test failed.\n"
                            + "There are many reasons why this could have happened.\n"
                            + "Most likely, you don't have the selected \"IN\" and \"OUT\" ports\n"
                            + "connected to each other with a MIDI cable.", e);
            return false;
        }
        return true;
    }

    /**
     * This runs a few tests on a midi in/out pair. The idea is that you connect
     * the two ports with a midi cable and it basically tries to send one of
     * every kind of message and it checks to see if those messages get back to
     * the "in" port intact.
     */
    void runLoopbackTest(String outputName, int inport, int outport, MidiMessage msg)
            throws Exception {
        midiService.clearSysexInputQueue(inport);

        // If it's a sysex message, we need to make sure that it's got a 0xF7 on
        // the end.
        // If not, we'll put one on...
        if (msg instanceof SysexMessage) {
            // We need to send the stop message....
            // SysexMessage sysexstop = new SysexMessage();
            byte[] buffer = msg.getMessage();
            int len = msg.getLength();
            // buffer.length may not be equal to msg.getLength()
            // if(buffer[buffer.length-1] != (byte)
            // ShortMessage.END_OF_EXCLUSIVE) {
            if (buffer[len - 1] != (byte) ShortMessage.END_OF_EXCLUSIVE) {
                // There's not a 0xF7 at the end. We need to put one there....
                buffer = new byte[len + 1];
                System.arraycopy(msg.getMessage(), 0, buffer, 0, len);
                buffer[len] = (byte) ShortMessage.END_OF_EXCLUSIVE;
                ((SysexMessage) msg).setMessage(buffer, buffer.length);
            }
        }

        // Send it
        midiService.send(outputName, msg, midiSettings.getMidiOutBufSize(),
                midiSettings.getMidiOutDelay());

        try {
            // 1 sec =~ 4KB sysex data
            MidiMessage inmsg = midiService.getMessage(inport, 1000);
            if (areEqual(msg, inmsg)) {
                return;
            } else {
                String actualMsg =
                        MidiMessageFormatter.midiMessageToString(inmsg, true);
                String expectedMsg =
                        MidiMessageFormatter.midiMessageToString(msg, true);
                throw new InputMismatchException(String.format(
                        ERROR_MSG_STRING, actualMsg, expectedMsg));
            }
        } catch (TimeoutException e) {
            ErrorMsg.reportError("Warning",
                    "Didn't see anything come into the input");
            throw e;
        }
    }

    /**
     * This just returns a vector full of MidiMessage objects that the tester
     * uses to send out through the loopback. - emenaker 2003.03.24
     * @throws Exception
     */
    private List<MidiMessage> getMidiMessages() throws Exception {
        List<MidiMessage> msgList = new ArrayList<MidiMessage>();
        ShortMessage msg = new ShortMessage();
        // XXX shortMessage test does not work with new MIDI layer.
        if (midiSettings.isTestShortMessage()) {
            // Make a bunch of messages and try sending
            // them. Why use data bytes 0x4B, 0x70? Well,
            // it's binary 0100110001110000 (a zero, a
            // one, two zeroes, two ones, etc.) I wanted
            // to pick some sequence that, if it was
            // shifted a little bit, wouldn't match
            // iteself - emenaker 2003.03.20

            // Channel Voice Messages
            // lower MIDI driver may convert to NOTE_ON for running status
            // msg.setMessage(ShortMessage.NOTE_OFF, 0x4B, 0x70); // 2B
            // msgList.add((MidiMessage) msg.clone());
            msg.setMessage(ShortMessage.NOTE_ON, 0x4B, 0x70); // 2B
            msgList.add((MidiMessage) msg.clone());
            msg.setMessage(ShortMessage.POLY_PRESSURE, 0x4B, 0x70); // 2B
            msgList.add((MidiMessage) msg.clone());
            msg.setMessage(ShortMessage.CONTROL_CHANGE, 0x4B, 0x70); // 2B
            msgList.add((MidiMessage) msg.clone());
            msg.setMessage(ShortMessage.PROGRAM_CHANGE, 0x4B, 0x70); // 1B
            msgList.add((MidiMessage) msg.clone());
            msg.setMessage(ShortMessage.CHANNEL_PRESSURE, 0x4B, 0x70); // 1B
            msgList.add((MidiMessage) msg.clone());
            msg.setMessage(ShortMessage.PITCH_BEND, 0x4B, 0x70); // 2B
            msgList.add((MidiMessage) msg.clone());

            // System Common Messages
            msg.setMessage(ShortMessage.MIDI_TIME_CODE, 0x4B, 0x70); // 1B
            msgList.add((MidiMessage) msg.clone());
            msg.setMessage(ShortMessage.SONG_POSITION_POINTER, 0x4B, 0x70); // 2B
            msgList.add((MidiMessage) msg.clone());
            msg.setMessage(ShortMessage.SONG_SELECT, 0x4B, 0x70); // 1B
            msgList.add((MidiMessage) msg.clone());
        }
        if (midiSettings.isTestSysexMessage()) {
            // Sysex messages
            SysexMessage sysexmsg = new SysexMessage();

            byte[] sysexpayload = new byte[MAX_SYSEX_SIZE];
            // Initialize the buffer... making sure that
            // no bytes are greater than 128
            sysexpayload[0] = (byte) 0xF0; // Sysex-Start command
            for (int i = 1; i < sysexpayload.length; i++) {
                sysexpayload[i] = (byte) (i % 128);
            }
            // Start with a message of a certain size and
            // keep increasing until we get to the biggest
            // size we've got - emenaker 2003.03.20
            for (int i = 10; i < sysexpayload.length; i += 20) {
                sysexmsg.setMessage(sysexpayload, i);
                msgList.add((MidiMessage) sysexmsg.clone());
            }
        }
        return (msgList);
    }

    /**
     * This tests two MidiMessage objects for equality. A byte-by-byte
     * comparison of the actual message data is performed. - emenaker 2003.03.23
     * @param msg1
     * @param msg2
     * @return True if the messages are equal
     */
    public boolean areEqual(MidiMessage msg1, MidiMessage msg2) {
        int msg1size = msg1.getLength();
        int msg2size = msg2.getLength();
        log.info(msg1 + ", " + msg1size + ": " + msg2 + ", " + msg2size);
        if (msg1size != msg2size) {
            return false;
        }

        byte[] thisdata = msg1.getMessage();
        byte[] thatdata = msg2.getMessage();
        for (int i = 0; i < msg1size; i++) {
            if (thisdata[i] != thatdata[i]) {
                return false;
            }
        }
        // If we made it this far, then we checked all bytes and none mismatched
        return true;
    }

    public MidiService getMidiService() {
        return midiService;
    }

    @Inject
    public void setMidiService(MidiService midiService) {
        this.midiService = midiService;
    }

    @Inject
    public MidiSettings getMidiSettings() {
        return midiSettings;
    }

    public void setMidiSettings(MidiSettings midiSettings) {
        this.midiSettings = midiSettings;
    }
}
