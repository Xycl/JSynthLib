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

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.jsynthlib.xmldevice.XmlDeviceSpecDocument;
import org.jsynthlib.xmldevice.XmlDeviceSpecDocument.XmlDeviceSpec;

/**
 * @author Pascal Collberg
 */
public class XMLDeviceDescriptor extends DeviceDescriptor {

    private final XmlDeviceSpec xmlDeviceSpec;

    public XMLDeviceDescriptor(File xmlFile) throws XmlException, IOException {
        XmlDeviceSpecDocument document =
                XmlDeviceSpecDocument.Factory.parse(xmlFile);
        xmlDeviceSpec = document.getXmlDeviceSpec();
        setManufacturer(xmlDeviceSpec.getManufacturer());
        setDeviceName(xmlDeviceSpec.getManufacturer() + " "
                + xmlDeviceSpec.getModelName());
        setDeviceId(xmlDeviceSpec.getInquiryId());
        setShortName(xmlDeviceSpec.getModelName());
        setDeviceClass(xmlFile.getName().replace(".xml", ""));
    }

    public XmlDeviceSpec getXmlDeviceSpec() {
        return xmlDeviceSpec;
    }
}
