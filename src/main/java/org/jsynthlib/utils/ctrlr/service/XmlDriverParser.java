package org.jsynthlib.utils.ctrlr.service;

import org.apache.log4j.Logger;
import org.jsynthlib.core.impl.PopupHandlerProvider;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.utils.ctrlr.CtrlrMidiService;
import org.jsynthlib.utils.ctrlr.DriverParseException;
import org.jsynthlib.utils.ctrlr.controller.LuaFactoryFacade;
import org.jsynthlib.utils.ctrlr.domain.DriverModel;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public abstract class XmlDriverParser {

    private final transient Logger log = Logger.getLogger(getClass());

    @Inject
    private PopupManager popupManager;

    @Inject
    private DriverModel model;

    private IDriver driver;

    @Inject
    private ConverterDeviceFactory deviceFactory;

    @Inject
    @Named("className")
    private String driverClass;

    @Inject
    private CtrlrMidiService midiService;

    @Inject
    private LuaFactoryFacade luaFacade;

    @Inject
    @Named("editor")
    private LuaMethodProvider luaMethodProvider;

    @Inject
    @Named("prefix")
    protected String prefix;

    public void parse() throws DriverParseException {
        PopupHandlerProvider.setInjector(popupManager);

        Device device = deviceFactory.getDevice();
        device.setMidiService(midiService);
        for (IDriver drvr : device) {
            if (drvr.getClass().getName().equals(driverClass)) {
                this.driver = drvr;
                break;
            }
        }

        if (driver == null) {
            throw new DriverParseException("Could not find driver "
                    + driverClass);
        } else if (!driver.canCreatePatch()) {
            log.info("Driver cannot create patches");
        } else {
            parseDriver();
            luaFacade.newLoadMenuController();
            luaFacade.newReceiveMenuController();
            luaFacade.newSaveMenuController();
            luaFacade.newWriteMenuController();
            model.driverParseComplete();
            luaMethodProvider.driverParseComplete();
        }
    }

    protected abstract void parseDriver() throws DriverParseException;

    public PopupManager getPopupManager() {
        return popupManager;
    }

    public IDriver getDriver() {
        return driver;
    }

    public DriverModel getModel() {
        return model;
    }
}
