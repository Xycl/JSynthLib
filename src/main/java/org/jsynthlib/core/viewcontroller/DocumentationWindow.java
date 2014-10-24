package org.jsynthlib.core.viewcontroller;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.Utility;
import org.jsynthlib.core.viewcontroller.desktop.JSLDesktop;

public class DocumentationWindow extends JDialog {

    private static final long serialVersionUID = 1L;

    /**
     * Open a viewer window for a document.
     * @param contentType
     *            Content type of the document.
     * @param url
     *            URL of the document.
     * @see JEditorPane
     */
    public DocumentationWindow(String contentType, String url) {
        super(JSLDesktop.Factory.getRootFrame(), "JSynthLib Documentation Viewer", false);
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        final MyEditorPane jt = new MyEditorPane();
        jt.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if ((e.getDescription()).startsWith("#")) {
                        // Link of the same file
                        try {
                            jt.myScrollToReference(e.getDescription());
                        } catch (Exception e2) {
                            ErrorMsg.reportError("Error", e.getDescription()
                                    .toString(), e2);
                        }
                    } else {
                        // Link to other file
                        try {
                            jt.setPage(e.getURL());
                        } catch (UnknownHostException uhe) {
                            JOptionPane.showMessageDialog(getContentPane(),
                                    "Unknown Host \"" + e.getURL()
                                            + "\". Maybe you're not online.");
                        } catch (Exception e3) {
                            ErrorMsg.reportError("Error",
                                    e.getURL().toString(), e3);
                        }
                    }
                }
            }
        });
        JScrollPane pane = new JScrollPane();
        pane.getViewport().add(jt);

        getContentPane().add(pane, BorderLayout.CENTER);
        pane.getVerticalScrollBar().addAdjustmentListener(
                new AdjustmentListener() {
                    @Override
                    public void adjustmentValueChanged(AdjustmentEvent e) {
                        jt.repaint();
                    }
                });
        try {
            jt.setContentType(contentType);
            if (url.startsWith("http:")) {
                    jt.setPage(new java.net.URL(url));
            } else {
                jt.setPage(getClass().getResource("/" + url));
            }
        } catch (java.net.MalformedURLException e) {
            ErrorMsg.reportError("Error", "Wrong URL", e);
        } catch (java.io.IOException e) {
            ErrorMsg.reportError("Error", "Error opening documentation", e);
        }

        jt.setCaretPosition(0);
        jt.setEditable(false);

        JButton ok = new JButton("Close");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okPressed();
            }
        });
        getContentPane().add(ok, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(ok);
        setSize(500, 400);

        Utility.centerWindow(this);
    }

    void okPressed() {
        this.setVisible(false);
    }

    class MyEditorPane extends JEditorPane {
        public void myScrollToReference(String s) {
            super.scrollToReference(s.substring(1));
        }
    }
}
