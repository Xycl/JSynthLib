package org.jsynthlib.device.viewcontroller.widgets;

/**
 * A Label widget.
 * @version $Id: LabelWidget.java 951 2005-03-06 05:05:32Z hayashi $
 * @see SysexWidget
 */
public class LabelWidget extends SysexWidget {
    /**
     * Creates a new <code>LabelWidget</code> instance.
     * @param label
     *            a label text.
     */
    public LabelWidget(String label) {
        super(label, null, null, null);

        layoutWidgets();
        createWidgets();
    }

    protected void createWidgets() {
        // do nothing
    }

    protected void layoutWidgets() {
        // setLayout(new BorderLayout());
        add(getJLabel());
        // add(new JLabel(label), BorderLayout.CENTER);
    }

    public void setEnabled(boolean e) {
        getJLabel().setEnabled(e);
    }
}
