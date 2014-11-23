package org.jsynthlib.utils.ctrlr.builder.component;

import java.awt.Rectangle;

import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.builder.CtrlrComponentBuilderFactory;
import org.jsynthlib.utils.ctrlr.builder.SliderSpecWrapper;
import org.jsynthlib.utils.ctrlr.driverContext.DriverContext;
import org.jsynthlib.xmldevice.CombinedGroup;
import org.jsynthlib.xmldevice.CombinedIntPatchParam;

import com.google.inject.Inject;

public class UiCombinedGroupBuilder extends
CtrlrComponentBuilderBase<CombinedGroup> {

    private final CtrlrComponentBuilderFactory factory;

    @Inject
    public UiCombinedGroupBuilder(DriverContext context) {
        super(context);
        this.factory = context.getInstance(CtrlrComponentBuilderFactory.class);
    }

    @Override
    public ModulatorType createComponent(PanelType panel, ModulatorType group,
            int vstIndex, Rectangle rect) {
        CombinedIntPatchParam[] paramArray = getObject().getParamArray();
        Rectangle rectangle =
                new Rectangle((int) rect.getX(), (int) rect.getY() + 20,
                        24 + paramArray.length * 60, 73);
        UiGroupBuilder groupFactory =
                factory.newUiGroupBuilder("combinedGroup");
        groupFactory.setUiGroupText("");
        ModulatorType modulator =
                groupFactory.createComponent(panel, group, vstIndex, rectangle);

        String valueExpression =
                "setGlobal (0, setBit (global.k0, __BIT__, modulatorValue))";
        for (int i = 0; i < paramArray.length; i++) {
            CombinedIntPatchParam param = paramArray[i];
            Rectangle paramRect = new Rectangle(12 + (60 * i), 19, 33, 47);
            UiButtonBuilder builder =
                    factory.newUiButtonBuilder(SliderSpecWrapper.Factory
                            .newWrapper(param, getObject()));
            builder.setValueExpression(valueExpression.replace("__BIT__",
                    Integer.toString(param.getLeftShift())));
            builder.createComponent(panel, modulator, vstIndex, paramRect);
        }
        return modulator;
    }

    @Override
    protected String getModulatorName() {
        return "";
    }
}
