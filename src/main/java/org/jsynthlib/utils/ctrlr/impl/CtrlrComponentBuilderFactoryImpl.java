package org.jsynthlib.utils.ctrlr.impl;

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.jsynthlib.utils.ctrlr.CtrlrComponentBuilderFactory;
import org.jsynthlib.utils.ctrlr.PanelResourceManager;
import org.jsynthlib.utils.ctrlr.SysexFormulaParser;
import org.jsynthlib.utils.ctrlr.builder.CtrlrComponentBuilder;
import org.jsynthlib.utils.ctrlr.builder.UiButtonBuilder;
import org.jsynthlib.utils.ctrlr.builder.UiCombinedGroupBuilder;
import org.jsynthlib.utils.ctrlr.builder.UiEnvelopeBuilder;
import org.jsynthlib.utils.ctrlr.builder.UiGroupBuilder;
import org.jsynthlib.utils.ctrlr.builder.UiImageButtonBuilder;
import org.jsynthlib.utils.ctrlr.builder.UiKnobBuilder;
import org.jsynthlib.utils.ctrlr.builder.UiTabBuilder;
import org.jsynthlib.xmldevice.CombinedGroup;
import org.jsynthlib.xmldevice.CombinedIntPatchParam;
import org.jsynthlib.xmldevice.EnvelopeSpec;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamGroup;
import org.jsynthlib.xmldevice.StringParamSpec;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CtrlrComponentBuilderFactoryImpl implements
        CtrlrComponentBuilderFactory {

    private final transient Logger log = Logger.getLogger(getClass());
    private final SysexFormulaParser formulaParser;
    private final HashSet<CombinedGroup> handledCombinedGroups;
    private final PanelResourceManager resourceManager;

    @Inject
    public CtrlrComponentBuilderFactoryImpl(SysexFormulaParser formulaParser,
            PanelResourceManager resourceManager) {
        this.formulaParser = formulaParser;
        this.resourceManager = resourceManager;
        handledCombinedGroups = new HashSet<CombinedGroup>();
    }

    @Override
    public CtrlrComponentBuilder<? extends Object> newFactory(Object object) {
        if (object instanceof IntParamSpec) {
            IntParamSpec paramSpec = (IntParamSpec) object;
            if (paramSpec.isSetPatchParamResources()) {
                return new UiImageButtonBuilder(paramSpec, formulaParser,
                        resourceManager);
            } else if (paramSpec.isSetPatchParamValues()) {
                // Choose which factory to use.
                // return new UiComboFactory(paramSpec, formulaParser);
                return new UiButtonBuilder(paramSpec, formulaParser);
            } else if (paramSpec.getMin() == 0 && paramSpec.getMax() == 1) {
                return new UiButtonBuilder(paramSpec, formulaParser);
            } else {
                return new UiKnobBuilder(paramSpec, formulaParser);
            }
        } else if (object instanceof PatchParamGroup) {
            PatchParamGroup group = (PatchParamGroup) object;
            return new UiGroupBuilder(group);
        } else if (object instanceof PatchParamGroup[]) {
            PatchParamGroup[] groups = (PatchParamGroup[]) object;
            return new UiTabBuilder(groups);
        } else if (object instanceof StringParamSpec) {
            StringParamSpec paramSpec = (StringParamSpec) object;
        } else if (object instanceof EnvelopeSpec) {
            EnvelopeSpec envelopeSpec = (EnvelopeSpec) object;
            return new UiEnvelopeBuilder(envelopeSpec, formulaParser);
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
                return new UiCombinedGroupBuilder(combGroup, formulaParser);
            }
        } else {
            log.warn("Unsupported xml type: " + object.getClass().getName());
        }

        return null;
    }
}
