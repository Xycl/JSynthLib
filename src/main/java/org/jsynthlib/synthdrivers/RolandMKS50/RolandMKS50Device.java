// written by Kenneth L. Martinez
// @version $Id: RolandMKS50Device.java 1167 2011-10-09 02:22:49Z billzwicky $

package org.jsynthlib.synthdrivers.RolandMKS50;

import java.util.prefs.Preferences;

import org.jsynthlib.device.model.Device;

public class RolandMKS50Device extends Device {
    /** Creates new RolandMKS50Device */
    public RolandMKS50Device() {
        super("Roland", "Alpha Juno-1/MKS-50", null, null,
                "Kenneth L. Martinez");
    }

    /** Constructor for for actual work. */
    public void setup(Preferences prefs) {
        super.setup(prefs);

        addDriver(new MKS50ToneBankDriver());
        addDriver(new MKS50ToneSingleDriver());
        addDriver(new MKS50PatchBankDriver());
        addDriver(new MKS50PatchSingleDriver());
    }
}
