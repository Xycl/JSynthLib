package org.jsynthlib.device.viewcontroller.widgets;

import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.Field;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.apache.log4j.Logger;
import org.fest.swing.exception.ComponentLookupException;
import org.jsynthlib.core.ContainerDisplayer;
import org.jsynthlib.core.TitleFinder.FrameWrapper;
import org.jsynthlib.core.valuesetter.IValueSetter;
import org.jsynthlib.test.adapter.WidgetAdapter;

public abstract class AbstractSwingWidgetAdapter extends WidgetAdapter {

    private SysexWidget sysexWidget;

    public AbstractSwingWidgetAdapter(SysexWidget sysexWidget) {
        setMin(sysexWidget.getValueMin());
        setEnabled(sysexWidget.isEnabled());
        setSysexWidget(sysexWidget);
    }

    @Override
    public boolean isShowing() {
        if (sysexWidget == null) {
            return false;
        } else {
            return sysexWidget.isShowing();
        }
    }


    public SysexWidget getSysexWidget() {
        return sysexWidget;
    }

    public void setSysexWidget(SysexWidget sysexWidget) {
        this.sysexWidget = sysexWidget;
    }

    @Override
    public String getUniqueName(FrameWrapper frame) {
        if (sysexWidget == null) {
            return null;
        } else {
            return getUniqueName(frame, sysexWidget);
        }
    }

    public static String getUniqueName(FrameWrapper frame, SysexWidget widget) {
        Logger log = Logger.getLogger(AbstractSwingWidgetAdapter.class);
        JComponent parent = (JComponent) widget.getParent();
        String containerName = null;
        if (parent == null) {
            try {
                log.info("Showing table");
                containerName =
                        ContainerDisplayer.showTableAndGetNameRecursive(frame,
                                widget);
            } catch (ComponentLookupException e) {
                log.warn("Widget is not visible!");
            }
        } else {
            log.info("Showing container");
            containerName =
                    ContainerDisplayer.showContainerAndGetNameRecursive(frame,
                            widget);
        }

        String label = widget.getLabel();
        if (label == null || label.isEmpty()) {
            if (widget instanceof EnvelopeWidget) {
                label = "Envelope";
            } else if (widget.getParent() != null) {
                label = findNearestLabelRecursive(widget.getParent(), 0);
            }
        }
        if (label == null || label.isEmpty()) {
            log.warn("Label is not valid!");
        }
        String uniqueName = containerName + label;
        int index = 1;
        while (UNIQUE_NAMES.contains(uniqueName)) {
            log.warn("Editor has duplicate widgets! " + uniqueName);
            if (index == 1) {
                uniqueName = uniqueName + "-id" + index;
            } else {
                uniqueName =
                        uniqueName.replace("-id" + (index - 1), "-id" + index);
            }
            index++;
        }
        UNIQUE_NAMES.add(uniqueName);
        return uniqueName;
    }

    static String findNearestLabelRecursive(Container parent, int index) {
        if (index >= 3) {
            Logger.getLogger(AbstractSwingWidgetAdapter.class).warn("Could not find label!");
            return null;
        }
        Component[] components = parent.getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                String text = ((JLabel) component).getText();
                if (text != null && !text.isEmpty()) {
                    return text;
                }
            }
        }
        return findNearestLabelRecursive(parent.getParent(), index + 1);
    }

    @SuppressWarnings("unchecked")
    protected <T> T getField(String fieldName, Class<T> klass, Object object)
            throws IllegalAccessException, NoSuchFieldException {
        Field f = object.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return (T) f.get(object);
    }

    @Override
    public IValueSetter getValueSetter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getMax() {
        return sysexWidget.getValueMax();
    }
    
    
}
