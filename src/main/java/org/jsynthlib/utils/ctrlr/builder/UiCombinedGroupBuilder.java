package org.jsynthlib.utils.ctrlr.builder;

import java.awt.Rectangle;

import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.SliderSpecWrapper;
import org.jsynthlib.utils.ctrlr.SysexFormulaParser;
import org.jsynthlib.xmldevice.CombinedGroup;
import org.jsynthlib.xmldevice.CombinedIntPatchParam;

public class UiCombinedGroupBuilder extends
        CtrlrComponentBuilder<CombinedGroup> {

    private final SysexFormulaParser formulaParser;

    public UiCombinedGroupBuilder(CombinedGroup object,
            SysexFormulaParser formulaParser) {
        super(object);
        this.formulaParser = formulaParser;
    }

    @Override
    public ModulatorType createComponent(PanelType panel, ModulatorType group,
            int vstIndex, Rectangle rect) {
        CombinedIntPatchParam[] paramArray = object.getParamArray();
        Rectangle rectangle =
                new Rectangle((int) rect.getX(), (int) rect.getY() + 20,
                        24 + paramArray.length * 60, 73);
        UiGroupBuilder groupFactory = new UiGroupBuilder("envelope");
        groupFactory.setUiGroupText("");
        ModulatorType modulator =
                groupFactory.createComponent(panel, group, vstIndex, rectangle);

        String valueExpression =
                "setGlobal (0, setBit (global.k0, __BIT__, modulatorValue))";
        for (int i = 0; i < paramArray.length; i++) {
            CombinedIntPatchParam param = paramArray[i];
            Rectangle paramRect = new Rectangle(12 + (60 * i), 19, 33, 47);
            UiButtonBuilder factory =
                    new UiButtonBuilder(SliderSpecWrapper.Factory.newWrapper(
                            param, object.getMidiSender()), formulaParser);
            factory.setValueExpression(valueExpression.replace("__BIT__",
                    Integer.toString(param.getLeftShift())));
            factory.createComponent(panel, modulator, vstIndex, paramRect);
        }
        return modulator;
    }
}
