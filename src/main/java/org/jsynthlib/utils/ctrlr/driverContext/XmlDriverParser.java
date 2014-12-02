package org.jsynthlib.utils.ctrlr.driverContext;

import java.util.List;

import org.apache.log4j.Logger;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.utils.ctrlr.builder.PanelLuaManagerBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.CtrlrComponentBuilderBase;
import org.jsynthlib.utils.ctrlr.lua.decorator.DriverLuaHandler;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference;

import com.google.inject.Inject;

public abstract class XmlDriverParser {

    private final transient Logger log = Logger.getLogger(getClass());

    @Inject
    private PanelLuaManagerBuilder luaManagerBuilder;

    @Inject
    private DriverMethodParser jslMethodParser;

    @Inject
    private DriverLuaHandler undecoratedHandler;

    @Inject
    private ConverterDeviceFactory deviceFactory;

    @Inject
    private XmlDriverReference driverRef;

    public void parseDriverAndGeneratePanel(PanelType panel) {
        String driverClass = driverRef.getDriverClass();
        IDriver driver = null;
        Device device = deviceFactory.getDevice();
        for (IDriver drvr : device) {
            if (drvr.getClass().getName().equals(driverClass)) {
                driver = drvr;
                break;
            }
        }
        if (driver == null) {
            log.info("Could not find driver " + driverClass);
        } else if (!driver.canCreatePatch()) {
            log.info("Driver cannot create patches");
        } else {
            List<CtrlrComponentBuilderBase<?>> modulatorList = parseDriverGui();
            parseDriverMethods();
            generatePanel(modulatorList, panel);
        }
    }

    protected abstract List<CtrlrComponentBuilderBase<?>> parseDriverGui();

    protected void parseDriverMethods() {
        DriverLuaHandler luaHandler =
                jslMethodParser.getGetMethodDecorator(undecoratedHandler);
        luaHandler = jslMethodParser.getGetSetNameMethodDecorators(luaHandler);
        luaHandler = jslMethodParser.getLoadMethodDecorator(luaHandler);
        luaHandler = jslMethodParser.getSaveMethodDecorator(luaHandler);
        luaHandler = jslMethodParser.getSendMethodDecorator(luaHandler);
        luaHandler = jslMethodParser.getAssignMethodDecorator(luaHandler);
        luaHandler = jslMethodParser.getAssembleMethodDecorator(luaHandler);
        luaManagerBuilder.addDriverDecorator(luaHandler);
    }

    protected void generatePanel(
            List<CtrlrComponentBuilderBase<?>> modulatorList, PanelType panel) {
        int vstIndex = 0;
        for (CtrlrComponentBuilderBase<?> ctrlrComponentBuilderBase : modulatorList) {
            ctrlrComponentBuilderBase.createModulator(panel, null, vstIndex);
        }

    }
}
