package org.jsynthlib.utils.ctrlr.builder.component;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import org.jsynthlib.utils.ctrlr.builder.BuilderFactoryFacade;
import org.jsynthlib.utils.ctrlr.lua.decorator.DriverLuaHandler;
import org.jsynthlib.xmldevice.XmlDriverDefinition;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GlobalGroupBuilder extends UiGroupBuilder {

    enum Globalbuttons {

        GET("Get"), SEND("Send"), LOAD("Load"), SAVE("Save");

        private final String name;

        private Globalbuttons(String name) {
            this.name = name;
        }
    }

    private final Map<Globalbuttons, UiGlobalButtonBuilder> builderMap;

    private int xOffset = 4;

    private final DriverLuaHandler luaHandler;

    @Inject
    private XmlDriverDefinition driverDef;

    @Inject
    private BuilderFactoryFacade factoryFacade;

    @Inject
    public GlobalGroupBuilder(DriverLuaHandler luaHandler) {
        super("Global");
        this.luaHandler = luaHandler;
        builderMap =
                new HashMap<GlobalGroupBuilder.Globalbuttons, UiGlobalButtonBuilder>();

        for (Globalbuttons button : Globalbuttons.values()) {
            Rectangle rect = newRectangle(40, 20);
            UiGlobalButtonBuilder builder =
                    new UiGlobalButtonBuilder(button.name, rect);
            switch (button) {
            case GET:
                builder.setMethodName(luaHandler.getGetMethod());
                break;
            case SEND:
                builder.setMethodName(luaHandler.getSendMethod());
                break;
            case LOAD:
                builder.setMethodName(luaHandler.getLoadMethod());
                break;
            case SAVE:
                builder.setMethodName(luaHandler.getSaveMethod());
                break;
            default:
                throw new IllegalStateException("Bad global button");
            }
            add(builder);
            builderMap.put(button, builder);
        }

    }

    public void setPatchNameBuilder(UiLabelBuilder patchNameBuilder) {
        Rectangle pnRect =
                newRectangle(patchNameBuilder.getLength() * 10 + 10, 20);
        patchNameBuilder.setRect(pnRect);
        patchNameBuilder.setModulatorName(luaHandler.getNameModulator());
        patchNameBuilder.setUiLabelText("New Patch");
        patchNameBuilder.setUiLabelChangedCbk(luaHandler.getSetNameMethod());
        add(patchNameBuilder);

        for (int i = driverDef.getPatchNameStart(); i < driverDef
                .getPatchNameStart() + patchNameBuilder.getLength(); i++) {
            GlobalSliderSpecWrapper wrapper =
                    new GlobalSliderSpecWrapper(luaHandler.getDriverPrefix()
                            + i);
            wrapper.setOffset(i);
            add(factoryFacade.newNameCharSliderBuilder(wrapper));
        }

        UiLabelBuilder labelBuilder =
                factoryFacade.newUiLabelBuilder("driverStatus");
        labelBuilder.setLabelVisible(false);
        labelBuilder.setRect(newRectangle(100, 20));
        add(labelBuilder);
    }

    public void setPatchCharMax(int max) {
        for (CtrlrComponentBuilderBase<?> builder : this) {
            if (builder instanceof NameCharSliderBuilder) {
                NameCharSliderBuilder sliderBuilder =
                        (NameCharSliderBuilder) builder;
                sliderBuilder.setPatchCharMax(max);
            }
        }
    }

    Rectangle newRectangle(int width, int height) {
        int x = xOffset;
        int y = 4;
        xOffset += width + 5;
        return new Rectangle(x, y, width, height);
    }

    @Override
    protected String getModulatorName() {
        return "globalPatchControls";
    }
}
