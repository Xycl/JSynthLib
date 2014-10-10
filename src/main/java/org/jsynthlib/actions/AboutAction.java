package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JOptionPane;

import org.jsynthlib.core.Constants;

public class AboutAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public AboutAction(Map<Action, Integer> mnemonics) {
        super("About");
        mnemonics.put(this, new Integer('A'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(null, "JSynthLib Version "
                + Constants.VERSION
                + "\nCopyright (C) 2000-04 Brian Klock et al.\n"
                + "See 'Help -> License' for more info.", "About JSynthLib",
                JOptionPane.INFORMATION_MESSAGE);
    }

}
