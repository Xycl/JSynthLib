/*
 * Copyright 2014 Pascal Collberg
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package org.jsynthlib.device.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.table.AbstractTableModel;

import org.jsynthlib.midi.domain.MidiSettings;

/**
 * @author Pascal Collberg
 */
@Singleton
public class DeviceList extends AbstractTableModel implements List<Device> {

    private static final long serialVersionUID = 1L;

    public enum Columns {
        /** Synth ID */
        SYNTH_NAME("Synth ID"),
        /** Device */
        DEVICE("Device"),
        /** MIDI In Port */
        MIDI_IN("MIDI In Port"),
        /** MIDI Out Port */
        MIDI_OUT("MIDI Out Port"),
        /** Channel # */
        MIDI_CHANNEL("Channel #"),
        /** Device ID */
        MIDI_DEVICE_ID("Device ID");

        static Columns getColumnByOrdinal(int ordinal) {
            return Columns.values()[ordinal];
        }

        private final String name;

        private Columns(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private final List<Device> devices;
    private final MidiSettings midiSettings;

    @Inject
    public DeviceList(MidiSettings midiSettings) {
        devices = new ArrayList<Device>();
        this.midiSettings = midiSettings;
    }

    @Override
    public int getColumnCount() {
        return Columns.values().length;
    }

    @Override
    public String getColumnName(int col) {
        return Columns.getColumnByOrdinal(col).getName();
    }

    @Override
    public int getRowCount() {
        return devices.size();
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public Object getValueAt(int row, int col) {
        Device myDevice = devices.get(row);

        switch (Columns.getColumnByOrdinal(col)) {
        case SYNTH_NAME:
            return myDevice.getSynthName();
        case DEVICE:
            return (myDevice.getManufacturerName() + " " + myDevice
                    .getModelName());
        case MIDI_IN:
            if (midiSettings.isInputAvailable()) {
                int port = myDevice.getInPort();
                if (midiSettings.getMultiMIDI()) {
                    port = midiSettings.getInitPortIn();
                }

                return midiSettings.getInputName(port); // wirski@op.pl
            } else {
                return "not available";
            }
        case MIDI_OUT:
            if (midiSettings.isOutputAvailable()) {
                String port = myDevice.getOutPortName();
                if (midiSettings.getMultiMIDI()) {
                    port = midiSettings.getOutputName(midiSettings.getInitPortOut());
                }
                return port; // wirski@op.pl
            } else {
                return "not available";
            }
        case MIDI_CHANNEL:
            return new Integer(myDevice.getChannel());
        case MIDI_DEVICE_ID:
            return new Integer(myDevice.getDeviceID());
        default:
            return null;
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        // Note that the data/cell address is constant,
        // no matter where the cell appears onscreen.
        switch (Columns.getColumnByOrdinal(col)) {
        case SYNTH_NAME:
        case MIDI_CHANNEL:
        case MIDI_DEVICE_ID:
            return true;
        case MIDI_OUT:
        case MIDI_IN:
            return midiSettings.getMultiMIDI();
        default:
            return false;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        Device dev = devices.get(row);
        switch (Columns.getColumnByOrdinal(col)) {
        case SYNTH_NAME:
            dev.setSynthName((String) value);
            break;
        case MIDI_IN:
            dev.setInPort(midiSettings.getInPort((String) value)); // wirski@op.pl
            break;
        case MIDI_OUT:
            dev.setOutPortName((String) value); // wirski@op.pl
            break;
        case MIDI_CHANNEL:
            dev.setChannel(((Integer) value).intValue());
            break;
        case MIDI_DEVICE_ID:
            dev.setDeviceID(((Integer) value).intValue());
            break;
        default:
            break;
        }
        fireTableCellUpdated(row, col); // really required???
    }

    @Override
    public int size() {
        return devices.size();
    }

    @Override
    public boolean isEmpty() {
        return devices.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return devices.contains(o);
    }

    @Override
    public int indexOf(Object o) {
        return devices.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return devices.lastIndexOf(o);
    }

    @Override
    public Iterator<Device> iterator() {
        return devices.iterator();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return devices.containsAll(c);
    }

    @Override
    public ListIterator<Device> listIterator() {
        return devices.listIterator();
    }

    @Override
    public Object[] toArray() {
        return devices.toArray();
    }

    @Override
    public ListIterator<Device> listIterator(int index) {
        return devices.listIterator(index);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return devices.toArray(a);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean removeAll = devices.removeAll(c);
        if (removeAll) {
            fireTableDataChanged();
        }
        return removeAll;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean retainAll = devices.retainAll(c);
        if (retainAll) {
            fireTableDataChanged();
        }
        return retainAll;
    }

    @Override
    public Device get(int index) {
        return devices.get(index);
    }

    @Override
    public Device set(int index, Device element) {
        Device device = devices.set(index, element);
        fireTableDataChanged();
        return device;
    }

    @Override
    public boolean add(Device e) {
        boolean add = devices.add(e);
        if (add) {
            fireTableDataChanged();
        }
        return add;
    }

    @Override
    public void add(int index, Device element) {
        devices.add(index, element);
        fireTableDataChanged();
    }

    @Override
    public String toString() {
        return devices.toString();
    }

    @Override
    public List<Device> subList(int fromIndex, int toIndex) {
        return devices.subList(fromIndex, toIndex);
    }

    @Override
    public Device remove(int index) {
        Device device = devices.remove(index);
        fireTableDataChanged();
        return device;
    }

    @Override
    public boolean remove(Object o) {
        boolean remove = devices.remove(o);
        if (remove) {
            fireTableDataChanged();
        }
        return remove;
    }

    @Override
    public boolean equals(Object o) {
        return devices.equals(o);
    }

    @Override
    public void clear() {
        devices.clear();
    }

    @Override
    public boolean addAll(Collection<? extends Device> c) {
        boolean addAll = devices.addAll(c);
        fireTableDataChanged();
        return addAll;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Device> c) {
        boolean addAll = devices.addAll(index, c);
        fireTableDataChanged();
        return addAll;
    }

    @Override
    public int hashCode() {
        return devices.hashCode();
    }
}
