package org.jsynthlib.utils.ctrlr.builder.component;

import java.awt.Rectangle;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.ctrlr.panel.ComponentType;
import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.PanelResourceManager;
import org.jsynthlib.utils.ctrlr.ResourceContainer;
import org.jsynthlib.utils.ctrlr.builder.SliderSpecWrapper;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamResources;
import org.jsynthlib.xmldevice.PatchParamValues;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class UiImageButtonBuilder extends CtrlrMidiComponentBuilder {

    public interface Factory {
        UiImageButtonBuilder newUiImageButtonBuilder(IntParamSpec paramSpec);
    }

    private final transient Logger log = Logger.getLogger(getClass());
    private IntParamSpec paramSpec;
    private PatchParamValues paramValues;

    @Inject
    private PanelResourceManager resourceManager;

    @Inject
    public UiImageButtonBuilder(@Assisted IntParamSpec paramSpec) {
        setObject(SliderSpecWrapper.Factory.newWrapper(paramSpec));
        if (paramSpec.isSetPatchParamResources()) {
            this.paramSpec = paramSpec;
        }
        if (paramSpec.isSetPatchParamValues()) {
            this.paramValues = paramSpec.getPatchParamValues();
        }

    }

    @Override
    public ModulatorType createComponent(PanelType panel, ModulatorType group,
            int vstIndex, Rectangle rect) {
        ModulatorType modulator = createModulator(panel, vstIndex);

        createMidiElement(modulator);
        ComponentType component = modulator.addNewComponent();
        setDefaultComponentFields(component, group, getObject().getName(),
                panel);

        int width = 57;
        int height = 44;

        if (paramSpec != null) {
            try {
                ResourceContainer resource =
                        resourceManager.addImageResource(panel, paramSpec);
                component.setUiImageButtonResource(resource.getName());
                width = resource.getWidth();
                height = resource.getHeight();
                component.setResourceImageWidth(width);
                component.setResourceImageHeight(height);
            } catch (NoSuchAlgorithmException | IOException e) {
                log.warn(e.getMessage(), e);
            }
        }

        component.setUiImageButtonMode(0);
        component.setResourceImagePaintMode(36);
        component.setResourceImageOrientation(0);

        if (paramValues != null) {
            String[] valueArray = paramValues.getPatchParamValueArray();
            setButtonText(component, valueArray, true);
            height += 32;
        } else if (paramSpec != null) {
            PatchParamResources resources = paramSpec.getPatchParamResources();
            String[] resourceArray = resources.getPatchParamResourceArray();
            setButtonText(component, resourceArray, false);
        }

        component.setUiButtonRepeat(0);
        component.setUiButtonRepeatRate(100);
        component.setComponentEffectAlpha(0);
        component.setUiType("uiImageButton");
        rect.setSize(width, height);
        setComponentRectangle(component, rect);

        return modulator;
    }

    void setButtonText(ComponentType component, String[] array, boolean showText) {
        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            String value = array[i];
            if (i > 0) {
                contentBuilder.append("\n");
            }
            if (showText) {
                contentBuilder.append(value);
            } else {
                contentBuilder.append(Integer.toString(i));
            }
        }
        component.setUiImageButtonTextColour("ffffffff");
        component.setUiImageButtonContent(contentBuilder.toString());
        component.setUiImageButtonTextPosition("none");
        if (showText) {
            component.setUiImageButtonTextWidth(64);
            component.setUiImageButtonTextHeight(20);
        } else {
            component.setUiImageButtonTextWidth(0);
            component.setUiImageButtonTextHeight(0);
        }
        component.setUiButtonTextFont("Arial;12;0;0;0;0;1");
        component.setUiButtonTextJustification("centred");

    }

}
