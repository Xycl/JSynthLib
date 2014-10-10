package org.jsynthlib.utils.editor;

import javax.swing.JPanel;

import org.jsynthlib.device.viewcontroller.AbstractDriverEditor;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

class JavaContainerFactory implements IPanelContainerFactory {
    private final JCodeModel codeModel;
    private final String packageName;
    private final String fileNamePrefix;

    public JavaContainerFactory(JCodeModel codeModel, String packageName, String fileNamePrefix) {
        this.codeModel = codeModel;
        this.packageName = packageName;
        this.fileNamePrefix = fileNamePrefix;
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.utils.editor.IPanelContainerFactory#createEditorClass()
     */
    @Override
    public PanelContainer createEditorClass()
            throws JClassAlreadyExistsException {
        String className = packageName + "." + fileNamePrefix + "Editor";

        JDefinedClass panelClass = codeModel._class(className);
        panelClass._extends(AbstractDriverEditor.class);
        panelClass.staticRef("// CHECKSTYLE:OFF");
//        JBlock init = panelClass.init();
//        init.directStatement("// CHECKSTYLE:OFF");
        JMethod constructorMethod = panelClass.constructor(JMod.PUBLIC);


        return new PanelContainer(panelClass, constructorMethod, null,
                JExpr._this(), fileNamePrefix);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.utils.editor.IPanelContainerFactory#generateGroupPanelClass(org.jsynthlib.utils.editor.PanelContainer, java.lang.String, java.lang.String)
     */
    @Override
    public PanelContainer generateGroupPanelClass(PanelContainer origin,
            String path, String name) throws JClassAlreadyExistsException {
        String javaName = getJavaName(path + "/" + fileNamePrefix + name + "Panel");
        String className = packageName + "." + javaName;

        JDefinedClass panelClass = codeModel._class(className);
        panelClass._extends(JPanel.class);
        JMethod constructorMethod = panelClass.constructor(JMod.PUBLIC);
        constructorMethod.body().invoke(JExpr._this(), "setName").arg(name);
        return new PanelContainer(panelClass, constructorMethod,
                origin, JExpr._this(), javaName);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.utils.editor.IPanelContainerFactory#generateGroupPanel(org.jsynthlib.utils.editor.PanelContainer, java.lang.String, java.lang.String)
     */
    @Override
    public PanelContainer generateGroupPanel(PanelContainer origin,
            String path, String name) {
        String javaName = getJavaName(path + "/" + fileNamePrefix + name + "Panel");
        JFieldVar newField =
                origin.getPanelClass().field(JMod.PRIVATE, JPanel.class,
                        javaName);

        JMethod currentMethod = origin.getCreateMethod();
        currentMethod.body().assign(JExpr._this().ref(newField),
                JExpr._new(codeModel._ref(JPanel.class)));
        currentMethod.body().invoke(newField, "setName").arg(name);
        return new PanelContainer(origin.getPanelClass(), currentMethod, origin,
                newField, javaName);
    }

    static String getJavaName(String name) {
        return name.replaceAll("[ /\\-]|(<[^>]+>)", "").trim();
    }

}
