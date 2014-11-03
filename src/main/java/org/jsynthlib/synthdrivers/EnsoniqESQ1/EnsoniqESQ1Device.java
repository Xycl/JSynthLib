/*
 * EnsoniqESQ1Device.java
 *
 * Created on 10. Oktober 2001, 22:07
 */

package org.jsynthlib.synthdrivers.EnsoniqESQ1;

import org.jsynthlib.device.model.XMLDevice;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;

/**
 * @author Gerrit Gehnen
 * @version $Id: EnsoniqESQ1Device.java 646 2004-07-30 16:57:44Z ribrdb $
 */
public class EnsoniqESQ1Device extends XMLDevice {

    private static final String infoText =
            "Whenever JSynthLib sends a Patch to the ESQ, the synthesizer will go to a screen where you must hit "
                    + "'exit' on the front panel before you can send another patch. This is kind of annoying, so if anyone "
                    + "knows a way around this let me know. The other ESQ librarians I checked out had the same problem, so "
                    + "it may be impossible";

    public EnsoniqESQ1Device(XmlDeviceDefinition xmlDeviceSpec) {
        super(xmlDeviceSpec);
    }

//    /** Creates new EnsoniqESQ1Device */
//    public EnsoniqESQ1Device() {
//        super("Ensoniq", "ESQ-1", "F07E..06020F0200...........F7", infoText,
//                "Brian Klock");
//    }

//    /** Constructor for for actual work. */
//    @Override
//    public void setup(Preferences prefs) {
//        super.setup(prefs);
//
//        // setSynthName("ESQ1");
//        addDriver(new EnsoniqESQ1BankDriver());
//        addDriver(new EnsoniqESQ1SingleDriver());
//    }
}
