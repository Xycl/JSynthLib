package org.jsynthlib.actions;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import org.apache.log4j.Logger;
import org.jsynthlib.core.viewcontroller.desktop.JSLDesktop;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;

public abstract class JSLAbstractAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    protected final transient Logger log = Logger.getLogger(getClass());

    public JSLAbstractAction() {
        super();
    }

    public JSLAbstractAction(String name, Icon icon) {
        super(name, icon);
    }

    public JSLAbstractAction(String name) {
        super(name);
    }

    protected final JSLFrame getSelectedFrame() {
        return JSLDesktop.Factory.getDesktop().getSelectedFrame();
    }
}
