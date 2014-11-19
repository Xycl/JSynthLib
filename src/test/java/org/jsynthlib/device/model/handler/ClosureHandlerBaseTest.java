package org.jsynthlib.device.model.handler;

import java.util.Properties;

import org.jsynthlib.xmldevice.ChecksumCalculatorDefinition;
import org.jsynthlib.xmldevice.HandlerDefinitionBase.Property;
import org.junit.Test;

public class ClosureHandlerBaseTest {

    @Test
    public void test() {
        ChecksumCalculatorDefinition definition =
                ChecksumCalculatorDefinition.Factory.newInstance();
        definition.setName("checksum");
        definition.setClosure("println \"${test}, ${sysex}\"");
        Property property = definition.addNewProperty();
        property.setKey("test");
        property = definition.addNewProperty();
        property.setKey("sysex");

        Properties properties = new Properties();
        properties.setProperty("test", "hello world");
        // Builder builder = new ClosureChecksumCalculator.Builder();
        // builder.setHandlerDefinition(definition);
        // builder.setPropertyValues(properties);
        // ClosureChecksumCalculator csc = builder.build();
        // csc.calculateChecksum(new byte[] {
        // 1, 2 });
    }
}
