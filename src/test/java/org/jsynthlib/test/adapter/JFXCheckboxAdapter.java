package org.jsynthlib.test.adapter;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;

import org.jemmy.fx.SceneDock;
import org.jsynthlib.core.valuesetter.IValueSetter;
import org.jsynthlib.xmldevice.CombinedIntPatchParam;
import org.jsynthlib.xmldevice.IntParamSpec;

public class JFXCheckboxAdapter extends AbstractJFXWidgetAdapter {

    private final CheckBox checkbox;

    JFXCheckboxAdapter(SceneDock scene, CheckBox checkbox, int max, int min) {
        super(scene, checkbox);
        this.checkbox = checkbox;
        setType(Type.CHECKBOX);
        setMax(max);
        setMin(min);
        if (checkbox.isSelected()) {
            setValue(max);
        } else {
            setValue(min);
        }
    }

    /**
     * @param scene
     * @param control
     */
    public JFXCheckboxAdapter(SceneDock scene, CheckBox checkbox,
            IntParamSpec xmlObject) {
        this(scene, checkbox, xmlObject.getMax(), xmlObject.getMin());
    }

    /**
     * @param sceneDock
     * @param checkBox2
     * @param param
     */
    public JFXCheckboxAdapter(SceneDock sceneDock, CheckBox checkBox2,
            CombinedIntPatchParam param) {
        this(sceneDock, checkBox2, param.getMax(), param.getMin());
    }

    /*
     * (non-Javadoc)
     * @see org.jsynthlib.test.adapter.WidgetAdapter#getValueSetter()
     */
    @Override
    public IValueSetter getValueSetter() {
        return new IValueSetter() {

            @Override
            public void setValue(final int value) {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        if (value == getMax()) {
                            checkbox.selectedProperty().set(true);
                        } else {
                            checkbox.selectedProperty().set(false);
                        }
                    }
                });
            }
        };
    }

}
