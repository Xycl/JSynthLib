package org.jsynthlib.utils.editor;

import com.sun.codemodel.JClassAlreadyExistsException;

interface IPanelContainerFactory {

    public abstract PanelContainer createEditorClass()
            throws JClassAlreadyExistsException;

    public abstract PanelContainer generateGroupPanelClass(
            PanelContainer origin, String path, String name)
            throws JClassAlreadyExistsException;

    public abstract PanelContainer generateGroupPanel(PanelContainer origin,
            String path, String name);

}