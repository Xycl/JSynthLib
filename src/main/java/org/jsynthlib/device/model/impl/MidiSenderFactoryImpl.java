package org.jsynthlib.device.model.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsynthlib.device.model.MidiSenderFactory;
import org.jsynthlib.device.model.handler.IPatchStringSender;
import org.jsynthlib.device.model.handler.ISender;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.xmldevice.HandlerDefinitionBase;
import org.jsynthlib.xmldevice.MidiSenderReference;
import org.jsynthlib.xmldevice.StringSenderReference;

public class MidiSenderFactoryImpl extends HandlerFactoryBase implements
MidiSenderFactory {

    private final transient Logger log = Logger.getLogger(getClass());

    public MidiSenderFactoryImpl(Map<String, HandlerDefinitionBase> bindingMap) {
        super(bindingMap);
    }

    @Override
    public ISender newSender(MidiSenderReference ref, Patch patch) {
        if (ref == null) {
            return null;
        }
        HandlerDefinitionBase csDef = getHandlerDefinition(ref.getName());
        try {
            ISender sender = null;
            if (csDef.getHandlerClass() != null) {
                sender =
                        ((ISender) Class.forName(csDef.getHandlerClass())
                                .newInstance());
            } else if (csDef.getClosure() != null) {
                // TODO: Implement
                // final String closure = csDef.getClosure();
                // checksumCalculator = new InjectedChecksumCalculator() {
                //
                // @Override
                // protected String getClosureString() {
                // return closure;
                // }
                // };
            }
            copyProperties(ref, sender, patch);
            return sender;
        } catch (InstantiationException | IllegalAccessException
                | InvocationTargetException | SecurityException
                | ClassNotFoundException e) {
            log.warn(e.getMessage(), e);
        } catch (ClassCastException e) {
            log.warn("Duplicate sender registrations", e);
        }
        return null;
    }

    @Override
    public IPatchStringSender newStringSender(StringSenderReference ref,
            Patch patch) {
        if (ref == null) {
            return null;
        }
        HandlerDefinitionBase csDef = getHandlerDefinition(ref.getName());
        try {
            IPatchStringSender stringSender = null;
            if (csDef.getHandlerClass() != null) {
                stringSender =
                        ((IPatchStringSender) Class.forName(
                                csDef.getHandlerClass()).newInstance());
            } else if (csDef.getClosure() != null) {
                // TODO: Implement
                // final String closure = csDef.getClosure();
                // checksumCalculator = new InjectedChecksumCalculator() {
                //
                // @Override
                // protected String getClosureString() {
                // return closure;
                // }
                // };
            }
            copyProperties(ref, stringSender, patch);
            return stringSender;
        } catch (InstantiationException | IllegalAccessException
                | InvocationTargetException | SecurityException
                | ClassNotFoundException e) {
            log.warn(e.getMessage(), e);
        } catch (ClassCastException e) {
            log.warn("Duplicate string sender registrations", e);
        }
        return null;

    }

}
