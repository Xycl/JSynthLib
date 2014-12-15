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

    @Inject
    @Named("prefix")
    private String prefix;

    private String nameModulatorName;
    private String receiveMenuName;
    private String writeMenuName;
    private String loadMenuName;
    private String saveMenuName;
    private String getNameMethodName;
    private String setNameMethodName;
    private String assignValuesMethodName;
    private String assignValuesToBankMethodName;
    private String assembleValuesMethodName;
    private String assembleValuesFromBankMethodName;
    private String infoLabelName;
    private int editorWidth;
    private int editorHeight;
    private int patchNameCharMax;
    private String bankDataVarName;
    private final AtomicInteger vstIndex;
    private int singlePatchSize;

    private final List<ModulatorControllerBase> rootModulators;

    private final List<MethodDescriptionPair> loadMenuOptions;
    private final List<MethodDescriptionPair> receiveMenuOptions;
    private final List<MethodDescriptionPair> saveMenuOptions;
    private final List<MethodDescriptionPair> writeMenuOptions;

    public DriverModel() {
        this.loadMenuOptions = new ArrayList<MethodDescriptionPair>();
        this.receiveMenuOptions = new ArrayList<MethodDescriptionPair>();
        this.saveMenuOptions = new ArrayList<MethodDescriptionPair>();
        this.writeMenuOptions = new ArrayList<MethodDescriptionPair>();
        this.vstIndex = new AtomicInteger(1);
        rootModulators = new ArrayList<ModulatorControllerBase>();
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

    public void setReceiveMenuName(String receiveMenuName) {
        this.receiveMenuName = receiveMenuName;
        setChanged();
        notifyObservers(receiveMenuName);
    }

    public String getWriteMenuName() {
        return writeMenuName;
    }

    public void setWriteMenuName(String writeMenuName) {
        this.writeMenuName = writeMenuName;
        setChanged();
        notifyObservers(writeMenuName);
    }

    public String getLoadMenuName() {
        return loadMenuName;
    }

    public void setLoadMenuName(String loadMenuName) {
        this.loadMenuName = loadMenuName;
        setChanged();
        notifyObservers(loadMenuName);
    }

    public String getSaveMenuName() {
        return saveMenuName;
    }

    public void setSaveMenuName(String saveMenuName) {
        this.saveMenuName = saveMenuName;
        setChanged();
        notifyObservers(saveMenuName);
    }

    public String getGetNameMethodName() {
        return getNameMethodName;
    }

    public void setGetNameMethodName(String getNameMethodName) {
        this.getNameMethodName = getNameMethodName;
        setChanged();
        notifyObservers(getNameMethodName);
    }

    public String getSetNameMethodName() {
        return setNameMethodName;
    }

    public void setSetNameMethodName(String setNameMethodName) {
        this.setNameMethodName = setNameMethodName;
        setChanged();
        notifyObservers(setNameMethodName);
    }

    public String getInfoLabelName() {
        return infoLabelName;
    }

    public void setInfoLabelName(String infoLabelName) {
        this.infoLabelName = infoLabelName;
        setChanged();
        notifyObservers(infoLabelName);
    }

    public int getPatchNameCharMax() {
        return patchNameCharMax;
    }

    public void setPatchNameCharMax(int patchNameCharMax) {
        this.patchNameCharMax = patchNameCharMax;
        setChanged();
        notifyObservers(infoLabelName);
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

    public List<MethodDescriptionPair> getWriteMenuOptions() {
        return writeMenuOptions;
    }

    public String getAssignValuesMethodName() {
        return assignValuesMethodName;
    }

    public String getAssembleValuesMethodName() {
        return assembleValuesMethodName;
    }

    public void setAssignValuesMethodName(String assignValuesMethodName) {
        this.assignValuesMethodName = assignValuesMethodName;
        setChanged();
        notifyObservers(assignValuesMethodName);
    }

    public void setAssembleValuesMethodName(String assembleValuesMethodName) {
        this.assembleValuesMethodName = assembleValuesMethodName;
        setChanged();
        notifyObservers(assembleValuesMethodName);
    }

    public List<MethodDescriptionPair> getLoadMenuOptions() {
        return loadMenuOptions;
    }

    public List<MethodDescriptionPair> getReceiveMenuOptions() {
        return receiveMenuOptions;
    }

    public List<MethodDescriptionPair> getSaveMenuOptions() {
        return saveMenuOptions;
    }

    public void addLoadMenuOption(MethodDescriptionPair loadMenuOption) {
        this.loadMenuOptions.add(loadMenuOption);
        setChanged();
        notifyObservers(loadMenuOptions);
    }

    public void addReceiveMenuOption(MethodDescriptionPair receiveMenuOption) {
        this.receiveMenuOptions.add(receiveMenuOption);
        setChanged();
        notifyObservers(receiveMenuOptions);
    }

    public void addSaveMenuOption(MethodDescriptionPair saveMenuOption) {
        this.saveMenuOptions.add(saveMenuOption);
        setChanged();
        notifyObservers(saveMenuOptions);
    }

    public void addWriteMenuOption(MethodDescriptionPair writeMenuOption) {
        this.writeMenuOptions.add(writeMenuOption);
        setChanged();
        notifyObservers(writeMenuOptions);
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

    public void setAssignValuesToBankMethodName(
            String assignValuesToBankMethodName) {
        this.assignValuesToBankMethodName = assignValuesToBankMethodName;
        setChanged();
        notifyObservers(assignValuesToBankMethodName);
    }

    public void setAssembleValuesFromBankMethodName(
            String assembleValuesFromBankMethodName) {
        this.assembleValuesFromBankMethodName =
                assembleValuesFromBankMethodName;
        setChanged();
        notifyObservers(assembleValuesFromBankMethodName);
    }

    public String getBankDataVarName() {
        return bankDataVarName;
    }

    public void setBankDataVarName(String bankDataVarName) {
        this.bankDataVarName = bankDataVarName;
        setChanged();
        notifyObservers(bankDataVarName);
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
}
