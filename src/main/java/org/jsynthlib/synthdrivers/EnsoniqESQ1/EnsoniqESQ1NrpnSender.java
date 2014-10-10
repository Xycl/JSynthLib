package org.jsynthlib.synthdrivers.EnsoniqESQ1;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import org.apache.log4j.Logger;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.device.model.ISender;

public class EnsoniqESQ1NrpnSender implements ISender {

    private static final int ESQ1_TIMEOUT = 20;
    private final transient Logger log = Logger.getLogger(getClass());
    private int currValue;
    private int nrpnLsb;
    private int nrpnMsb;
    private final ShortMessage lsbMsg;
    private final ShortMessage msbMsg;
    private ShortMessage dataIncrement;
    private ShortMessage dataDecrement;

    public EnsoniqESQ1NrpnSender() {
        lsbMsg = new ShortMessage();
        msbMsg = new ShortMessage();
        try {
            dataIncrement = new ShortMessage(0xB0, 0x60, 0x7F);
            dataDecrement = new ShortMessage(0xB0, 0x61, 0x7F);
            lsbMsg.setMessage(0xB0, 0x62, 0);
            msbMsg.setMessage(0xB0, 0x63, 0);
        } catch (InvalidMidiDataException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void send(IDriver driver, int value) {
        try {
            if (value < currValue) {
                while (value < currValue) {
                    sendNrpn(driver, false);
                }
            } else if (value > currValue) {
                while (value > currValue) {
                    sendNrpn(driver, true);
                }
            }
            // TODO: check real value.
        } catch (InvalidMidiDataException e) {
            log.warn(e.getMessage(), e);
        } catch (InterruptedException e) {
            log.warn(e.getMessage(), e);
        }
    }

    void sendNrpn(IDriver driver, boolean increment)
            throws InvalidMidiDataException, InterruptedException {
        driver.send(lsbMsg);
        Thread.sleep(ESQ1_TIMEOUT);
        driver.send(msbMsg);
        Thread.sleep(ESQ1_TIMEOUT);
        if (increment) {
            driver.send(dataIncrement);
            currValue++;
        } else {
            driver.send(dataDecrement);
            currValue--;
        }
        Thread.sleep(ESQ1_TIMEOUT);
    }

    public int getNrpn() {
        return nrpnLsb;
    }

    public void setNrpn(int nrpn) {
        if (nrpn < 128) {
            this.nrpnLsb = nrpn;
            this.nrpnMsb = 0;
        } else {
            this.nrpnLsb = nrpn - 128;
            this.nrpnMsb = 1;
        }

        try {
            lsbMsg.setMessage(0xB0, 0x62, nrpnLsb);
            msbMsg.setMessage(0xB0, 0x63, nrpnMsb);
        } catch (InvalidMidiDataException e) {
            log.warn(e.getMessage(), e);
        }
    }
}
