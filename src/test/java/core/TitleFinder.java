package core;

import java.awt.Component;
import java.awt.Container;
import java.util.HashMap;
import java.util.Map;

import org.fest.swing.fixture.ContainerFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JInternalFrameFixture;

import core.JSLFrame.JSLIFrame;
import core.JSLFrame.JSLJFrame;

@SuppressWarnings("rawtypes")
public class TitleFinder {

    private Map<String, ContainerFixture> map;
    private FrameFixture frameFixture;

    public static Map<String, ContainerFixture> getWindowTitles(
            FrameFixture frame) {
        TitleFinder finder = new TitleFinder(frame);
        Container component = frame.component();
        finder.findTitlesRecursive(component);
        return finder.map;
    }

    TitleFinder(FrameFixture frameFixture) {
        this.frameFixture = frameFixture;
        map = new HashMap<String, ContainerFixture>();
    }

    void findTitlesRecursive(Container component) {
        if (component instanceof JSLIFrame) {
            JSLIFrame frame = (JSLIFrame) component;
            map.put(frame.getTitle(), new JInternalFrameFixture(
                    frameFixture.robot, frame));
        } else if (component instanceof JSLJFrame) {
            JSLJFrame frame = (JSLJFrame) component;
            map.put(frame.getTitle(), new FrameFixture(frameFixture.robot,
                    frame));
        } else {
            Component[] components = component.getComponents();
            for (Component child : components) {
                if (child instanceof Container) {
                    findTitlesRecursive((Container) child);
                }
            }
        }
    }
}
