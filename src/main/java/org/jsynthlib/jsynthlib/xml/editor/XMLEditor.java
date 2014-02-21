package org.jsynthlib.jsynthlib.xml.editor;

import javax.swing.JPanel;

import org.jsynthlib.jsynthlib.xml.XMLPatch;
import org.jsynthlib.view.PatchEditorFrame;

public class XMLEditor extends PatchEditorFrame {

    public XMLEditor(EditorDescription desc, XMLPatch patch) throws Exception {
        super("XML Editor", patch, (JPanel) desc.loadRoot(patch));
    }

}
