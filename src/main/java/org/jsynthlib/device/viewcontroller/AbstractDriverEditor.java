/*
 * Copyright 2014 Pascal Collberg
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package org.jsynthlib.device.viewcontroller;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javax.swing.BoundedRangeModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton.ToggleButtonModel;
import javax.swing.event.ChangeEvent;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.jsynthlib.device.model.AbstractEnvelopeParam;
import org.jsynthlib.device.model.DefaultEnvelopeModel;
import org.jsynthlib.device.model.EnvelopeNode;
import org.jsynthlib.device.model.EnvelopeXParam;
import org.jsynthlib.device.model.EnvelopeYParam;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.device.model.IParamModel;
import org.jsynthlib.device.model.IPatchStringSender;
import org.jsynthlib.device.model.ISender;
import org.jsynthlib.device.model.PatchParam;
import org.jsynthlib.device.model.PatchParamLabel;
import org.jsynthlib.device.model.PatchStringModel;
import org.jsynthlib.device.viewcontroller.widgets.Envelope;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.xmldevice.EnvelopeNodeSpec;
import org.jsynthlib.xmldevice.EnvelopeSpec;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.MidiSender;
import org.jsynthlib.xmldevice.PatchParamSpec;
import org.jsynthlib.xmldevice.PatchParamValues;
import org.jsynthlib.xmldevice.PropertySpec;
import org.jsynthlib.xmldevice.StringModelSpec;
import org.jsynthlib.xmldevice.StringParamSpec;
import org.jsynthlib.xmldevice.StringSenderSpec;
import org.jsynthlib.xmldevice.UuidPatchParamSpec;
import org.jsynthlib.xmldevice.XEnvelopeParamSpec;
import org.jsynthlib.xmldevice.XmlDriverSpec;
import org.jsynthlib.xmldevice.YEnvelopeParamSpec;

import com.dreamfabric.DKnob;

/**
 * @author Pascal Collberg
 */
public abstract class AbstractDriverEditor extends JPanel {

    private static final long serialVersionUID = 1L;

    protected final transient Logger log = Logger.getLogger(getClass());

    protected final IDriver driver;

    protected final Patch patch;

    public AbstractDriverEditor(IDriver driver, Patch patch) {
        this.driver = driver;
        this.patch = patch;
    }

    public final void initializePatchParams(XmlDriverSpec xmlDriverSpec) {
        try {
            for (Field f : getClass().getDeclaredFields()) {
                try {
                    Class<?> cls = Class.forName(f.getType().getName());
                    if (f.isAnnotationPresent(PatchParam.class)) {
                        f.setAccessible(true);
                        PatchParam patchParam =
                                f.getAnnotation(PatchParam.class);
                        String uuid = patchParam.uuid();
                        PatchParamSpec paramSpec =
                                getPatchParamSpec(xmlDriverSpec, uuid);
                        if (JTextField.class.equals(cls)) {
                            JTextField jtf = (JTextField) f.get(this);
                            handleJTextField(jtf, (StringParamSpec) paramSpec);
                        } else if (JSlider.class.equals(cls)) {
                            JSlider js = (JSlider) f.get(this);
                            handleJSlider(js, (IntParamSpec) paramSpec);
                        } else if (JCheckBox.class.equals(cls)) {
                            JCheckBox js = (JCheckBox) f.get(this);
                            handleJCheckBox(js, (IntParamSpec) paramSpec);
                        } else if (JComboBox.class.equals(cls)) {
                            JComboBox js = (JComboBox) f.get(this);
                            handleJComboBox(js, (IntParamSpec) paramSpec);
                        } else if (DKnob.class.equals(cls)) {
                            DKnob js = (DKnob) f.get(this);
                            handleDKnob(js, (IntParamSpec) paramSpec);
                        } else if (Envelope.class.equals(cls)) {
                            Envelope js = (Envelope) f.get(this);
                            handleEnvelope(js, (EnvelopeSpec) paramSpec);
                        }
                    } else if (f.isAnnotationPresent(PatchParamLabel.class)) {
                        f.setAccessible(true);
                        PatchParamLabel patchParam =
                                f.getAnnotation(PatchParamLabel.class);
                        String uuid = patchParam.uuid();
                        PatchParamSpec paramSpec =
                                getPatchParamSpec(xmlDriverSpec, uuid);
                        if (JLabel.class.equals(cls)) {
                            JLabel js = (JLabel) f.get(this);
                            js.setText(paramSpec.getName());
                        }
                    }
                } catch (ClassNotFoundException e) {
                    log.debug(e.getMessage());
                }
            }
        } catch (IllegalAccessException e) {
            log.warn(e.getMessage(), e);
        }
    }

    void handleEnvelope(Envelope js, EnvelopeSpec paramSpec) {
        EnvelopeNodeSpec[] envelopeNodes = paramSpec.getEnvelopeNodeSpecArray();
        ArrayList<EnvelopeNode> nodeList = new ArrayList<EnvelopeNode>();
        for (EnvelopeNodeSpec envelopeNode : envelopeNodes) {
            EnvelopeXParam xParam = null;
            EnvelopeYParam yParam = null;

            XEnvelopeParamSpec xParamSpec = envelopeNode.getXParam();
            if (xParamSpec != null) {
                xParam =
                        new EnvelopeXParam(xParamSpec.getMin(),
                                xParamSpec.getMax(), xParamSpec.getName(),
                                xParamSpec.getInvert());
                if (xParam.isVariable()) {
                    final ISender sender =
                            newSender(xParamSpec.getMidiSender());
                    final IParamModel paramModel =
                            newParamModel(xParamSpec.getParamModel());
                    xParam.addChangeListener(new ChangeListener<AbstractEnvelopeParam>() {

                        @Override
                        public void changed(
                                ObservableValue<? extends AbstractEnvelopeParam> arg0,
                                AbstractEnvelopeParam arg1,
                                AbstractEnvelopeParam arg2) {
                            sender.send(driver, arg2.valueProperty().get());
                            paramModel.set(arg2.valueProperty().get());
                        }
                    });
                }
            }

            YEnvelopeParamSpec yParamSpec = envelopeNode.getYParam();
            if (yParamSpec != null) {
                yParam =
                        new EnvelopeYParam(yParamSpec.getMin(),
                                yParamSpec.getMax(), yParamSpec.getName(),
                                yParamSpec.getBase());
                if (yParam.isVariable()) {
                    final ISender sender =
                            newSender(yParamSpec.getMidiSender());
                    final IParamModel paramModel =
                            newParamModel(yParamSpec.getParamModel());
                    yParam.addChangeListener(new ChangeListener<AbstractEnvelopeParam>() {

                        @Override
                        public void changed(
                                ObservableValue<? extends AbstractEnvelopeParam> arg0,
                                AbstractEnvelopeParam arg1,
                                AbstractEnvelopeParam arg2) {
                            sender.send(driver, arg2.valueProperty().get());
                            paramModel.set(arg2.valueProperty().get());
                        }
                    });
                }
            }

            nodeList.add(new EnvelopeNode(xParam, yParam));
        }
        DefaultEnvelopeModel model =
                new DefaultEnvelopeModel(
                        nodeList.toArray(new EnvelopeNode[nodeList.size()]));
        js.setModel(model);
    }

    void handleDKnob(DKnob knob, final IntParamSpec paramSpec) {
        final int min = paramSpec.getMin();
        final int range = paramSpec.getMax() - paramSpec.getMin();

        final ISender sender = newSender(paramSpec.getMidiSender());
        final IParamModel paramModel = newParamModel(paramSpec.getParamModel());

        knob.setDragType(DKnob.SIMPLE_MOUSE_DIRECTION);

        int oValue = paramModel.get();
        knob.setToolTipText(Integer.toString(oValue + paramSpec.getBase()));
        // Set the current value
        knob.setValue(((float) oValue - min) / range);

        // Add a change listener to the knob
        knob.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                DKnob t = (DKnob) e.getSource();
                int oValue = Math.round(t.getValue() * range) + min;
                String oVStr = Integer.toString(oValue + paramSpec.getBase());
                t.setToolTipText(oVStr);
                t.setValueLabel(oVStr);
                sender.send(driver, oValue);
                paramModel.set(oValue);
            }
        });
        // mouse wheel event is supported by J2SE 1.4 and later
        knob.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                DKnob t = (DKnob) e.getSource();
                if (t.hasFocus()) {
                    t.setValue(t.getValue()
                            - (e.getWheelRotation() / (float) range));
                }
            }
        });
    }

    protected ISender newSender(MidiSender midiSender) {
        String className = midiSender.getSenderClass();
        PropertySpec[] properties = midiSender.getPropertyArray();
        return newPropertyBean(className, ISender.class, properties, true);
    }

    protected IParamModel newParamModel(
            org.jsynthlib.xmldevice.ParamModel paramModel) {
        String className = paramModel.getModelClass();
        PropertySpec[] properties = paramModel.getPropertyArray();
        return newPropertyBean(className, IParamModel.class, properties, true);
    }

    protected IPatchStringSender newStringSender(
            StringSenderSpec stringSenderSpec) {
        String className = stringSenderSpec.getStringSenderClass();
        PropertySpec[] properties = stringSenderSpec.getPropertyArray();
        return newPropertyBean(className, IPatchStringSender.class, properties, true);
    }

    protected PatchStringModel newPatchStringModel(
            StringModelSpec stringModelSpec) {
        String className = stringModelSpec.getStringModelClass();
        PropertySpec[] properties = stringModelSpec.getPropertyArray();
        return newPropertyBean(className, PatchStringModel.class, properties, true);
    }

    @SuppressWarnings("unchecked")
    <T> T newPropertyBean(String className, Class<T> returnClass,
            PropertySpec[] properties, boolean setPatch) {
        try {
            Class<T> klass = (Class<T>) Class.forName(className);
            T instance = klass.newInstance();
            for (PropertySpec property : properties) {
                BeanUtils.setProperty(instance, property.getName(),
                        property.getValue());
            }
            if (setPatch) {
                BeanUtils.setProperty(instance, "patch", patch);
            }
            return instance;
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return null;
        }

    }

    /**
     * @param js
     * @param paramSpec
     */
    void handleJComboBox(JComboBox js, IntParamSpec paramSpec) {
        PatchParamValues patchParamValues = paramSpec.getPatchParamValues();
        ComboBoxModel model =
                new DefaultComboBoxModel(
                        patchParamValues.getPatchParamValueArray());
        js.setModel(model);
        final ISender sender = newSender(paramSpec.getMidiSender());
        final IParamModel paramModel = newParamModel(paramSpec.getParamModel());
        js.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    JComboBox source = (JComboBox) e.getSource();
                    sender.send(driver, source.getSelectedIndex());
                    paramModel.set(source.getSelectedIndex());
                }
            }
        });
    }

    /**
     * @param js
     * @param paramSpec
     */
    void handleJCheckBox(JCheckBox js, final IntParamSpec paramSpec) {
        ToggleButtonModel model = new ToggleButtonModel();
        final ISender sender = newSender(paramSpec.getMidiSender());
        final IParamModel paramModel = newParamModel(paramSpec.getParamModel());
        model.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent arg0) {
                ToggleButtonModel source = (ToggleButtonModel) arg0.getSource();
                if (source.isSelected()) {
                    sender.send(driver, paramSpec.getMax());
                    paramModel.set(paramSpec.getMax());
                } else {
                    sender.send(driver, paramSpec.getMin());
                    paramModel.set(paramSpec.getMin());
                }
            }
        });
        js.setModel(model);
    }

    /**
     * @param js
     * @param paramSpec
     */
    void handleJSlider(JSlider js, IntParamSpec paramSpec) {
        BoundedRangeModel model =
                new DefaultBoundedRangeModel(0, 0, paramSpec.getMin(),
                        paramSpec.getMax());
        final ISender sender = newSender(paramSpec.getMidiSender());
        final IParamModel paramModel = newParamModel(paramSpec.getParamModel());
        model.addChangeListener(new javax.swing.event.ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                BoundedRangeModel source = (BoundedRangeModel) e.getSource();
                sender.send(driver, source.getValue());
                paramModel.set(source.getValue());
            }
        });
        js.setModel(model);
    }

    protected UuidPatchParamSpec getPatchParamSpec(XmlDriverSpec xmlDriverSpec,
            String uuid) {
        String queryExpressionStub =
                "declare namespace jsl='http://www.jsynthlib.org/xmldevice';"
                        + "//*[jsl:uuid='%s']";
        String query = String.format(queryExpressionStub, uuid);
        XmlObject[] xmlObjects = xmlDriverSpec.selectPath(query);
        if (xmlObjects.length == 1) {
            return (UuidPatchParamSpec) xmlObjects[0];
        } else {
            return null;
        }
    }

    void handleJTextField(JTextField jtf, StringParamSpec paramSpec) {
        StringSenderSpec senderSpec = paramSpec.getStringSender();
        IPatchStringSender sender = null;
        if (senderSpec != null) {
            sender = newStringSender(senderSpec);
        }
        StringModelSpec stringModelSpec = paramSpec.getStringModel();
        PatchStringModel model = null;
        if (senderSpec != null) {
            model = newPatchStringModel(stringModelSpec);
        }

        jtf.addFocusListener(new PatchNameFocusListener(sender, model));
        if (model == null) {
            jtf.setText(patch.getName());
        } else {
            jtf.setText(model.get());
        }
    }

    class PatchNameFocusListener implements FocusListener {
        private final IPatchStringSender sender;
        private final PatchStringModel model;

        public PatchNameFocusListener(IPatchStringSender sender, PatchStringModel model) {
            this.sender = sender;
            this.model = model;
        }

        @Override
        public void focusLost(FocusEvent e) {
            JTextField jtf = (JTextField) e.getComponent();
            String text = jtf.getText();
            if (sender == null) {
                driver.setPatchName(patch, text);
                driver.sendPatch(patch);
            } else {
                sender.send(text);
            }
            if (model != null) {
                model.set(text);
            }
        }

        @Override
        public void focusGained(FocusEvent e) {
        }
    }
}
