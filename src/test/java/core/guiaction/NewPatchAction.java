package core.guiaction;

import javax.swing.JButton;
import javax.swing.JComboBox;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JComboBoxFixture;

public class NewPatchAction extends AbstractGuiAction {

    private IPopupListener listener;
    private Class<?> driverClass;
    private String deviceName;

    public NewPatchAction(FrameFixture testFrame, String deviceName,
            Class<?> driverClass, final IPopupListener listener) {
        super(testFrame);
        this.deviceName = deviceName;
        this.driverClass = driverClass;
        this.listener = listener;
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

        final JComboBox component = comboBox.component();
        log.info("Driver to select: " + driverClass);
        if (component.isEnabled()) {
            setComboBoxValue(comboBox, new ComboBoxMatcher() {
                @Override
                public boolean matches(Object item) {
                    Class<?> klass = item.getClass();
                    return klass.equals(driverClass);
                }
            });
        }

        log.info("Selected item: " + component.getSelectedItem());

        dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

            @Override
            protected boolean isMatching(JButton component) {
                return component.getText().contains("Create");
            }
        }).click();

        waitForPopups(listener);
    }
}
