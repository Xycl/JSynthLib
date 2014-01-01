package core.guiaction;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JComboBoxFixture;

public class GetDriversForClassAction extends AbstractGuiAction {

    private String deviceName;
    @SuppressWarnings("rawtypes")
    private ArrayList<Class> list;

    @SuppressWarnings("rawtypes")
    public GetDriversForClassAction(FrameFixture testFrame, String deviceName) {
        super(testFrame);
        this.deviceName = deviceName;
        list = new ArrayList<Class>();
    }

    @Override
    public void perform() {
        DialogFixture dialog = openNewPatchDialogAndSelectDevice(deviceName);

        JComboBoxFixture comboBox =
                dialog.comboBox(new GenericTypeMatcher<JComboBox>(
                        JComboBox.class) {

                    @Override
                    protected boolean isMatching(JComboBox component) {
                        return "driverCb".equals(component.getName());
                    }
                });

        JComboBox component = comboBox.component();
        for (int i = 0; i < component.getItemCount(); i++) {
            list.add(component.getItemAt(i).getClass());
        }

        dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

            @Override
            protected boolean isMatching(JButton component) {
                return component.getText().contains("Cancel");
            }
        }).click();
    }

    @SuppressWarnings("rawtypes")
    public ArrayList<Class> getList() {
        return list;
    }

}
