package org.jsynthlib.utils.ctrlr.builder;

import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.builder.method.MethodBuilder;
import org.jsynthlib.utils.ctrlr.builder.method.MidiReceivedMethodBuilder;

public interface CtrlrLuaManagerBuilder {

    void createLuaManager(PanelType panel);

    void addMethodGroup(String name);

    void addMethod(String name, String code);

    void addMethod(String groupName, MethodBuilder methodBuilder);

    MidiReceivedMethodBuilder getMidiReceivedBuilder();
}
