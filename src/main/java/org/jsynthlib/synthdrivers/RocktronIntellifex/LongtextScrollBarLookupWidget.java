package org.jsynthlib.synthdrivers.RocktronIntellifex;

import javax.swing.JTextField;

import org.jsynthlib.device.model.handler.IParamModel;
import org.jsynthlib.device.model.handler.ISender;
import org.jsynthlib.device.viewcontroller.widgets.ScrollBarLookupWidget;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * Enhanced ScrollBarLookupWidget with right-aligned/size-able value.
 * @author Klaus Sailer
 * @version $Id$
 */
public class LongtextScrollBarLookupWidget extends ScrollBarLookupWidget {

    public LongtextScrollBarLookupWidget(String label, Patch patch, int min,
            int max, int labelWidth, int textWidth, IParamModel pmodel,
            ISender sender, String[] options) {
        super(label, patch, min, max, labelWidth, pmodel, sender, options);
        if (textWidth != 4) {
            text =
                    new JTextField(options[getValue() - super.getValueMin()],
                            textWidth);
            text.setEditable(false);
            layoutWidgets();
        }
        text.setHorizontalAlignment(JTextField.RIGHT);
    }
}
