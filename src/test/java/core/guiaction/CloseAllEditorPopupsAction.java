package core.guiaction;

import java.awt.Dialog;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JLabelFixture;

import core.IPopupHandler;
import core.PopupContainer;

public class CloseAllEditorPopupsAction extends AbstractGuiAction {

    private IPopupHandler handler;
    private ArrayList<PopupContainer> list;
    public CloseAllEditorPopupsAction(FrameFixture testFrame, final IPopupHandler handler) {
        super(testFrame);
        this.handler = handler;
        list = new ArrayList<PopupContainer>();
    }

    @Override
    public void perform() {
        
        try {
            DialogFixture dialog =
                    testFrame.dialog(new GenericTypeMatcher<Dialog>(
                            Dialog.class) {

                        @Override
                        protected boolean isMatching(Dialog component) {
                            return component.getTitle().contains(
                                    "Nova1 Patch Sender");
                        }
                    });
            dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

                @Override
                protected boolean isMatching(JButton component) {
                    return "Hide this".equals(component.getText());
                }
            }).click();

            list.add(getPopupContents(dialog));
            handler.onPopup(dialog.target.getTitle());

            dialog =
                    testFrame.dialog(new GenericTypeMatcher<Dialog>(
                            Dialog.class) {

                        @Override
                        protected boolean isMatching(Dialog component) {
                            return component.getTitle().contains(
                                    "Hide Nova1 Patch Sender");
                        }
                    });
            closeDialog(dialog.target.getTitle());

            list.add(getPopupContents(dialog));
            handler.onPopup(dialog.target.getTitle());
        } catch (Exception e) {
        }

        try {
            DialogFixture dialog =
                    testFrame.dialog(new GenericTypeMatcher<Dialog>(
                            Dialog.class) {

                        @Override
                        protected boolean isMatching(Dialog component) {
                            return component.getTitle().contains("Ensoniq ")
                                    && component.isShowing();
                        }
                    });
            closeDialog(dialog.target.getTitle());

            list.add(getPopupContents(dialog));
            handler.onPopup(dialog.target.getTitle());
        } catch (Exception e) {
        }
    }

    PopupContainer getPopupContents(DialogFixture dialog) {
        try {
            JLabelFixture label = dialog.label(new GenericTypeMatcher<JLabel>(JLabel.class) {

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
            PopupContainer popupContainer = new PopupContainer();
            popupContainer.setTitle(dialog.target.getTitle());
            popupContainer.setContents(label.target.getText());
            return popupContainer;
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return new PopupContainer();
        }
    }

    public ArrayList<PopupContainer> getList() {
        return list;
    }

}
