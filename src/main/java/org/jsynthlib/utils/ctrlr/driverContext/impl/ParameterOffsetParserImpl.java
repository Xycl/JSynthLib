package org.jsynthlib.utils.ctrlr.driverContext.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.jsynthlib.device.model.DeviceException;
import org.jsynthlib.device.model.handler.IParamModel;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.utils.ctrlr.driverContext.ConverterDeviceFactory;
import org.jsynthlib.utils.ctrlr.driverContext.ParameterOffsetParser;
import org.jsynthlib.xmldevice.ParamModelReference;
import org.jsynthlib.xmldevice.XmlDriverDefinition;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ParameterOffsetParserImpl implements ParameterOffsetParser {

    private final int patchSize;
    private final ConverterDeviceFactory factory;

    @Inject
    public ParameterOffsetParserImpl(ConverterDeviceFactory factory,
            XmlDriverDefinition driverDef) throws DeviceException {
        this.factory = factory;
        patchSize = driverDef.getPatchSize();
    }

    @Override
    public int[] parseParameterOffset(ParamModelReference ref) {
        Patch patch = new Patch();
        byte[] sysex = new byte[patchSize];
        patch.sysex = sysex;
        IParamModel paramModel = factory.newParamModel(ref, patch);

        HashMap<Integer, List<Integer>> valueToOffsetMap =
                new HashMap<Integer, List<Integer>>();

        // Run through the whole patch with only one byte set to 1 to see when
        // the model responds with a value.
        for (int i = 0; i < patchSize; i++) {
            Arrays.fill(sysex, (byte) 0);
            sysex[i] = (byte) 1;
            patch.sysex = sysex;
            int value = paramModel.get();
            List<Integer> list = valueToOffsetMap.get(value);
            if (list == null) {
                list = new ArrayList<Integer>();
                valueToOffsetMap.put(value, list);
            }
            list.add(i);
        }

        if (valueToOffsetMap.size() < 2) {
            throw new IllegalArgumentException(
                    "Could not find offset that triggers the model");
        } else {
            ArrayList<Integer> offsets = new ArrayList<Integer>();
            Iterator<Entry<Integer, List<Integer>>> iterator =
                    valueToOffsetMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<Integer, List<Integer>> entry = iterator.next();
                if (entry.getValue().size() <= 2) {
                    offsets.addAll(entry.getValue());
                }
            }
            Collections.sort(offsets);
            return toIntArray(offsets);
        }

    }

    int[] toIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }
}
