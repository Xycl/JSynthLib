package org.jsynthlib.device.viewcontroller.widgets;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.fest.swing.core.Robot;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.jsynthlib.core.valuesetter.IPatchNameValueSetter;
import org.jsynthlib.core.valuesetter.IValueSetter;

public class SwingPatchNameAdapter extends AbstractSwingWidgetAdapter {

    private final JTextComponentFixture fixture;

    public SwingPatchNameAdapter(PatchNameWidget widget, Robot robot) {
        super(widget);
        fixture =
                new JTextComponentFixture(robot, widget.name);
        setType(Type.PATCH_NAME);
    }

    @Override
    public IValueSetter getValueSetter() {
        return new SwingPatchNameValueSetter();
    }

    protected interface IClickable {
        void click();
    }
    
    public class SwingPatchNameValueSetter implements IPatchNameValueSetter {

        private final IClickable clickable;

        public SwingPatchNameValueSetter() {
            Container container = fixture.target.getParent();
            clickable = getClickableParentRecursive(container);
        }

        @Override
        public void setValue(int value) {
            fixture.deleteText();
            fixture.setText(VALUES[value]);
            clickable.click();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }


        protected IClickable getClickableParentRecursive(Container container) {
            if (container instanceof JFrame) {
                JFrame frame = (JFrame) container;
                final FrameFixture ff = new FrameFixture(frame);
                return new IClickable() {
                    @Override
                    public void click() {
                        ff.click();
                    }
                };
            } else if (container instanceof JTabbedPane) {
                JTabbedPane pane = (JTabbedPane) container;
                final JTabbedPaneFixture ff =
                        new JTabbedPaneFixture(fixture.robot, pane);
                return new IClickable() {
                    @Override
                    public void click() {
                        ff.click();
                    }
                };
            } else if (container instanceof JPanel) {
                JPanel pane = (JPanel) container;
                final JPanelFixture ff = new JPanelFixture(fixture.robot, pane);
                return new IClickable() {
                    @Override
                    public void click() {
                        ff.focus();
                    }
                };
            } else {
                return getClickableParentRecursive(container.getParent());
            }
        }

    }

}
