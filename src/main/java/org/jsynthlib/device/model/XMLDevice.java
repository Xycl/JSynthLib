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
package org.jsynthlib.device.model;

import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JPanel;

import org.jsynthlib.device.viewcontroller.XMLDeviceDetailsPanel;
import org.jsynthlib.xmldevice.PreferenceDefs;
import org.jsynthlib.xmldevice.PreferenceDefs.PreferenceDef;
import org.jsynthlib.xmldevice.XmlDeviceSpecDocument.XmlDeviceSpec;
import org.jsynthlib.xmldevice.XmlDriverDefs;
import org.jsynthlib.xmldevice.XmlDriverDefs.XmlDriverDef;

import com.google.inject.assistedinject.Assisted;

/**
 * @author Pascal Collberg
 */
public class XMLDevice extends Device {

    private final XmlDeviceSpec xmlDeviceSpec;
    private final PreferenceDefs preferencesDefs;

    public XMLDevice(@Assisted XmlDeviceSpec xmlDeviceSpec) {
        super(xmlDeviceSpec.getManufacturer(), xmlDeviceSpec.getModelName(),
                xmlDeviceSpec.getInquiryId(), xmlDeviceSpec.getInfoText(),
                xmlDeviceSpec.getAuthors());
        this.xmlDeviceSpec = xmlDeviceSpec;
        preferencesDefs = xmlDeviceSpec.getPreferenceDefs();
    }

    public IDriver getDriver(XmlDriverDef.DriverType.Enum driverType,
            String patchType) {
        List<IDriver> drivers = getDriverList();
        for (IDriver driver : drivers) {
            XmlDriverDef driverDef = getDriverDef(driver.getClass().getName());
            if (driverDef != null && driver.getPatchType().equals(patchType)
                    && driverDef.getDriverType().equals(driverType)) {
                return driver;
            }
        }
        return null;
    }

    XmlDriverDef getDriverDef(String driverClass) {
        XmlDriverDefs drivers = xmlDeviceSpec.getDrivers();
        XmlDriverDef[] xmlDriverDefs = drivers.getXmlDriverDefArray();
        for (XmlDriverDef xmlDriverDef : xmlDriverDefs) {
            if (xmlDriverDef.getDriverClass().equals(driverClass)) {
                return xmlDriverDef;
            }
        }
        return null;
    }

    @Override
    public void setup(Preferences preferences) {
        super.setup(preferences);
        if (preferencesDefs != null) {
            PreferenceDef[] preferenceDefs =
                    preferencesDefs.getPreferenceDefArray();
            for (PreferenceDef preferenceDef : preferenceDefs) {
                if (!PreferenceUtil.containsPreference(preferences,
                        preferenceDef.getName())) {
                    switch (preferenceDef.getType().intValue()) {
                    case PreferenceDef.Type.INT_BOOLEAN:
                        preferences.putBoolean(preferenceDef.getName(), Boolean
                                .parseBoolean(preferenceDef.getDefaultValue()));
                        break;
                    case PreferenceDef.Type.INT_INTEGER:
                        int defaultValue = 0;
                        try {
                            defaultValue =
                                    Integer.parseInt(preferenceDef
                                            .getDefaultValue());
                        } catch (NumberFormatException e) {
                        }
                        preferences.putInt(preferenceDef.getName(),
                                defaultValue);
                        break;
                    case PreferenceDef.Type.INT_STRING:
                    default:
                        preferences.put(preferenceDef.getName(),
                                preferenceDef.getDefaultValue());
                        break;
                    }
                }
            }
        }
    }

    @Override
    public JPanel config() {
        if (preferencesDefs == null
                || preferencesDefs.getPreferenceDefArray().length == 0) {
            return super.config();
        } else {
            return new XMLDeviceDetailsPanel(getPreferences(), preferencesDefs);
        }
    }
}
