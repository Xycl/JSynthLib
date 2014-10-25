/*
 * Copyright 2014 Pascal Collberg
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package org.jsynthlib.device.viewcontroller.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JMenuItem;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.data.TableCell;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JCheckBoxFixture;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JMenuItemFixture;
import org.fest.swing.fixture.JSliderFixture;
import org.fest.swing.fixture.JTableFixture;
import org.jsynthlib.core.GuiHandler;
import org.jsynthlib.core.GuiHandler.ISearchHandler;
import org.jsynthlib.core.GuiHandler.SearchFields;
import org.jsynthlib.core.GuiHandler.SortFields;
import org.jsynthlib.core.OsUtil;
import org.jsynthlib.core.SysexWidgetFinder;
import org.jsynthlib.core.TitleFinder.FrameWrapper;
import org.jsynthlib.core.guiaction.AbstractGuiAction.IPopupListener;
import org.jsynthlib.core.valuesetter.SwingCheckBoxValueSetter;
import org.jsynthlib.core.valuesetter.SwingComboBoxValueSetter;
import org.jsynthlib.core.valuesetter.SwingKnobValueSetter;
import org.jsynthlib.core.valuesetter.SwingSliderValueSetter;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.patch.model.PatchEditFactory;
import org.jsynthlib.patch.model.impl.PatchEdit;
import org.jsynthlib.synthdrivers.RolandD10.RolandD10ToneDriver;
import org.jsynthlib.synthdrivers.RolandD50.D50SingleDriver;
import org.jsynthlib.synthdrivers.YamahaDX7.YamahaDX7VoiceBankDriver;
import org.jsynthlib.synthdrivers.YamahaDX7.YamahaDX7VoiceSingleDriver;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LibraryTest {

    private static final Logger LOG = Logger.getLogger(LibraryTest.class);

    private final IPopupListener listener = new IPopupListener() {

        @Override
        public void onPopupDetected(DialogFixture dialog) {
        }
    };

    private static File patchTestFolder;

    {
        Assume.assumeTrue(OsUtil.isWindows());
        FailOnThreadViolationRepaintManager.install();
        GuiActionRunner.execute(new GuiQuery<PatchEdit>() {
            @Override
            protected PatchEdit executeInEDT() {
                PatchEditFactory patchEditFactory =
                        JSynthLibInjector.getInstance(PatchEditFactory.class);
                return patchEditFactory
                        .newPatchEdit(new ArrayList<String>(), 2);
            }
        });
    }

    private FrameFixture testFrame;
    private GuiHandler guiHandler;

    @BeforeClass
    public static void setupBeforeClass() {
        patchTestFolder = new File("patchTestFolder");
        patchTestFolder.mkdir();
    }

    @AfterClass
    public static void tearDownAfterClass() {
        try {
            FileUtils.deleteDirectory(patchTestFolder);
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    @Before
    public void setUp() throws Exception {
        testFrame = new FrameFixture(PatchEdit.getInstance());
        testFrame.show();
        testFrame.maximize();
        guiHandler = new GuiHandler(testFrame);
    }

    @After
    public void tearDown() throws Exception {
        guiHandler.uninstallDevice(null);
        testFrame.cleanUp();
    }

    @Test
    public void testDeleteDups() throws InterruptedException {
        Pattern pattern =
                Pattern.compile("(\\d+) Patches and Scenes were deleted");
        guiHandler.installDevice("Roland", "Roland D-50");

        FrameWrapper library = guiHandler.openLibrary();
        JTableFixture table = library.table();
        Thread.sleep(100);

        guiHandler.newPatch(library, "Roland D-50", D50SingleDriver.class,
                listener);
        String[][] contents = table.contents();
        int rows = contents.length;
        assertEquals(1, rows);

        JMenuItemFixture menuItem =
                testFrame.menuItem(new GenericTypeMatcher<JMenuItem>(
                        JMenuItem.class) {

                    @Override
                    protected boolean isMatching(JMenuItem component) {
                        return component.getActionCommand().equals(
                                "Delete Dups...");
                    }
                });
        assertFalse(menuItem.target.isEnabled());

        guiHandler.newPatch(library, "Roland D-50", D50SingleDriver.class,
                listener);
        contents = table.contents();
        rows = contents.length;
        assertEquals(2, rows);

        String deleteDups = guiHandler.deleteDups();
        Matcher matcher = pattern.matcher(deleteDups);
        assertTrue(matcher.find());
        int numDeleted = Integer.parseInt(matcher.group(1));
        assertEquals(1, numDeleted);
        contents = table.contents();
        rows = contents.length;
        assertEquals(1, rows);

        guiHandler.newPatch(library, "Roland D-50", D50SingleDriver.class,
                listener);
        contents = table.contents();
        rows = contents.length;
        assertEquals(2, rows);

        FrameWrapper patchEditor =
                guiHandler.openPatchEditor(table, -1, 0, listener, false);

        List<SysexWidget> sysexWidgets =
                SysexWidgetFinder.findSysexWidgets(patchEditor);
        int numEditedParams = 0;
        for (final SysexWidget sysexWidget : sysexWidgets) {
            if (sysexWidget instanceof CheckBoxWidget) {
                CheckBoxWidget widget = (CheckBoxWidget) sysexWidget;
                final JCheckBoxFixture fixture =
                        new JCheckBoxFixture(testFrame.robot, widget.cb);
                if (fixture.target.isEnabled()) {
                    new SwingCheckBoxValueSetter(fixture, widget.getValueMin())
                            .setValue(widget.getValueMax());
                    numEditedParams++;
                }
            } else if (sysexWidget instanceof ComboBoxWidget) {
                ComboBoxWidget widget = (ComboBoxWidget) sysexWidget;

                final JComboBoxFixture fixture =
                        new JComboBoxFixture(testFrame.robot, widget.cb);
                if (fixture.target.isEnabled()) {
                    new SwingComboBoxValueSetter(fixture, widget.getValueMin())
                            .setValue(widget.getValueMax());
                    numEditedParams++;
                }
            } else if (sysexWidget instanceof KnobWidget) {
                KnobWidget widget = (KnobWidget) sysexWidget;
                if (widget.isEnabled()) {
                    new SwingKnobValueSetter(widget).setValue(widget.getValueMax());
                    numEditedParams++;
                }
            } else if (sysexWidget instanceof ScrollBarWidget) {
                ScrollBarWidget widget = (ScrollBarWidget) sysexWidget;
                final JSliderFixture fixture =
                        new JSliderFixture(testFrame.robot, widget.slider);
                if (fixture.target.isEnabled()) {
                    new SwingSliderValueSetter(fixture).setValue(widget
                            .getValueMax());
                    numEditedParams++;
                }
            }

            if (numEditedParams == 2) {
                break;
            }
        }

        if (patchEditor != null) {
            LOG.info("Close patch editor frame");
            guiHandler.closeFrame(patchEditor, true);
        }
        if (library != null) {
            LOG.info("Selecting library frame");
            guiHandler.selectLibraryFrame(library);
        }

        deleteDups = guiHandler.deleteDups();
        matcher = pattern.matcher(deleteDups);
        assertTrue(matcher.find());
        numDeleted = Integer.parseInt(matcher.group(1));
        assertEquals(0, numDeleted);
        contents = table.contents();
        rows = contents.length;
        assertEquals(2, rows);

        guiHandler.closeLibrary(library);
        guiHandler.uninstallDevice("Roland D-50");
    }

    @Test
    public void testSaveAndOpenLibrary() throws Exception {
        File patchlib =
                new File(patchTestFolder, "testSaveAndOpenLibrary.patchlib");
        File xml =
                new File(patchTestFolder, "testSaveAndOpenLibrary.patchlib.xml");

        assertTrue(patchTestFolder.exists());
        assertTrue(patchTestFolder.isDirectory());

        deleteFileIfExists(patchlib);
        deleteFileIfExists(xml);

        guiHandler.installDevice("Roland", "Roland D-50");
        guiHandler.installDevice("Roland", "Roland D-10");
        guiHandler.installDevice("Yamaha", "Yamaha DX7");

        FrameWrapper library = guiHandler.openLibrary();
        Thread.sleep(100);

        guiHandler.newPatch(library, "Roland D-50", D50SingleDriver.class,
                listener);
        guiHandler.newPatch(library, "Roland D-10", RolandD10ToneDriver.class,
                listener);
        guiHandler.newPatch(library, "Yamaha DX7",
                YamahaDX7VoiceSingleDriver.class, listener);

        guiHandler.saveLibrary(patchTestFolder, "testSaveAndOpenLibrary");

        assertTrue("Assert patchlib exists", patchlib.exists());
        assertTrue("Patchlib is file", patchlib.isFile());
        assertTrue("XML exists", xml.exists());
        assertTrue("XML is file", xml.isFile());

        guiHandler.closeLibrary(library);

        FrameWrapper library2 = guiHandler.openLibrary(patchlib);
        JTableFixture table = library2.table();

        String[][] contents = table.contents();
        assertEquals("Check table size", 3, contents.length);
        TableCell tableCell = TableCell.row(0).column(0);
        table.cell(tableCell).requireValue("D-50");
        tableCell = TableCell.row(1).column(0);
        table.cell(tableCell).requireValue("D-10");
        tableCell = TableCell.row(2).column(0);
        table.cell(tableCell).requireValue("DX7");

        guiHandler.closeLibrary(library2);

        guiHandler.uninstallDevice(null);
    }

    @Test
    public void testSaveAndOpenLibraryWithMetadata() throws Exception {
        String rolandData1 = "Roland Data1";
        String rolandData2 = "Roland Data2";
        String rolandComment = "Roland Comment";
        String yamahaData1 = "Yamaha Data1";
        String yamahaData2 = "Yamaha Data2";
        String yamahaComment = "Yamaha Comment";

        assertTrue(patchTestFolder.exists());
        assertTrue(patchTestFolder.isDirectory());

        File patchlib =
                new File(patchTestFolder,
                        "testSaveAndOpenLibraryWithMetadata.patchlib");
        File xml =
                new File(patchTestFolder,
                        "testSaveAndOpenLibraryWithMetadata.patchlib.xml");

        deleteFileIfExists(patchlib);
        deleteFileIfExists(xml);

        guiHandler.installDevice("Roland", "Roland D-50");
        guiHandler.installDevice("Yamaha", "Yamaha DX7");

        FrameWrapper library = guiHandler.openLibrary();
        Thread.sleep(100);

        guiHandler.newPatch(library, "Roland D-50", D50SingleDriver.class,
                listener);
        guiHandler.newPatch(library, "Yamaha DX7",
                YamahaDX7VoiceSingleDriver.class, listener);

        guiHandler.addPatchMetaData(library, 0, GuiHandler.FIELD1, rolandData1);
        guiHandler.addPatchMetaData(library, 0, GuiHandler.FIELD2, rolandData2);
        guiHandler.addPatchMetaData(library, 0, GuiHandler.COMMENT,
                rolandComment);

        guiHandler.addPatchMetaData(library, 1, GuiHandler.FIELD1, yamahaData1);
        guiHandler.addPatchMetaData(library, 1, GuiHandler.FIELD2, yamahaData2);
        guiHandler.addPatchMetaData(library, 1, GuiHandler.COMMENT,
                yamahaComment);

        guiHandler.saveLibrary(patchTestFolder,
                "testSaveAndOpenLibraryWithMetadata");

        assertTrue("Patchlib exists", patchlib.exists());
        assertTrue("Patchlib is file", patchlib.isFile());
        assertTrue("XML exists", xml.exists());
        assertTrue("XML is file", xml.isFile());

        guiHandler.closeLibrary(library);

        FrameWrapper library2 = guiHandler.openLibrary(patchlib);
        JTableFixture table = library2.table();

        String[][] contents = table.contents();
        assertEquals("Check table size", 2, contents.length);
        table.cell(TableCell.row(0).column(0)).requireValue("D-50");
        table.cell(TableCell.row(0).column(3)).requireValue(rolandData1);
        table.cell(TableCell.row(0).column(4)).requireValue(rolandData2);
        table.cell(TableCell.row(0).column(5)).requireValue(rolandComment);
        table.cell(TableCell.row(1).column(0)).requireValue("DX7");
        table.cell(TableCell.row(1).column(3)).requireValue(yamahaData1);
        table.cell(TableCell.row(1).column(4)).requireValue(yamahaData2);
        table.cell(TableCell.row(1).column(5)).requireValue(yamahaComment);

        guiHandler.closeLibrary(library2);

        guiHandler.uninstallDevice(null);
    }

    @Test
    public void testCutCopyPasteBetweenLibraries() throws InterruptedException {
        guiHandler.installDevice("Roland", "Roland D-50");
        guiHandler.installDevice("Yamaha", "Yamaha DX7");

        FrameWrapper library = guiHandler.openLibrary();
        JTableFixture table = library.table();
        Thread.sleep(100);

        guiHandler.newPatch(library, "Roland D-50", D50SingleDriver.class,
                listener);
        guiHandler.newPatch(library, "Yamaha DX7",
                YamahaDX7VoiceSingleDriver.class, listener);

        FrameWrapper library2 = guiHandler.openLibrary();
        JTableFixture table2 = library2.table();

        assertEquals(2, table.contents().length);
        assertEquals(0, table2.contents().length);

        guiHandler.cutCopyPastePatch(library, 0, 0, library2, false);

        assertEquals(1, table.contents().length);
        assertEquals(1, table2.contents().length);

        guiHandler.cutCopyPastePatch(library, 0, 0, library2, true);

        assertEquals(1, table.contents().length);
        assertEquals(2, table2.contents().length);

        guiHandler.closeLibrary(library);
        guiHandler.closeLibrary(library2);

        guiHandler.uninstallDevice(null);
    }

    @Test
    public void testCutCopyPasteBetweenBanks() throws InterruptedException {
        guiHandler.installDevice("Yamaha", "Yamaha DX7");

        FrameWrapper library = guiHandler.openLibrary();
        JTableFixture table = library.table();
        Thread.sleep(100);

        guiHandler.newPatch(library, "Yamaha DX7",
                YamahaDX7VoiceBankDriver.class, listener);
        FrameWrapper bank1 =
                guiHandler.openPatchEditor(table, -1, 0, listener, false);
        guiHandler.setPatchName(bank1, 0, 0, "test");
        guiHandler.setPatchName(bank1, 0, 1, "test2");
        bank1.table().cell(TableCell.row(0).column(0))
                .requireValue(Pattern.compile("01 test\\s+"));

        guiHandler.newPatch(library, "Yamaha DX7",
                YamahaDX7VoiceBankDriver.class, listener);
        FrameWrapper bank2 =
                guiHandler.openPatchEditor(table, -1, 0, listener, false);

        guiHandler.cutCopyPastePatch(bank1, 0, 0, bank2, false);

        bank1.table().cell(TableCell.row(0).column(0))
                .requireValue(Pattern.compile("01\\s+"));
        bank2.table().cell(TableCell.row(0).column(0))
                .requireValue(Pattern.compile("01 test\\s+"));

        guiHandler.cutCopyPastePatch(bank1, 0, 1, bank2, true);

        bank1.table().cell(TableCell.row(0).column(1))
                .requireValue(Pattern.compile("09 test2\\s+"));
        bank2.table().cell(TableCell.row(0).column(1))
                .requireValue(Pattern.compile("09 test2\\s+"));

        guiHandler.closeFrame(bank1, false);
        guiHandler.closeFrame(bank2, false);
        guiHandler.closeLibrary(library);
        guiHandler.uninstallDevice(null);
    }

    @Test
    public void testDragNDropBetweenBanks() throws InterruptedException {
        guiHandler.installDevice("Yamaha", "Yamaha DX7");

        FrameWrapper library = guiHandler.openLibrary();
        JTableFixture table = library.table();
        Thread.sleep(100);

        guiHandler.newPatch(library, "Yamaha DX7",
                YamahaDX7VoiceBankDriver.class, listener);
        FrameWrapper bank1 =
                guiHandler.openPatchEditor(table, -1, 0, listener, false);
        guiHandler.setPatchName(bank1, 0, 0, "test");
        guiHandler.setPatchName(bank1, 0, 1, "test2");
        bank1.table().cell(TableCell.row(0).column(0))
                .requireValue(Pattern.compile("01 test\\s+"));

        guiHandler.dragNdropPatch(bank1, 0, 0, 1, 1);

        bank1.table().cell(TableCell.row(0).column(0))
                .requireValue(Pattern.compile("01 test\\s+"));
        bank1.table().cell(TableCell.row(1).column(1))
                .requireValue(Pattern.compile("\\d+ test\\s+"));

        guiHandler.closeFrame(bank1, false);
        guiHandler.closeLibrary(library);
        guiHandler.uninstallDevice(null);

    }

    @Test
    public void testSortLibrary() throws Exception {
        String rolandData1 = "Roland Data1";
        String yamahaData1 = "Yamaha Data1";

        guiHandler.installDevice("Roland", "Roland D-50");
        guiHandler.installDevice("Yamaha", "Yamaha DX7");

        FrameWrapper library = guiHandler.openLibrary();
        JTableFixture table = library.table();
        Thread.sleep(100);

        guiHandler.newPatch(library, "Roland D-50", D50SingleDriver.class,
                listener);
        guiHandler.newPatch(library, "Yamaha DX7",
                YamahaDX7VoiceSingleDriver.class, listener);

        guiHandler.addPatchMetaData(library, 0, GuiHandler.FIELD1, rolandData1);
        guiHandler.addPatchMetaData(library, 1, GuiHandler.FIELD1, yamahaData1);

        table.cell(TableCell.row(0).column(0)).requireValue("D-50");
        table.cell(TableCell.row(1).column(0)).requireValue("DX7");

        guiHandler.sortLibrary(library, SortFields.PATCH_NAME);

        String[][] contents = table.contents();
        assertEquals("Check table size", 2, contents.length);
        table.cell(TableCell.row(0).column(0)).requireValue("DX7");
        table.cell(TableCell.row(1).column(0)).requireValue("D-50");

        guiHandler.sortLibrary(library, SortFields.FIELD1);

        contents = table.contents();
        assertEquals("Check table size", 2, contents.length);
        table.cell(TableCell.row(0).column(0)).requireValue("D-50");
        table.cell(TableCell.row(1).column(0)).requireValue("DX7");

        guiHandler.closeLibrary(library);
        guiHandler.uninstallDevice(null);
    }

    @Test
    public void testSearchInLibrary() throws Exception {
        String rolandData1 = "Roland Data1";
        String yamahaData1 = "Yamaha Data1";

        guiHandler.installDevice("Roland", "Roland D-50");
        guiHandler.installDevice("Yamaha", "Yamaha DX7");

        FrameWrapper library = guiHandler.openLibrary();
        JTableFixture table = library.table();
        Thread.sleep(100);

        guiHandler.newPatch(library, "Roland D-50", D50SingleDriver.class,
                listener);
        guiHandler.newPatch(library, "Yamaha DX7",
                YamahaDX7VoiceSingleDriver.class, listener);

        guiHandler.addPatchMetaData(library, 0, GuiHandler.FIELD1, rolandData1);
        guiHandler.addPatchMetaData(library, 1, GuiHandler.FIELD1, yamahaData1);

        ISearchHandler searchHandler = guiHandler.openSearchDialog(library);
        searchHandler.setField(SearchFields.PATCH_NAME);
        searchHandler.findFirst("INIT");
        table.requireSelectedRows(1);
        searchHandler.findNext();

        GenericTypeMatcher<JDialog> matcher =
                new GenericTypeMatcher<JDialog>(JDialog.class) {

                    @Override
                    protected boolean isMatching(JDialog component) {
                        return "Search Complete".equals(component.getTitle())
                                && component.isShowing();
                    }
                };

        DialogFixture dialog = testFrame.dialog(matcher);
        guiHandler.closeDialog(dialog);

        searchHandler.setField(SearchFields.FIELD1);
        searchHandler.findFirst("Roland");
        table.requireSelectedRows(0);

        searchHandler.setField(SearchFields.ALL_FIELDS);
        searchHandler.findFirst("Yamaha");
        table.requireSelectedRows(1);

        searchHandler.findFirst("Testingtesting");
        dialog = testFrame.dialog(matcher);
        guiHandler.closeDialog(dialog);

        searchHandler.cancel();

        guiHandler.closeLibrary(library);
        guiHandler.uninstallDevice(null);
    }

    void deleteFileIfExists(File file) throws IOException {
        if (file.exists()) {
            if (!file.delete()) {
                throw new IOException("Failed to delete "
                        + file.getAbsolutePath());
            }
        }
    }

}
