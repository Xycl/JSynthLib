package org.jsynthlib.editorbuilder.widgets;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import org.apache.log4j.Logger;

public class WidgetBeanInfo extends SimpleBeanInfo {

    private static final Logger LOG = Logger.getLogger(WidgetBeanInfo.class);

    private static final PropertyDescriptor[] props;
    static {
        PropertyDescriptor[] p = null;
        try {
            p = new PropertyDescriptor[] {
                new PropertyDescriptor("id", Widget.class),
            // new PropertyDescriptor("type", Widget.class),
                    };
        } catch (IntrospectionException e) {
            p = new PropertyDescriptor[0];
            LOG.info("Debug Me!", e);
        }
        props = p;
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return props;
    }
}
