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
package org.jsynthlib.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.IDriver;

/**
 * @author Pascal Collberg
 */
public class DriverParser {

    private final transient Logger log = Logger.getLogger(getClass());

    /**
     * @param args
     */
    public static void main(String[] args) {
        DriverParser driverParser = new DriverParser();
        try {
            DeviceParser deviceParser = new DeviceParser();
            List<Device> devices = deviceParser.getAllDevices();
            driverParser.parseDrivers(devices);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public void parseDrivers(List<Device> devices) throws IOException,
            URISyntaxException {
        for (Device device : devices) {
            try {
                for (int i = 0; i < device.driverCount(); i++) {
                    IDriver driver = device.getDriver(i);
                    Field[] fields =
                            driver.getClass().getSuperclass().getSuperclass()
                                    .getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        String name = field.getName();
                        Class<?> type = field.getType();
                        if (type.isArray()) {
                            Class<?> componentType = type.getComponentType();
                            log.info("Array," + name + ","
                                    + componentType.getName());
                        } else {
                            log.info(name + "," + type.getName());
                        }

                    }
                }
                break;
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }
}
