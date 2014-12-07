package org.jsynthlib.utils.ctrlr.builder.component;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import org.jsynthlib.utils.ctrlr.builder.BuilderFactoryFacade;
import org.jsynthlib.utils.ctrlr.lua.DriverLuaBean;
import org.jsynthlib.xmldevice.XmlDriverDefinition;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GlobalGroupBuilder extends UiGroupBuilder {

    enum Globalbuttons {

        RECEIVE("Reeive"), WRITE("Write"), LOAD("Load"), SAVE("Save");

        private final String name;

        private Globalbuttons(String name) {
            this.name = name;
        }
    }

    private final Map<Globalbuttons, UiGlobalButtonBuilder> builderMap;

    private int xOffset = 4;

    @Inject
    private XmlDriverDefinition driverDef;

    @Inject
    private BuilderFactoryFacade factoryFacade;

    private final DriverLuaBean luaBean;

    @Inject
    public GlobalGroupBuilder(DriverLuaBean luaBean) {
        super("Global");
        this.luaBean = luaBean;

        builderMap =
                new HashMap<GlobalGroupBuilder.Globalbuttons, UiGlobalButtonBuilder>();

        for (Globalbuttons button : Globalbuttons.values()) {
            Rectangle rect = newRectangle(40, 20);
            UiGlobalButtonBuilder builder =
                    new UiGlobalButtonBuilder(button.name, rect);
            switch (button) {
            case RECEIVE:
                builder.setMethodName(luaBean.getReceiveMethodName());
                break;
            case WRITE:
                builder.setMethodName(luaBean.getWriteMethodName());
                break;
            case LOAD:
                builder.setMethodName(luaBean.getLoadMethodName());
                break;
            case SAVE:
                builder.setMethodName(luaBean.getSaveMethodName());
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
        patchNameBuilder
        .setModulatorName(luaBean.getNameModulatorName());
        patchNameBuilder.setUiLabelText("New Patch");
        patchNameBuilder
        .setUiLabelChangedCbk(luaBean
                .getSetNameMethodName());
        add(patchNameBuilder);

        for (int i = driverDef.getPatchNameStart(); i < driverDef
                .getPatchNameStart() + patchNameBuilder.getLength(); i++) {
            GlobalSliderSpecWrapper wrapper =
                    new GlobalSliderSpecWrapper(
                            luaBean.getDriverPrefix()
                            + i);
            wrapper.setOffset(i);
            add(factoryFacade.newNameCharSliderBuilder(wrapper));
        }

        UiLabelBuilder labelBuilder =
                factoryFacade.newUiLabelBuilder("driverStatus");
        labelBuilder.setLabelBgColor("0xFF000000");
        labelBuilder.setModulatorName(luaBean.getInfoLabelName());
        labelBuilder.setLabelVisible(false);
        labelBuilder.setRect(newRectangle(400, 40));
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
        return luaBean.getDriverPrefix() + "_globalPatchControls";
    }
}
