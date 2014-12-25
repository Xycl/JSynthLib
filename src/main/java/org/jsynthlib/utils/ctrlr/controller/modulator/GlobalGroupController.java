package org.jsynthlib.utils.ctrlr.controller.modulator;

import java.awt.Rectangle;
import java.util.Observable;
import java.util.Observer;

import org.jsynthlib.utils.ctrlr.controller.ModulatorFactoryFacade;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiGlobalButtonController.Globalbuttons;
import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.utils.ctrlr.domain.GlobalSliderSpecWrapper;
import org.jsynthlib.utils.ctrlr.domain.SliderSpecWrapper;
import org.jsynthlib.xmldevice.MidiSenderReference;
import org.jsynthlib.xmldevice.ParamModelReference;
import org.jsynthlib.xmldevice.StringArray;
import org.jsynthlib.xmldevice.XmlDriverDefinition;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class GlobalGroupController extends UiGroupController implements
Observer {

    private int xOffset = 4;

    @Inject
    private XmlDriverDefinition driverDef;

    @Inject
    @Named("prefix")
    private String prefix;

    @Inject
    private DriverModel model;

    @Inject
    private ModulatorFactoryFacade factoryFacade;

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
            Rectangle rect = newRectangle(50, 20);
            UiGlobalButtonController controller =
                    factoryFacade.newUiGlobalButtonController(button);
            controller.setWidth(50);
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

        PatchNumberSliderSpec sliderSpec = new PatchNumberSliderSpec();
        if (sliderSpec.getMax() > 1) {
            UiComboController patchSelectController =
                    factoryFacade.newUiComboController(sliderSpec);
            patchSelectController.setUiComboContent(sliderSpec
                    .getComboContent());
            patchSelectController.setComponentVisibleName("Patch");
            patchSelectController.setWidth(100);
            patchSelectController.setHeight(40);
            patchSelectController.setRect(newRectangle(4, 100, 40));
            patchSelectController.setLuaModulatorValueChange(model
                    .getPatchSelectMethodName());
            add(patchSelectController);
        }
    }

    @Override
    public boolean add(ModulatorControllerBase e) {
        if (e instanceof PatchNameController) {
            PatchNameController pnc = (PatchNameController) e;
            Rectangle pnRect =
                    newRectangle(driverDef.getPatchNameSize() * 10 + 10, 20);
            pnc.setUiLabelFontSize(16);
            pnc.setUiLabelJustification("left");
            pnc.setRect(pnRect);
        }
        return super.add(e);
    }

    Rectangle newRectangle(int width, int height) {
        int y = 20;
        return newRectangle(y, width, height);
    }

    Rectangle newRectangle(int y, int width, int height) {
        int x = xOffset;
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
            // infoLabelBuilder.setModulatorName(model.getInfoLabelName());
        }
    }

    class PatchNumberSliderSpec implements SliderSpecWrapper {

        private final String[] stringArray;
        private final String comboContent;

        public PatchNumberSliderSpec() {
            StringArray patchNumbers = driverDef.getPatchNumbers();
            stringArray = patchNumbers.getStringArray();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < stringArray.length; i++) {
                String string = stringArray[i];
                if (i > 0) {
                    sb.append("\n");
                }
                sb.append(string).append("=").append(i);
            }
            this.comboContent = sb.toString();
        }

        @Override
        public String getName() {
            return model.getPatchSelectName();
        }

        @Override
        public int getMin() {
            return 0;
        }

        @Override
        public int getMax() {
            return stringArray.length;
        }

        @Override
        public MidiSenderReference getMidiSender() {
            return null;
        }

        @Override
        public boolean isSetMidiSender() {
            return false;
        }

        @Override
        public boolean isSetParamModel() {
            return false;
        }

        @Override
        public ParamModelReference getParamModel() {
            return null;
        }

        public String getComboContent() {
            return comboContent;
        }

    }
}
