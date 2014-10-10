package org.jsynthlib.device.model;

import java.util.Arrays;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

public final class PreferenceUtil {

    private static final Logger LOG = Logger.getLogger(PreferenceUtil.class);

    private PreferenceUtil() {
    }

    public static boolean containsPreference(Preferences preferences,
            String name) {
        try {
            String[] keys = preferences.keys();
            List<String> list = Arrays.asList(keys);
            return list.contains(name);
        } catch (BackingStoreException e) {
            LOG.warn(e.getMessage(), e);
            return false;
        }
    }

}
