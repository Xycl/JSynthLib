/*
 * Copyright 2014 Pascal Collberg
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package org.jsynthlib.inject;

import org.jsynthlib.core.AppConfig;
import org.jsynthlib.core.impl.AppConfigImpl;
import org.jsynthlib.device.model.DeviceFactory;
import org.jsynthlib.device.model.DeviceManager;
import org.jsynthlib.device.model.DriverIdentifier;
import org.jsynthlib.device.model.impl.DeviceFactoryImpl;
import org.jsynthlib.device.model.impl.DeviceManagerImpl;
import org.jsynthlib.device.model.impl.DriverIdentifierImpl;
import org.jsynthlib.midi.service.MasterKeyboardService;
import org.jsynthlib.midi.service.MidiLoopbackService;
import org.jsynthlib.midi.service.MidiMonitorService;
import org.jsynthlib.midi.service.MidiScanService;
import org.jsynthlib.midi.service.MidiService;
import org.jsynthlib.midi.service.impl.MasterKeyboardServiceImpl;
import org.jsynthlib.midi.service.impl.MidiLoopbackServiceImpl;
import org.jsynthlib.midi.service.impl.MidiMonitorServiceImpl;
import org.jsynthlib.midi.service.impl.MidiScanServiceImpl;
import org.jsynthlib.midi.service.impl.MidiServiceImpl;
import org.jsynthlib.patch.model.MultiPatchImporter;
import org.jsynthlib.patch.model.PatchEditFactory;
import org.jsynthlib.patch.model.PatchFactory;
import org.jsynthlib.patch.model.impl.PatchEdit;
import org.jsynthlib.patch.model.impl.PatchFactoryImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * @author Pascal Collberg
 */
public class JSynthLibInjector extends AbstractModule {

    private static final Injector INJECTOR = Guice
            .createInjector(new JSynthLibInjector());

    public static <T> T getInstance(Class<T> type) {
        return INJECTOR.getInstance(type);
    }

    public static Injector getInjector() {
        return INJECTOR;
    }

    /*
     * (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        bind(MasterKeyboardService.class).to(MasterKeyboardServiceImpl.class);
        bind(MidiLoopbackService.class).to(MidiLoopbackServiceImpl.class);
        bind(MidiMonitorService.class).to(MidiMonitorServiceImpl.class);
        bind(MidiScanService.class).to(MidiScanServiceImpl.class);
        bind(MidiService.class).to(MidiServiceImpl.class);
        bind(MultiPatchImporter.class).to(PatchFactoryImpl.class);
        bind(PatchFactory.class).to(PatchFactoryImpl.class);
        bind(DriverIdentifier.class).to(DriverIdentifierImpl.class);
        bind(AppConfig.class).to(AppConfigImpl.class);
        install(new FactoryModuleBuilder().implement(PatchEdit.class,
                PatchEdit.class).build(PatchEditFactory.class));
        bind(DeviceManager.class).to(DeviceManagerImpl.class);
        bind(DeviceFactory.class).to(DeviceFactoryImpl.class);
    }

}
