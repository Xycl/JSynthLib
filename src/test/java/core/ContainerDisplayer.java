package core;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTableFixture;

import core.JSLFrame.JSLFrameProxy;

public class ContainerDisplayer {

    private static final Logger LOG = Logger
            .getLogger(ContainerDisplayer.class);

    public static String showContainerAndGetNameRecursive(Robot robot,
            JComponent component) {
        ContainerDisplayer containerDisplayer = new ContainerDisplayer(robot);
        containerDisplayer.parseParentsRecursive(component);
        if (!component.isVisible()) {
            containerDisplayer.selectParents();
        }
        return containerDisplayer.title;
    }

    public static String showTableAndGetNameRecursive(Robot robot,
            SysexWidget sysexWidget, JPanel jPanel) {
        ContainerDisplayer containerDisplayer = new ContainerDisplayer(robot);
        JComponent component = containerDisplayer.parseTableRecursive(sysexWidget, jPanel);
        return showContainerAndGetNameRecursive(robot, component);
    }

    JComponent parseTableRecursive(final SysexWidget sysexWidget, JPanel jPanel) {
        JPanelFixture panelFixture = new JPanelFixture(robot, jPanel);
        JTableFixture table =
                panelFixture
                        .table(new GenericTypeMatcher<JTable>(JTable.class) {

                            @Override
                            protected boolean isMatching(JTable component) {
                                TableModel model = component.getModel();
                                int columnCount = model.getColumnCount();
                                int rowCount = model.getRowCount();
                                for (int i = 0; i < columnCount; i++) {
                                    for (int j = 0; j < rowCount; j++) {
                                        TableCellRenderer cellRenderer =
                                                component.getCellRenderer(j, i);
                                        Component rendererComponent =
                                                cellRenderer
                                                        .getTableCellRendererComponent(
                                                                component,
                                                                null, false,
                                                                false, j, i);
                                        if (rendererComponent instanceof SysexWidget
                                                && sysexWidget
                                                        .equals(rendererComponent)) {
                                            return true;
                                        }
                                    }
                                }
                                return false;
                            }
                        });
        return (JComponent) table.target.getParent();
    }

    interface ISelectable {
        void select();
    }

    private List<ISelectable> selectables;
    private String title;
    private Robot robot;

    ContainerDisplayer(Robot robot) {
        selectables = new ArrayList<ISelectable>();
        this.robot = robot;
    }

    void parseParentsRecursive(JComponent component) {
        if (component instanceof JSLFrameProxy) {
            setTitle("");
            return;
        }
        if (component == null) {
            setTitle("");
            return;
        }
        Border border = component.getBorder();
        if (border != null && border instanceof TitledBorder) {
            if (!component.isVisible()) {
                JComponent parent = (JComponent) component.getParent();
                if (parent instanceof JTabbedPane) {
                    JTabbedPane tabbedPane = (JTabbedPane) parent;
                    final int indexOfComponent =
                            tabbedPane.indexOfComponent(component);
                    final JTabbedPaneFixture fixture =
                            new JTabbedPaneFixture(robot, tabbedPane);
                    LOG.info("Adding tabbedpane: "
                            + getTabbedPaneString(tabbedPane));
                    selectables.add(new ISelectable() {

                        @Override
                        public void select() {
                            fixture.selectTab(indexOfComponent);
                        }
                    });
                }
                parseParentsRecursive(parent);
            }

            TitledBorder titledBorder = (TitledBorder) border;
            setTitle(titledBorder.getTitle());
        } else {
            Container parent = component.getParent();
            if (parent instanceof JTabbedPane) {
                JTabbedPane tabbedPane = (JTabbedPane) parent;
                final int indexOfComponent =
                        tabbedPane.indexOfComponent(component);
                final JTabbedPaneFixture fixture =
                        new JTabbedPaneFixture(robot, tabbedPane);
                LOG.info("Adding tabbedpane: "
                        + getTabbedPaneString(tabbedPane));
                selectables.add(new ISelectable() {

                    @Override
                    public void select() {
                        fixture.selectTab(indexOfComponent);
                    }
                });
                setTitle(tabbedPane.getTitleAt(indexOfComponent));
            }
            parseParentsRecursive((JComponent) parent);
        }
    }

    void setTitle(String title) {
        if (this.title == null) {
            this.title = title;
        }
    }

    void selectParents() {
        Collections.reverse(selectables);
        for (ISelectable selectable : selectables) {
            selectable.select();
        }
    }

    String getTabbedPaneString(JTabbedPane tabbedPane) {
        StringBuilder sb = new StringBuilder();
        int tabCount = tabbedPane.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            sb.append(tabbedPane.getTitleAt(i)).append(", ");
        }
        return sb.toString();
    }
}
