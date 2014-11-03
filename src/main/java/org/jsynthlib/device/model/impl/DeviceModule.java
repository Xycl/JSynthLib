package org.jsynthlib.device.model.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jsynthlib.device.model.ChecksumCalculatorFactory;
import org.jsynthlib.device.model.DriverFactory;
import org.jsynthlib.device.model.MidiSenderFactory;
import org.jsynthlib.device.model.ParamModelFactory;
import org.jsynthlib.device.model.XmlDriverEditorControllerFactory;
import org.jsynthlib.device.viewcontroller.XmlDriverEditorController;
import org.jsynthlib.xmldevice.ChecksumCalculatorDefinition;
import org.jsynthlib.xmldevice.HandlerDefinitionBase;
import org.jsynthlib.xmldevice.MidiSenderDefinition;
import org.jsynthlib.xmldevice.ParamModelDefinition;
import org.jsynthlib.xmldevice.StringModelDefinition;
import org.jsynthlib.xmldevice.StringSenderDefinition;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class DeviceModule extends AbstractModule {

    private final Map<String, HandlerDefinitionBase> bindingMap;

    public DeviceModule(Map<String, HandlerDefinitionBase> nameBindingMap) {
        this.bindingMap = nameBindingMap;
    }

    @Override
    protected void configure() {
        bind(DriverFactory.class).to(DriverFactoryImpl.class);
        install(new FactoryModuleBuilder().implement(
                XmlDriverEditorController.class,
                XmlDriverEditorController.class).build(
                XmlDriverEditorControllerFactory.class));
    }

    @Provides
    ChecksumCalculatorFactory provideChecksumFactory() {
        final Map<String, HandlerDefinitionBase> csBindingMap =
                new HashMap<String, HandlerDefinitionBase>();
        Iterator<Entry<String, HandlerDefinitionBase>> iterator =
                bindingMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, HandlerDefinitionBase> entry = iterator.next();
            if (entry.getValue() instanceof ChecksumCalculatorDefinition) {
                csBindingMap.put(entry.getKey(), entry.getValue());
            }
        }
        return new ChecksumCalculatorFactoryImpl(csBindingMap);
    }

    @Provides
    MidiSenderFactory provideSenderFactory() {
        final Map<String, HandlerDefinitionBase> senderBindingMap =
                new HashMap<String, HandlerDefinitionBase>();
        Iterator<Entry<String, HandlerDefinitionBase>> iterator =
                bindingMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, HandlerDefinitionBase> entry = iterator.next();
            if (entry.getValue() instanceof MidiSenderDefinition
                    || entry.getValue() instanceof StringSenderDefinition) {
                senderBindingMap.put(entry.getKey(), entry.getValue());
            }
        }
        return new MidiSenderFactoryImpl(senderBindingMap);
    }

    @Provides
    ParamModelFactory provideParamModelFactory() {
        final Map<String, HandlerDefinitionBase> pmBindingMap =
                new HashMap<String, HandlerDefinitionBase>();
        Iterator<Entry<String, HandlerDefinitionBase>> iterator =
                bindingMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, HandlerDefinitionBase> entry = iterator.next();
            if (entry.getValue() instanceof ParamModelDefinition
                    || entry.getValue() instanceof StringModelDefinition) {
                pmBindingMap.put(entry.getKey(), entry.getValue());
            }
        }
        return new ParamModelFactoryImpl(pmBindingMap);
    }

}
