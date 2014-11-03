package org.jsynthlib.device.model.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsynthlib.device.model.ParamModelFactory;
import org.jsynthlib.device.model.PatchStringModel;
import org.jsynthlib.device.model.handler.IParamModel;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.xmldevice.HandlerDefinitionBase;
import org.jsynthlib.xmldevice.ParamModelReference;
import org.jsynthlib.xmldevice.StringModelReference;

public class ParamModelFactoryImpl extends HandlerFactoryBase implements
        ParamModelFactory {

    private final transient Logger log = Logger.getLogger(getClass());

    public ParamModelFactoryImpl(Map<String, HandlerDefinitionBase> bindingMap) {
        super(bindingMap);
    }

    @Override
    public IParamModel newParamModel(ParamModelReference ref, Patch patch) {
        if (ref == null) {
            return null;
        }
        HandlerDefinitionBase csDef = getHandlerDefinition(ref.getName());
        try {
            IParamModel paramModel = null;
            if (csDef.getHandlerClass() != null) {
                paramModel =
                        ((IParamModel) Class.forName(csDef.getHandlerClass())
                                .newInstance());
                copyProperties(ref, paramModel, patch);
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

            return paramModel;
        } catch (InstantiationException | IllegalAccessException
                | InvocationTargetException | SecurityException
                | ClassNotFoundException e) {
            log.warn(e.getMessage(), e);
        } catch (ClassCastException e) {
            log.warn("Duplicate param model registrations", e);
        }
        return null;
    }

    @Override
    public PatchStringModel newPatchStringModel(StringModelReference ref,
            Patch patch) {
        if (ref == null) {
            return null;
        }
        HandlerDefinitionBase csDef = getHandlerDefinition(ref.getName());
        try {
            PatchStringModel stringModel = null;
            if (csDef.getHandlerClass() != null) {
                stringModel =
                        ((PatchStringModel) Class.forName(
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
            copyProperties(ref, stringModel, patch);
            return stringModel;
        } catch (InstantiationException | IllegalAccessException
                | InvocationTargetException | SecurityException
                | ClassNotFoundException e) {
            log.warn(e.getMessage(), e);
        } catch (ClassCastException e) {
            log.warn("Duplicate string model registrations", e);
        }
        return null;

    }

}
