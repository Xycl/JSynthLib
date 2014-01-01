package core;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JLabelFixture;

import core.guiaction.AbstractGuiAction.IPopupListener;

public class PopupListener implements IPopupListener {

    private List<PopupContainer> popups;

    public PopupListener() {
        super();
        this.popups = new ArrayList<PopupContainer>();
    }

    @Override
    public void onPopupDetected(DialogFixture dialog) {
        PopupContainer container = new PopupContainer();
        container.setTitle(dialog.target.getTitle());
        JLabelFixture label;
        try {
            label = dialog.label(new GenericTypeMatcher<JLabel>(JLabel.class) {

                private boolean first = true;

                @Override
                protected boolean isMatching(JLabel arg0) {
                    boolean retval = first;
                    if (first) {
                        first = false;
                    }
                    return retval;
                }
            });
            container.setContents(label.target.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
        popups.add(container);
    }

    public List<PopupContainer> getPopups() {
        return popups;
    }
}
