package org.jsynthlib.utils.ctrlr.driverContext;

import org.jsynthlib.xmldevice.MidiSenderReference;

public interface SysexFormulaParser {

    String parseSysexFormula(MidiSenderReference ref, int min, int max);
}
