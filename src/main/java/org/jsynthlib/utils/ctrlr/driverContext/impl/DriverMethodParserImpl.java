package org.jsynthlib.utils.ctrlr.driverContext.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.utils.SingletonMidiDeviceProvider.MidiRecordSession;
import org.jsynthlib.utils.SysexUtils;
import org.jsynthlib.utils.ctrlr.CtrlrMidiService;
import org.jsynthlib.utils.ctrlr.builder.component.GlobalGroupBuilder;
import org.jsynthlib.utils.ctrlr.driverContext.ConverterDeviceFactory;
import org.jsynthlib.utils.ctrlr.driverContext.DriverMethodParser;
import org.jsynthlib.utils.ctrlr.driverContext.PopupManager;
import org.jsynthlib.utils.ctrlr.driverContext.PopupManager.PopupSession;
import org.jsynthlib.utils.ctrlr.lua.DecoratorFactoryFacade;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultAssembleValuesDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultGetMethodDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultLoadMethodDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultSaveMethodDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultSendMethodDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultSendMethodDecorator.SendPatchMessage;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultSetNameMethodDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DriverLuaHandler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class DriverMethodParserImpl implements DriverMethodParser {

    private final transient Logger log = Logger.getLogger(getClass());

    @Inject
    private DecoratorFactoryFacade factoryFacade;

    @Inject
    private PopupManager popupManager;
    private IDriver driver;

    private final CtrlrMidiService midiService;

    @Inject
    private GlobalGroupBuilder globalGroupBuilder;

    @Inject
    public DriverMethodParserImpl(ConverterDeviceFactory deviceFactory,
            @Named("className") String driverClass, CtrlrMidiService midiService) {
        this.midiService = midiService;
        Device device = deviceFactory.getDevice();
        device.setMidiService(midiService);
        for (IDriver drvr : device) {
            if (drvr.getClass().getName().equals(driverClass)) {
                this.driver = drvr;
                break;
            }
        }
        if (driver == null) {
            throw new IllegalArgumentException("Could not find driver "
                    + driverClass);
        }
    }

    @Override
    public DriverLuaHandler getGetMethodDecorator(
            DriverLuaHandler undecoratedHandler) {
        DefaultGetMethodDecorator decorator =
                factoryFacade.newDefaultGetMethodDecorator(undecoratedHandler);
        MidiRecordSession midiRecordSession = midiService.openSession();
        PopupSession popupSession = popupManager.openSession();
        driver.requestPatchDump(0, 0);
        String midiMessages = midiService.closeSession(midiRecordSession);
        List<String> popups = popupManager.closeSession(popupSession);

        String[] split = midiMessages.split(";");
        if (split.length > 1) {
            List<String> msgList = Arrays.asList(split);
            msgList.remove("");
            decorator.addMidiMessages(msgList);
        }

        decorator.addPopups(popups);
        return decorator;
    }

    @Override
    public DriverLuaHandler getSendMethodDecorator(
            DriverLuaHandler undecoratedHandler) {
        DefaultSendMethodDecorator decorator =
                factoryFacade.newDefaultSendMethodDecorator(undecoratedHandler);
        String sysexID = driver.getSysexID().replaceAll("\\*{2}", "FF");
        byte[] sysexIdBytes = SysexUtils.stringToSysex(sysexID);
        Patch patch = driver.createPatch();
        MidiRecordSession session = midiService.openSession();
        driver.sendPatch(patch);
        String sentMessages = midiService.closeSession(session);
        List<String> sentMsgList = Arrays.asList(sentMessages.split(";"));
        sentMsgList.remove("");

        for (String sentMessage : sentMsgList) {
            byte[] msgBytes = SysexUtils.stringToSysex(sentMessage);
            boolean staticMsg = false;
            for (int i = 0; i < sysexIdBytes.length; i++) {
                byte b = sysexIdBytes[i];
                if (b != 0xFF && b != msgBytes[i]) {
                    log.info("Message did not match sysex id: " + sentMessage);
                    staticMsg = true;
                    break;
                }
            }
            if (!staticMsg && msgBytes.length != driver.getPatchSize()) {
                staticMsg = true;
            }
            decorator.addMessage(new SendPatchMessage(sentMessage, staticMsg));
        }
        return decorator;
    }

    @Override
    public DriverLuaHandler getLoadMethodDecorator(
            DriverLuaHandler undecoratedHandler) {
        DefaultLoadMethodDecorator decorator =
                factoryFacade.newDefaultLoadMethodDecorator(undecoratedHandler);
        return decorator;
    }

    @Override
    public DriverLuaHandler getSaveMethodDecorator(
            DriverLuaHandler undecoratedHandler) {
        DefaultSaveMethodDecorator decorator =
                factoryFacade.newDefaultSaveMethodDecorator(undecoratedHandler);
        return decorator;
    }

    private static final int CHAR_START = 32;
    private static final int CHAR_END = 122;

    @Override
    public DriverLuaHandler getGetSetNameMethodDecorators(
            DriverLuaHandler undecoratedHandler) {
        Patch patch = new Patch();
        HashMap<Integer, String> charMap = new HashMap<Integer, String>();
        char[] c = new char[driver.getPatchNameSize()];
        for (int i = CHAR_START; i <= CHAR_END; i++) {
            Arrays.fill(c, (char) 32);
            c[0] = (char) i;
            patch.sysex = new byte[driver.getPatchSize()];
            String string = new String(c);
            driver.setPatchName(patch, string);
            int key = patch.sysex[driver.getPatchNameStart()];
            String value =
                    StringEscapeUtils.escapeJava(Character.toString(c[0]));
            if (!charMap.containsKey(key)) {
                charMap.put(key, value);
            }
        }

        ArrayList<Integer> keys = new ArrayList<Integer>(charMap.keySet());
        Collections.sort(keys);

        globalGroupBuilder.setPatchCharMax(keys.get(keys.size() - 1));

        String[] array = new String[keys.get(keys.size() - 1) + 1];
        Arrays.fill(array, "|");
        for (Integer key : keys) {
            array[key] = charMap.get(key);
        }

        DefaultSetNameMethodDecorator defaultSetNameDecorator =
                factoryFacade.newDefaultSetNameDecorator(undecoratedHandler,
                        array);

        return factoryFacade.newDefaultGetNameDecorator(
                defaultSetNameDecorator, array);
    }

    @Override
    public DriverLuaHandler getAssignMethodDecorator(
            DriverLuaHandler undecoratedHandler) {
        return factoryFacade
                .newDefaultAssignValuesDecorator(undecoratedHandler);
    }

    @Override
    public DriverLuaHandler getAssembleMethodDecorator(
            DriverLuaHandler undecoratedHandler) {
        DefaultAssembleValuesDecorator decorator =
                factoryFacade
                .newDefaultAssembleValuesDecorator(undecoratedHandler);
        return decorator;
    }
}
