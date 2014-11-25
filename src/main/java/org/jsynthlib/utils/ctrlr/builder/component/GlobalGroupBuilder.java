package org.jsynthlib.utils.ctrlr.builder.component;

import java.awt.Rectangle;
import java.util.ArrayList;

import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.builder.CtrlrLuaManagerBuilder;
import org.jsynthlib.utils.ctrlr.builder.SliderSpecWrapper;
import org.jsynthlib.utils.ctrlr.builder.method.MethodBuilder;
import org.jsynthlib.utils.ctrlr.driverContext.GlobalPatchMethodParser;
import org.jsynthlib.xmldevice.MidiSenderReference;
import org.jsynthlib.xmldevice.ParamModelReference;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class GlobalGroupBuilder extends CtrlrComponentBuilderBase<String> {

    enum Globalbuttons {

        GET("Get"), SEND("Send"), LOAD("Load"), SAVE("Save");

        private final String name;

        private Globalbuttons(String name) {
            this.name = name;
        }
    }

    @Inject
    private CtrlrLuaManagerBuilder luaManagerBuilder;

    @Inject
    private UiGroupBuilder.Factory uiGroupBuilderFactory;

    @Inject
    private UiLabelBuilder.Factory uiLabelBuilderFactory;

    @Inject
    private GlobalPatchMethodParser methodParser;

    @Inject
    @Named("prefix")
    private String prefix;

    @Override
    public ModulatorType createComponent(PanelType panel, ModulatorType group,
            int vstIndex, Rectangle rect) {
        UiGroupBuilder groupBuilder =
                uiGroupBuilderFactory.newUiGroupBuilder("Global");
        ModulatorType globalGroup =
                groupBuilder.createComponent(panel, null, 0, rect);

        for (Globalbuttons button : Globalbuttons.values()) {
            addGlobalButton(rect, button, panel, globalGroup, vstIndex);

            // TODO: Remove
            break;
        }

        UiLabelBuilder labelBuilder =
                uiLabelBuilderFactory.newUiLabelBuilder("driverStatus");
        labelBuilder.setLabelVisible(false);
        labelBuilder.createComponent(panel, globalGroup, 0, rect);

        return globalGroup;
    }

    void addGlobalButton(Rectangle labelRect, Globalbuttons button,
            PanelType panel, ModulatorType group, int vstIndex) {
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
        int x =
                (int) ((labelRect.getWidth() / 4) * button.ordinal() + labelRect
                        .getX());
        Rectangle rect = new Rectangle(x, 2, width, 20);
        new UiGlobalButtonBuilder(button.name, methodBuilder, rect)
                .createComponent(panel, group, vstIndex);
        luaManagerBuilder.addMethod(prefix, methodBuilder);
    }

    @Override
    protected String getModulatorName() {
        return "globalPatchControls";
    }

    static class UiGlobalButtonBuilder extends UiButtonBuilder {

        private final Rectangle rect;

        public UiGlobalButtonBuilder(String name, MethodBuilder methodbuilder,
                Rectangle rect) {
            super(new GlobalSliderSpec(name));
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
