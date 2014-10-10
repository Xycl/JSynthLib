package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.midi.viewcontroller.MidiMonitor;

public class MonitorAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;


    public MonitorAction(Map<Action, Integer> mnemonics) {
        super("MIDI Monitor", null);
        this.setEnabled(true);
        mnemonics.put(this, new Integer('M'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MidiMonitor midiMonitorView = JSynthLibInjector.getInstance(MidiMonitor.class);
    }
}
