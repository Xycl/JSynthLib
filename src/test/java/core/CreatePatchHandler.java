package core;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlObject;
import org.fest.swing.data.TableCell;
import org.fest.swing.data.TableCell.TableCellBuilder;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTableFixture;
import org.jsynthlib.gui.widgets.AbstractPatchHandler;
import org.jsynthlib.patch.LoadedBank;
import org.jsynthlib.patch.LoadedParam;
import org.jsynthlib.patch.PatchFile;
import org.jsynthlib.patch.PatchFilesDocument;
import org.jsynthlib.patch.PatchFilesDocument.PatchFiles;

public class CreatePatchHandler extends AbstractPatchHandler {

    private PatchFilesDocument patchFilesDocument;
    private PatchFiles patchFiles;

    public CreatePatchHandler(File outputFile, FrameFixture testFrame) {
        super(outputFile, testFrame);
        patchFilesDocument = PatchFilesDocument.Factory.newInstance();
        patchFiles = patchFilesDocument.addNewPatchFiles();
    }

    @Override
    public void handleDocument(String manufacturer, String deviceName) {
        patchFiles.setManufacturer(manufacturer);
        patchFiles.setName(deviceName);
    }

    @Override
    protected PatchFile handleDriverFile(String driverName, File syxFile) {
        PatchFile patchFile = patchFiles.addNewPatchFile();
        patchFile.setDriverName(driverName);
        patchFile.setFileName(syxFile.getName());
        return patchFile;
    }

    @Override
    public void saveDocument() throws IOException {
        patchFilesDocument.save(outputFile);
    }

    @Override
    public void handlePatchRow(JTableFixture table, PatchFile patchFile) {
        String[][] contents = table.contents();
        TableCellBuilder cellBuilder = TableCell.row(contents.length - 1);
        String synth = table.cell(cellBuilder.column(GuiHandler.SYNTH)).value();
        patchFile.setSynth(synth);

        String type = table.cell(cellBuilder.column(GuiHandler.TYPE)).value();
        patchFile.setType(type);

        String patchName =
                table.cell(cellBuilder.column(GuiHandler.PATCH_NAME)).value();
        patchFile.setName(patchName);
    }

    @Override
    protected LoadedBank handleLoadedBank(PatchFile patchFile, int index, String value) {
        LoadedBank loadedBank = patchFile.addNewLoadedBanks();
        loadedBank.setIndex(index);
        loadedBank.setName(value);
        return loadedBank;
    }

    @Override
    protected void handleParamValue(XmlObject parent, int index, String value) {
        LoadedParam loadedParam = null;
        if (parent instanceof PatchFile) {
            PatchFile patchFile = (PatchFile) parent;
            loadedParam = patchFile.addNewLoadedParams();
        } else if (parent instanceof LoadedBank) {
            LoadedBank loadedBank = (LoadedBank) parent;
            loadedParam = loadedBank.addNewLoadedParams();
        }
        loadedParam.setIndex(index);
        loadedParam.setValue(value);
    }
}
