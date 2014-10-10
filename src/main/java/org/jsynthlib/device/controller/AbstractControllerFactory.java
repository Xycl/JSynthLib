package org.jsynthlib.device.controller;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import org.jsynthlib.device.view.Envelope;
import org.jsynthlib.device.view.Knob;
import org.jsynthlib.xmldevice.XmlDriverSpec;

public class AbstractControllerFactory implements Callback<Class<?>, Object> {

    private final XmlDriverSpec driverSpec;

    private final Map<Class<?>, ConcreteControllerFactory> factoryMap;

    public AbstractControllerFactory(XmlDriverSpec driverSpec) {
        this.driverSpec = driverSpec;
        factoryMap = new HashMap<Class<?>, AbstractControllerFactory.ConcreteControllerFactory>();
        factoryMap.put(Envelope.class, new EnvelopeControllerFactory());
        factoryMap.put(Knob.class, new SliderControllerFactory());
        factoryMap.put(Slider.class, new SliderControllerFactory());
        factoryMap.put(TextField.class, new TextFieldControllerFactory());
        factoryMap.put(ComboBox.class, new ComboBoxControllerFactory());
        factoryMap.put(CheckBox.class, new CheckBoxControllerFactory());
    }

    @Override
    public Object call(Class<?> arg0) {
        if (factoryMap.containsKey(arg0)) {
            return factoryMap.get(arg0).call();
        } else {
            return null;
        }
    }

    interface ConcreteControllerFactory {
        Object call();
    }

    class EnvelopeControllerFactory implements ConcreteControllerFactory {

        @Override
        public Object call() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    class SliderControllerFactory implements ConcreteControllerFactory {

        @Override
        public Object call() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    class TextFieldControllerFactory implements ConcreteControllerFactory {

        @Override
        public Object call() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    class ComboBoxControllerFactory implements ConcreteControllerFactory {

        @Override
        public Object call() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    class CheckBoxControllerFactory implements ConcreteControllerFactory {

        @Override
        public Object call() {
            // TODO Auto-generated method stub
            return null;
        }

    }
}
