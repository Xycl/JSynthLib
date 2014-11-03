package org.jsynthlib.device.model;

import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.xmldevice.XmlDriverDefinition;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference;

class XMLConverter extends Converter {

    public XMLConverter(XmlDriverDefinition driverSpec) {
        super(XmlDriverReference.DriverType.CONVERTER.toString(), driverSpec.getAuthors());
    }

    /*
     * (non-Javadoc)
     * @see
     * org.jsynthlib.device.model.Converter#extractPatch(org.jsynthlib.patch
     * .model.impl.Patch)
     */
    @Override
    public Patch[] extractPatch(Patch p) {
        // TODO Auto-generated method stub
        return null;
    }


}
