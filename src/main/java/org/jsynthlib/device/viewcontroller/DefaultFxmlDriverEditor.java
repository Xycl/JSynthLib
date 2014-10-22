package org.jsynthlib.device.viewcontroller;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import org.jsynthlib.device.model.AbstractEnvelopeParam;
import org.jsynthlib.device.model.CombinedGroupModel;
import org.jsynthlib.device.model.DefaultEnvelopeModel;
import org.jsynthlib.device.model.EnvelopeNode;
import org.jsynthlib.device.model.EnvelopeXParam;
import org.jsynthlib.device.model.EnvelopeYParam;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.device.model.IParamModel;
import org.jsynthlib.device.model.IPatchStringSender;
import org.jsynthlib.device.model.ISender;
import org.jsynthlib.device.model.PatchStringModel;
import org.jsynthlib.device.view.Envelope;
import org.jsynthlib.device.view.Knob;
import org.jsynthlib.midi.service.MidiMessageFormatter;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.xmldevice.CombinedGroup;
import org.jsynthlib.xmldevice.CombinedIntPatchParam;
import org.jsynthlib.xmldevice.EnvelopeNodeSpec;
import org.jsynthlib.xmldevice.EnvelopeSpec;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamGroup;
import org.jsynthlib.xmldevice.PatchParamResources;
import org.jsynthlib.xmldevice.PatchParamValues;
import org.jsynthlib.xmldevice.PatchParams;
import org.jsynthlib.xmldevice.StringModelSpec;
import org.jsynthlib.xmldevice.StringParamSpec;
import org.jsynthlib.xmldevice.StringSenderSpec;
import org.jsynthlib.xmldevice.XEnvelopeParamSpec;
import org.jsynthlib.xmldevice.XmlDriverSpec;
import org.jsynthlib.xmldevice.XmlPatchDriverSpecDocument.XmlPatchDriverSpec;
import org.jsynthlib.xmldevice.YEnvelopeParamSpec;

public class DefaultFxmlDriverEditor extends AbstractDriverEditor {

    private static final long serialVersionUID = 1L;

    private final JFXPanel jfxPanel;

    private final XmlPatchDriverSpec xmlDriverSpec;

    private Scene scene;

    private ObservableMap<String, Object> namespace;

    public DefaultFxmlDriverEditor(final IDriver d,
            final XmlPatchDriverSpec xmlDriverSpec, final Patch p) {
        super(d, p);
        System.out.println(MidiMessageFormatter.hexDump(p.sysex, 0, -1, 0));
        Platform.setImplicitExit(false);
        jfxPanel = new JFXPanel();
        this.xmlDriverSpec = xmlDriverSpec;

        final Semaphore semaphore = new Semaphore(0);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(jfxPanel);
                log.info("Scene initialized");
                semaphore.release();
            }
        });

        try {
            log.info("Waiting for scene");
            semaphore.acquire();
            log.info("Scene done adding jfxpanel");
            double width = scene.getWidth();
            double height = scene.getHeight();
            jfxPanel.setSize((int) width, (int) height);
            add(jfxPanel);
        } catch (InterruptedException e) {
            log.warn(e.getMessage(), e);
        }
    }

    final void initFX(JFXPanel fxPanel) {
        // This method is invoked on JavaFX thread
        try {
            String fxmlName =
                    driver.getClass().getName().replace('.', '/')
                            + "Editor.fxml";
            log.info("Loading fxml: " + fxmlName);
            FXMLLoader fxmlLoader =
                    new FXMLLoader(getClass().getClassLoader().getResource(
                            fxmlName));
            Parent root = (Parent) fxmlLoader.load();

            namespace = fxmlLoader.getNamespace();

            scene = new Scene(root);
            scene.getStylesheets().add("application.css");
            initNodeRecursive(xmlDriverSpec.getPatchParams());
            fxPanel.setScene(scene);
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    final void initNodeRecursive(PatchParams patchParams) {
        EnvelopeSpec[] envelopeSpecs = patchParams.getEnvelopeSpecArray();
        for (EnvelopeSpec envelopeSpec : envelopeSpecs) {
            Envelope envelope =
                    (Envelope) namespace.get(envelopeSpec.getUuid().trim());
            handleEnvelope(envelope, envelopeSpec);
        }

        IntParamSpec[] intParamSpecs = patchParams.getIntParamSpecArray();
        for (IntParamSpec intParamSpec : intParamSpecs) {
            Object node = namespace.get(intParamSpec.getUuid().trim());
            if (node instanceof Slider) {
                Slider knob = (Slider) node;
                handleSlider(knob, intParamSpec);
            } else if (node instanceof CheckBox) {
                CheckBox cb = (CheckBox) node;
                handleCheckBox(cb, intParamSpec);
            } else if (node instanceof ComboBox<?>) {
                @SuppressWarnings("unchecked")
                ComboBox<String> cb = (ComboBox<String>) node;
                handleComboBox(cb, intParamSpec);
            }
        }

        PatchParamGroup[] patchParamGroups =
                patchParams.getPatchParamGroupArray();
        for (PatchParamGroup patchParamGroup : patchParamGroups) {
            initNodeRecursive(patchParamGroup);
        }

        CombinedGroup[] combinedGroups = patchParams.getCombinedgroupArray();
        for (CombinedGroup combinedGroup : combinedGroups) {
            handleCombinedGroup(combinedGroup);
        }

        StringParamSpec[] stringParamSpecs =
                patchParams.getStringParamSpecArray();
        for (StringParamSpec stringParamSpec : stringParamSpecs) {
            TextField textField =
                    (TextField) namespace.get(stringParamSpec.getUuid().trim());
            handleTextField(textField, stringParamSpec);
        }
    }

    /**
     * @param combinedGroup
     * @param pane
     */
    void handleCombinedGroup(CombinedGroup combinedGroup) {
        CombinedIntPatchParam[] paramSpecs = combinedGroup.getParamArray();
        final ISender sender = newSender(combinedGroup.getMidiSender());
        final CombinedGroupModel paramModel =
                (CombinedGroupModel) newParamModel(combinedGroup
                        .getParamModel());
        for (final CombinedIntPatchParam intParamSpec : paramSpecs) {
            Object node = namespace.get(intParamSpec.getUuid().trim());
            if (node instanceof CheckBox) {
                final CheckBox cb = (CheckBox) node;
                cb.selectedProperty().bindBidirectional(
                        paramModel.getBooleanProperty(intParamSpec
                                .getLeftShift()));
                cb.selectedProperty().addListener(
                        new CombinedGroupChangeListener(intParamSpec,
                                paramModel, sender));
            }
        }
    }

    class CombinedGroupChangeListener implements ChangeListener<Boolean> {

        private final CombinedIntPatchParam intParamSpec;
        private final CombinedGroupModel paramModel;
        private final ISender sender;

        public CombinedGroupChangeListener(CombinedIntPatchParam intParamSpec,
                CombinedGroupModel paramModel, ISender sender) {
            super();
            this.intParamSpec = intParamSpec;
            this.paramModel = paramModel;
            this.sender = sender;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> arg0,
                Boolean arg1, Boolean arg2) {
            if (arg1.booleanValue() == arg2.booleanValue()) {
                return;
            }
            paramModel.setBit(intParamSpec.getLeftShift(), arg2.booleanValue());
            int value = paramModel.get();
            sender.send(driver, value);
        }
    }

    void handleTextField(final TextField tf, StringParamSpec paramSpec) {
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

        tf.focusedProperty().addListener(
                new TextFieldChangeListener(tf, sender, model));
        if (model == null) {
            tf.textProperty().set(driver.getPatchName(patch));
        } else {
            tf.textProperty().set(model.get());
        }
    }

    class TextFieldChangeListener implements ChangeListener<Boolean> {
        private final IPatchStringSender sender;
        private final TextField tf;
        private final PatchStringModel model;

        public TextFieldChangeListener(TextField tf, IPatchStringSender sender,
                PatchStringModel model) {
            this.sender = sender;
            this.model = model;
            this.tf = tf;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> arg0,
                Boolean arg1, Boolean arg2) {
            if (!arg2.booleanValue()) {
                String text = tf.getText();
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
                    xParam.valueProperty().set(paramModel.get());
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
                    yParam.valueProperty().set(paramModel.get());
                }
            }

            nodeList.add(new EnvelopeNode(xParam, yParam));
        }
        DefaultEnvelopeModel model =
                new DefaultEnvelopeModel(
                        nodeList.toArray(new EnvelopeNode[nodeList.size()]));
        js.setModel(model);
    }

    void handleSlider(final Slider knob, final IntParamSpec paramSpec) {
        final int range = paramSpec.getMax() - paramSpec.getMin();

        final ISender sender = newSender(paramSpec.getMidiSender());
        final IParamModel paramModel = newParamModel(paramSpec.getParamModel());

        int oValue = paramModel.get();

        // Set the current value
        knob.setValue(oValue);
        knob.setMax(paramSpec.getMax());
        knob.setMin(paramSpec.getMin());

        setTooltip(knob, knob.valueProperty(), paramSpec.getBase());

        // Add a change listener to the knob
        knob.valueProperty().addListener(
                new KnobValueChangeListener(paramSpec, sender, paramModel));

        // mouse wheel event is supported by J2SE 1.4 and later
        knob.setOnScroll(new EventHandler<ScrollEvent>() {

            @Override
            public void handle(ScrollEvent arg0) {
                Knob knob = (Knob) arg0.getSource();
                if (knob.focusedProperty().get()) {
                    DoubleProperty valueProperty = knob.valueProperty();
                    valueProperty.set(valueProperty.get()
                            - (arg0.getDeltaY() / range));
                }
            }
        });
    }

    class KnobValueChangeListener implements ChangeListener<Number> {

        private final ImageView imgView;
        private final ISender sender;
        private final IParamModel paramModel;
        private Image[] images;

        public KnobValueChangeListener(IntParamSpec paramSpec, ISender sender,
                IParamModel paramModel) {
            this.sender = sender;
            this.paramModel = paramModel;
            imgView = (ImageView) namespace.get("img" + paramSpec.getUuid());
            PatchParamResources paramResources =
                    paramSpec.getPatchParamResources();
            if (paramResources != null) {
                String[] resourceArray =
                        paramResources.getPatchParamResourceArray();
                images = new Image[resourceArray.length];
                for (int i = 0; i < resourceArray.length; i++) {
                    String resource = resourceArray[i];
                    InputStream stream =
                            driver.getClass().getClassLoader()
                                    .getResourceAsStream(resource);
                    images[i] = new Image(stream);
                }

                if (imgView != null) {
                    imgView.setImage(images[paramModel.get()]);
                }
            }

        }

        @Override
        public void changed(ObservableValue<? extends Number> arg0,
                Number arg1, Number arg2) {
            if (arg1.intValue() == arg2.intValue()) {
                return;
            }
            sender.send(driver, arg2.intValue());
            paramModel.set(arg2.intValue());
            if (imgView != null && images != null) {
                imgView.setImage(images[arg2.intValue()]);
            }
        }
    }

    /**
     * @param js
     * @param paramSpec
     */
    void handleComboBox(final ComboBox<String> js, final IntParamSpec paramSpec) {
        PatchParamValues patchParamValues = paramSpec.getPatchParamValues();
        ObservableList<String> items = js.getItems();
        String[] paramValueArray = patchParamValues.getPatchParamValueArray();
        for (String paramValue : paramValueArray) {
            items.add(paramValue);
        }
        final ISender sender = newSender(paramSpec.getMidiSender());
        final IParamModel paramModel = newParamModel(paramSpec.getParamModel());
        js.valueProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0,
                    String arg1, String arg2) {
                if (arg1 != null && arg1.equals(arg2)) {
                    return;
                }

                int value = js.getItems().indexOf(arg2) + paramSpec.getBase();
                if (sender != null) {
                    sender.send(driver, value);
                }
                if (paramModel != null) {
                    paramModel.set(value);
                }
            }
        });
        js.valueProperty().set(js.getItems().get(paramModel.get()));
    }

    /**
     * @param js
     * @param paramSpec
     */
    void handleCheckBox(CheckBox js, final IntParamSpec paramSpec) {
        final ISender sender = newSender(paramSpec.getMidiSender());
        final IParamModel paramModel = newParamModel(paramSpec.getParamModel());
        js.selectedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> arg0,
                    Boolean arg1, Boolean arg2) {
                if (arg1.booleanValue() == arg2.booleanValue()) {
                    return;
                }

                if (arg2.booleanValue()) {
                    sender.send(driver, paramSpec.getMax());
                    paramModel.set(paramSpec.getMax());
                } else {
                    sender.send(driver, paramSpec.getMin());
                    paramModel.set(paramSpec.getMin());
                }
            }
        });
    }

    public XmlDriverSpec getXmlDriverSpec() {
        return xmlDriverSpec;
    }

    void setTooltip(final Control node, DoubleProperty observable, int base) {
        final Tooltip tooltip = new Tooltip();
        node.setTooltip(tooltip);
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        WidgetDataFormat widgetDataFormat = new WidgetDataFormat(base);
        tooltip.textProperty().bindBidirectional(observable, widgetDataFormat);

        node.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> arg0,
                    Boolean arg1, Boolean arg2) {
                if (arg2) {
                    Point2D p =
                            node.localToScene(
                                    (node.getLayoutBounds().getMaxX() - node
                                            .getLayoutBounds().getMinX()) / 2,
                                    node.getLayoutBounds().getMaxY());
                    tooltip.show(node, p.getX(), p.getY() + 60);
                } else {
                    tooltip.hide();
                }
            }
        });
        node.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
            }
        });
        node.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
            }
        });
    }
}
