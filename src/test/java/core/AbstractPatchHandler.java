package core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTableFixture;
import org.jsynthlib.patch.LoadedBank;
import org.jsynthlib.patch.PatchFile;

import synthdrivers.QuasimidiQuasar.MultiWidget;
import core.EnvelopeWidget.Node;
import core.SysexWidget.IParamModel;
import core.TitleFinder.FrameWrapper;
import core.guiaction.AbstractGuiAction.IPopupListener;

public abstract class AbstractPatchHandler {

    protected final Logger log = Logger.getLogger(getClass());

    public static final String DISABLED_VALUE = "DISABLED";

    protected final FrameFixture testFrame;
    protected final File outputFile;

    private GuiHandler guiHandler;

    public AbstractPatchHandler(File outputFile, FrameFixture testFrame) {
        this.testFrame = testFrame;
        this.outputFile = outputFile;
        guiHandler = new GuiHandler(testFrame);
    }

    public abstract void handleDocument(String manufacturer, String deviceName);

    public PatchFile handleDriver(String driverName) {
        File syxFile =
                new File(outputFile.getParentFile(), driverName + ".syx");
        if (syxFile.exists()) {
            return handleDriverFile(driverName, syxFile);
        } else {
            return null;
        }
    }

    protected abstract PatchFile handleDriverFile(String driverName,
            File syxFile);

    public abstract void handlePatchRow(JTableFixture table, PatchFile patchFile);

    public abstract void saveDocument() throws IOException;

    protected abstract LoadedBank handleLoadedBank(PatchFile patchFile,
            int index, String value);

    public void handleBankEditor(JTableFixture table, PatchFile patchFile) {
        String[][] contents = table.contents();
        int index = 0;
        for (int i = 0; i < contents.length; i++) {
            for (int j = 0; j < contents[0].length; j++) {
                LoadedBank loadedBank =
                        handleLoadedBank(patchFile, index, contents[i][j]);

                FrameWrapper bankPatchEditor =
                        guiHandler.openPatchEditor(table, i, j,
                                new IPopupListener() {
                                    @Override
                                    public void onPopupDetected(
                                            DialogFixture dialog) {
                                    }
                                }, false);
                if (bankPatchEditor != null) {
                    List<SysexWidget> sysexWidgets2 =
                            SysexWidgetFinder.findSysexWidgets(bankPatchEditor);
                    for (int k = 0; k < sysexWidgets2.size(); k++) {
                        SysexWidget sysexWidget = sysexWidgets2.get(k);
                        handleParam(bankPatchEditor, sysexWidget, k, loadedBank);
                    }
                }
                
                guiHandler.closeFrame(bankPatchEditor, false);
                index++;
            }
        }
    }

    protected abstract void handleParamValue(XmlObject parent, int index,
            String value);

    public void handleParam(FrameWrapper frame, SysexWidget sysexWidget,
            int index, XmlObject parent) {
        if (sysexWidget instanceof EnvelopeWidget) {
            try {
                EnvelopeWidget envWidget = (EnvelopeWidget) sysexWidget;
                StringBuilder envelopeValue = new StringBuilder();
                Node[] nodes = envWidget.nodes;
                for (int i = 0; i < nodes.length; i++) {
                    Node node = nodes[i];

                    final IParamModel modelX = getNodeParamModel(node, true);
                    if (modelX != null) {
                        int valueX = modelX.get();
                        envelopeValue.append(valueX).append(";");
                    }

                    final IParamModel modelY = getNodeParamModel(node, false);
                    if (modelY != null) {
                        int valueY = modelY.get();
                        envelopeValue.append(valueY).append(";");
                    }
                }
                handleParamValue(parent, index, envelopeValue.toString());
            } catch (NoSuchFieldException e) {
                log.warn(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                log.warn(e.getMessage(), e);
            }
        } else if (sysexWidget instanceof LabelWidget) {
            // Skip...
        } else if (sysexWidget instanceof PatchNameWidget) {
            PatchNameWidget widget = (PatchNameWidget) sysexWidget;
            String text = widget.name.getText();
            handleParamValue(parent, index, text);
        } else if (sysexWidget instanceof MultiWidget) {
            // TODO: implement
        } else {
            int value = sysexWidget.getValue();
            handleParamValue(parent, index, Integer.toString(value));
        }

    }

    protected IParamModel getNodeParamModel(Node node, boolean x)
            throws NoSuchFieldException, IllegalAccessException {
        String fieldName = "pmodelY";
        if (x) {
            fieldName = "pmodelX";
        }
        return getField(fieldName, IParamModel.class, node);
    }

    @SuppressWarnings("unchecked")
    protected <T> T getField(String fieldName, Class<T> klass, Object object)
            throws IllegalAccessException, NoSuchFieldException {
        Field f = object.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return (T) f.get(object);
    }

}
