package org.jsynthlib.utils.ctrlr.builder.component;

import org.ctrlr.panel.MidiType;
import org.ctrlr.panel.ModulatorType;
import org.jsynthlib.utils.ctrlr.builder.SliderSpecWrapper;
import org.jsynthlib.utils.ctrlr.driverContext.ParameterOffsetParser;
import org.jsynthlib.utils.ctrlr.driverContext.SysexFormulaParser;
import org.jsynthlib.xmldevice.MidiSenderReference;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public abstract class CtrlrMidiComponentBuilder extends
CtrlrComponentBuilderBase<SliderSpecWrapper> {

    @Inject
    private SysexFormulaParser formulaParser;
    @Inject
    private ParameterOffsetParser offsetParser;

    @Inject
    @Named("prefix")
    private String prefix;

    private int midiMessageCtrlrValue = 0;

    @Override
    protected void createMidiElement(ModulatorType modulator) {
        if (getObject().isSetMidiSender()) {
            MidiSenderReference ref = getObject().getMidiSender();
            int min = getMin();
            int max = getMax();
            createMidiElement(modulator,
                    formulaParser.parseSysexFormula(ref, min, max));
        }
    }

    protected void createMidiElement(ModulatorType modulator,
            String sysexFormula) {
        MidiType midiType = modulator.addNewMidi();
        midiType.setMidiMessageType(5);
        midiType.setMidiMessageChannelOverride(0);
        midiType.setMidiMessageChannel(1);
        midiType.setMidiMessageCtrlrNumber(1);
        midiType.setMidiMessageCtrlrValue(midiMessageCtrlrValue);
        midiType.setMidiMessageMultiList("");
        midiType.setMidiMessageSysExFormula(sysexFormula);
    }

    @Override
    protected String getModulatorName() {
        if (getObject().isSetParamModel()) {
            int[] parameterOffset =
                    offsetParser.parseParameterOffset(getObject()
                            .getParamModel());
            if (parameterOffset != null && parameterOffset.length > 0) {
                return prefix + Integer.toString(parameterOffset[0]);
            }
        }
        return prefix + getObject().getName();
    }

    @Override
    public void setObject(SliderSpecWrapper object) {
        setMin(object.getMin());
        setMax(object.getMax());
        super.setObject(object);
    }

    protected int getMidiMessageCtrlrValue() {
        return midiMessageCtrlrValue;
    }

    protected void setMidiMessageCtrlrValue(int midiMessageCtrlrValue) {
        this.midiMessageCtrlrValue = midiMessageCtrlrValue;
    }

}
