package org.jsynthlib.utils.ctrlr.service;

import org.jsynthlib.xmldevice.MidiSenderReference;

public interface SysexFormulaParser {

    String parseSysexFormula(MidiSenderReference ref, int min, int max);
}
