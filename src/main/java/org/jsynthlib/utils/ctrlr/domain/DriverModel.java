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
package org.jsynthlib.utils.ctrlr.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsynthlib.utils.ctrlr.controller.modulator.ModulatorControllerBase;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * @author Pascal Collberg
 */
@Singleton
public class DriverModel extends Observable {

    private final String patchSelectName;
    private String nameModulatorName;
    private final String receiveMenuName;
    private final String writeMenuName;
    private final String loadMenuName;
    private final String saveMenuName;
    private final String getNameMethodName;
    private final String setNameMethodName;
    private final String assignValuesMethodName;
    private final String assignValuesToBankMethodName;
    private final String assembleValuesMethodName;
    private final String assembleValuesFromBankMethodName;
    // private String infoLabelName;
    private int editorWidth;
    private int editorHeight;
    private int patchNameCharMax;
    private final String bankDataVarName;
    private final AtomicInteger vstIndex;
    private int singlePatchSize;

    private final List<ModulatorControllerBase> rootModulators;
    private final String prefix;
    private final String patchSelectMethodName;

    @Inject
    public DriverModel(@Named("prefix") String prefix) {
        this.vstIndex = new AtomicInteger(1);
        rootModulators = new ArrayList<ModulatorControllerBase>();
        this.prefix = prefix;
        this.patchSelectName = prefix + "_PatchSelectControl";
        this.patchSelectMethodName = prefix + "_PatchSelect";
        this.assembleValuesMethodName = prefix + "_AssembleValues";
        this.assembleValuesFromBankMethodName = prefix + "_GetPatch";
        this.assignValuesMethodName = prefix + "_AssignValues";
        this.assignValuesToBankMethodName = prefix + "_PutPatch";
        this.getNameMethodName = prefix + "_GetPatchName";
        bankDataVarName = prefix + "BankData";
        loadMenuName = prefix + "_LoadMenu";
        receiveMenuName = prefix + "_ReceiveMenu";
        saveMenuName = prefix + "_SaveMenu";
        setNameMethodName = prefix + "_SetPatchName";
        writeMenuName = prefix + "_WriteMenu";
    }

    public int getNextVstIndex() {
        return vstIndex.getAndIncrement();
    }

    public String getNameModulatorName() {
        return nameModulatorName;
    }

    public void setNameModulatorName(String nameModulatorName) {
        this.nameModulatorName = nameModulatorName;
        setChanged();
        notifyObservers(nameModulatorName);
    }

    public String getReceiveMenuName() {
        return receiveMenuName;
    }

    public String getWriteMenuName() {
        return writeMenuName;
    }

    public String getLoadMenuName() {
        return loadMenuName;
    }

    public String getSaveMenuName() {
        return saveMenuName;
    }

    public String getGetNameMethodName() {
        return getNameMethodName;
    }

    public String getSetNameMethodName() {
        return setNameMethodName;
    }

    public int getPatchNameCharMax() {
        return patchNameCharMax;
    }

    public void setPatchNameCharMax(int patchNameCharMax) {
        this.patchNameCharMax = patchNameCharMax;
        setChanged();
        notifyObservers(patchNameCharMax);
    }

    public int getEditorWidth() {
        return editorWidth;
    }

    public int getEditorHeight() {
        return editorHeight;
    }

    public void setEditorWidth(int editorWidth) {
        this.editorWidth = editorWidth;
        setChanged();
        notifyObservers(editorWidth);
    }

    public void setEditorHeight(int editorHeight) {
        this.editorHeight = editorHeight;
        setChanged();
        notifyObservers(editorHeight);
    }

    public String getAssignValuesMethodName() {
        return assignValuesMethodName;
    }

    public String getAssembleValuesMethodName() {
        return assembleValuesMethodName;
    }

    public void driverParseComplete() {
        setChanged();
        notifyObservers();
    }

    public String getAssignValuesToBankMethodName() {
        return assignValuesToBankMethodName;
    }

    public String getAssembleValuesFromBankMethodName() {
        return assembleValuesFromBankMethodName;
    }

    public String getBankDataVarName() {
        return bankDataVarName;
    }

    public int getSinglePatchSize() {
        return singlePatchSize;
    }

    public void setSinglePatchSize(int singlePatchSize) {
        this.singlePatchSize = singlePatchSize;
        setChanged();
        notifyObservers(singlePatchSize);
    }

    public boolean addRootModulator(ModulatorControllerBase e) {
        return rootModulators.add(e);
    }

    public List<ModulatorControllerBase> getRootModulators() {
        return rootModulators;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getPatchSelectName() {
        return patchSelectName;
    }

    /**
     * @return
     */
    public String getPatchSelectMethodName() {
        return patchSelectMethodName;
    }

    // public String getInfoLabelName() {
    // return infoLabelName;
    // }

}
