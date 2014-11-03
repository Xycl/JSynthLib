package org.jsynthlib.device.model.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsynthlib.device.model.ChecksumCalculatorFactory;
import org.jsynthlib.midi.service.ChecksumCalculator;
import org.jsynthlib.xmldevice.ChecksumCalculatorReference;
import org.jsynthlib.xmldevice.HandlerDefinitionBase;

public class ChecksumCalculatorFactoryImpl extends HandlerFactoryBase implements
ChecksumCalculatorFactory {

    private final transient Logger log = Logger.getLogger(getClass());

    public ChecksumCalculatorFactoryImpl(
            Map<String, HandlerDefinitionBase> bindingMap) {
        super(bindingMap);
    }

    @Override
    public ChecksumCalculator newChecksumCalculator(
            ChecksumCalculatorReference ref) {
        HandlerDefinitionBase csDef = getHandlerDefinition(ref.getName());
        try {
            ChecksumCalculator checksumCalculator = null;
            if (csDef.getHandlerClass() != null) {
                checksumCalculator =
                        ((ChecksumCalculator) Class.forName(
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
            copyProperties(ref, checksumCalculator);
            return checksumCalculator;
        } catch (InstantiationException e) {
            log.warn(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            log.warn(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.warn(e.getMessage(), e);
        } catch (SecurityException e) {
            log.warn(e.getMessage(), e);
        } catch (ClassCastException e) {
            log.warn("Duplicate checksum calculator registrations", e);
        } catch (ClassNotFoundException e) {
            log.warn(e.getMessage(), e);
        }
        return null;
    }
}
