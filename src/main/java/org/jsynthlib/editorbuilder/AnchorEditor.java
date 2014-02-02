package org.jsynthlib.editorbuilder;

import java.awt.Component;
import java.beans.PropertyEditorSupport;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

public class AnchorEditor extends PropertyEditorSupport implements
        ChangeListener {
    private final transient Logger log = Logger.getLogger(getClass());

    private Anchor editorValue;

    public Component getCustomEditor() {
        OldAnchorEditor ae = new OldAnchorEditor(editorValue);
        ae.addChangeListener(this);
        return ae;
    }

    public boolean supportsCustomEditor() {
        return true;
    }

    public void setValue(Object value) {
        try {
            editorValue = (Anchor) ((Anchor) value).clone();
        } catch (CloneNotSupportedException e) {
            log.warn(e.getMessage(), e);
        }
        super.setValue(value);
    }

    /*
     * (non-Javadoc)
     * @see
     * javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent
     * )
     */
    public void stateChanged(ChangeEvent e) {
        try {
            super.setValue(((Anchor) e.getSource()).clone());
        } catch (CloneNotSupportedException e1) {
            log.warn(e1.getMessage(), e1);
        }
    }
}
