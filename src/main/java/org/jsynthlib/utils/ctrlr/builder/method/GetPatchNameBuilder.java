package org.jsynthlib.utils.ctrlr.builder.method;

import org.jsynthlib.utils.ctrlr.driverContext.DriverContext;
import org.jsynthlib.xmldevice.XmlDriverDefinition;

import com.google.inject.Inject;

public class GetPatchNameBuilder extends MethodBuilder {

    private static final String CODE_STUB =
            "function getPatchName(midi)&#13;&#10;"
                    + "&#9;local patchNameStart = __PATCH_NAME_START__&#13;&#10;"
                    + "&#9;local patchNameSize = __PATCH_NAME_SIZE__&#13;&#10;"
                    + "&#9;local name = &quot;&quot;&#13;&#10;"
                    + "&#9;for i = patchNameStart,(patchNameStart + patchNameSize) "
                    + "do -- gets the voice name&#13;&#10;"
                    + "&#9;&#9;midiParam = midi:getData():getByte(i)&#13;&#10;"
                    + "&#9;&#9;name = name..string.char(midiParam)&#13;&#10;"
                    + "&#9;end&#13;&#10;" + "&#9;return name&#13;&#10;" + "end";

    private final int patchNameStart;
    private final int patchNameSize;

    @Inject
    public GetPatchNameBuilder(DriverContext context) {
        super("getPatchName");
        XmlDriverDefinition driverDefinition = context.getDriverDefinition();
        this.patchNameSize = driverDefinition.getPatchNameSize();
        this.patchNameStart = driverDefinition.getPatchNameStart();

    }

    @Override
    public String getCode() {
        String s =
                CODE_STUB.replaceAll("__PATCH_NAME_START__",
                        Integer.toString(patchNameStart));
        s =
                s.replaceAll("__PATCH_NAME_SIZE__",
                        Integer.toString(patchNameSize));
        return s;
    }
}
