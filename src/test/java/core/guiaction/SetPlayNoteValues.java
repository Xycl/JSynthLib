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

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JSliderFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;

public class SetPlayNoteValues extends AbstractGuiAction {

    private int noteValue;
    private int velocityValue;
    private int durationValue;

    public SetPlayNoteValues(FrameFixture testFrame, int noteValue,
            int velocityValue, int durationValue) {
        super(testFrame);
        this.noteValue = noteValue;
        this.velocityValue = velocityValue;
        this.durationValue = durationValue;
    }

    @Override
    public void perform() {
        DialogFixture fixture = openPreferencesDialog();

        JTabbedPaneFixture tabbedPane = fixture.tabbedPane();
        tabbedPane.selectTab("Play Note");
        JSliderFixture noteSlider = fixture.slider("midiNote");
        noteSlider.slideTo(noteValue);

        JSliderFixture velocity = fixture.slider("velocity");
        velocity.slideTo(velocityValue);

        JSliderFixture duration = fixture.slider("duration");
        duration.slideTo(durationValue);

        closeDialog(fixture);
    }

}
