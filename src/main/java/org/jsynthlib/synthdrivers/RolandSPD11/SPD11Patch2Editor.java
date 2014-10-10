/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jsynthlib.synthdrivers.RolandSPD11;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jsynthlib.device.model.ParamModel;
import org.jsynthlib.device.viewcontroller.PatchEditorFrame;
import org.jsynthlib.device.viewcontroller.widgets.CheckBoxWidget;
import org.jsynthlib.device.viewcontroller.widgets.ComboBoxWidget;
import org.jsynthlib.device.viewcontroller.widgets.KnobLookupWidget;
import org.jsynthlib.device.viewcontroller.widgets.KnobWidget;
import org.jsynthlib.device.viewcontroller.widgets.ScrollBarWidget;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.patch.model.PatchFactory;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * @author peter
 */
public class SPD11Patch2Editor extends PatchEditorFrame {
    private PatchFactory patchFactory;

    // private Patch patch;
    public SPD11Patch2Editor(Patch patch, String[] programNumbers) {
        super("Roland SPD11 Patch Editor", patch);
        scrollPane.setBackground(Color.RED);
        // ptchNum will stay the same for the whole patch
        int ptchNum = patch.sysex[6];
        int pdNum = 0; // padNum will go up to 32
        // start with FxPane, copied from settingsEditor.
        // scrollPane.setLayout(new GridLayout(0,1));
        scrollPane.setLayout(new GridBagLayout());
        // SPD11SettingsEditor.addFxPane(patch,scrollPane);
        // this.patch = patch;
        pack();
        
        patchFactory = JSynthLibInjector.getInstance(PatchFactory.class);
        // **
        JPanel fxPane = new JPanel(); // instantiate a new JPanel named lfoPane
        fxPane.setLayout(new GridBagLayout());
        // create ScrollBarWidget for FX parameter o work together with FX Type
        final ScrollBarWidget fxParam =
                new ScrollBarWidget(SPD11_Constants.FX_PARAMS[patch.sysex[12]],
                        patch, 0, 31, 1, new ParamModel(patch, 13),
                        new SPD11PadParamSender(ptchNum, pdNum, 4));
        // fxParam.setSize(10,1000); //doesn't seem to work
        fxPane.setBackground(Color.red);
        fxParam.setPreferredSize(new Dimension(520, 25));
        // fxParam.setLabel(SPD11_Constants.FX_PARAMS[patch.sysex[12]]); //
        // change label according to the Fx Type.
        // create ComboBoxWidget for FX Type
        ComboBoxWidget fxType =
                new ComboBoxWidget("FxType", patch, new ParamModel(patch, 12),
                        new SPD11PadParamSender(ptchNum, pdNum, 3),
                        SPD11_Constants.SPD11_FXTYPES);
        fxType.addEventListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    int i = ((JComboBox) e.getSource()).getSelectedIndex();
                    fxParam.setLabel(SPD11_Constants.FX_PARAMS[i]);
                }
            }
        });
        // add ScrollBarWidgets:
        addWidget(fxPane, fxParam, 3, 0, 1, 1, 1); // slidernum
        addWidget(fxPane, new ScrollBarWidget("Fx Level", patch, 0, 15, 0,
                new ParamModel(patch, 14), new SPD11PadParamSender(ptchNum,
                        pdNum, 5)), 3, 1, 1, 1, 2);
        addWidget(fxPane, new ScrollBarWidget("Pedal Level", patch, 0, 15, 0,
                new ParamModel(patch, 16), new SPD11PadParamSender(ptchNum,
                        pdNum, 7)), 4, 1, 1, 1, 3);
        // add CheckBoxWidgets:
        addWidget(fxPane, new CheckBoxWidget("Layer On", patch, new ParamModel(
                patch, 10), new SPD11PadParamSender(ptchNum, pdNum, 1)), 1, 1,
                1, 1, 4);
        addWidget(fxPane, new CheckBoxWidget("FX On", patch, new ParamModel(
                patch, 11), new SPD11PadParamSender(ptchNum, pdNum, 2)), 2, 1,
                1, 1, 5);
        // add ComboBoxWidgets:"Bank A/B", "FX Type" and "Pedal Control"
        addWidget(fxPane, new ComboBoxWidget("Bank", patch, new ParamModel(
                patch, 9), new SPD11PadParamSender(ptchNum, pdNum, 0),
                new String[] {
                        "A", "B" }), 0, 1, 1, 1, 6);
        addWidget(fxPane, fxType, 0, 0, 3, 1, 6);
        addWidget(fxPane, new ComboBoxWidget("PedalControl", patch,
                new ParamModel(patch, 15), new SPD11PadParamSender(ptchNum,
                        pdNum, 6), SPD11_Constants.SPD11_PEDALCONTROL), 4, 0,
                1, 1, 6);
        // scrollPane.add(fxPane);
        // now the panes for each 8 pads, 32 in total
        String title;
        JPanel partOne = new JPanel();
        partOne.setLayout(new GridLayout(0, 1));
        for (int i = 0; i < 8; i++) {
            title = "Layer A - Pad " + (i + 1);
            Color kleur = (i < 4 ? Color.ORANGE : Color.GREEN);
            JPanel padPane = new JPanel();
            padPane.setBackground(kleur);
            padPane.setLayout(new GridBagLayout());
            padPane.setBorder(new TitledBorder(new EtchedBorder(
                    EtchedBorder.RAISED), title, TitledBorder.LEFT,
                    TitledBorder.CENTER));
            addPadPane(patch, padPane, i, programNumbers);
            partOne.add(padPane);
        }
        JPanel partTwo = new JPanel();
        partTwo.setLayout(new GridLayout(0, 1));
        for (int i = 0; i < 8; i++) {
            title = "Layer A - Pad " + (i + 9);
            Color kleur = (i < 4 ? Color.YELLOW : Color.CYAN);
            JPanel padPane = new JPanel();
            padPane.setBackground(kleur);
            padPane.setLayout(new GridBagLayout());
            padPane.setBorder(new TitledBorder(new EtchedBorder(
                    EtchedBorder.RAISED), title, TitledBorder.LEFT,
                    TitledBorder.CENTER));
            addPadPane(patch, padPane, i + 8, programNumbers);
            partTwo.add(padPane);
        }
        JPanel partThree = new JPanel();
        partThree.setLayout(new GridLayout(0, 1));
        for (int i = 0; i < 8; i++) {
            title = "Layer B - Pad " + (i + 1);
            Color kleur = (i < 4 ? Color.ORANGE : Color.GREEN);
            JPanel padPane = new JPanel();
            padPane.setBackground(kleur);
            padPane.setLayout(new GridBagLayout());
            padPane.setBorder(new TitledBorder(new EtchedBorder(
                    EtchedBorder.RAISED), title, TitledBorder.LEFT,
                    TitledBorder.CENTER));
            addPadPane(patch, padPane, i + 16, programNumbers);
            partThree.add(padPane);
        }
        JPanel partFour = new JPanel();
        partFour.setLayout(new GridLayout(0, 1));
        for (int i = 0; i < 8; i++) {
            title = "Layer B - Pad " + (i + 9);
            Color kleur = (i < 4 ? Color.YELLOW : Color.CYAN);
            JPanel padPane = new JPanel();
            padPane.setBackground(kleur);
            padPane.setLayout(new GridBagLayout());
            padPane.setBorder(new TitledBorder(new EtchedBorder(
                    EtchedBorder.RAISED), title, TitledBorder.LEFT,
                    TitledBorder.CENTER));
            addPadPane(patch, padPane, i + 24, programNumbers);
            partFour.add(padPane);
        }
        JTabbedPane mixer = new JTabbedPane();
        // mixer.addTab("Settings", fxPane);
        mixer.addTab("Layer A - Pad 1 to 8", partOne);
        mixer.addTab("Layer A - Pad 9 to 16", partTwo);
        mixer.addTab("Layer B - Pad 1 to 8", partThree);
        mixer.addTab("Layer B - Pad 9 to 16", partFour);
        gbc.gridx = 0;
        gbc.gridy = 1;
        // gbc.weightx=5;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        // fxParam.setMinimumSize(new Dimension(0,mixer.WIDTH)); //doesn't seem
        // to work
        // fxPane.setMinimumSize(new Dimension(0,mixer.getWidth()));
        scrollPane.add(fxPane, gbc);
        gbc.gridy = 2;
        scrollPane.add(mixer, gbc);
        // */
        pack();
    }

    /**
     * adds a PadPane to a given JPanel for a given CompletePatch at a given
     * offset.
     * @param cPatch
     *            the patch,
     * @param pane
     * @param padNum
     *            a value from 0 to 31
     */
    protected void addPadPane(Patch cPatch, JPanel pane, int padNum, String[] programNumbers) {
        // temporary hack to get the editing working, but the changes don't stay
        // in JSynthLib because we create a new padpatch.
        // TODO: rewrite SPD11PadModel/Senders to work with a CompletePatch
        byte[] ped = new byte[28];
        System.arraycopy(cPatch.sysex, 19 + (28 * padNum), ped, 0, 28);
        Patch pad = patchFactory.createNewPatch(ped);
        int pdNum = cPatch.sysex[19 + (28 * padNum) + 7];// ped[7];
        int ptchNum = ped[6]; // I can use this to check if we're working with
                              // the right pad/settings, but remove all that ped
                              // stuff
        gbc.weightx = 0;
        addWidget(pane, new ComboBoxWidget("Inst", pad, new SPD11PadModel(
                cPatch, 19 + (28 * padNum) + 9, true), new SPD11PadInstSender(
                ptchNum, pdNum), SPD11_Constants.INSTRUMENTS), // Inst
                // gridx, gridy, gridwidth, gridheight, slidernum
                0, 0, 1, 1, 0);
        addWidget(pane, new ComboBoxWidget("Curv", pad, new SPD11PadModel(pad,
                15), new SPD11PadParamSender(ptchNum, pdNum, 6),
                SPD11_Constants.CURVES), 0, 1, 1, 1, 0);
        addWidget(pane, new KnobWidget("Level", pad, 0, 15, 0,
                new SPD11PadModel(pad, 11), new SPD11PadParamSender(ptchNum,
                        pdNum, 2)), 1, 0, 1, 2, 1 + padNum);
        addWidget(pane, new KnobWidget("Pitch", pad, 0, 48, -24,
                new SPD11PadModel(pad, 12), new SPD11PadParamSender(ptchNum,
                        pdNum, 3)), 2, 0, 1, 2, 0);
        addWidget(pane, new KnobWidget("Decay", pad, 0, 62, -31,
                new SPD11PadDecayModel(pad, 13), new SPD11PadDecaySender(
                        ptchNum, pdNum, 4)), 3, 0, 1, 2, 0);
        addWidget(pane, new KnobLookupWidget("Pan", pad, new SPD11PadModel(pad,
                14), new SPD11PadParamSender(ptchNum, pdNum, 5),
                SPD11_Constants.PANPOSITIONS), 4, 0, 1, 2, 33 + padNum);
        addWidget(pane, new KnobWidget("FxSend", pad, 0, 15, 0,
                new SPD11PadModel(pad, 16), new SPD11PadParamSender(ptchNum,
                        pdNum, 7)), 5, 0, 1, 2, 65 + padNum);
        addWidget(pane, new ComboBoxWidget("MidiNote", pad, new SPD11PadModel(
                pad, 18), new SPD11PadParamSender(ptchNum, pdNum, 9),
                SPD11_Constants.NOTENUMBERS), 6, 0, 1, 1, 0);
        addWidget(pane, new ComboBoxWidget("MidiProg", pad, new SPD11PadModel(
                pad, 24), new SPD11PadParamSender(ptchNum, pdNum, 15),
                programNumbers), 6, 1, 1, 1, 0);
        addWidget(pane, new ComboBoxWidget("MidiCurv", pad, new SPD11PadModel(
                pad, 22), new SPD11PadParamSender(ptchNum, pdNum, 13),
                SPD11_Constants.CURVES), 7, 0, 1, 1, 0);
        addWidget(pane, new ComboBoxWidget("MidiTxCh", pad, new SPD11PadModel(
                pad, 17), new SPD11PadParamSender(ptchNum, pdNum, 8),
                SPD11_Constants.CHANNELNUMBERS), 7, 1, 1, 1, 0);
        addWidget(pane, new KnobWidget("MidiGtTime", pad, 0, 40, 0,
                new SPD11PadModel(pad, 20), new SPD11PadParamSender(ptchNum,
                        pdNum, 11)), 8, 0, 1, 2, 0);
        addWidget(pane, new KnobWidget("MidiPan", pad, 0, 16, -8,
                new SPD11PadModel(pad, 21), new SPD11PadParamSender(ptchNum,
                        pdNum, 12)), 9, 0, 1, 2, 0);
        addWidget(pane, new KnobWidget("MidiSens", pad, 0, 14, 0,
                new SPD11PadModel(pad, 23), new SPD11PadParamSender(ptchNum,
                        pdNum, 14)), 10, 0, 1, 2, 0);
        gbc.gridwidth = 0;
        gbc.gridheight = 4;
        // gbc.fill = GridBagConstraints.NONE; //
        // gbc.anchor = GridBagConstraints.CENTER; //
        pack();
    }

}
