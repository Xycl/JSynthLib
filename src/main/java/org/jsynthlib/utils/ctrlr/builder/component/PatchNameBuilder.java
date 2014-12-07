package org.jsynthlib.utils.ctrlr.builder.component;

import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.builder.BuilderFactoryFacade;
import org.jsynthlib.utils.ctrlr.lua.DriverLuaBean;
import org.jsynthlib.xmldevice.StringParamSpec;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class PatchNameBuilder extends UiLabelBuilder {

    public interface Factory {
        PatchNameBuilder newPatchNameBuilder(StringParamSpec paramSpec);
    }

    @Inject
    public PatchNameBuilder(@Assisted StringParamSpec paramSpec,
            DriverLuaBean luaBean,
            BuilderFactoryFacade factoryFacade,
            GlobalGroupBuilder globalGroupBuilder) {
        super("PatchName");
        UiLabelBuilder patchNameLabelBuilder =
                factoryFacade.newUiLabelBuilder(luaBean
                        .getNameModulatorName());

        patchNameLabelBuilder.setEditOnSingleClick(true);
        patchNameLabelBuilder.setExcludeFromSnapshot(true);
        patchNameLabelBuilder.setLength(paramSpec.getLength());
        patchNameLabelBuilder.setMuteOnStart(true);
        patchNameLabelBuilder.setUiLabelChangedCbk(luaBean
                .getSetNameMethodName());
        patchNameLabelBuilder.setModulatorName(luaBean
                .getNameModulatorName());
        patchNameLabelBuilder.setLength(paramSpec.getLength());

        globalGroupBuilder.setPatchNameBuilder(patchNameLabelBuilder);
    }

    @Override
    public ModulatorType createModulator(PanelType panel, ModulatorType group,
            int vstIndex) {
        return null;
    }
}
