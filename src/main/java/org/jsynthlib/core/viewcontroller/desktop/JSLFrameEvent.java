package org.jsynthlib.core.viewcontroller.desktop;

import java.awt.AWTEvent;

public class JSLFrameEvent extends AWTEvent {

    private static final long serialVersionUID = 1L;

    public static final int ACTIVATED = AWTEvent.RESERVED_ID_MAX;
    public static final int CLOSED = ACTIVATED + 1;
    public static final int CLOSING = CLOSED + 1;
    public static final int DEACTIVATED = CLOSING + 1;
    public static final int DEICONIFIED = DEACTIVATED + 1;
    public static final int ICONIFIED = DEICONIFIED + 1;
    public static final int OPENED = ICONIFIED + 1;

    private final JSLFrame source;

    public JSLFrameEvent(JSLFrame source, int id) {
        super(source, id);
        this.source = source;
    }

    @Override
    public void consume() {
        super.consume();
    }

    @Override
    public boolean isConsumed() {
        return super.isConsumed();
    }

    public JSLFrame getJSLFrame() {
        return source;
    }

}
