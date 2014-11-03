package org.jsynthlib.test.adapter;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.fest.swing.core.BasicComponentFinder;
import org.fest.swing.core.ComponentFinder;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.Robot;
import org.fest.swing.exception.ComponentLookupException;
import org.jemmy.fx.SceneDock;
import org.jemmy.lookup.Lookup;
import org.jemmy.lookup.LookupCriteria;
import org.jsynthlib.core.SysexWidgetFinder;
import org.jsynthlib.core.TitleFinder.FrameWrapper;
import org.jsynthlib.core.valuesetter.IValueSetter;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.core.viewcontroller.desktop.mdi.MDIFrameProxy;
import org.jsynthlib.device.view.Envelope;
import org.jsynthlib.device.view.Knob;
import org.jsynthlib.device.viewcontroller.XmlDriverEditorController;
import org.jsynthlib.device.viewcontroller.JSLDriverEditorFrame;
import org.jsynthlib.device.viewcontroller.widgets.AbstractSwingWidgetAdapter;
import org.jsynthlib.device.viewcontroller.widgets.CheckBoxWidget;
import org.jsynthlib.device.viewcontroller.widgets.ComboBoxWidget;
import org.jsynthlib.device.viewcontroller.widgets.EnvelopeWidget;
import org.jsynthlib.device.viewcontroller.widgets.KnobWidget;
import org.jsynthlib.device.viewcontroller.widgets.LabelWidget;
import org.jsynthlib.device.viewcontroller.widgets.PatchNameWidget;
import org.jsynthlib.device.viewcontroller.widgets.ScrollBarLookupWidget;
import org.jsynthlib.device.viewcontroller.widgets.ScrollBarWidget;
import org.jsynthlib.device.viewcontroller.widgets.SpinnerWidget;
import org.jsynthlib.device.viewcontroller.widgets.SwingCheckboxAdapter;
import org.jsynthlib.device.viewcontroller.widgets.SwingComboBoxAdapter;
import org.jsynthlib.device.viewcontroller.widgets.SwingEnvelopeAdapter;
import org.jsynthlib.device.viewcontroller.widgets.SwingIdComboBoxAdapter;
import org.jsynthlib.device.viewcontroller.widgets.SwingKnobAdapter;
import org.jsynthlib.device.viewcontroller.widgets.SwingMultiAdapter;
import org.jsynthlib.device.viewcontroller.widgets.SwingPatchNameAdapter;
import org.jsynthlib.device.viewcontroller.widgets.SwingScrollBarLookupAdapter;
import org.jsynthlib.device.viewcontroller.widgets.SwingScrollbarAdapter;
import org.jsynthlib.device.viewcontroller.widgets.SwingSpinnerAdapter;
import org.jsynthlib.device.viewcontroller.widgets.SwingTreeAdapter;
import org.jsynthlib.device.viewcontroller.widgets.SysexWidget;
import org.jsynthlib.device.viewcontroller.widgets.TreeWidget;
import org.jsynthlib.synthdrivers.QuasimidiQuasar.MultiWidget;
import org.jsynthlib.synthdrivers.YamahaUB99.IdComboWidget;
import org.jsynthlib.test.adapter.WidgetAdapter.Type;
import org.jsynthlib.xmldevice.CombinedGroup;
import org.jsynthlib.xmldevice.CombinedIntPatchParam;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParams;
import org.jsynthlib.xmldevice.XmlDriverDefinition;

public class WidgetAdapterProvider {

    private final Logger log = Logger.getLogger(getClass());

    public List<WidgetAdapter> findSysexWidgets(FrameWrapper patchEditor) {
        log.info("Fetching all sysexWidgets");
        ComponentFinder componentFinder =
                BasicComponentFinder.finderWithCurrentAwtHierarchy();

        JFXPanel jfxPanel = null;
        try {
            jfxPanel =
                    componentFinder.find(patchEditor.component(),
                            new GenericTypeMatcher<JFXPanel>(JFXPanel.class) {

                                @Override
                                protected boolean isMatching(JFXPanel arg0) {
                                    return true;
                                }
                            });
        } catch (ComponentLookupException e) {
        }

        ArrayList<WidgetAdapter> list = new ArrayList<WidgetAdapter>();
        if (jfxPanel == null) {
            List<SysexWidget> swingWidgets =
                    SysexWidgetFinder.findSysexWidgets(patchEditor);
            for (SysexWidget sysexWidget : swingWidgets) {
                WidgetAdapter adapter =
                        newSwingAdapter(sysexWidget, patchEditor);
                list.add(adapter);
            }
        } else {
            SceneDock sceneDock = new SceneDock();
            Lookup<Node> lookup =
                    sceneDock.asParent().lookup(new LookupCriteria<Node>() {

                        @Override
                        public boolean check(Node control) {
                            String id = control.getId();
                            return id != null && id.matches("[a-zA-Z0-9]{32}")
                                    && (control instanceof Control || control instanceof Envelope);
                        }
                    });
            int size = lookup.size();
            log.debug("Found " + size + " in lookup!");

            XmlDriverDefinition xmlDriverSpec = null;
            Container component = patchEditor.component();
            if (component instanceof MDIFrameProxy) {
                MDIFrameProxy proxy = (MDIFrameProxy) component;
                JSLFrame jslFrame = proxy.getJSLFrame();
                if (jslFrame instanceof JSLDriverEditorFrame) {
                    JSLDriverEditorFrame frame =
                            (JSLDriverEditorFrame) jslFrame;
                    JPanel editorPanel = frame.getEditorPanel();
                    if (editorPanel instanceof XmlDriverEditorController) {
                        XmlDriverEditorController fxmlEditor =
                                (XmlDriverEditorController) editorPanel;
                        xmlDriverSpec = fxmlEditor.getXmlDriverSpec();
                    }
                }
            }

            for (int i = 0; i < size; i++) {
                try {
                    Node control = lookup.get(i);
                    String id = control.getId();
                    XmlObject xmlObject = getXmlObjectById(xmlDriverSpec, id);
                    log.debug(control.getClass().getName());
                    WidgetAdapter adapter =
                            newJFXAdapter(control, sceneDock,
                                    xmlObject);
                    if (adapter != null) {
                        list.add(adapter);
                    }
                } catch (XmlException e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }

        return list;
    }

    XmlObject getXmlObjectById(XmlDriverDefinition driverSpec, String id)
            throws XmlException {
        StringBuilder sb = new StringBuilder();
        sb.append("declare namespace jsl='http://www.jsynthlib.org/xmldevice';");
        sb.append("//*[jsl:uuid = '").append(id).append("']");
        XmlObject[] xmlObjects = driverSpec.execQuery(sb.toString());

        if (xmlObjects.length == 1) {
            XmlObject xmlObject = xmlObjects[0];

            PatchParams patchParams =
                    PatchParams.Factory.parse(xmlObject.xmlText());
            IntParamSpec[] intParamSpecArray =
                    patchParams.getIntParamSpecArray();

            if (intParamSpecArray.length == 1) {
                return intParamSpecArray[0];
            } else {
                CombinedGroup combinedGroup =
                        CombinedGroup.Factory.parse(xmlObject.xmlText());
                CombinedIntPatchParam[] paramArray =
                        combinedGroup.getParamArray();
                if (paramArray.length == 1) {
                    return paramArray[0];
                }
            }

            return xmlObjects[0];
        } else {
            return null;
        }
    }

    WidgetAdapter newJFXAdapter(Node control, SceneDock sceneDock,
            XmlObject xmlObject) {
        if (control instanceof Knob) {
            Knob knob = (Knob) control;
            return new JFXKnobAdapter(sceneDock, knob);
        } else if (control instanceof Slider) {
            Slider slider = (Slider) control;
        } else if (control instanceof ComboBox) {
            ComboBox comboBox = (ComboBox) control;
            if (xmlObject instanceof IntParamSpec) {
                IntParamSpec param = (IntParamSpec) xmlObject;
                return new JFXComboBoxAdapter(sceneDock, comboBox, param);
            } else if (xmlObject instanceof CombinedIntPatchParam) {
                CombinedIntPatchParam param = (CombinedIntPatchParam) xmlObject;
                return new JFXComboBoxAdapter(sceneDock, comboBox, param);
            }
        } else if (control instanceof CheckBox) {
            CheckBox checkBox = (CheckBox) control;
            if (xmlObject instanceof IntParamSpec) {
                IntParamSpec param = (IntParamSpec) xmlObject;
                return new JFXCheckboxAdapter(sceneDock, checkBox, param);
            } else if (xmlObject instanceof CombinedIntPatchParam) {
                CombinedIntPatchParam param = (CombinedIntPatchParam) xmlObject;
                return new JFXCheckboxAdapter(sceneDock, checkBox, param);
            }
        } else if (control instanceof TextField) {
            TextField textField = (TextField) control;
            return  new JFXPatchNameAdapter(sceneDock, textField);
        } else if (control instanceof Envelope) {
            Envelope envelope = (Envelope) control;
            return new JFXEnvelopeAdapter(sceneDock, envelope);
        }
        return null;
    }

    WidgetAdapter newSwingAdapter(SysexWidget sysexWidget,
            FrameWrapper patchEditor) {
        Robot robot = patchEditor.getRobot();
        WidgetAdapter adapter = null;
        if (sysexWidget instanceof EnvelopeWidget) {
            adapter = new SwingEnvelopeAdapter((EnvelopeWidget) sysexWidget);
        } else if (sysexWidget instanceof CheckBoxWidget) {
            adapter =
                    new SwingCheckboxAdapter((CheckBoxWidget) sysexWidget,
                            robot);
        } else if (sysexWidget instanceof ComboBoxWidget) {
            adapter =
                    new SwingComboBoxAdapter((ComboBoxWidget) sysexWidget,
                            robot);
        } else if (sysexWidget instanceof IdComboWidget) {
            adapter =
                    new SwingIdComboBoxAdapter((IdComboWidget) sysexWidget,
                            robot);
        } else if (sysexWidget instanceof KnobWidget) {
            adapter = new SwingKnobAdapter((KnobWidget) sysexWidget);
        } else if (sysexWidget instanceof PatchNameWidget) {
            adapter =
                    new SwingPatchNameAdapter((PatchNameWidget) sysexWidget,
                            robot);
        } else if (sysexWidget instanceof ScrollBarWidget) {
            adapter =
                    new SwingScrollbarAdapter((ScrollBarWidget) sysexWidget,
                            robot);
        } else if (sysexWidget instanceof SpinnerWidget) {
            adapter =
                    new SwingSpinnerAdapter((SpinnerWidget) sysexWidget, robot);
        } else if (sysexWidget instanceof TreeWidget) {
            adapter = new SwingTreeAdapter((TreeWidget) sysexWidget, robot);
        } else if (sysexWidget instanceof ScrollBarLookupWidget) {
            adapter =
                    new SwingScrollBarLookupAdapter(
                            (ScrollBarLookupWidget) sysexWidget, robot);
        } else if (sysexWidget instanceof MultiWidget) {
            adapter = new SwingMultiAdapter((MultiWidget) sysexWidget, robot);
        } else if (sysexWidget instanceof LabelWidget) {
            adapter = new AbstractSwingWidgetAdapter(sysexWidget) {

                @Override
                public IValueSetter getValueSetter() {
                    // TODO Auto-generated method stub
                    return null;
                }
            };
            adapter.setType(Type.LABEL);
        }
        return adapter;
    }
}
