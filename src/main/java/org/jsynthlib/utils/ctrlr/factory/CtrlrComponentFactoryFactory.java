package org.jsynthlib.utils.ctrlr.factory;

import org.jsynthlib.xmldevice.CombinedGroup;
import org.jsynthlib.xmldevice.EnvelopeSpec;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamGroup;
import org.jsynthlib.xmldevice.PatchParamValues;
import org.jsynthlib.xmldevice.StringParamSpec;

public class CtrlrComponentFactoryFactory {

    public CtrlrComponentFactory<? extends Object> newFactory(Object object) {
        if (object instanceof IntParamSpec) {
            IntParamSpec paramSpec = (IntParamSpec) object;
            PatchParamValues paramValues = paramSpec.getPatchParamValues();
            if (paramValues == null) {
                if (paramSpec.getMin() == 0 && paramSpec.getMax() == 1) {
                    // CheckBox checkBox = paramChildren.addNewCheckBox();
                } else {
                    return new UiKnobFactory(paramSpec);
                }
            } else {
                return new UiComboFactory(paramSpec);
            }
        } else if (object instanceof PatchParamGroup) {
            PatchParamGroup group = (PatchParamGroup) object;
            return new UiGroupFactory(group);
        } else if (object instanceof PatchParamGroup[]) {
            PatchParamGroup[] groups = (PatchParamGroup[]) object;
            return new UiTabFactory(groups);
        } else if (object instanceof StringParamSpec) {
            StringParamSpec paramSpec = (StringParamSpec) object;
        } else if (object instanceof EnvelopeSpec) {
            EnvelopeSpec envelopeSpec = (EnvelopeSpec) object;
        } else if (object instanceof CombinedGroup) {
            CombinedGroup combGroup = (CombinedGroup) object;
        }

        return null;
    }
}
