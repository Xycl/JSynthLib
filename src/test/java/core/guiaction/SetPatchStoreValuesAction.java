package core.guiaction;

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JComboBoxFixture;

public class SetPatchStoreValuesAction extends AbstractGuiAction {

    private String patchNum;
    private String bank;
    private DialogFixture dialogFixture;

    public SetPatchStoreValuesAction(FrameFixture testFrame,
            DialogFixture dialogFixture, final String bank,
            final String patchNum) {
        super(testFrame);
        this.dialogFixture = dialogFixture;
        this.bank = bank;
        this.patchNum = patchNum;
    }

    @Override
    public void perform() {
        if (bank != null) {
            JComboBoxFixture bankComboBox = dialogFixture.comboBox("bankCb");
            setComboBoxValue(bankComboBox, new ComboBoxMatcher() {

                @Override
                public boolean matches(Object item) {
                    return bank.equals(item.toString());
                }
            });
        }

        if (patchNum != null) {
            JComboBoxFixture patchNumComboBox =
                    dialogFixture.comboBox("patchNumCb");
            setComboBoxValue(patchNumComboBox, new ComboBoxMatcher() {

                @Override
                public boolean matches(Object item) {
                    return patchNum.equals(item.toString());
                }
            });
        }
    }

}
