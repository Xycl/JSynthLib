package org.jsynthlib.jsynthlib.xml.editor;

import javax.swing.JPanel;

import org.jsynthlib.gui.PatchEditorFrame;
import org.jsynthlib.jsynthlib.xml.XMLPatch;

public class XMLEditor extends PatchEditorFrame {

    public XMLEditor(EditorDescription desc, XMLPatch patch) throws Exception {
        super("XML Editor", patch, (JPanel) desc.loadRoot(patch));
    }

}
