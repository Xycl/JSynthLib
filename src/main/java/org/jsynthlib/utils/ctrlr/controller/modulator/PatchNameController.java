package org.jsynthlib.utils.ctrlr.controller.modulator;

import java.util.Observable;
import java.util.Observer;

import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.xmldevice.StringParamSpec;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class PatchNameController extends UiLabelController implements Observer {

    public interface Factory {
        PatchNameController newPatchNameController(StringParamSpec paramSpec);
    }

    private final StringParamSpec paramSpec;
    private final DriverModel model;

    @Inject
    public PatchNameController(@Assisted StringParamSpec paramSpec,
            DriverModel model) {
        super(paramSpec.getName());
        model.addObserver(this);
        this.model = model;
        this.paramSpec = paramSpec;
    }

    @Override
    public void init() {
        super.init();
        setEditOnSingleClick(true);
        setUiLabelText("New Patch");
        setExcludeFromSnapshot(true);
        setLength(paramSpec.getLength());
        setMuteOnStart(true);
        String nameModId = getUniqueName(paramSpec.getName());
        setModulatorName(nameModId);
        model.setNameModulatorName(nameModId);
    }

    @Override
    public void update(Observable o, Object arg) {
        setUiLabelChangedCbk(model.getSetNameMethodName());
    }
}
