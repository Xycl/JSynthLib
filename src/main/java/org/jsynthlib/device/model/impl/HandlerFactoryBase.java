package org.jsynthlib.device.model.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.log4j.Logger;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.xmldevice.HandlerDefinitionBase;
import org.jsynthlib.xmldevice.HandlerDefinitionBase.Property;
import org.jsynthlib.xmldevice.HandlerReferenceBase;
import org.jsynthlib.xmldevice.HandlerReferenceBase.PropertyValue;

public abstract class HandlerFactoryBase {

    private final transient Logger log = Logger.getLogger(getClass());
    private final Map<String, HandlerDefinitionBase> bindingMap;

    public HandlerFactoryBase(Map<String, HandlerDefinitionBase> bindingMap) {
        this.bindingMap = bindingMap;
    }

    protected HandlerDefinitionBase getHandlerDefinition(String name) {
        return bindingMap.get(name);
    }

    protected Properties getHandlerProperties(HandlerReferenceBase ref) {
        HandlerDefinitionBase def = getHandlerDefinition(ref.getName());
        Set<String> definedProperties = getDefinitionProperties(def);
        Properties properties = new Properties();
        PropertyValue[] propertyArray = ref.getPropertyValueArray();
        for (PropertyValue property : propertyArray) {
            if (definedProperties.contains(property.getKey())) {
                properties.setProperty(property.getKey(), property.getValue());
            } else {
                log.warn("Found undefined property: " + property.getKey());
            }
        }
        return properties;
    }

    Set<String> getDefinitionProperties(HandlerDefinitionBase def) {
        HashSet<String> set = new HashSet<String>();
        Property[] propertyArray = def.getPropertyArray();
        for (Property property : propertyArray) {
            set.add(property.getKey());
        }
        return set;
    }

    void copyProperties(HandlerReferenceBase ref, Object bean, Patch patch)
            throws IllegalAccessException, InvocationTargetException {
        copyProperties(ref, bean);
        BeanUtils.setProperty(bean, "patch", patch);
    }

    void copyProperties(HandlerReferenceBase ref, Object bean)
            throws IllegalAccessException, InvocationTargetException {
        Properties properties = getHandlerProperties(ref);
        BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
        beanUtilsBean.copyProperties(bean, properties);
    }
}
