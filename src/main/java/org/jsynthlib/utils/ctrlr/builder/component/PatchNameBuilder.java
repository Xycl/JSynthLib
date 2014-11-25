package org.jsynthlib.utils.ctrlr.builder.component;

import java.awt.Rectangle;

import javax.inject.Named;

import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.builder.CtrlrLuaManagerBuilder;
import org.jsynthlib.utils.ctrlr.builder.method.GetPatchNameBuilder;
import org.jsynthlib.utils.ctrlr.builder.method.SetPatchNameBuilder;
import org.jsynthlib.xmldevice.StringParamSpec;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class PatchNameBuilder extends UiLabelBuilder {

    public interface Factory {
        PatchNameBuilder newPatchNameBuilder(StringParamSpec paramSpec);
    }

    @Inject
    private CtrlrLuaManagerBuilder luaManagerBuilder;

    @Inject
    private SetPatchNameBuilder setPatchNameBuilder;

    @Inject
    private GetPatchNameBuilder getPatchNameBuilder;

    @Inject
    @Named("prefix")
    private String prefix;

    @Inject
    public PatchNameBuilder(@Assisted StringParamSpec paramSpec) {
        super("PatchName");
        setLength(paramSpec.getLength());
    }

    @Override
    public ModulatorType createComponent(PanelType panel, ModulatorType group,
            int vstIndex, Rectangle rect) {

        ModulatorType modulator =
                super.createComponent(panel, group, vstIndex, rect);
        luaManagerBuilder.addMethod(prefix, setPatchNameBuilder);
        luaManagerBuilder.addMethod(prefix, getPatchNameBuilder);

        return modulator;
    }
}
