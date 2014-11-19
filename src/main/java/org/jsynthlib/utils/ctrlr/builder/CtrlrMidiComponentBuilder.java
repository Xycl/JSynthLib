package org.jsynthlib.utils.ctrlr.builder;

import org.ctrlr.panel.ModulatorType;
import org.jsynthlib.utils.ctrlr.SliderSpecWrapper;
import org.jsynthlib.utils.ctrlr.SysexFormulaParser;
import org.jsynthlib.xmldevice.MidiSenderReference;

public abstract class CtrlrMidiComponentBuilder extends
        CtrlrComponentBuilder<SliderSpecWrapper> {

    private final SysexFormulaParser formulaParser;

    protected CtrlrMidiComponentBuilder(SliderSpecWrapper object,
            SysexFormulaParser formulaParser) {
        super(object);
        this.formulaParser = formulaParser;
    }

    protected void createMidiElement(ModulatorType modulator) {
        if (object.isSetMidiSender()) {
            MidiSenderReference ref = object.getMidiSender();
            int min = object.getMin();
            int max = object.getMax();
            createMidiElement(modulator,
                    formulaParser.parseSysexFormula(ref, min, max));
        }

    }
}
