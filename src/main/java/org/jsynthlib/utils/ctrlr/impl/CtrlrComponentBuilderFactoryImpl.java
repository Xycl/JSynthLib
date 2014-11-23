package org.jsynthlib.utils.ctrlr.impl;

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.jsynthlib.utils.ctrlr.builder.CtrlrComponentBuilderFactory;
import org.jsynthlib.utils.ctrlr.builder.SliderSpecWrapper;
import org.jsynthlib.utils.ctrlr.builder.component.CtrlrComponentBuilderBase;
import org.jsynthlib.utils.ctrlr.builder.component.PatchNameBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiButtonBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiCombinedGroupBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiEnvelopeBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiGroupBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiImageButtonBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiIncDecButtonsBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiKnobBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiLabelBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiTabBuilder;
import org.jsynthlib.utils.ctrlr.driverContext.DriverContext;
import org.jsynthlib.xmldevice.CombinedGroup;
import org.jsynthlib.xmldevice.CombinedIntPatchParam;
import org.jsynthlib.xmldevice.EnvelopeParamSpec;
import org.jsynthlib.xmldevice.EnvelopeSpec;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamGroup;
import org.jsynthlib.xmldevice.StringParamSpec;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class CtrlrComponentBuilderFactoryImpl implements
CtrlrComponentBuilderFactory {

    private final transient Logger log = Logger.getLogger(getClass());
    private final HashSet<CombinedGroup> handledCombinedGroups;
    private final Injector injector;

    @Inject
    public CtrlrComponentBuilderFactoryImpl(DriverContext context) {
        handledCombinedGroups = new HashSet<CombinedGroup>();
        injector = context.getInjector();
    }

    @Override
    public CtrlrComponentBuilderBase<? extends Object> newFactory(Object object) {
        if (object instanceof IntParamSpec) {
            IntParamSpec paramSpec = (IntParamSpec) object;
            if (paramSpec.isSetPatchParamResources()) {
                return newUiImageButtonBuilder(paramSpec);
            } else if (paramSpec.isSetPatchParamValues()) {
                // Choose which factory to use.
                // return newUiComboFactory(paramSpec);
                return newUiButtonBuilder(paramSpec);
            } else if (paramSpec.getMin() == 0 && paramSpec.getMax() == 1) {
                return newUiButtonBuilder(paramSpec);
            } else {
                return newUiKnobBuilder(paramSpec);
            }
        } else if (object instanceof PatchParamGroup) {
            PatchParamGroup group = (PatchParamGroup) object;
            return newUiGroupBuilder(group);
        } else if (object instanceof PatchParamGroup[]) {
            PatchParamGroup[] groups = (PatchParamGroup[]) object;
            return newUiTabBuilder(groups);
        } else if (object instanceof StringParamSpec) {
            StringParamSpec paramSpec = (StringParamSpec) object;
            // return newPatchNameBuilder(paramSpec);
        } else if (object instanceof EnvelopeSpec) {
            EnvelopeSpec envelopeSpec = (EnvelopeSpec) object;
            return newUiEnvelopeBuilder(envelopeSpec);
        } else if (object instanceof CombinedIntPatchParam) {
            CombinedIntPatchParam param = (CombinedIntPatchParam) object;
            XmlCursor cursor = param.newCursor();
            cursor.toParent();
            CombinedGroup combGroup = (CombinedGroup) cursor.getObject();
            cursor.dispose();
            if (handledCombinedGroups.contains(combGroup)) {
                log.debug("Skipping handled combined group");
            } else {
                handledCombinedGroups.add(combGroup);
                return newUiCombinedGroupBuilder(combGroup);
            }
        } else {
            log.warn("Unsupported xml type: " + object.getClass().getName());
        }
        return null;
    }

    @Override
    public PatchNameBuilder newPatchNameBuilder(StringParamSpec paramSpec) {
        PatchNameBuilder instance =
                injector.getInstance(PatchNameBuilder.class);
        instance.setLength(paramSpec.getLength());
        instance.setObject(paramSpec.getName());
        return instance;
    }

    @Override
    public UiImageButtonBuilder newUiImageButtonBuilder(IntParamSpec paramSpec) {
        UiImageButtonBuilder instance =
                injector.getInstance(UiImageButtonBuilder.class);
        instance.setIntParamSpec(paramSpec);
        return instance;
    }

    @Override
    public UiButtonBuilder newUiButtonBuilder(IntParamSpec paramSpec) {
        UiButtonBuilder instance = injector.getInstance(UiButtonBuilder.class);
        instance.setIntParamSpec(paramSpec);
        return instance;
    }

    @Override
    public UiKnobBuilder newUiKnobBuilder(IntParamSpec paramSpec) {
        UiKnobBuilder instance = injector.getInstance(UiKnobBuilder.class);
        instance.setIntParamSpec(paramSpec);
        return instance;
    }

    @Override
    public UiGroupBuilder newUiGroupBuilder(PatchParamGroup group) {
        UiGroupBuilder instance = injector.getInstance(UiGroupBuilder.class);
        instance.setPatchParamGroup(group);
        return instance;
    }

    @Override
    public UiTabBuilder newUiTabBuilder(PatchParamGroup[] groups) {
        UiTabBuilder instance = injector.getInstance(UiTabBuilder.class);
        instance.setObject(groups);
        return instance;
    }

    @Override
    public UiEnvelopeBuilder newUiEnvelopeBuilder(EnvelopeSpec envelopeSpec) {
        UiEnvelopeBuilder instance =
                injector.getInstance(UiEnvelopeBuilder.class);
        instance.setObject(envelopeSpec);
        return instance;
    }

    @Override
    public UiCombinedGroupBuilder newUiCombinedGroupBuilder(
            CombinedGroup combGroup) {
        UiCombinedGroupBuilder instance =
                injector.getInstance(UiCombinedGroupBuilder.class);
        instance.setObject(combGroup);
        return instance;
    }

    @Override
    public UiIncDecButtonsBuilder newUiIncDecButtonsBuilder(
            EnvelopeParamSpec param, int displayIndex) {
        UiIncDecButtonsBuilder instance =
                injector.getInstance(UiIncDecButtonsBuilder.class);
        instance.setEnvelopeParamSpec(param, displayIndex);
        return instance;
    }

    @Override
    public UiButtonBuilder newUiButtonBuilder(SliderSpecWrapper newWrapper) {
        UiButtonBuilder instance = injector.getInstance(UiButtonBuilder.class);
        instance.setObject(newWrapper);
        return instance;
    }

    @Override
    public UiLabelBuilder newUiLabelBuilder(String string) {
        UiLabelBuilder instance = injector.getInstance(UiLabelBuilder.class);
        instance.setObject(string);
        return instance;
    }

    @Override
    public UiGroupBuilder newUiGroupBuilder(String string) {
        UiGroupBuilder instance = injector.getInstance(UiGroupBuilder.class);
        instance.setObject(string);
        return instance;
    }
}
