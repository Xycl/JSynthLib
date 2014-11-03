package org.jsynthlib.device.model;

import org.jsynthlib.device.model.handler.IPatchStringSender;
import org.jsynthlib.device.model.handler.ISender;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.xmldevice.MidiSenderReference;
import org.jsynthlib.xmldevice.StringSenderReference;

public interface MidiSenderFactory {

    ISender newSender(MidiSenderReference ref, Patch patch);

    IPatchStringSender newStringSender(StringSenderReference ref, Patch patch);
}
