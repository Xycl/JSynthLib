package org.jsynthlib.synthdrivers.RocktronIntellifex;

import javax.swing.JTextField;

import org.jsynthlib.device.model.handler.IParamModel;
import org.jsynthlib.device.model.handler.ISender;
import org.jsynthlib.device.viewcontroller.widgets.ScrollBarWidget;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * Enhanced ScrollBarWidget with right-aligned/size-able value.
 * @author Klaus Sailer
 * @version $Id$
 */
public class LongtextScrollBarWidget extends ScrollBarWidget {

    public LongtextScrollBarWidget(String label, Patch patch, int min,
            int max, int base, int labelWidth, int textWidth,
            IParamModel pmodel, ISender sender) {
        super(label, patch, min, max, base, labelWidth, pmodel, sender);
        if (textWidth != 4) {
            text =
                    new JTextField(new Integer(getValue() + base).toString(),
                            textWidth);
            text.setEditable(false);
            layoutWidgets();
        }
        text.setHorizontalAlignment(JTextField.RIGHT);
    }
}