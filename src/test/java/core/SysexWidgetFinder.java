package core;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SysexWidgetFinder {
    private List<SysexWidget> list;
    private Map<Container, List<SysexWidget>> widgetMap;

    public static List<SysexWidget> findSysexWidgets(Container container) {
        SysexWidgetFinder finder = new SysexWidgetFinder();
        finder.findSysexWidgetsRecursive(container);
        finder.widgetMap.put(container, finder.list);
        return finder.list;
    }

    SysexWidgetFinder() {
        list = new ArrayList<SysexWidget>();
        widgetMap = new HashMap<Container, List<SysexWidget>>();
    }

    void findSysexWidgetsRecursive(Container container) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            if (component instanceof SysexWidget) {
                SysexWidget widget = (SysexWidget) component;
                list.add(widget);
            } else if (component instanceof Container) {
                Container cont = (Container) component;
                findSysexWidgetsRecursive(cont);
            }
        }
    }
}
