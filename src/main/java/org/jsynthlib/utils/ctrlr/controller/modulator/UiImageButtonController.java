package org.jsynthlib.utils.ctrlr.controller.modulator;

import java.awt.Rectangle;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.jsynthlib.utils.ctrlr.domain.SliderSpecWrapper;
import org.jsynthlib.utils.ctrlr.service.PanelResourceManager;
import org.jsynthlib.utils.ctrlr.service.ResourceContainer;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamResources;
import org.jsynthlib.xmldevice.PatchParamValues;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class UiImageButtonController extends MidiModulatorControllerBase {

    public interface Factory {
        UiImageButtonController newUiImageButtonController(
                IntParamSpec paramSpec);
    }

    private final transient Logger log = Logger.getLogger(getClass());

    @Inject
    private PanelResourceManager resourceManager;

    private int width;

    private int height;

    private final IntParamSpec paramSpec;

    @Inject
    public UiImageButtonController(@Assisted IntParamSpec paramSpec) {
        super(SliderSpecWrapper.Factory.newWrapper(paramSpec));

        width = 57;
        height = 44;
        this.paramSpec = paramSpec;
    }

    // @Override
    // protected String getName() {
    // return getObject().getName();
    // }

    @Override
    public void init() {
        super.init();
        if (paramSpec != null) {
            try {
                ResourceContainer resource =
                        resourceManager.addImageResource(getPanel(), paramSpec);
                getComponent().setUiImageButtonResource(resource.getName());
                width = resource.getWidth();
                height = resource.getHeight();
                getComponent().setResourceImageWidth(width);
                getComponent().setResourceImageHeight(height);
            } catch (NoSuchAlgorithmException | IOException e) {
                log.warn(e.getMessage(), e);
            }
        }

        getComponent().setUiImageButtonMode(0);
        getComponent().setResourceImagePaintMode(36);
        getComponent().setResourceImageOrientation(0);

        if (paramSpec.isSetPatchParamValues()) {
            PatchParamValues paramValues = paramSpec.getPatchParamValues();
            String[] valueArray = paramValues.getPatchParamValueArray();
            setButtonText(valueArray, true);
            height += 32;
        } else if (paramSpec != null) {
            PatchParamResources resources = paramSpec.getPatchParamResources();
            String[] resourceArray = resources.getPatchParamResourceArray();
            setButtonText(resourceArray, false);
        }

        getComponent().setUiButtonRepeat(0);
        getComponent().setUiButtonRepeatRate(100);
        getComponent().setComponentEffectAlpha(0);
        getComponent().setUiType("uiImageButton");
    }

    @Override
    public void setRect(Rectangle rect) {
        rect.setSize(width, height);
        super.setRect(rect);
    }

    void setButtonText(String[] array, boolean showText) {
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
        getComponent().setUiImageButtonTextColour("ffffffff");
        getComponent().setUiImageButtonContent(contentBuilder.toString());
        getComponent().setUiImageButtonTextPosition("none");
        if (showText) {
            getComponent().setUiImageButtonTextWidth(64);
            getComponent().setUiImageButtonTextHeight(20);
        } else {
            getComponent().setUiImageButtonTextWidth(0);
            getComponent().setUiImageButtonTextHeight(0);
        }
        getComponent().setUiButtonTextFont("Arial;12;0;0;0;0;1");
        getComponent().setUiButtonTextJustification("centred");
    }
}
