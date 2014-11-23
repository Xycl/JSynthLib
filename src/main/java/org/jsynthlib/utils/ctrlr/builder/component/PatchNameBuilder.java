package org.jsynthlib.utils.ctrlr.builder.component;

import java.awt.Rectangle;
import java.util.ArrayList;

import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.builder.CtrlrLuaManagerBuilder;
import org.jsynthlib.utils.ctrlr.builder.SliderSpecWrapper;
import org.jsynthlib.utils.ctrlr.builder.method.GetPatchNameBuilder;
import org.jsynthlib.utils.ctrlr.builder.method.MethodBuilder;
import org.jsynthlib.utils.ctrlr.builder.method.SetPatchNameBuilder;
import org.jsynthlib.utils.ctrlr.driverContext.DriverContext;
import org.jsynthlib.utils.ctrlr.driverContext.GlobalPatchMethodParser;
import org.jsynthlib.xmldevice.MidiSenderReference;
import org.jsynthlib.xmldevice.ParamModelReference;

import com.google.inject.Inject;

public class PatchNameBuilder extends UiLabelBuilder {

    private final CtrlrLuaManagerBuilder luaManagerBuilder;

    enum Globalbuttons {

        GET("Get"), SEND("Send"), LOAD("Load"), SAVE("Save");

        private final String name;

        private Globalbuttons(String name) {
            this.name = name;
        }
    }

    @Inject
    public PatchNameBuilder(DriverContext context,
            CtrlrLuaManagerBuilder luaMangerBuilder) {
        super(context);
        setObject("PatchName");
        this.luaManagerBuilder = luaMangerBuilder;

    }

    @Override
    public ModulatorType createComponent(PanelType panel, ModulatorType group,
            int vstIndex, Rectangle rect) {

        DriverContext driverContext = getContext();
        ModulatorType modulator =
                super.createComponent(panel, group, vstIndex, rect);
        String groupName = driverContext.getDriverPrefix();
        CtrlrLuaManagerBuilder luaManagerBuilder =
                driverContext.getInstance(CtrlrLuaManagerBuilder.class);
        SetPatchNameBuilder setPatchNameBuilder =
                driverContext.getInstance(SetPatchNameBuilder.class);
        luaManagerBuilder.addMethod(groupName, setPatchNameBuilder);
        GetPatchNameBuilder getPatchNameBuilder =
                driverContext.getInstance(GetPatchNameBuilder.class);
        luaManagerBuilder.addMethod(groupName, getPatchNameBuilder);

        for (Globalbuttons button : Globalbuttons.values()) {
            addGlobalButton(rect, button, panel, group, vstIndex);
        }
        return modulator;
    }

    void addGlobalButton(Rectangle labelRect, Globalbuttons button,
            PanelType panel, ModulatorType group, int vstIndex) {
        GlobalPatchMethodParser methodParser =
                getContext().getInstance(GlobalPatchMethodParser.class);
        MethodBuilder methodBuilder = null;
        switch (button) {
        case GET:
            methodBuilder = methodParser.getPatchRequestBuilder();
            break;
        case SEND:
            methodBuilder = methodParser.getPatchStoreBuilder();
            break;
        case LOAD:
            methodBuilder = methodParser.getPatchLoadBuilder();
            break;
        case SAVE:
            methodBuilder = methodParser.getPatchSaveBuilder();
            break;
        default:
            throw new IllegalArgumentException("Invalid button " + button);
        }
        int width = (int) (labelRect.getWidth() / 4) - 4;
        int xOffset = (int) (labelRect.getWidth() / 4) * button.ordinal();
        Rectangle rect =
                new Rectangle((int) (labelRect.getX() + xOffset),
                        (int) (labelRect.getY() + labelRect.getHeight()),
                        width, 20);
        new UiGlobalButtonBuilder(getContext(), button.name, methodBuilder,
                rect).createComponent(panel, group, vstIndex);
        luaManagerBuilder.addMethod(getContext().getDriverPrefix(),
                methodBuilder);

    }

    static class UiGlobalButtonBuilder extends UiButtonBuilder {

        private final Rectangle rect;

        public UiGlobalButtonBuilder(DriverContext context, String name,
                MethodBuilder methodbuilder, Rectangle rect) {
            super(context);
            setObject(new GlobalSliderSpec(name));
            ArrayList<String> contents = new ArrayList<String>();
            contents.add(name);
            setContents(contents);
            setButtonColorOff(getButtonColorOn());
            this.rect = rect;
            setWidth((int) rect.getWidth());
            setHeight((int) rect.getHeight());
            setLabelVisible(false);
            setLuaModulatorValueChange(methodbuilder.getName());
        }

        @Override
        protected void createMidiElement(ModulatorType modulator) {
            createMidiElement(modulator, "");
        }

        public ModulatorType createComponent(PanelType panel,
                ModulatorType group, int vstIndex) {
            return super.createComponent(panel, group, vstIndex, rect);
        }

    }

    static class GlobalSliderSpec implements SliderSpecWrapper {

        private final String name;

        public GlobalSliderSpec(String name) {
            super();
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getMin() {
            return 0;
        }

        @Override
        public int getMax() {
            return 1;
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

    }
}
