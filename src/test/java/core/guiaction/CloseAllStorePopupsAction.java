package core.guiaction;

import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JLabel;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JLabelFixture;

import core.IPopupHandler;
import core.PopupContainer;

public class CloseAllStorePopupsAction extends AbstractGuiAction {

    private IPopupHandler handler;
    private ArrayList<PopupContainer> list;

    public CloseAllStorePopupsAction(FrameFixture testFrame,
            final IPopupHandler handler) {
        super(testFrame);
        this.handler = handler;
        list = new ArrayList<PopupContainer>();
    }

    @Override
    public void perform() {

        try {
            while (true) {
                DialogFixture dialog =
                        testFrame.dialog(new GenericTypeMatcher<JDialog>(
                                JDialog.class) {

                            @Override
                            protected boolean isMatching(JDialog component) {
                                return !component.getTitle().equals(
                                        "Store Sysex Data")
                                        && component.isShowing();
                            }
                        });

                log.info("Found patch store popup. Closing...");
                list.add(getPopupContents(dialog));
                handler.onPopup(dialog.target.getTitle());

                dialog.close();

                Thread.sleep(100);
            }
        } catch (Exception e) {
        }
    }

    PopupContainer getPopupContents(DialogFixture dialog) {
        try {
            JLabelFixture label =
                    dialog.label(new GenericTypeMatcher<JLabel>(JLabel.class) {

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
