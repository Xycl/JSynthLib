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
package core.guiaction;

import java.io.File;
import java.util.List;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JFileChooserFixture;

import core.TitleFinder;
import core.TitleFinder.FrameWrapper;

public class OpenLibraryAction extends AbstractGuiAction {

    private FrameWrapper container;

    private File file;

    public OpenLibraryAction(FrameFixture testFrame, File patchlib) {
        super(testFrame);
        this.file = patchlib;
    }

    @Override
    public void perform() {
        List<FrameWrapper> windowTitlesBefore =
                TitleFinder.getWindowTitles(testFrame);
        if (file == null) {
            clickMenuItem("New Library");
        } else {
            clickMenuItem("Open...");
            JFileChooserFixture fileChooser = testFrame.fileChooser();
            File parentFile = file.getParentFile();
            if (parentFile != null) {
                fileChooser.setCurrentDirectory(parentFile);
            }
            fileChooser.selectFile(file);
            fileChooser.approve();
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }

        List<FrameWrapper> windowTitlesAfter =
                TitleFinder.getWindowTitles(testFrame);
        container = getOpenedFrame(windowTitlesBefore, windowTitlesAfter);
    }

    public FrameWrapper getFixture() {
        return container;
    }
}
