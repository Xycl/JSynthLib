package org.jsynthlib.utils.ctrlr.lua;

import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultAssignValuesDecorator.Factory;

public interface DecoratorFactoryFacade
        extends
        Factory,
        org.jsynthlib.utils.ctrlr.lua.decorator.DefaultGetMethodDecorator.Factory,
        org.jsynthlib.utils.ctrlr.lua.decorator.DefaultGetNameMethodDecorator.Factory,
        org.jsynthlib.utils.ctrlr.lua.decorator.DefaultLoadMethodDecorator.Factory,
        org.jsynthlib.utils.ctrlr.lua.decorator.DefaultMidiReceivedDecorator.Factory,
        org.jsynthlib.utils.ctrlr.lua.decorator.DefaultSetNameMethodDecorator.Factory,
        org.jsynthlib.utils.ctrlr.lua.decorator.DefaultAssembleValuesDecorator.Factory,
        org.jsynthlib.utils.ctrlr.lua.decorator.DefaultSendMethodDecorator.Factory,
        org.jsynthlib.utils.ctrlr.lua.decorator.DefaultSaveMethodDecorator.Factory {

}
