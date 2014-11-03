package org.jsynthlib.device.model;

import org.jsynthlib.midi.service.ChecksumCalculator;
import org.jsynthlib.xmldevice.ChecksumCalculatorReference;

public interface ChecksumCalculatorFactory {

    ChecksumCalculator newChecksumCalculator(ChecksumCalculatorReference ref);
}
