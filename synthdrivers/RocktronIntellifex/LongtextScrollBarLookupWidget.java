package synthdrivers.RocktronIntellifex;

import java.awt.Dimension;

import javax.swing.JTextField;

import core.IPatch;
import core.ScrollBarLookupWidget;

/**
 * Enhanced ScrollBarLookupWidget with right-aligned/size-able value.
 * @author  Klaus Sailer
 * @version $Id$
 */
public class LongtextScrollBarLookupWidget extends ScrollBarLookupWidget {

    public LongtextScrollBarLookupWidget(String label, IPatch patch, int min, int max, int labelWidth, int textWidth,
            IParamModel pmodel, ISender sender, String[] options) {
        super(label, patch, min, max, labelWidth, pmodel, sender, options);
        if (textWidth != 4)
        {
            text = new JTextField(options[getValue()-super.getValueMin()], textWidth);
            text.setEditable(false);
            layoutWidgets();
        }
        text.setHorizontalAlignment(JTextField.RIGHT);
    }
}
