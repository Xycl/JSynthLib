package org.jsynthlib.synthdrivers.RolandSPD11;

import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jsynthlib.device.model.handler.ParamModel;
import org.jsynthlib.device.model.handler.SysexSender;
import org.jsynthlib.device.viewcontroller.PatchEditorFrame;
import org.jsynthlib.device.viewcontroller.widgets.ScrollBarWidget;
import org.jsynthlib.patch.model.impl.Patch;

class RolandSPD11PatchEditor extends PatchEditorFrame {
    public RolandSPD11PatchEditor(Patch patch) {
        super("Roland SPD11 Patch Editor", patch);
        JTabbedPane patchPane = new JTabbedPane();
        JPanel ptchstPane = new JPanel();
        ptchstPane.setLayout(new GridBagLayout());
        patchPane.addTab("PatchSettings", ptchstPane);
        addWidget(ptchstPane, new ScrollBarWidget(" Fx Time", // the label
                patch, // always "patch"
                0, 31, // min & max values for fader
                0, // offset value
                new ParamModel(patch, 100), new VcedSender(21)), 1, 1, // horizontal/vertical
                                                                       // location
                3, 1, // horizontal/vertical size
                1); // unique fader number
        // addWidget...
        JTabbedPane padPane = new JTabbedPane();
        patchPane.addTab("PadSettings", padPane);
        JPanel pds1to8Pane = new JPanel();
        padPane.addTab("1>8", pds1to8Pane);

    }
}

// TODO: remove this !!!
class VcedSender extends SysexSender {
    public VcedSender(int param) {

    }
}

class AcedSender extends SysexSender {
    public AcedSender(int param) {

    }
}