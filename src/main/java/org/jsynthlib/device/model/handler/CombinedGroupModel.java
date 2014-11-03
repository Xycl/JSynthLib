package org.jsynthlib.device.model.handler;

import java.util.ArrayList;
import java.util.BitSet;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.jsynthlib.patch.model.impl.Patch;

public class CombinedGroupModel extends ParamModel {

    private final BitSet bits;
    private final ArrayList<BooleanProperty> propertyList;

    public CombinedGroupModel() {
        bits = new BitSet();
        propertyList = new ArrayList<BooleanProperty>(32);
    }

    @Override
    public void set(int value) {
        setBits(value);
        super.set(value);
    }

    int getBitsValue() {
        int value = 0;
        for (int i = 0; i < bits.length(); ++i) {
            value += bits.get(i) ? (1L << i) : 0L;
        }
        return value;
    }

    void setBits(int value) {
        int index = 0;
        while (value != 0) {
            setBitValue(index, value % 2 != 0);
            ++index;
            value = value >>> 1;
        }
    }

    void setBitValue(int index, boolean value) {
        bits.set(index, value);
        getBooleanProperty(index).set(value);
    }

    public void setBit(int bit, boolean set) {
        setBitValue(bit, set);
        super.set(getBitsValue());
    }

    @Override
    public void setPatch(Patch patch) {
        super.setPatch(patch);
        set(patch.sysex[offset]);
    }


    public BooleanProperty getBooleanProperty(int index) {
        BooleanProperty booleanProperty = null;
        if (propertyList.size() > index) {
            booleanProperty = propertyList.get(index);
        }

        if (booleanProperty == null) {
            booleanProperty = new SimpleBooleanProperty(bits.get(index));
            propertyList.add(index, booleanProperty);
        }
        return booleanProperty;
    }
}
