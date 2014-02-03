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
import java.awt.Container;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsynthlib.gui.widgets.SysexWidget;

import core.TitleFinder.FrameWrapper;

public class SysexWidgetFinder {
    private List<SysexWidget> list;
    private Map<Container, List<SysexWidget>> widgetMap;

    public static List<SysexWidget> findSysexWidgets(FrameWrapper frame) {
        SysexWidgetFinder finder = new SysexWidgetFinder();
        finder.findSysexWidgetsRecursive(frame.component());
        finder.widgetMap.put(frame.component(), finder.list);
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
