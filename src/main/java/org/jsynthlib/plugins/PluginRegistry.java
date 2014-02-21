package org.jsynthlib.plugins;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import org.codehaus.groovy.control.CompilationFailedException;
import org.jsynthlib.jsynthlib.Dummy;
import org.jsynthlib.utils.ResourceURLFilter;
import org.jsynthlib.utils.Resources;

import core.ErrorMsg;

/**
 * @author ribrdb
 */
public class PluginRegistry {
    private static boolean plugins_loaded = false;
    private static HashMap checksums = new HashMap();
    private static HashMap decoders = new HashMap();
    private static GroovyShell shell = new GroovyShell();
    private static GroovyClassLoader loader = new GroovyClassLoader();

    private PluginRegistry() {
    }

    public static void registerChecksum(String name, Class plugin) {
        checksums.put(name, new PluginEntry(plugin));
    }

    public static void registerDecoder(String name, Class plugin) {
        decoders.put(name, new PluginEntry(plugin));
    }

    public static void registerChecksum(String name, Constructor c,
            Object[] args) {
        checksums.put(name, new PluginEntry(c, args));
    }

    public static void registerDecoder(String name, Constructor c, Object[] args) {
        decoders.put(name, new PluginEntry(c, args));
    }

    public static void loadPlugins() throws CompilationFailedException,
            IOException, URISyntaxException {
        Set<URL> xmlfiles =
                Resources.getResourceURLs(Dummy.class, new ResourceURLFilter() {

                    @Override
                    public boolean accept(URL resourceUrl) {
                        String path = resourceUrl.getPath();
                        return path.contains("org/jsynthlib/plugins")
                                && path.endsWith(".groovy");
                    }
                });

        for (URL url : xmlfiles) {
            InputStreamReader reader = new InputStreamReader(url.openStream());
            try {
                shell.evaluate(reader);
            } catch (Exception ex) {
                ErrorMsg.reportError("Error loading plugin",
                        "Error loading plugin " + url.getPath() + "\n"
                                + ex.getMessage(), ex);
            }
        }
        plugins_loaded = true;
    }

    public static Decoder getDecoder(String name)
            throws InstantiationException, IllegalAccessException,
            InvocationTargetException, CompilationFailedException, IOException, URISyntaxException {
        // XXX: lazily load for now. may want to change this later
        if (!plugins_loaded)
            loadPlugins();
        Decoder d = null;
        PluginEntry e = (PluginEntry) decoders.get(name);
        if (e != null)
            d = (Decoder) e.create();
        return d;
    }

    public static Checksum getChecksum(String name)
            throws InstantiationException, IllegalAccessException,
            InvocationTargetException, CompilationFailedException, IOException, URISyntaxException {
        if (!plugins_loaded)
            loadPlugins();
        Checksum c = null;
        PluginEntry e = (PluginEntry) checksums.get(name);
        if (e != null)
            c = (Checksum) e.create();
        return c;
    }

    public static GroovyShell groovyShell() {
        return shell;
    }

    public static GroovyClassLoader groovyLoader() {
        return loader;
    }

    private static class PluginEntry {
        private Class theclass;
        private Constructor constructor;
        private Object[] args;

        private PluginEntry(Class plugin) {
            theclass = plugin;
            constructor = null;
            args = null;
        }

        private PluginEntry(Constructor c, Object[] args) {
            theclass = null;
            constructor = c;
            this.args = args;
        }

        public Object create() throws InstantiationException,
                IllegalAccessException, InvocationTargetException {
            if (theclass == null) {
                return constructor.newInstance(args);
            } else {
                return theclass.newInstance();
            }
        }
    }

    private static class PluginFilter implements FileFilter {

        public boolean accept(File pathname) {
            if (pathname.isDirectory())
                return false;
            String name = pathname.getName();
            if (name.endsWith(".groovy"))
                return true;
            return false;
        }

    }

}
