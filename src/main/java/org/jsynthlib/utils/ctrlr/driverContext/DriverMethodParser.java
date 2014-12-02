package org.jsynthlib.utils.ctrlr.driverContext;

import org.jsynthlib.utils.ctrlr.lua.decorator.DriverLuaHandler;

public interface DriverMethodParser {

    DriverLuaHandler getGetMethodDecorator(DriverLuaHandler undecoratedHandler);

    DriverLuaHandler getSendMethodDecorator(DriverLuaHandler undecoratedHandler);

    DriverLuaHandler getLoadMethodDecorator(DriverLuaHandler undecoratedHandler);

    DriverLuaHandler getSaveMethodDecorator(DriverLuaHandler undecoratedHandler);

    DriverLuaHandler getAssignMethodDecorator(
            DriverLuaHandler undecoratedHandler);

    DriverLuaHandler getAssembleMethodDecorator(
            DriverLuaHandler undecoratedHandler);

    DriverLuaHandler getGetSetNameMethodDecorators(
            DriverLuaHandler undecoratedHandler);
}
