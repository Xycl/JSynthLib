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
package org.jsynthlib.test.adapter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;

import org.apache.xmlbeans.XmlObject;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;
import org.junit.Test;

/**
 * @author Pascal Collberg
 */
public class WidgetAdapterProviderTest {

    @Test
    public void testGetXmlObjectById() throws Exception {
        InputStream stream =
                getClass()
                        .getClassLoader()
                        .getResourceAsStream(
                                "org/jsynthlib/synthdrivers/RolandD50/D50SingleDriver.xml");
        XmlSingleDriverDefinitionDocument document =
                XmlSingleDriverDefinitionDocument.Factory.parse(stream);
        XmlSingleDriverDefinition xmlDriverSpec = document.getXmlSingleDriverDefinition();
        WidgetAdapterProvider tested = new WidgetAdapterProvider();
        XmlObject result =
                tested.getXmlObjectById(xmlDriverSpec,
                        "e554b5523e2e4eaaa8310be499f28175");
        assertNotNull(result);

        result =
                tested.getXmlObjectById(xmlDriverSpec,
                        "e554b5523e2e4eaaa8310be499f");
        assertNull(result);
    }

}
