package org.jsynthlib.utils.ctrlr.builder.method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.ctrlr.panel.LuaManagerMethodsType;
import org.ctrlr.panel.LuaManagerType;
import org.ctrlr.panel.LuaMethodGroupType;
import org.ctrlr.panel.LuaMethodType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.builder.CtrlrLuaManagerBuilder;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class CtrlrLuaManagerBuilderImpl implements CtrlrLuaManagerBuilder {

    private final MidiReceivedMethodBuilder midiReceivedBuilder;
    private final HashMap<String, List<LuaMethod>> luaMethods;

    @Inject
    public CtrlrLuaManagerBuilderImpl(
            Provider<MidiReceivedMethodBuilder> midiReceivedProvider) {
        luaMethods = new HashMap<String, List<LuaMethod>>();
        this.midiReceivedBuilder = midiReceivedProvider.get();
        addMethodGroup("");
    }

    @Override
    public void createLuaManager(PanelType panel) {
        addMethod("", midiReceivedBuilder);
        LuaManagerType luaManager = panel.addNewLuaManager();
        final LuaManagerMethodsType methods =
                luaManager.addNewLuaManagerMethods();
        for (Entry<String, List<LuaMethod>> entry : luaMethods.entrySet()) {
            if (entry.getKey().isEmpty()) {
                createGroup(new LuaMethodContainer() {

                    @Override
                    public LuaMethodType newMethod() {
                        return methods.addNewLuaMethod();
                    }
                }, entry.getValue());
            } else {
                final LuaMethodGroupType methodGroup =
                        methods.addNewLuaMethodGroup();
                methodGroup.setName(entry.getKey());
                methodGroup.setUuid(UUID.randomUUID().toString()
                        .replace("_", ""));
                createGroup(new LuaMethodContainer() {

                    @Override
                    public LuaMethodType newMethod() {
                        return methodGroup.addNewLuaMethod();
                    }
                }, entry.getValue());
            }
        }
    }

    void createGroup(LuaMethodContainer container, List<LuaMethod> methods) {
        for (LuaMethod luaMethod : methods) {
            LuaMethodType method = container.newMethod();
            method.setLuaMethodName(luaMethod.name);
            method.setLuaMethodCode(luaMethod.code);
            method.setUuid(UUID.randomUUID().toString().replace("_", ""));
            method.setLuaMethodLinkedProperty("");
            method.setLuaMethodSource(0);
            method.setLuaMethodValid(1);
        }
    }

    @Override
    public void addMethodGroup(String name) {
        ArrayList<LuaMethod> methodList = new ArrayList<LuaMethod>();
        luaMethods.put(name, methodList);
    }

    @Override
    public void addMethod(String name, String code) {
        addMethod("", new MethodBuilder(name) {

            @Override
            public String getCode() {
                return code;
            }
        });
    }

    @Override
    public void addMethod(String groupName, MethodBuilder methodBuilder) {
        if (!luaMethods.containsKey(groupName)) {
            throw new IllegalArgumentException("Could not find group "
                    + groupName);
        }
        List<LuaMethod> list = luaMethods.get(groupName);
        LuaMethod luaMethod = new LuaMethod();
        luaMethod.code = methodBuilder.getCode();
        luaMethod.name = methodBuilder.getName();
        list.add(luaMethod);
    }

    @Override
    public MidiReceivedMethodBuilder getMidiReceivedBuilder() {
        return midiReceivedBuilder;
    }

    static class LuaMethod {
        private String name;
        private String code;
    }

    interface LuaMethodContainer {
        LuaMethodType newMethod();
    }
}
