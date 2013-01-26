package synthdrivers.RocktronIntellifex;

import javax.swing.JTextField;

import core.IPatch;
import core.ScrollBarWidget;
import core.SysexWidget.IParamModel;
import core.SysexWidget.ISender;

/**
 * Enhanced ScrollBarWidget with right-aligned/size-able value.
 * @author  Klaus Sailer
 * @version $Id$
 */
public class LongtextScrollBarWidget extends ScrollBarWidget {

    public LongtextScrollBarWidget(String label, IPatch patch, int min, int max, int base, int labelWidth, int textWidth,
            IParamModel pmodel, ISender sender) {
        super(label, patch, min, max, base, labelWidth, pmodel, sender);
        if (textWidth != 4)
        {
            text = new JTextField(new Integer(getValue() + base).toString(), textWidth);
            text.setEditable(false);
            layoutWidgets();
        }
        text.setHorizontalAlignment(JTextField.RIGHT);
    }
}