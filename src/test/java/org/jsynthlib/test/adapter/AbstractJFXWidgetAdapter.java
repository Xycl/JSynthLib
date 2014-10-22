package org.jsynthlib.test.adapter;

import java.util.ArrayList;
import java.util.Collections;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

import org.apache.log4j.Logger;
import org.jemmy.TimeoutExpiredException;
import org.jemmy.fx.SceneDock;
import org.jemmy.interfaces.Label;
import org.jemmy.lookup.Lookup;
import org.jemmy.lookup.LookupCriteria;
import org.jsynthlib.core.TitleFinder.FrameWrapper;
import org.jsynthlib.device.view.Envelope;

public abstract class AbstractJFXWidgetAdapter extends WidgetAdapter {

    private static final Logger LOG = Logger
            .getLogger(AbstractJFXWidgetAdapter.class);

    private final Control control;
    private final SceneDock sceneDock;

    public AbstractJFXWidgetAdapter(SceneDock scene, Control control) {
        this.control = control;
        this.sceneDock = scene;
        setEnabled(!control.isDisabled());
    }

    @Override
    public boolean isShowing() {
        return control.isVisible();
    }

    @Override
    public String getUniqueName(FrameWrapper frame) {
        return getUniqueNameAndDisplayWidget(control, sceneDock);
    }

    static String getUniqueNameAndDisplayWidget(Control control,
            SceneDock sceneDock) {
        try {
            ControlDisplayer controlDisplayer =
                    new ControlDisplayer(sceneDock, control);
            controlDisplayer.showWidget();
            return "/" + controlDisplayer.getPath()
                    + controlDisplayer.getName();
        } catch (TimeoutExpiredException e) {
            return control.getId();
        }
    }

    static class ControlDisplayer {
        private final ArrayList<String> titleList = new ArrayList<String>();
        private final Node control;
        private String name;
        private AnchorPane lastAnchorPane;

        public ControlDisplayer(SceneDock sceneDock, Node control) {
            this.control = control;
            if (control instanceof CheckBox) {
                CheckBox cb = (CheckBox) control;
                name = cb.getText();
            } else {
                final String labelId = "lbl" + control.getId();
                Lookup<Node> lookup =
                        sceneDock.asParent().lookup(new LookupCriteria<Node>() {

                            @Override
                            public boolean check(Node node) {
                                return labelId.equals(node.getId());
                            }
                        });
                if (control instanceof Envelope) {
                    name = "envelope";
                } else {
                    try {
                        Label label = lookup.as(Label.class);
                        name = label.text();
                    } catch (TimeoutExpiredException e) {
                        LOG.warn("Failed to find label " + labelId);
                        throw e;
                    }
                }
            }
        }

        public void showWidget() {
            Parent parent = control.getParent();
            showWidget(parent);
        }

        void showWidget(Parent parent) {
            LOG.info(parent.getClass().getName());
            if (parent instanceof TitledPane) {
                TitledPane pane = (TitledPane) parent;
                titleList.add(pane.textProperty().get());
            } else if (parent instanceof TabPane) {
                TabPane pane = (TabPane) parent;
                final SingleSelectionModel<Tab> selectionModel =
                        pane.getSelectionModel();
                ObservableList<Tab> tabs = pane.getTabs();
                for (final Tab tab : tabs) {
                    Node node = tab.contentProperty().get();
                    if (node.equals(lastAnchorPane)) {
                        titleList.add(tab.getText());
                        Platform.runLater(new Runnable() {

                            @Override
                            public void run() {
                                selectionModel.select(tab);
                            }
                        });

                        break;
                    }
                }
            } else if (parent instanceof AnchorPane) {
                lastAnchorPane = (AnchorPane) parent;
            }
            Parent grandParent = parent.getParent();
            if (grandParent != null) {
                showWidget(grandParent);
            }
        }

        public String getPath() {
            StringBuilder sb = new StringBuilder();
            Collections.reverse(titleList);
            for (String title : titleList) {
                sb.append(title.trim()).append("/");
            }
            return sb.toString().replaceAll("/+", "/");
        }

        public String getName() {
            return name;
        }
    }
}
