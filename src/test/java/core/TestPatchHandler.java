package core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.fest.swing.data.TableCell;
import org.fest.swing.data.TableCell.TableCellBuilder;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTableFixture;
import org.jsynthlib.patch.LoadedBank;
import org.jsynthlib.patch.LoadedParam;
import org.jsynthlib.patch.PatchFile;
import org.jsynthlib.patch.PatchFilesDocument;
import org.jsynthlib.patch.PatchFilesDocument.PatchFiles;

public class TestPatchHandler extends AbstractPatchHandler {

    private PatchFilesDocument patchDocument;
    private PatchFiles patchFiles;

    public TestPatchHandler(File outputFile, FrameFixture testFrame)
            throws XmlException, IOException {
        super(outputFile, testFrame);
        log.info("Using file " + outputFile.getAbsolutePath());
        patchDocument = PatchFilesDocument.Factory.parse(outputFile);
        patchFiles = patchDocument.getPatchFiles();
    }

    public String getManufacturer() {
        return patchFiles.getManufacturer();
    }

    public String getDeviceName() {
        return patchFiles.getName();
    }

    @Override
    public void handleDocument(String manufacturer, String deviceName) {
        assertEquals("Check manufacturer " + deviceName,
                patchFiles.getManufacturer(), manufacturer);
        assertEquals("Check device " + deviceName, patchFiles.getName(),
                deviceName);
    }

    @Override
    protected PatchFile handleDriverFile(String driverName, File syxFile) {
        PatchFile[] patchFileArray = patchFiles.getPatchFileArray();
        for (PatchFile driver : patchFileArray) {
            if (driver.getDriverName().equals(driverName)) {
                assertEquals(driver.getFileName(), syxFile.getName());
                return driver;
            }
        }
        fail("Could not find driver " + driverName);
        return null;
    }

    @Override
    public void saveDocument() {
    }

    @Override
    public void handlePatchRow(JTableFixture table, PatchFile patchFile) {
        String[][] contents = table.contents();
        TableCellBuilder cellBuilder = TableCell.row(contents.length - 1);
        String synth = table.cell(cellBuilder.column(GuiHandler.SYNTH)).value();
        assertEquals("Check synth name", patchFile.getSynth(), synth);

        String type = table.cell(cellBuilder.column(GuiHandler.TYPE)).value();
        assertEquals("Check synth type", patchFile.getType(), type);

        String patchName =
                table.cell(cellBuilder.column(GuiHandler.PATCH_NAME)).value();
        assertEquals("Check patch name", patchFile.getName(), patchName);        
    }

    @Override
    protected LoadedBank handleLoadedBank(PatchFile patchFile, int index, String value) {
        LoadedBank[] loadedBanks = patchFile.getLoadedBanksArray();
        assertTrue(loadedBanks.length > index);
        assertEquals("Check patch position", loadedBanks[index].getIndex(), index);
        assertEquals("Check patch name in bank", loadedBanks[index].getName(), value);
        return loadedBanks[index];
    }

    @Override
    protected void handleParamValue(XmlObject parent, int index, String value) {
        LoadedParam[] loadedParams = null;
        if (parent instanceof PatchFile) {
            PatchFile patchFile = (PatchFile) parent;
            loadedParams = patchFile.getLoadedParamsArray();
        } else if (parent instanceof LoadedBank) {
            LoadedBank loadedBank = (LoadedBank) parent;
            loadedParams = loadedBank.getLoadedParamsArray();
        }

        assertTrue(loadedParams.length > index);
        assertEquals("Check param position", loadedParams[index].getIndex(), index);
        assertEquals("Check param value", loadedParams[index].getValue(), value);
    }

}
