package org.jsynthlib.utils.ctrlr.controller.modulator;

import javax.inject.Named;

import org.ctrlr.panel.MidiType;
import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.utils.ctrlr.domain.SliderSpecWrapper;
import org.jsynthlib.utils.ctrlr.service.ParameterOffsetParser;
import org.jsynthlib.utils.ctrlr.service.SysexFormulaParser;
import org.jsynthlib.xmldevice.MidiSenderReference;

import com.google.inject.Inject;

public abstract class MidiModulatorControllerBase extends
ModulatorControllerBase {

    private MidiType midiType;
    private final SliderSpecWrapper sliderSpec;
    private final int min;
    private final int max;

    @Inject
    @Named("prefix")
    private String prefix;

    @Inject
    private SysexFormulaParser formulaParser;

    @Inject
    private ParameterOffsetParser offsetParser;

    @Inject
    private DriverModel model;

    public MidiModulatorControllerBase(SliderSpecWrapper sliderSpec) {
        this.sliderSpec = sliderSpec;
        min = sliderSpec.getMin();
        max = sliderSpec.getMax();
    }

    @Override
    public void init() {
        super.init();
        setMin(min);
        setMax(max);
        if (getVstIndex() == 0) {
            setVstIndex(model.getNextVstIndex());
        }
        setModulatorName(prefix + sliderSpec.getName());
        setComponentVisibleName(sliderSpec.getName());

        midiType = getModulator().addNewMidi();
        midiType.setMidiMessageType(5);
        midiType.setMidiMessageChannelOverride(0);
        midiType.setMidiMessageChannel(1);
        setMidiMessageCtrlrValue(0);
        midiType.setMidiMessageCtrlrNumber(1);
        midiType.setMidiMessageMultiList("");
        if (sliderSpec.isSetMidiSender()) {
            MidiSenderReference ref = sliderSpec.getMidiSender();
            String sysexFormula =
                    formulaParser.parseSysexFormula(ref, min, max);
            setMidiMessageSysExFormula(sysexFormula);
        } else {
            setMidiMessageSysExFormula("");
        }

        if (sliderSpec.isSetParamModel()) {
            int[] parameterOffset =
                    offsetParser.parseParameterOffset(sliderSpec
                            .getParamModel());
            if (parameterOffset != null && parameterOffset.length > 0) {
                setModulatorName(prefix + Integer.toString(parameterOffset[0]));
            }
        }

    }

    public void setMidiMessageSysExFormula(String sysexFormula) {
        midiType.setMidiMessageSysExFormula(sysexFormula);
    }

    public void setMidiMessageCtrlrValue(int midiMessageCtrlrValue) {
        if (midiType != null) {
            midiType.setMidiMessageCtrlrValue(midiMessageCtrlrValue);
        }
    }

    public SliderSpecWrapper getSliderSpec() {
        return sliderSpec;
    }
}
