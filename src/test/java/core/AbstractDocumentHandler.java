package core;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTableFixture;
import org.jsynthlib.driver.Xmldriver;
import org.jsynthlib.driver.Xmleditor;

import core.EnvelopeWidget.Node;
import core.SysexWidget.IParamModel;

public abstract class AbstractDocumentHandler {

    protected final Logger log = Logger.getLogger(getClass());
    protected final File outputFile;
    protected final FrameFixture testFrame;
    private final List<String> uniqueNames;
    protected GuiHandler guiHandler;

    public AbstractDocumentHandler(File outputFile, FrameFixture testFrame) {
        this.testFrame = testFrame;
        this.outputFile = outputFile;
        uniqueNames = new ArrayList<String>();
        guiHandler = new GuiHandler(testFrame);
    }

    public abstract Xmldriver handleDriver(String driverName);

    public abstract void handleDocument(String manufacturer, String deviceName);

    public abstract void handleStore(Xmldriver driver, JTableFixture table,
            Map<String, List<String>> bankMap);

    public abstract Xmleditor handleEditor(Xmldriver driver, boolean editable,
            String editorName, List<PopupContainer> popups);

    abstract void handleParam(Xmleditor editor, SysexWidget sysexWidget,
            JPanel jPanel);

    abstract void saveDocument() throws IOException;

    public abstract void handlePatch(Xmleditor editor, JTableFixture table);

    public String getUniqueName(SysexWidget sysexWidget, JPanel jPanel) {
        JComponent parent = (JComponent) sysexWidget.getParent();
        String containerName = null;
        if (parent == null) {
            containerName =
                    ContainerDisplayer.showTableAndGetNameRecursive(
                            testFrame.robot, sysexWidget, jPanel);
        } else {
            containerName =
                    ContainerDisplayer.showContainerAndGetNameRecursive(
                            testFrame.robot, parent);
        }

        String label = sysexWidget.getLabel();
        if (label == null || label.isEmpty()) {
            if (sysexWidget instanceof EnvelopeWidget) {
                label = "Envelope";
            } else {
                label = findNearestLabelRecursive(sysexWidget.getParent(), 0);
            }
        }
        if (label == null || label.isEmpty()) {
            log.warn("Label is not valid!");
        }
        String uniqueName = containerName + "/" + label;
        int index = 1;
        while (uniqueNames.contains(uniqueName)) {
            log.warn("Editor has duplicate widgets! " + uniqueName);
            if (index == 1) {
                uniqueName = uniqueName + "-id" + index;
            } else {
                uniqueName =
                        uniqueName.replace("-id" + (index - 1), "-id" + index);
            }
            index++;
        }
        uniqueNames.add(uniqueName);
        return uniqueName;
    }

    String findNearestLabelRecursive(Container parent, int index) {
        if (index >= 3) {
            log.warn("Could not find label!");
            return null;
        }
        Component[] components = parent.getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                String text = ((JLabel) component).getText();
                if (text != null && !text.isEmpty()) {
                    return text;
                }
            }
        }
        return findNearestLabelRecursive(parent.getParent(), index + 1);
    }

    protected interface IClickable {
        void click();
    }

    protected IClickable getClickableParentRecursive(Container container) {
        if (container instanceof JFrame) {
            JFrame frame = (JFrame) container;
            final FrameFixture fixture = new FrameFixture(frame);
            return new IClickable() {
                @Override
                public void click() {
                    fixture.click();
                }
            };
        } else if (container instanceof JTabbedPane) {
            JTabbedPane pane = (JTabbedPane) container;
            final JTabbedPaneFixture fixture =
                    new JTabbedPaneFixture(testFrame.robot, pane);
            return new IClickable() {
                @Override
                public void click() {
                    fixture.click();
                }
            };
        } else if (container instanceof JPanel) {
            JPanel pane = (JPanel) container;
            final JPanelFixture fixture =
                    new JPanelFixture(testFrame.robot, pane);
            return new IClickable() {
                @Override
                public void click() {
                    fixture.focus();
                }
            };
        } else {
            return getClickableParentRecursive(container.getParent());
        }
    }

    protected IParamModel getNodeParamModel(Node node, boolean x)
            throws NoSuchFieldException, IllegalAccessException {
        String fieldName = "pmodelY";
        if (x) {
            fieldName = "pmodelX";
        }
        return getField(fieldName, IParamModel.class, node);
    }

    protected String getNodeName(Node node, boolean x)
            throws NoSuchFieldException, IllegalAccessException {
        String fieldName = "nameY";
        if (x) {
            fieldName = "nameX";
        }
        return getField(fieldName, String.class, node);
    }

    @SuppressWarnings("unchecked")
    protected <T> T getField(String fieldName, Class<T> klass, Object object)
            throws IllegalAccessException, NoSuchFieldException {
        Field f = object.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return (T) f.get(object);
    }
}
