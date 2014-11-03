package org.jsynthlib.device.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.jsynthlib.device.model.DeviceException;
import org.jsynthlib.device.model.impl.HandlerBindingMap.DeviceNode;
import org.jsynthlib.device.model.impl.HandlerBindingMap.DeviceTree;
import org.jsynthlib.xmldevice.ChecksumCalculatorDefinition;
import org.jsynthlib.xmldevice.DeviceConfiguration;
import org.jsynthlib.xmldevice.DeviceConfiguration.MidiSenderDefinitions;
import org.jsynthlib.xmldevice.HandlerDefinitionBase;
import org.jsynthlib.xmldevice.MidiSenderDefinition;
import org.jsynthlib.xmldevice.ParamModelDefinition;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;
import org.junit.Before;
import org.junit.Test;

public class HandlerBindingMapTest {

    private HandlerBindingMap tested;

    @Before
    public void setUp() {
        tested = new HandlerBindingMap();
        tested.getMap().clear();
    }

    @Test
    public void testGetHandlerPath() {
        String handlerPath =
                tested.getHandlerPath("yamaha.dxfamily", "dxVoiceSender");
        assertEquals("yamaha.dxfamily.dxVoiceSender", handlerPath);

        handlerPath =
                tested.getHandlerPath("yamaha", "yamahaChecksumCalculator");
        assertEquals("yamaha.yamahaChecksumCalculator", handlerPath);
    }

    @Test
    public void testGetNodeByPath() {
        DeviceTree deviceTree = tested.getDeviceTree();
        DeviceNode root = deviceTree.getRoot();

        DeviceNode yamahaNode = new HandlerBindingMap.DeviceNode();
        yamahaNode.setName("yamaha");
        yamahaNode.setHashCode(10);
        yamahaNode.setParent(root);
        root.getChildren().add(yamahaNode);

        DeviceNode rolandNode = new HandlerBindingMap.DeviceNode();
        rolandNode.setName("roland");
        rolandNode.setHashCode(12);
        rolandNode.setParent(root);
        root.getChildren().add(rolandNode);

        DeviceNode ensoniqNode = new HandlerBindingMap.DeviceNode();
        ensoniqNode.setName("ensoniq");
        ensoniqNode.setHashCode(12);
        ensoniqNode.setParent(root);
        root.getChildren().add(ensoniqNode);

        DeviceNode dxFamilyNode = new HandlerBindingMap.DeviceNode();
        dxFamilyNode.setName("dxfamily");
        dxFamilyNode.setHashCode(11);
        dxFamilyNode.setParent(yamahaNode);
        yamahaNode.getChildren().add(dxFamilyNode);

        assertTrue(root.containsChild(yamahaNode));
        assertTrue(yamahaNode.containsChild(dxFamilyNode));

        DeviceNode result = tested.getNodeByPath("org.jsynthlib.yamaha");
        assertNull(result);

        result = tested.getNodeByPath("yamaha");
        assertNotNull(result);
        assertEquals("yamaha", result.getName());

        result = tested.getNodeByPath("yamaha.dxfamily");
        assertNotNull(result);
        assertEquals("dxfamily", result.getName());
        assertEquals(yamahaNode, result.getParent());

        result = tested.getNodeByPath("ensoniq");
        assertNotNull(result);
        assertEquals("ensoniq", result.getName());
        assertEquals(root, result.getParent());

        result = tested.getNodeByPath("ensoniq.shouldNotExist");
        assertNull(result);
    }

    @Test
    public void testRemoveNodePaths() {
        String path = "yamaha.dxfamily";
        Map<String, HandlerDefinitionBase> map = tested.getMap();
        map.put("yamaha.checksumCalculator",
                HandlerDefinitionBase.Factory.newInstance());
        map.put(path + ".dxSender", HandlerDefinitionBase.Factory.newInstance());
        map.put(path + ".dxStringModel",
                HandlerDefinitionBase.Factory.newInstance());
        map.put(path + ".dxStringSender",
                HandlerDefinitionBase.Factory.newInstance());
        assertEquals(4, map.keySet().size());
        assertTrue(map.keySet().contains(path + ".dxStringSender"));
        tested.removeNodeBindings(path);
        assertEquals(1, map.keySet().size());
        assertTrue(map.keySet().contains("yamaha.checksumCalculator"));
        assertFalse(map.keySet().contains(path + ".dxStringSender"));
    }

    @Test
    public void testAddDeviceWManufacturer() throws DeviceException {
        XmlDeviceDefinition def = XmlDeviceDefinition.Factory.newInstance();
        def.setManufacturer("manufacturer");
        def.setModelName("deviceModel");
        DeviceConfiguration config = def.addNewConfiguration();
        MidiSenderDefinitions msDefinitions =
                config.addNewMidiSenderDefinitions();
        MidiSenderDefinition msDefinition =
                msDefinitions.addNewMidiSenderDefinition();
        msDefinition.setHandlerClass("this.is.a.Class");
        msDefinition.setName("sender");
        tested.addDevice(def);

        DeviceTree deviceTree = tested.getDeviceTree();
        DeviceNode root = deviceTree.getRoot();

        DeviceNode manufacturerNode = root.getChild("manufacturer");
        assertNotNull(manufacturerNode);

        DeviceNode deviceFamilyNode = manufacturerNode.getChild("deviceFamily");
        assertNull(deviceFamilyNode);

        DeviceNode deviceNode = manufacturerNode.getChild("deviceModel");
        assertNotNull(deviceNode);

        Map<String, HandlerDefinitionBase> map = tested.getMap();
        assertFalse(map
                .containsKey("manufacturer.deviceFamily.deviceModel.sender"));
        assertTrue(map.containsKey("manufacturer.manufCs"));
        assertFalse(map
                .containsKey("manufacturer.deviceFamily.familyStringModel"));
        assertTrue(map.containsKey("manufacturer.deviceModel.sender"));
        HandlerDefinitionBase handler =
                map.get("manufacturer.deviceModel.sender");
        assertEquals(handler, msDefinition);
    }

    @Test
    public void testAddDeviceWFamily() throws DeviceException {
        XmlDeviceDefinition def = XmlDeviceDefinition.Factory.newInstance();
        def.setFamily("manufacturer.deviceFamily");
        def.setModelName("deviceModel");
        DeviceConfiguration config = def.addNewConfiguration();
        MidiSenderDefinitions msDefinitions =
                config.addNewMidiSenderDefinitions();
        MidiSenderDefinition msDefinition =
                msDefinitions.addNewMidiSenderDefinition();
        msDefinition.setHandlerClass("this.is.a.Class");
        msDefinition.setName("sender");
        tested.addDevice(def);

        DeviceTree deviceTree = tested.getDeviceTree();
        DeviceNode root = deviceTree.getRoot();

        DeviceNode manufacturerNode = root.getChild("manufacturer");
        assertNotNull(manufacturerNode);

        DeviceNode deviceFamilyNode = manufacturerNode.getChild("deviceFamily");
        assertNotNull(deviceFamilyNode);

        DeviceNode deviceNode = deviceFamilyNode.getChild("deviceModel");
        assertNotNull(deviceNode);

        Map<String, HandlerDefinitionBase> map = tested.getMap();
        assertTrue(map
                .containsKey("manufacturer.deviceFamily.deviceModel.sender"));
        assertTrue(map.containsKey("manufacturer.manufCs"));
        assertTrue(map
                .containsKey("manufacturer.deviceFamily.familyStringModel"));
        assertFalse(map.containsKey("manufacturer.deviceFamily.sender"));
        HandlerDefinitionBase handler =
                map.get("manufacturer.deviceFamily.deviceModel.sender");
        assertEquals(handler, msDefinition);
    }

    @Test
    public void testRemoveDevice() throws DeviceException {
        DeviceTree deviceTree = tested.getDeviceTree();
        DeviceNode root = deviceTree.getRoot();

        DeviceNode yamahaNode = new HandlerBindingMap.DeviceNode();
        yamahaNode.setName("yamaha");
        yamahaNode.setHashCode(10);
        yamahaNode.setParent(root);
        root.getChildren().add(yamahaNode);

        DeviceNode rolandNode = new HandlerBindingMap.DeviceNode();
        rolandNode.setName("roland");
        rolandNode.setHashCode(12);
        rolandNode.setParent(root);
        root.getChildren().add(rolandNode);

        DeviceNode ensoniqNode = new HandlerBindingMap.DeviceNode();
        ensoniqNode.setName("ensoniq");
        ensoniqNode.setHashCode(11);
        ensoniqNode.setParent(root);
        root.getChildren().add(ensoniqNode);

        DeviceNode dxFamilyNode = new HandlerBindingMap.DeviceNode();
        dxFamilyNode.setName("dxfamily");
        dxFamilyNode.setHashCode(14);
        dxFamilyNode.setParent(yamahaNode);
        yamahaNode.getChildren().add(dxFamilyNode);

        DeviceNode dx7Node = new HandlerBindingMap.DeviceNode();
        dx7Node.setName("dx7");
        dx7Node.setHashCode(13);
        dx7Node.setParent(dxFamilyNode);
        dxFamilyNode.getChildren().add(dx7Node);

        Map<String, HandlerDefinitionBase> map = tested.getMap();

        map.put("yamaha.dxfamily.dxSender",
                MidiSenderDefinition.Factory.newInstance());
        map.put("ensoniq.nrpnSender",
                MidiSenderDefinition.Factory.newInstance());

        assertEquals(2, map.size());
        tested.removeDevice("yamaha.dxfamily.dx7");
        assertEquals(1, map.size());
        assertFalse(map.containsKey("yamaha.dxfamily.dxSender"));
        DeviceNode child = deviceTree.getRoot().getChild("yamaha");
        assertNull(child);
    }

    @Test
    public void testGetNodeHandlers() {
        DeviceTree deviceTree = tested.getDeviceTree();
        DeviceNode root = deviceTree.getRoot();

        DeviceNode yamahaNode = new HandlerBindingMap.DeviceNode();
        yamahaNode.setName("yamaha");
        yamahaNode.setHashCode(10);
        yamahaNode.setParent(root);
        root.getChildren().add(yamahaNode);

        DeviceNode rolandNode = new HandlerBindingMap.DeviceNode();
        rolandNode.setName("roland");
        rolandNode.setHashCode(12);
        rolandNode.setParent(root);
        root.getChildren().add(rolandNode);

        DeviceNode ensoniqNode = new HandlerBindingMap.DeviceNode();
        ensoniqNode.setName("ensoniq");
        ensoniqNode.setHashCode(11);
        ensoniqNode.setParent(root);
        root.getChildren().add(ensoniqNode);

        DeviceNode dxFamilyNode = new HandlerBindingMap.DeviceNode();
        dxFamilyNode.setName("dxfamily");
        dxFamilyNode.setHashCode(14);
        dxFamilyNode.setParent(yamahaNode);
        yamahaNode.getChildren().add(dxFamilyNode);

        DeviceNode dx7Node = new HandlerBindingMap.DeviceNode();
        dx7Node.setName("dx7");
        dx7Node.setHashCode(13);
        dx7Node.setParent(dxFamilyNode);
        dxFamilyNode.getChildren().add(dx7Node);

        Map<String, HandlerDefinitionBase> map = tested.getMap();

        map.put("yamaha.dxfamily.dxSender",
                MidiSenderDefinition.Factory.newInstance());
        map.put("yamaha.checksum",
                ChecksumCalculatorDefinition.Factory.newInstance());
        map.put("yamaha.dxfamily.mymodel",
                ParamModelDefinition.Factory.newInstance());
        map.put("ensoniq.nrpnSender",
                MidiSenderDefinition.Factory.newInstance());
        Map<String, HandlerDefinitionBase> result =
                tested.getNodeHandlers(rolandNode);
        assertTrue(result.isEmpty());

        result = tested.getNodeHandlers(ensoniqNode);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("nrpnSender"));

        result = tested.getNodeHandlers(dxFamilyNode);
        assertEquals(2, result.size());
        assertTrue(result.containsKey("dxSender"));
        assertTrue(result.containsKey("mymodel"));
    }

    @Test
    public void testGetDeviceBindings() {
        DeviceTree deviceTree = tested.getDeviceTree();
        DeviceNode root = deviceTree.getRoot();

        DeviceNode yamahaNode = new HandlerBindingMap.DeviceNode();
        yamahaNode.setName("yamaha");
        yamahaNode.setHashCode(10);
        yamahaNode.setParent(root);
        root.getChildren().add(yamahaNode);

        DeviceNode rolandNode = new HandlerBindingMap.DeviceNode();
        rolandNode.setName("roland");
        rolandNode.setHashCode(12);
        rolandNode.setParent(root);
        root.getChildren().add(rolandNode);

        DeviceNode ensoniqNode = new HandlerBindingMap.DeviceNode();
        ensoniqNode.setName("ensoniq");
        ensoniqNode.setHashCode(11);
        ensoniqNode.setParent(root);
        root.getChildren().add(ensoniqNode);

        DeviceNode dxFamilyNode = new HandlerBindingMap.DeviceNode();
        dxFamilyNode.setName("dxfamily");
        dxFamilyNode.setHashCode(14);
        dxFamilyNode.setParent(yamahaNode);
        yamahaNode.getChildren().add(dxFamilyNode);

        DeviceNode dx7Node = new HandlerBindingMap.DeviceNode();
        dx7Node.setName("dx7");
        dx7Node.setHashCode(13);
        dx7Node.setParent(dxFamilyNode);
        dxFamilyNode.getChildren().add(dx7Node);

        Map<String, HandlerDefinitionBase> map = tested.getMap();

        map.put("defaultChecksumCalculator",
                ChecksumCalculatorDefinition.Factory.newInstance());
        map.put("yamaha.dxfamily.dxSender",
                MidiSenderDefinition.Factory.newInstance());
        map.put("yamaha.checksum",
                ChecksumCalculatorDefinition.Factory.newInstance());
        map.put("yamaha.dxfamily.dx7.mymodel",
                ParamModelDefinition.Factory.newInstance());
        map.put("ensoniq.nrpnSender",
                MidiSenderDefinition.Factory.newInstance());
        Map<String, HandlerDefinitionBase> result =
                tested.getDeviceBindings(rolandNode.getPath());
        assertEquals(1, result.size());
        assertTrue(result.containsKey("defaultChecksumCalculator"));

        result = tested.getDeviceBindings(ensoniqNode.getPath());
        assertEquals(2, result.size());
        assertTrue(result.containsKey("nrpnSender"));
        assertTrue(result.containsKey("defaultChecksumCalculator"));

        result = tested.getDeviceBindings(dx7Node.getPath());
        assertEquals(4, result.size());
        assertTrue(result.containsKey("dxSender"));
        assertTrue(result.containsKey("mymodel"));
        assertTrue(result.containsKey("checksum"));
        assertTrue(result.containsKey("defaultChecksumCalculator"));
    }
}
