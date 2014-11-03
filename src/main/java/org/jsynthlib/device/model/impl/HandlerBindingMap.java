package org.jsynthlib.device.model.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.jsynthlib.device.model.DeviceException;
import org.jsynthlib.device.model.handler.CombinedGroupModel;
import org.jsynthlib.device.model.handler.DefaultChecksumCalculator;
import org.jsynthlib.device.model.handler.IParamModel;
import org.jsynthlib.device.model.handler.IPatchStringSender;
import org.jsynthlib.device.model.handler.ISender;
import org.jsynthlib.device.model.handler.ParamModel;
import org.jsynthlib.device.model.handler.PatchNameSender;
import org.jsynthlib.device.model.handler.SysexSender;
import org.jsynthlib.midi.service.ChecksumCalculator;
import org.jsynthlib.xmldevice.ChecksumCalculatorDefinition;
import org.jsynthlib.xmldevice.DeviceConfiguration;
import org.jsynthlib.xmldevice.DeviceConfiguration.ChecksumCalculatorDefinitions;
import org.jsynthlib.xmldevice.DeviceConfiguration.MidiSenderDefinitions;
import org.jsynthlib.xmldevice.DeviceConfiguration.ParamModelDefinitions;
import org.jsynthlib.xmldevice.DeviceConfiguration.StringModelDefinitions;
import org.jsynthlib.xmldevice.DeviceConfiguration.StringSenderDefinitions;
import org.jsynthlib.xmldevice.HandlerDefinitionBase;
import org.jsynthlib.xmldevice.HandlerDefinitionBase.Property;
import org.jsynthlib.xmldevice.MidiSenderDefinition;
import org.jsynthlib.xmldevice.ParamModelDefinition;
import org.jsynthlib.xmldevice.StringModelDefinition;
import org.jsynthlib.xmldevice.StringSenderDefinition;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;
import org.jsynthlib.xmldevice.XmlDeviceFamilyDefinitionDocument;
import org.jsynthlib.xmldevice.XmlDeviceFamilyDefinitionDocument.XmlDeviceFamilyDefinition;

public class HandlerBindingMap {

    private final transient Logger log = Logger.getLogger(getClass());

    private static final char PATH_SEPARATOR = '.';

    private final Map<String, HandlerDefinitionBase> map;
    private final DeviceTree deviceTree;

    public HandlerBindingMap() {
        map = new HashMap<String, HandlerDefinitionBase>();
        deviceTree = new DeviceTree();

        Map<String, Class<?>> defaultHandlerMap =
                new HashMap<String, Class<?>>();
        defaultHandlerMap.put("defaultParamModel", ParamModel.class);
        defaultHandlerMap.put("defaultNameSender", PatchNameSender.class);
        defaultHandlerMap.put("sysexSender", SysexSender.class);
        defaultHandlerMap.put("defaultCsCalculator",
                DefaultChecksumCalculator.class);
        defaultHandlerMap.put("combinedGroupModel", CombinedGroupModel.class);
        try {
            addDefaultHandlers(defaultHandlerMap);
        } catch (InstantiationException | IllegalAccessException
                | InvocationTargetException | NoSuchMethodException e) {
            log.warn("Failed to add default handlers", e);
        }
    }

    final void addDefaultHandlers(Map<String, Class<?>> defaultHandlerMap)
            throws InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        Iterator<Entry<String, Class<?>>> iterator =
                defaultHandlerMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, Class<?>> entry = iterator.next();
            Class<?> handlerClass = entry.getValue();
            HandlerDefinitionBase handler = null;
            if (IParamModel.class.isAssignableFrom(handlerClass)) {
                handler = ParamModelDefinition.Factory.newInstance();
            } else if (IPatchStringSender.class.isAssignableFrom(handlerClass)) {
                handler = StringSenderDefinition.Factory.newInstance();
            } else if (ISender.class.isAssignableFrom(handlerClass)) {
                handler = MidiSenderDefinition.Factory.newInstance();
            } else if (ChecksumCalculator.class.isAssignableFrom(handlerClass)) {
                handler = ChecksumCalculatorDefinition.Factory.newInstance();
            } else if (StringModelDefinition.class
                    .isAssignableFrom(handlerClass)) {
                handler = StringModelDefinition.Factory.newInstance();
            } else {
                throw new IllegalArgumentException("Illegal handler class: "
                        + handlerClass.getName());
            }
            handler.setName(entry.getKey());
            handler.setHandlerClass(handlerClass.getName());
            Object instance = handlerClass.newInstance();
            Map<?, ?> beanProperties =
                    BeanUtilsBean.getInstance().describe(instance);
            Iterator<?> propertyIterator = beanProperties.keySet().iterator();
            while (propertyIterator.hasNext()) {
                String propertyKey = (String) propertyIterator.next();
                Property property = handler.addNewProperty();
                property.setKey(propertyKey);
            }
            map.put(entry.getKey(), handler);
        }
    }

    public String addDevice(XmlDeviceDefinition definition)
            throws DeviceException {
        try {
            String familyName = null;
            String manufacturer = definition.getManufacturer();
            if (manufacturer == null) {
                familyName = definition.getFamily();
            } else {
                familyName = manufacturer;
            }

            DeviceNode parent = deviceTree.root;
            String[] split = familyName.split("\\" + PATH_SEPARATOR);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < split.length; i++) {
                String nodeName = split[i];
                if (i > 0) {
                    sb.append('.');
                }

                sb.append(nodeName);
                DeviceNode familyNode = getNodeByPath(sb.toString());
                if (familyNode == null) {
                    familyNode = new DeviceNode();
                    XmlDeviceFamilyDefinition familyDefinition =
                            getDeviceFamilyDefinition(sb.toString());
                    if (familyDefinition == null) {
                        familyNode.name = nodeName;
                    } else {
                        familyNode.name = familyDefinition.getName();
                        familyNode.hashCode = familyDefinition.hashCode();
                        addNodeBindings(sb.toString(),
                                familyDefinition.getConfiguration());
                    }
                    familyNode.parent = parent;
                    parent.children.add(familyNode);
                }
                parent = familyNode;
            }

            DeviceNode deviceNode = new DeviceNode();
            deviceNode.name = definition.getModelName();
            deviceNode.hashCode = definition.hashCode();
            deviceNode.parent = parent;
            parent.children.add(deviceNode);
            addNodeBindings(deviceNode.getPath(), definition.getConfiguration());
            return deviceNode.getPath();
        } catch (XmlException | IOException e) {
            throw new DeviceException(e);
        }
    }

    public void removeDevice(String devicePath) throws DeviceException {
        DeviceNode currNode = getNodeByPath(devicePath);
        if (currNode == null) {
            throw new DeviceException(
                    "Device path to be uninstalled is not valid " + devicePath);
        }
        DeviceNode parent = currNode.parent;
        parent.children.remove(currNode);
        removeNodeBindings(currNode.getPath());
        while (parent.children.isEmpty()) {
            currNode = parent;
            parent = currNode.parent;
            parent.children.remove(currNode);
            removeNodeBindings(currNode.getPath());
        }
    }

    public Map<String, HandlerDefinitionBase> getDeviceBindings(
            String devicePath) {
        DeviceNode deviceNode = getNodeByPath(devicePath);
        if (deviceNode == null) {
            throw new IllegalArgumentException("Invalid device path: "
                    + devicePath);
        }

        HashMap<String, HandlerDefinitionBase> result =
                new HashMap<String, HandlerDefinitionBase>();
        DeviceNode currNode = deviceNode;

        do {
            Map<String, HandlerDefinitionBase> nodeHandlers =
                    getNodeHandlers(currNode);
            // Don't override handlers defined in children
            nodeHandlers.keySet().removeAll(result.keySet());
            result.putAll(nodeHandlers);
            currNode = currNode.parent;
        } while (currNode != null);
        return result;
    }

    Map<String, HandlerDefinitionBase> getNodeHandlers(DeviceNode node) {
        HashMap<String, HandlerDefinitionBase> result =
                new HashMap<String, HandlerDefinitionBase>();
        String nodePath = node.getPath();
        Iterator<Entry<String, HandlerDefinitionBase>> iterator =
                map.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, HandlerDefinitionBase> entry = iterator.next();
            int lastSeparator = entry.getKey().lastIndexOf(PATH_SEPARATOR);
            String handlerNodePath = "";
            if (lastSeparator >= 0) {
                handlerNodePath = entry.getKey().substring(0, lastSeparator);
            }

            if (handlerNodePath.equals(nodePath)) {
                result.put(entry.getKey().substring(lastSeparator + 1),
                        entry.getValue());
            }
        }
        return result;
    }

    void addNodeBindings(String path, DeviceConfiguration config) {
        // Checksums
        if (config.isSetChecksumCalculatorDefinitions()) {
            ChecksumCalculatorDefinitions defs =
                    config.getChecksumCalculatorDefinitions();
            ChecksumCalculatorDefinition[] array =
                    defs.getChecksumCalculatorDefinitionArray();
            for (ChecksumCalculatorDefinition def : array) {
                map.put(getHandlerPath(path, def.getName()), def);
            }
        }

        // Midi senders
        if (config.isSetMidiSenderDefinitions()) {
            MidiSenderDefinitions defs = config.getMidiSenderDefinitions();
            MidiSenderDefinition[] array = defs.getMidiSenderDefinitionArray();
            for (MidiSenderDefinition def : array) {
                map.put(getHandlerPath(path, def.getName()), def);
            }
        }

        // Param models
        if (config.isSetParamModelDefinitions()) {
            ParamModelDefinitions defs = config.getParamModelDefinitions();
            ParamModelDefinition[] array = defs.getParamModelDefinitionArray();
            for (ParamModelDefinition def : array) {
                map.put(getHandlerPath(path, def.getName()), def);
            }
        }

        // String models
        if (config.isSetStringModelDefinitions()) {
            StringModelDefinitions defs = config.getStringModelDefinitions();
            StringModelDefinition[] array =
                    defs.getStringModelDefinitionArray();
            for (StringModelDefinition def : array) {
                map.put(getHandlerPath(path, def.getName()), def);
            }
        }

        // String senders
        if (config.isSetStringSenderDefinitions()) {
            StringSenderDefinitions defs = config.getStringSenderDefinitions();
            StringSenderDefinition[] array =
                    defs.getStringSenderDefinitionArray();
            for (StringSenderDefinition def : array) {
                map.put(getHandlerPath(path, def.getName()), def);
            }
        }
    }

    String getHandlerPath(String rootPath, String handlerName) {
        return new StringBuilder().append(rootPath).append(PATH_SEPARATOR)
                .append(handlerName).toString();
    }

    void removeNodeBindings(String path) {
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String keyPath = key.substring(0, key.lastIndexOf('.'));
            if (keyPath.equals(path)) {
                iterator.remove();
            }
        }
    }

    DeviceNode getNodeByPath(String path) {
        String[] split = path.split("\\" + PATH_SEPARATOR);
        DeviceNode currNode = deviceTree.root;
        for (String nodeName : split) {
            DeviceNode child = currNode.getChild(nodeName);
            if (child == null) {
                return null;
            } else {
                currNode = child;
            }
        }
        return currNode;
    }

    XmlDeviceFamilyDefinition getDeviceFamilyDefinition(String path)
            throws XmlException, IOException {
        String entityName = path;
        if (path.contains(".")) {
            entityName = path.substring(path.lastIndexOf(PATH_SEPARATOR) + 1);
        }
        entityName =
                entityName.substring(0, 1).toUpperCase()
                + entityName.substring(1);
        String entityPath =
                new StringBuilder().append("org/jsynthlib/synthdrivers")
                .append('/').append(path.replace(PATH_SEPARATOR, '/'))
                .append('/').append(entityName).append(".xml")
                .toString();
        InputStream stream =
                getClass().getClassLoader().getResourceAsStream(entityPath);
        if (stream == null) {
            return null;
        } else {
            XmlDeviceFamilyDefinitionDocument definitionDocument =
                    XmlDeviceFamilyDefinitionDocument.Factory.parse(stream);
            return definitionDocument.getXmlDeviceFamilyDefinition();
        }
    }

    static class DeviceTree {
        private final DeviceNode root;

        public DeviceTree() {
            root = new DeviceNode();
            root.name = "";
        }

        DeviceNode getRoot() {
            return root;
        }

    }

    static class DeviceNode {

        private String name;
        private int hashCode;
        private DeviceNode parent;
        private List<DeviceNode> children;

        public DeviceNode() {
            children = new ArrayList<DeviceNode>();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result =
                    prime * result + ((parent == null) ? 0 : parent.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            DeviceNode other = (DeviceNode) obj;
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
            if (parent == null) {
                if (other.parent != null) {
                    return false;
                }
            } else if (!parent.equals(other.parent)) {
                return false;
            }
            return true;
        }

        public boolean containsChild(DeviceNode o) {
            return children.contains(o);
        }

        public DeviceNode getChild(String childName) {
            DeviceNode temp = new DeviceNode();
            temp.name = childName;
            temp.parent = this;
            int indexOf = children.indexOf(temp);
            if (indexOf < 0) {
                return null;
            } else {
                return children.get(indexOf);
            }
        }

        public String getPath() {
            LinkedList<String> list = new LinkedList<String>();
            StringBuilder sb = new StringBuilder();

            DeviceNode currNode = this;
            do {
                list.addFirst(currNode.name);
                currNode = currNode.parent;
            } while (currNode != null);

            // Remove root node
            list.removeFirst();

            boolean first = true;
            for (String string : list) {
                if (first) {
                    first = false;
                } else {
                    sb.append(PATH_SEPARATOR);
                }
                sb.append(string);
            }
            return sb.toString();
        }

        String getName() {
            return name;
        }

        void setName(String name) {
            this.name = name;
        }

        int getHashCode() {
            return hashCode;
        }

        void setHashCode(int hashCode) {
            this.hashCode = hashCode;
        }

        DeviceNode getParent() {
            return parent;
        }

        void setParent(DeviceNode parent) {
            this.parent = parent;
        }

        List<DeviceNode> getChildren() {
            return children;
        }

        void setChildren(List<DeviceNode> children) {
            this.children = children;
        }
    }

    Map<String, HandlerDefinitionBase> getMap() {
        return map;
    }

    DeviceTree getDeviceTree() {
        return deviceTree;
    }
}
