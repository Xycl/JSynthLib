package org.jsynthlib.utils.ctrlr.lua;

import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultAssembleValuesDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultAssignValuesDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultGetMethodDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultGetNameMethodDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultLoadMethodDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultMidiReceivedDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultSaveMethodDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultSendMethodDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultSetNameMethodDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DriverLuaHandler;

import com.google.inject.Inject;

public class DecoratorFactoryFacadeImpl implements DecoratorFactoryFacade {

    @Inject
    private DefaultAssignValuesDecorator.Factory defaultAssignValuesFactory;

    @Inject
    private DefaultGetMethodDecorator.Factory defaultGetMethodFactory;

    @Inject
    private DefaultGetNameMethodDecorator.Factory defaultGetNameMethodFactory;

    @Inject
    private DefaultLoadMethodDecorator.Factory defaultLoadMethodFactory;

    @Inject
    private DefaultMidiReceivedDecorator.Factory defaultMidiReceivedFactory;

    @Inject
    private DefaultSetNameMethodDecorator.Factory defaultSetNameMethodFactory;

    @Inject
    private DefaultAssembleValuesDecorator.Factory defaultAssembleValuesFactory;

    @Inject
    private DefaultSendMethodDecorator.Factory defaultSendMethodFactory;

    @Inject
    private DefaultSaveMethodDecorator.Factory defaultSaveMethodFactory;

    @Override
    public DefaultAssignValuesDecorator newDefaultAssignValuesDecorator(
            DriverLuaHandler decoratedHandler) {
        return defaultAssignValuesFactory
                .newDefaultAssignValuesDecorator(decoratedHandler);
    }

    @Override
    public DefaultGetMethodDecorator newDefaultGetMethodDecorator(
            DriverLuaHandler decoratedHandler) {
        return defaultGetMethodFactory
                .newDefaultGetMethodDecorator(decoratedHandler);
    }

    @Override
    public DefaultGetNameMethodDecorator newDefaultGetNameDecorator(
            DriverLuaHandler decoratedHandler, String[] chars) {
        return defaultGetNameMethodFactory.newDefaultGetNameDecorator(
                decoratedHandler, chars);
    }

    @Override
    public DefaultLoadMethodDecorator newDefaultLoadMethodDecorator(
            DriverLuaHandler decoratedHandler) {
        return defaultLoadMethodFactory
                .newDefaultLoadMethodDecorator(decoratedHandler);
    }

    @Override
    public DefaultMidiReceivedDecorator newDefaultMidiReceivedDecorator(
            DriverLuaHandler decoratedHandler) {
        return defaultMidiReceivedFactory
                .newDefaultMidiReceivedDecorator(decoratedHandler);
    }

    @Override
    public DefaultSetNameMethodDecorator newDefaultSetNameDecorator(
            DriverLuaHandler decoratedHandler, String[] chars) {
        return defaultSetNameMethodFactory.newDefaultSetNameDecorator(
                decoratedHandler, chars);
    }

    @Override
    public DefaultAssembleValuesDecorator newDefaultAssembleValuesDecorator(
            DriverLuaHandler decoratedHandler) {
        return defaultAssembleValuesFactory
                .newDefaultAssembleValuesDecorator(decoratedHandler);
    }

    @Override
    public DefaultSendMethodDecorator newDefaultSendMethodDecorator(
            DriverLuaHandler decoratedHandler) {
        return defaultSendMethodFactory
                .newDefaultSendMethodDecorator(decoratedHandler);
    }

    @Override
    public DefaultSaveMethodDecorator newDefaultSaveMethodDecorator(
            DriverLuaHandler decoratedHandler) {
        return defaultSaveMethodFactory
                .newDefaultSaveMethodDecorator(decoratedHandler);
    }

}
