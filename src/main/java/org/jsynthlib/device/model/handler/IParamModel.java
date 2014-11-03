package org.jsynthlib.device.model.handler;


/**
 * Interface for Paramer Model. Parameter Model keeps track of the changes
 * to the patch so that when we next call up this patch the changes are
 * there. This is also used to set the widgets to the correct values for a
 * particular patch when the Single Editor is opened.
 * @see ParamModel
 */
public interface IParamModel {
    /** Set a parameter value <code>value</code>. */
    void set(int value);

    /** Get a parameter value. */
    int get();
}
