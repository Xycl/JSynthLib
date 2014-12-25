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
    private String receiveMenuName;
    private String writeMenuName;
    private String loadMenuName;
    private String saveMenuName;
    private final String getNameMethodName;
    private final String setNameMethodName;
    private final String assignValuesMethodName;
    private final String assignBankMethodName;
    private final String putPatchMethodName;
    private final String assembleValuesMethodName;
    private final String getPatchMethodName;
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
        this.getPatchMethodName = prefix + "_getPatch";
        this.assignValuesMethodName = prefix + "_AssignValues";
        this.assignBankMethodName = prefix + "_AssignBank";
        this.putPatchMethodName = prefix + "_putPatch";
        this.getNameMethodName = prefix + "_GetPatchName";
        bankDataVarName = prefix + "BankData";
        setNameMethodName = prefix + "_SetPatchName";
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

    public String getPutPatchMethodName() {
        return putPatchMethodName;
    }

    public String getGetPatchMethodName() {
        return getPatchMethodName;
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

    public String getAssignBankMethodName() {
        return assignBankMethodName;
    }

    public void setReceiveMenuName(String receiveMenuName) {
        this.receiveMenuName = receiveMenuName;
        setChanged();
        notifyObservers(receiveMenuName);
    }

    public void setWriteMenuName(String writeMenuName) {
        this.writeMenuName = writeMenuName;
        setChanged();
        notifyObservers(writeMenuName);
    }

    public void setLoadMenuName(String loadMenuName) {
        this.loadMenuName = loadMenuName;
        setChanged();
        notifyObservers(loadMenuName);
    }

    public void setSaveMenuName(String saveMenuName) {
        this.saveMenuName = saveMenuName;
        setChanged();
        notifyObservers(saveMenuName);
    }

}
