package org.jsynthlib.device.model.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlString;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.utils.SysexUtils;
import org.jsynthlib.xmldevice.Property;
import org.jsynthlib.xmldevice.StringArray;
import org.jsynthlib.xmldevice.XmlDriverDefinition;
import org.jsynthlib.xmldevice.XmlDriverDefinition.CustomProperties;

public final class DriverBeanUtil {

    private static final Logger LOG = Logger.getLogger(DriverBeanUtil.class);

    private static final DriverBeanUtil INSTANCE = new DriverBeanUtil();
    private BeanUtilsBean beanUtilsBean;

    private DriverBeanUtil() {
        beanUtilsBean = BeanUtilsBean.getInstance();
        ConvertUtilsBean convertUtils = beanUtilsBean.getConvertUtils();
        Class<?> xmlStringArrayClass = StringArray.class;
        Class<? extends String[]> stringArrayClass = new String[0].getClass();
        Class<? extends byte[]> byteArrayClass = new byte[0].getClass();
        convertUtils.register(new Converter() {
            @Override
            public Object convert(Class arg0, Object arg1) {
                if (arg1 instanceof String[]) {
                    String[] array = (String[]) arg1;
                    StringArray stringArray = StringArray.Factory.newInstance();
                    for (String string : array) {
                        XmlString xmlString = stringArray.addNewString();
                        xmlString.setStringValue(string);
                    }
                    return stringArray;
                } else {
                    return null;
                }
            }

        }, xmlStringArrayClass);
        final Converter defaultConverter =
                convertUtils.lookup(stringArrayClass);
        convertUtils.register(new Converter() {
            @Override
            public Object convert(Class arg0, Object arg1) {
                if (arg1 instanceof StringArray) {
                    StringArray array = (StringArray) arg1;
                    String[] stringArray = array.getStringArray();
                    String[] retVal = new String[stringArray.length];
                    for (int i = 0; i < stringArray.length; i++) {
                        retVal[i] = stringArray[i];
                    }
                    return retVal;
                } else {
                    return defaultConverter.convert(arg0, arg1);
                }
            }

        }, stringArrayClass);

        final Converter defaultByteConverter =
                convertUtils.lookup(byteArrayClass);
        convertUtils.register(new Converter() {
            @Override
            public Object convert(Class arg0, Object arg1) {
                if (arg1 instanceof String) {
                    String array = (String) arg1;
                    return SysexUtils.stringToSysex(array);
                } else {
                    return defaultByteConverter.convert(arg0, arg1);
                }
            }

        }, byteArrayClass);
    }

    public static void copyXmlProperties(IDriver dest, XmlDriverDefinition orig)
            throws IllegalAccessException, InvocationTargetException {
        INSTANCE.beanUtilsBean.copyProperties(dest, orig);
        CustomProperties customProperties = orig.getCustomProperties();
        if (customProperties != null) {
            copyCustomProperties(dest, customProperties);
        }
    }

    public static void copyCustomProperties(IDriver dest,
            CustomProperties customProperties) {
        Map<String, String> map = new HashMap<String, String>();
        Property[] customPropertyArray =
                customProperties.getCustomPropertyArray();
        for (Property property : customPropertyArray) {
            map.put(property.getName(), property.getValue());
        }
        try {
            INSTANCE.beanUtilsBean.populate(dest, map);
        } catch (IllegalAccessException e) {
            LOG.warn(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    public static void copyPreferences(IDriver driver) {
        Device device = driver.getDevice();
        Preferences preferences = device.getPreferences();
        try {
            String[] keys = preferences.keys();
            for (String string : keys) {
                String value = preferences.get(string, null);
                if (value != null) {
                    INSTANCE.beanUtilsBean.copyProperty(driver, string, value);
                }
            }
        } catch (BackingStoreException e) {
            LOG.warn(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            LOG.warn(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }
    }
}
