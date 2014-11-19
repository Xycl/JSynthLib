package org.jsynthlib.utils.ctrlr;

import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.impl.XmlDriverEditorParser;

public interface XmlDriverEditorParserFactory {
    XmlDriverEditorParser create(String className, PanelType panel);
}
