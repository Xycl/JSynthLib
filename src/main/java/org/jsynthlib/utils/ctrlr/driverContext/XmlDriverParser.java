package org.jsynthlib.utils.ctrlr.driverContext;

import java.util.List;

import org.apache.log4j.Logger;
import org.jsynthlib.core.impl.PopupHandlerProvider;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.utils.ctrlr.CtrlrMidiService;
import org.jsynthlib.utils.ctrlr.builder.component.CtrlrComponentBuilderBase;
import org.jsynthlib.utils.ctrlr.lua.DriverLuaBean;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public abstract class XmlDriverParser {

    private final transient Logger log = Logger.getLogger(getClass());

    @Inject
    private PopupManager popupManager;

    @Inject
    private DriverLuaBean luaBean;

    private IDriver driver;


    @Inject
    private ConverterDeviceFactory deviceFactory;

    @Inject
    @Named("className")
    private String driverClass;

    @Inject
    private CtrlrMidiService midiService;

    @Inject
    @Named("prefix")
    protected String prefix;

    public List<CtrlrComponentBuilderBase<?>> parseDriver() {
        PopupHandlerProvider.setInjector(popupManager);

        Device device = deviceFactory.getDevice();
        device.setMidiService(midiService);
        for (IDriver drvr : device) {
            if (drvr.getClass().getName().equals(driverClass)) {
                this.driver = drvr;
                break;
            }
        }
        List<CtrlrComponentBuilderBase<?>> modulatorList = null;
        if (driver == null) {
            throw new IllegalArgumentException("Could not find driver "
                    + driverClass);
        } else if (!driver.canCreatePatch()) {
            log.info("Driver cannot create patches");
        } else {
            modulatorList = parseDriverGui();
            parseDriverMethods();
        }
        return modulatorList;
    }

    protected abstract List<CtrlrComponentBuilderBase<?>> parseDriverGui();

    protected abstract void parseDriverMethods();

    public PopupManager getPopupManager() {
        return popupManager;
    }

    public IDriver getDriver() {
        return driver;
    }

    public DriverLuaBean getLuaBean() {
        return luaBean;
    }
}
