package org.jsynthlib.utils.ctrlr.impl;

import java.util.Arrays;
import java.util.List;

import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.utils.SingletonMidiDeviceProvider.MidiRecordSession;
import org.jsynthlib.utils.ctrlr.CtrlrMidiService;
import org.jsynthlib.utils.ctrlr.builder.method.MethodBuilder;
import org.jsynthlib.utils.ctrlr.builder.method.PatchRequestMethodBuilder;
import org.jsynthlib.utils.ctrlr.driverContext.CtrlrConverterDeviceFactory;
import org.jsynthlib.utils.ctrlr.driverContext.GlobalPatchMethodParser;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class GlobalPatchMethodParserImpl implements GlobalPatchMethodParser {

    @Inject
    private PatchRequestMethodBuilder methodBuilder;

    @Inject
    private CtrlrMidiService midiService;
    private IDriver driver;

    @Inject
    public GlobalPatchMethodParserImpl(
            CtrlrConverterDeviceFactory deviceFactory,
            @Named("className") String driverClass) {

        Device device = deviceFactory.getDevice();
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
    public MethodBuilder getPatchRequestBuilder() {
        MidiRecordSession midiRecordSession = midiService.openSession();
        driver.requestPatchDump(0, 0);
        String session = midiService.closeSession(midiRecordSession);

        String[] split = session.split(";");
        if (split.length > 1) {
            List<String> msgList = Arrays.asList(split);
            msgList.remove("");
            methodBuilder.addAll(msgList);
        }

        return methodBuilder;
    }

    @Override
    public MethodBuilder getPatchStoreBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MethodBuilder getPatchLoadBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MethodBuilder getPatchSaveBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

}
