package org.jsynthlib.synthdrivers.YamahaDX7.common;

import java.io.UnsupportedEncodingException;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.device.model.XMLPatchDriver;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.xmldevice.XmlPatchDriverSpecDocument.XmlPatchDriverSpec;

public abstract class DX7FamilyXmlVoicePatchDriver extends XMLPatchDriver {

    private boolean spbp;
    private boolean swOffMemProt;
    private boolean tipsMsg;

    public DX7FamilyXmlVoicePatchDriver(XmlPatchDriverSpec driverSpec) {
        super(driverSpec);
    }

    @Override
    public void setPatchName(Patch p, String name) {
        if (patchNameSize == 0) {
            ErrorMsg.reportError("Error",
                    "The Driver for this patch does not support Patch Name Editing.");
            return;
        }

        while (name.length() < patchNameSize) {
            name = name + " ";
        }

        byte[] namebytes = new byte[patchNameSize];
        try {
            namebytes = name.getBytes("US-ASCII");
            for (int i = 0; i < patchNameSize; i++) {
                p.sysex[patchNameStart + i] = namebytes[i];
            }
            calculateChecksum(p);
        } catch (UnsupportedEncodingException ex) {
            return;
        }
    }

    public boolean isSpbp() {
        return spbp;
    }

    public void setSpbp(boolean spbp) {
        this.spbp = spbp;
    }

    public boolean isSwOffMemProt() {
        return swOffMemProt;
    }

    public void setSwOffMemProt(boolean swOffMemProt) {
        this.swOffMemProt = swOffMemProt;
    }

    public boolean isTipsMsg() {
        return tipsMsg;
    }

    public void setTipsMsg(boolean tipsMsg) {
        this.tipsMsg = tipsMsg;
    }

}
