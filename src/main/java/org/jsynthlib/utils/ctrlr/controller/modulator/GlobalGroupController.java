package org.jsynthlib.utils.ctrlr.controller.modulator;

import java.awt.Rectangle;
import java.util.Observable;
import java.util.Observer;

import org.jsynthlib.utils.ctrlr.controller.ModulatorFactoryFacade;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiGlobalButtonController.Globalbuttons;
import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.utils.ctrlr.domain.GlobalSliderSpecWrapper;
import org.jsynthlib.xmldevice.XmlDriverDefinition;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class GlobalGroupController extends UiGroupController implements Observer {

    private int xOffset = 4;

    @Inject
    private XmlDriverDefinition driverDef;

    @Inject
    @Named("prefix")
    private String prefix;

    @Inject
    private ModulatorFactoryFacade factoryFacade;

    private UiLabelController infoLabelBuilder;

    @Inject
    public GlobalGroupController(DriverModel model) {
        super("Global");
        model.addObserver(this);
    }

    @Override
    public void init() {
        super.init();
        setModulatorName(prefix + "_globalPatchControls");
        for (Globalbuttons button : Globalbuttons.values()) {
            Rectangle rect = newRectangle(40, 20);
            UiGlobalButtonController controller =
                    factoryFacade.newUiGlobalButtonController(button);
            controller.setWidth(40);
            controller.setHeight(20);
            controller.setRect(rect);
            add(controller);
        }

        for (int i = driverDef.getPatchNameStart(); i < driverDef
                .getPatchNameStart() + driverDef.getPatchNameSize(); i++) {
            GlobalSliderSpecWrapper wrapper =
                    new GlobalSliderSpecWrapper(prefix + i);
            wrapper.setOffset(i);
            add(factoryFacade.newNameCharSliderController(wrapper));
        }

        infoLabelBuilder =
                factoryFacade.newUiLabelController("driverStatus");
        infoLabelBuilder.setLabelBgColor("0xFF000000");
        infoLabelBuilder.setLabelVisible(false);
        infoLabelBuilder.setRect(newRectangle(400, 40));
        add(infoLabelBuilder);
    }


    @Override
    public boolean add(ModulatorControllerBase e) {
        if (e instanceof PatchNameController) {
            PatchNameController pnc = (PatchNameController) e;
            Rectangle pnRect =
                    newRectangle(driverDef.getPatchNameSize() * 10 + 10, 20);
            pnc.setRect(pnRect);
        }
        return super.add(e);
    }

    Rectangle newRectangle(int width, int height) {
        int x = xOffset;
        int y = 4;
        xOffset += width + 5;
        return new Rectangle(x, y, width, height);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof DriverModel) {
            DriverModel model = (DriverModel) o;

            for (ModulatorControllerBase builder : this) {
                if (builder instanceof NameCharSliderController) {
                    NameCharSliderController sliderBuilder =
                            (NameCharSliderController) builder;
                    sliderBuilder.setPatchCharMax(model.getPatchNameCharMax());
                }
            }
            infoLabelBuilder.setModulatorName(model.getInfoLabelName());
        }
    }
}
