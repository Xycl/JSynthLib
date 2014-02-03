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
package core;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JScrollBarFixture;
import org.fest.swing.fixture.JScrollPaneFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTableFixture;
import org.jsynthlib.gui.desktop.JSLFrameProxy;
import org.jsynthlib.gui.widgets.SysexWidget;

import core.TitleFinder.FrameWrapper;

public class ContainerDisplayer {

    private static final Logger LOG = Logger
            .getLogger(ContainerDisplayer.class);

    public static String showContainerAndGetNameRecursive(FrameWrapper frame,
            JComponent component) {
        ContainerDisplayer containerDisplayer = new ContainerDisplayer(frame);
        containerDisplayer.parseParentsRecursive((JComponent) component
                .getParent());
        if (!component.isShowing()) {
            LOG.info("Selecting tabs...");
            containerDisplayer.selectParents();
        }
        if (component instanceof SysexWidget) {
            SysexWidget widget = (SysexWidget) component;
            containerDisplayer.scrollToSysexWidget(widget);
        }
        return containerDisplayer.getPath();
    }

    public static String showTableAndGetNameRecursive(FrameWrapper frame,
            SysexWidget sysexWidget) {
        ContainerDisplayer containerDisplayer = new ContainerDisplayer(frame);
        JComponent component =
                containerDisplayer.parseTableRecursive(sysexWidget);
        String nameRecursive = showContainerAndGetNameRecursive(frame, component);
        containerDisplayer.scrollToSysexWidget(sysexWidget);
        return nameRecursive;
    }

    interface ISelectable {
        void select();
    }

    private List<ISelectable> selectables;
    private List<String> titleList;
    private FrameWrapper frame;

    ContainerDisplayer(FrameWrapper frame) {
        this.frame = frame;
        selectables = new ArrayList<ISelectable>();
        titleList = new ArrayList<String>();
    }

    void parseParentsRecursive(JComponent component) {
        if (component instanceof JSLFrameProxy) {
            addTitle("");
            return;
        }
        if (component == null) {
            addTitle("");
            return;
        }
        Border border = component.getBorder();
        JComponent parent = (JComponent) component.getParent();
        if (border != null && border instanceof TitledBorder) {
            TitledBorder titledBorder = (TitledBorder) border;
            LOG.info("Adding border: " + titledBorder.getTitle());
            addTitle(titledBorder.getTitle());
        } 
        if (parent instanceof JTabbedPane) {
            JTabbedPane tabbedPane = (JTabbedPane) parent;
            final int indexOfComponent = tabbedPane.indexOfComponent(component);
            final JTabbedPaneFixture fixture =
                    new JTabbedPaneFixture(frame.getRobot(), tabbedPane);

            selectables.add(new ISelectable() {
                @Override
                public void select() {
                    scrollToTab(fixture);
                    LOG.info("Selecting tab " + indexOfComponent);
                    fixture.selectTab(indexOfComponent);
                    int selectedIndex = fixture.target.getSelectedIndex();
                    if (selectedIndex != indexOfComponent) {
                        LOG.warn("Could not select correct tab!");
                        GuiActionRunner.execute(new GuiQuery<Object>() {

                            @Override
                            protected Object executeInEDT() throws Throwable {
                                fixture.target.setSelectedIndex(indexOfComponent);
                                return null;
                            }
                        });
                    }
                }
            });
            LOG.info("Adding tabbedpane: "
                    + tabbedPane.getTitleAt(indexOfComponent));
            addTitle(tabbedPane.getTitleAt(indexOfComponent));
        }

        parseParentsRecursive(parent);
    }

    JComponent parseTableRecursive(final SysexWidget sysexWidget) {

        JTableFixture table =
                frame.table(new GenericTypeMatcher<JTable>(JTable.class) {

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
                                                        component, null, false,
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

    void scrollToTab(JTabbedPaneFixture fixture) {
        Rectangle rectangle = new Rectangle(fixture.target.getLocation());
        scrollToComponent(rectangle);
    }

    void scrollToSysexWidget(SysexWidget widget) {
        JPanelFixture panelFixture =
                new JPanelFixture(frame.getRobot(), widget);
        if (widget.isShowing()) {
            panelFixture.click();
            widget.validate();
            LOG.info("Showing " + widget.isShowing());
            Rectangle rectangle =
                    new Rectangle(widget.getLocation(), widget
                            .getSize());
            scrollToComponent(rectangle);
        } else {
            LOG.warn("Widgte " + widget.toString() + " is not visible!!");
        }
    }

    void scrollToComponent(final Rectangle rectangle) {
        final JScrollPaneFixture scrollPaneFixture =
                frame.scrollPane(new GenericTypeMatcher<JScrollPane>(
                        JScrollPane.class) {

                    @Override
                    protected boolean isMatching(JScrollPane component) {
                        return component.getParent() != null
                                && "null.contentPane".equals(component
                                        .getParent().getName());
                    }
                });
        Rectangle visibleRect = scrollPaneFixture.target.getVisibleRect();
        if (visibleRect.contains(rectangle)) {
            LOG.info("Component at " + rectangle.toString() + " already visibile in frame..");
            return;
        }

        final JScrollBarFixture verticalScrollBar =
                scrollPaneFixture.verticalScrollBar();
        resetScrollbar(verticalScrollBar);

        final JScrollBarFixture horizontalScrollBar =
                scrollPaneFixture.horizontalScrollBar();
        resetScrollbar(horizontalScrollBar);

        GuiActionRunner.execute(new GuiQuery<Object>() {

            @Override
            protected Object executeInEDT() throws Throwable {
                JViewport viewport = scrollPaneFixture.target.getViewport();
                viewport.scrollRectToVisible(rectangle);
                return null;
            }
        });
    }

    void resetScrollbar(JScrollBarFixture scrollbar) {
        if (scrollbar != null && scrollbar.target.isEnabled()
                && scrollbar.target.isShowing()) {
            scrollbar.scrollToMinimum();
        }
    }

    void addTitle(String title) {
        titleList.add(title);
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

    String getPath() {
        StringBuilder sb = new StringBuilder();
        Collections.reverse(titleList);
        for (String title : titleList) {
            sb.append(title.trim()).append("/");
        }
        return sb.toString().replaceAll("/+", "/");
    }
}
