package org.jsynthlib.utils.ctrlr.controller.modulator;

import java.awt.Rectangle;

import org.jsynthlib.utils.ctrlr.controller.ModulatorFactoryFacade;
import org.jsynthlib.utils.ctrlr.domain.SliderSpecWrapper;
import org.jsynthlib.xmldevice.CombinedGroup;
import org.jsynthlib.xmldevice.CombinedIntPatchParam;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class UiCombinedGroupController extends UiGroupController {

    public interface Factory {
        UiCombinedGroupController newUiCombinedGroupController(
                CombinedGroup combGroup);
    }

    @Inject
    private ModulatorFactoryFacade buttonFactory;
    private final CombinedGroup combGroup;
    private final CombinedIntPatchParam[] paramArray;

    @Inject
    public UiCombinedGroupController(@Assisted CombinedGroup combGroup) {
        super("combinedGroup");

        this.combGroup = combGroup;
        paramArray = combGroup.getParamArray();
    }

    @Override
    public void init() {
        super.init();

        setUiGroupText(combGroup.getName());
        setModulatorName(getUniqueName("combinedGroup"));

        String valueExpression =
                "setGlobal (0, setBit (global.k0, __BIT__, modulatorValue))";
        for (int i = 0; i < paramArray.length; i++) {
            CombinedIntPatchParam param = paramArray[i];
            Rectangle paramRect = new Rectangle(12 + 60 * i, 19, 33, 47);
            UiButtonController builder =
                    buttonFactory
                    .newUiButtonController(SliderSpecWrapper.Factory
                            .newWrapper(param, combGroup));
            builder.setValueExpression(valueExpression.replace("__BIT__",
                    Integer.toString(param.getLeftShift())));
            builder.setRect(paramRect);
            add(builder);
        }
    }

    @Override
    public void setRect(Rectangle rect) {
        Rectangle rectangle =
                new Rectangle((int) rect.getX(), (int) rect.getY() + 20,
                        24 + paramArray.length * 60, 73);
        super.setRect(rectangle);
    }

}
