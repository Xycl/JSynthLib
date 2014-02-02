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
package core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

public final class SynthDriversManager {

    private static final Logger LOG = Logger
            .getLogger(SynthDriversManager.class);

    public static List<ManufacturerDriverPair> getDeviceList()
            throws IOException {
        ArrayList<ManufacturerDriverPair> list =
                new ArrayList<ManufacturerDriverPair>();

        Properties properties = new Properties();
        properties.load(SynthDriversManager.class
                .getResourceAsStream("/synthdrivers.properties"));
        Iterator<Entry<Object, Object>> iterator =
                properties.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Object, Object> entry = iterator.next();
            String key = (String) entry.getKey();
            if (key.startsWith("deviceName.")) {
                String driverName = properties.getProperty(key);
                String manufacturer =
                        properties.getProperty("manufacturer."
                                + key.substring("deviceName.".length()));
                if ("Generic Unknown Driver".equals(driverName)) {
                    LOG.info("Skipping generic driver...");
                    continue;
                }
                list.add(new ManufacturerDriverPair(manufacturer, driverName));
            }
        }

        return list;
    }

    private SynthDriversManager() {
    }

    public static class ManufacturerDriverPair {
        private String manufacturer;
        private String driverName;

        public ManufacturerDriverPair(String manufacturer, String driverName) {
            super();
            this.manufacturer = manufacturer;
            this.driverName = driverName;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public String getDriverName() {
            return driverName;
        }
    }
}
