package org.jsynthlib.synthdrivers.YamahaFS1R;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.jsynthlib.device.model.handler.ParamModel;
import org.jsynthlib.device.model.handler.SysexSender;
import org.jsynthlib.device.viewcontroller.widgets.ComboBoxWidget;
import org.jsynthlib.patch.model.impl.Patch;

public class ComboActionWidget extends ComboBoxWidget {
    protected ComboActionListener mListener;

    public ComboActionWidget(String l, Patch p, ParamModel ofs, SysexSender s,
            String[] o, ComboActionListener aListener) {
        super(l, p, ofs, s, o);
        mListener = aListener;
        cb.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                mListener.notifyChange(cb.getSelectedIndex());
            }
        });
    }

}
