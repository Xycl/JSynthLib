/*
 * $Id$
 * NovationNovaDevice.java
 *
 * Created on 10. Oktober 2001, 22:09
 *
 * @version $Id$
 *
 */

package synthdrivers.NovationNova1;

import core.*;
import java.util.prefs.Preferences;
public class NovationNova1Device extends Device
{
    /** Creates new NovationNovaDevice */
    public NovationNova1Device ()
    {
	super ("Novation","Nova 1","F07E..06020020290100210020000000F7",null,"Yves Lefebvre");
    }

    /** Constructor for for actual work. */
    public NovationNova1Device(Preferences prefs) {
	this();
	this.prefs = prefs;

	addDriver (new NovationNova1BankDriver ());
        addDriver (new NovationNova1SingleDriver ());
        addDriver (new NovationNova1SinglePerformanceDriver ());
    }
}
