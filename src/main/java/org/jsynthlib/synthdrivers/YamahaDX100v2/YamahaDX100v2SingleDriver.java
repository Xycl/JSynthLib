/*
 * @version $Id: YamahaDX100SingleDriver.java 662 2004-08-13 03:08:21Z hayashi $
 */
package org.jsynthlib.synthdrivers.YamahaDX100v2;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.device.model.XMLSingleDriver;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;

public class YamahaDX100v2SingleDriver extends XMLSingleDriver {

    public YamahaDX100v2SingleDriver(XmlSingleDriverDefinition driverSpec) {
        super(driverSpec);
    }

    @Override
    public void calculateChecksum(Patch ip) {
        Patch p = ip;
        if (p.sysex.length > 101) {
            byte[] newSysex = new byte[101];
            System.arraycopy(p.sysex, 0, newSysex, 0, 101);
            p.sysex = newSysex;
        }
        calculateChecksum(p, 6, 98, 99); // calculate VCED Checksum
    }

    @Override
    public Patch createNewPatch() {
        return super.createNewPatch();
    }

    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        setBankNum(bankNum);
        setPatchNum(patchNum);
        sendPatch(p);

        ErrorMsg.reportError(
                "Yamaha DX 4op",
                "The patch has been placed in the edit buffer.\n You must choose to store it from the synths\nfront panel");
    }

    // @Override
    // public JSLFrame editPatch(Patch p) {
    // return new YamahaDX100SingleEditor((Patch) p);
    // }

}
