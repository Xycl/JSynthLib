package org.jsynthlib.utils.editor;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMethod;

class PanelContainer {
    private final JDefinedClass panelClass;
    private final JMethod createMethod;
    private final PanelContainer caller;
    private final JExpression field;
    private final String baseName;

    public PanelContainer(JDefinedClass panelClass, JMethod createMethod,
            PanelContainer caller, JExpression field, String baseName) {
        super();
        this.panelClass = panelClass;
        this.createMethod = createMethod;
        this.caller = caller;
        this.field = field;
        this.baseName = baseName;
    }

    public JDefinedClass getPanelClass() {
        return panelClass;
    }

    public JMethod getCreateMethod() {
        return createMethod;
    }

    public PanelContainer getCaller() {
        return caller;
    }

    public JExpression getField() {
        return field;
    }

    public String getName() {
        return EditorGenerator.getJavaName(baseName);
    }

    public String getBaseName() {
        return baseName;
    }
}
