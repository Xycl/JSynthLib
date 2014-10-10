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
package org.jsynthlib.core;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.ColorFixture;
import org.fest.swing.fixture.ComponentFixture;
import org.fest.swing.fixture.ComponentFixtureExtension;
import org.fest.swing.fixture.ContainerFixture;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FontFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JCheckBoxFixture;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JInternalFrameFixture;
import org.fest.swing.fixture.JLabelFixture;
import org.fest.swing.fixture.JListFixture;
import org.fest.swing.fixture.JMenuItemFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JProgressBarFixture;
import org.fest.swing.fixture.JRadioButtonFixture;
import org.fest.swing.fixture.JScrollBarFixture;
import org.fest.swing.fixture.JScrollPaneFixture;
import org.fest.swing.fixture.JSliderFixture;
import org.fest.swing.fixture.JSpinnerFixture;
import org.fest.swing.fixture.JSplitPaneFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.fixture.JToggleButtonFixture;
import org.fest.swing.fixture.JToolBarFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.fest.swing.timing.Timeout;
import org.jsynthlib.core.viewcontroller.desktop.mdi.MDIFrameProxy;
import org.jsynthlib.core.viewcontroller.desktop.sdi.SDIFrameProxy;

public class TitleFinder {

    private final List<FrameWrapper> map;
    private final FrameFixture frameFixture;

    public static List<FrameWrapper> getWindowTitles(FrameFixture frame) {
        TitleFinder finder = new TitleFinder(frame);
        Container component = frame.component();
        finder.findTitlesRecursive(component);
        return finder.map;
    }

    TitleFinder(FrameFixture frameFixture) {
        this.frameFixture = frameFixture;
        map = new ArrayList<FrameWrapper>();
    }

    void findTitlesRecursive(Container component) {
        if (component instanceof MDIFrameProxy) {
            MDIFrameProxy frame = (MDIFrameProxy) component;
            JInternalFrameFixture internalFrameFixture =
                    new JInternalFrameFixture(frameFixture.robot, frame);
            FrameWrapper wrapper = new FrameWrapper(internalFrameFixture);
            map.add(wrapper);
        } else if (component instanceof SDIFrameProxy) {
            SDIFrameProxy frame = (SDIFrameProxy) component;
            FrameWrapper wrapper =
                    new FrameWrapper(
                            new FrameFixture(frameFixture.robot, frame));
            map.add(wrapper);
        } else {
            Component[] components = component.getComponents();
            for (Component child : components) {
                if (child instanceof Container) {
                    findTitlesRecursive((Container) child);
                }
            }
        }
    }

    public static class FrameWrapper {
        private final ContainerFixture<?> wrappedContainer;

        public FrameWrapper(ContainerFixture<?> wrappedContainer) {
            super();
            this.wrappedContainer = wrappedContainer;
        }

        public Robot getRobot() {
            return wrappedContainer.robot;
        }

        public JButtonFixture button() {
            return wrappedContainer.button();
        }

        public JButtonFixture button(
                GenericTypeMatcher<? extends JButton> matcher) {
            return wrappedContainer.button(matcher);
        }

        public JButtonFixture button(String name) {
            return wrappedContainer.button(name);
        }

        public JCheckBoxFixture checkBox() {
            return wrappedContainer.checkBox();
        }

        public JCheckBoxFixture checkBox(
                GenericTypeMatcher<? extends JCheckBox> matcher) {
            return wrappedContainer.checkBox(matcher);
        }

        public JCheckBoxFixture checkBox(String name) {
            return wrappedContainer.checkBox(name);
        }

        public JComboBoxFixture comboBox() {
            return wrappedContainer.comboBox();
        }

        public JComboBoxFixture comboBox(
                GenericTypeMatcher<? extends JComboBox> matcher) {
            return wrappedContainer.comboBox(matcher);
        }

        public JComboBoxFixture comboBox(String name) {
            return wrappedContainer.comboBox(name);
        }

        public DialogFixture dialog() {
            return wrappedContainer.dialog();
        }

        public DialogFixture dialog(Timeout timeout) {
            return wrappedContainer.dialog(timeout);
        }

        public DialogFixture dialog(GenericTypeMatcher<? extends Dialog> matcher) {
            return wrappedContainer.dialog(matcher);
        }

        public DialogFixture dialog(
                GenericTypeMatcher<? extends Dialog> matcher, Timeout timeout) {
            return wrappedContainer.dialog(matcher, timeout);
        }

        public DialogFixture dialog(String name) {
            return wrappedContainer.dialog(name);
        }

        public DialogFixture dialog(String name, Timeout timeout) {
            return wrappedContainer.dialog(name, timeout);
        }

        public final ColorFixture background() {
            return wrappedContainer.background();
        }

        public JFileChooserFixture fileChooser() {
            return wrappedContainer.fileChooser();
        }

        public JFileChooserFixture fileChooser(
                GenericTypeMatcher<? extends JFileChooser> matcher) {
            return wrappedContainer.fileChooser(matcher);
        }

        public JFileChooserFixture fileChooser(
                GenericTypeMatcher<? extends JFileChooser> matcher,
                Timeout timeout) {
            return wrappedContainer.fileChooser(matcher, timeout);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof FrameWrapper) {
                FrameWrapper f2 = (FrameWrapper) obj;
                return wrappedContainer.component().equals(f2.component());
            } else {
                return false;
            }
        }

        public final FontFixture font() {
            return wrappedContainer.font();
        }

        public final ColorFixture foreground() {
            return wrappedContainer.foreground();
        }

        public JFileChooserFixture fileChooser(Timeout timeout) {
            return wrappedContainer.fileChooser(timeout);
        }

        public JFileChooserFixture fileChooser(String name) {
            return wrappedContainer.fileChooser(name);
        }

        public JFileChooserFixture fileChooser(String name, Timeout timeout) {
            return wrappedContainer.fileChooser(name, timeout);
        }

        @Override
        public int hashCode() {
            return wrappedContainer.component().hashCode();
        }

        public JLabelFixture label() {
            return wrappedContainer.label();
        }

        public JLabelFixture label(GenericTypeMatcher<? extends JLabel> matcher) {
            return wrappedContainer.label(matcher);
        }

        public JLabelFixture label(String name) {
            return wrappedContainer.label(name);
        }

        public JListFixture list() {
            return wrappedContainer.list();
        }

        public JListFixture list(GenericTypeMatcher<? extends JList> matcher) {
            return wrappedContainer.list(matcher);
        }

        public JListFixture list(String name) {
            return wrappedContainer.list(name);
        }

        public JMenuItemFixture menuItemWithPath(String... path) {
            return wrappedContainer.menuItemWithPath(path);
        }

        public JMenuItemFixture menuItem(String name) {
            return wrappedContainer.menuItem(name);
        }

        public JMenuItemFixture menuItem(
                GenericTypeMatcher<? extends JMenuItem> matcher) {
            return wrappedContainer.menuItem(matcher);
        }

        public JOptionPaneFixture optionPane() {
            return wrappedContainer.optionPane();
        }

        public JOptionPaneFixture optionPane(Timeout timeout) {
            return wrappedContainer.optionPane(timeout);
        }

        public JPanelFixture panel() {
            return wrappedContainer.panel();
        }

        public JPanelFixture panel(GenericTypeMatcher<? extends JPanel> matcher) {
            return wrappedContainer.panel(matcher);
        }

        public JPanelFixture panel(String name) {
            return wrappedContainer.panel(name);
        }

        public JProgressBarFixture progressBar() {
            return wrappedContainer.progressBar();
        }

        public JProgressBarFixture progressBar(
                GenericTypeMatcher<? extends JProgressBar> matcher) {
            return wrappedContainer.progressBar(matcher);
        }

        public JProgressBarFixture progressBar(String name) {
            return wrappedContainer.progressBar(name);
        }

        public JRadioButtonFixture radioButton() {
            return wrappedContainer.radioButton();
        }

        public JRadioButtonFixture radioButton(
                GenericTypeMatcher<? extends JRadioButton> matcher) {
            return wrappedContainer.radioButton(matcher);
        }

        public JRadioButtonFixture radioButton(String name) {
            return wrappedContainer.radioButton(name);
        }

        public JScrollBarFixture scrollBar() {
            return wrappedContainer.scrollBar();
        }

        public JScrollBarFixture scrollBar(
                GenericTypeMatcher<? extends JScrollBar> matcher) {
            return wrappedContainer.scrollBar(matcher);
        }

        public JScrollBarFixture scrollBar(String name) {
            return wrappedContainer.scrollBar(name);
        }

        public JScrollPaneFixture scrollPane() {
            return wrappedContainer.scrollPane();
        }

        public JScrollPaneFixture scrollPane(
                GenericTypeMatcher<? extends JScrollPane> matcher) {
            return wrappedContainer.scrollPane(matcher);
        }

        public JScrollPaneFixture scrollPane(String name) {
            return wrappedContainer.scrollPane(name);
        }

        public JSliderFixture slider() {
            return wrappedContainer.slider();
        }

        public JSliderFixture slider(
                GenericTypeMatcher<? extends JSlider> matcher) {
            return wrappedContainer.slider(matcher);
        }

        public JSliderFixture slider(String name) {
            return wrappedContainer.slider(name);
        }

        public JSpinnerFixture spinner() {
            return wrappedContainer.spinner();
        }

        public JSpinnerFixture spinner(
                GenericTypeMatcher<? extends JSpinner> matcher) {
            return wrappedContainer.spinner(matcher);
        }

        public JSpinnerFixture spinner(String name) {
            return wrappedContainer.spinner(name);
        }

        public JSplitPaneFixture splitPane() {
            return wrappedContainer.splitPane();
        }

        public JSplitPaneFixture splitPane(
                GenericTypeMatcher<? extends JSplitPane> matcher) {
            return wrappedContainer.splitPane(matcher);
        }

        public JSplitPaneFixture splitPane(String name) {
            return wrappedContainer.splitPane(name);
        }

        public JTabbedPaneFixture tabbedPane() {
            return wrappedContainer.tabbedPane();
        }

        public JTabbedPaneFixture tabbedPane(
                GenericTypeMatcher<? extends JTabbedPane> matcher) {
            return wrappedContainer.tabbedPane(matcher);
        }

        public JTabbedPaneFixture tabbedPane(String name) {
            return wrappedContainer.tabbedPane(name);
        }

        public JTableFixture table() {
            return wrappedContainer.table();
        }

        public JTableFixture table(GenericTypeMatcher<? extends JTable> matcher) {
            return wrappedContainer.table(matcher);
        }

        public JTableFixture table(String name) {
            return wrappedContainer.table(name);
        }

        public JTextComponentFixture textBox() {
            return wrappedContainer.textBox();
        }

        public JTextComponentFixture textBox(
                GenericTypeMatcher<? extends JTextComponent> matcher) {
            return wrappedContainer.textBox(matcher);
        }

        public JTextComponentFixture textBox(String name) {
            return wrappedContainer.textBox(name);
        }

        public JToggleButtonFixture toggleButton() {
            return wrappedContainer.toggleButton();
        }

        public JToggleButtonFixture toggleButton(
                GenericTypeMatcher<? extends JToggleButton> matcher) {
            return wrappedContainer.toggleButton(matcher);
        }

        public JToggleButtonFixture toggleButton(String name) {
            return wrappedContainer.toggleButton(name);
        }

        public JToolBarFixture toolBar() {
            return wrappedContainer.toolBar();
        }

        public JToolBarFixture toolBar(
                GenericTypeMatcher<? extends JToolBar> matcher) {
            return wrappedContainer.toolBar(matcher);
        }

        public JToolBarFixture toolBar(String name) {
            return wrappedContainer.toolBar(name);
        }

        public JTreeFixture tree() {
            return wrappedContainer.tree();
        }

        public JTreeFixture tree(GenericTypeMatcher<? extends JTree> matcher) {
            return wrappedContainer.tree(matcher);
        }

        public JTreeFixture tree(String name) {
            return wrappedContainer.tree(name);
        }

        public <C extends Component, F extends ComponentFixture<C>> F with(
                ComponentFixtureExtension<C, F> extension) {
            return wrappedContainer.with(extension);
        }

        public void focus() {
            if (wrappedContainer instanceof FrameFixture) {
                FrameFixture fixture = (FrameFixture) wrappedContainer;
                fixture.focus();
            } else if (wrappedContainer instanceof JInternalFrameFixture) {
                JInternalFrameFixture fixture =
                        (JInternalFrameFixture) wrappedContainer;
                fixture.focus();
            } else {
                throw new IllegalStateException("Illegal frame class: "
                        + wrappedContainer.getClass().getName());
            }
        }

        public void close() {
            if (wrappedContainer instanceof FrameFixture) {
                FrameFixture fixture = (FrameFixture) wrappedContainer;
                fixture.close();
            } else if (wrappedContainer instanceof JInternalFrameFixture) {
                JInternalFrameFixture fixture =
                        (JInternalFrameFixture) wrappedContainer;
                fixture.close();
            } else {
                throw new IllegalStateException("Illegal frame class: "
                        + wrappedContainer.getClass().getName());
            }
        }

        public void moveToFront() {
            if (wrappedContainer instanceof FrameFixture) {
                FrameFixture fixture = (FrameFixture) wrappedContainer;
                fixture.moveToFront();
            } else if (wrappedContainer instanceof JInternalFrameFixture) {
                JInternalFrameFixture fixture =
                        (JInternalFrameFixture) wrappedContainer;
                fixture.moveToFront();
            } else {
                throw new IllegalStateException("Illegal frame class: "
                        + wrappedContainer.getClass().getName());
            }
        }

        public void moveToBack() {
            if (wrappedContainer instanceof FrameFixture) {
                FrameFixture fixture = (FrameFixture) wrappedContainer;
                fixture.moveToBack();
            } else if (wrappedContainer instanceof JInternalFrameFixture) {
                JInternalFrameFixture fixture =
                        (JInternalFrameFixture) wrappedContainer;
                fixture.moveToBack();
            } else {
                throw new IllegalStateException("Illegal frame class: "
                        + wrappedContainer.getClass().getName());
            }
        }

        public Container component() {
            return wrappedContainer.component();
        }

        public String getTitle() {
            if (wrappedContainer instanceof FrameFixture) {
                FrameFixture fixture = (FrameFixture) wrappedContainer;
                return fixture.target.getTitle();
            } else if (wrappedContainer instanceof JInternalFrameFixture) {
                JInternalFrameFixture fixture =
                        (JInternalFrameFixture) wrappedContainer;
                return fixture.target.getTitle();
            } else {
                throw new IllegalStateException("Illegal frame class: "
                        + wrappedContainer.getClass().getName());
            }
        }

        @Override
        public String toString() {
            if (wrappedContainer instanceof FrameFixture) {
                FrameFixture fixture = (FrameFixture) wrappedContainer;
                return fixture.target.getTitle();
            } else if (wrappedContainer instanceof JInternalFrameFixture) {
                JInternalFrameFixture fixture =
                        (JInternalFrameFixture) wrappedContainer;
                return fixture.target.getTitle();
            } else {
                throw new IllegalStateException("Illegal frame class: "
                        + wrappedContainer.getClass().getName());
            }
        }

        public void maximize() {
            if (wrappedContainer instanceof FrameFixture) {
                FrameFixture fixture = (FrameFixture) wrappedContainer;
                fixture.maximize();
            } else if (wrappedContainer instanceof JInternalFrameFixture) {
                JInternalFrameFixture fixture =
                        (JInternalFrameFixture) wrappedContainer;
                fixture.maximize();
            } else {
                throw new IllegalStateException("Illegal frame class: "
                        + wrappedContainer.getClass().getName());
            }
        }
    }
}
