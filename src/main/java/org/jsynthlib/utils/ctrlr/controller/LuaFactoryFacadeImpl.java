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
package org.jsynthlib.utils.ctrlr.controller;

import java.util.List;

import org.jsynthlib.utils.ctrlr.controller.lua.AssembleValuesController;
import org.jsynthlib.utils.ctrlr.controller.lua.AssembleValuesFromBankController;
import org.jsynthlib.utils.ctrlr.controller.lua.AssignValuesController;
import org.jsynthlib.utils.ctrlr.controller.lua.AssignValuesToBankController;
import org.jsynthlib.utils.ctrlr.controller.lua.GetNameMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.LoadBankMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.LoadMenuController;
import org.jsynthlib.utils.ctrlr.controller.lua.LoadPatchMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.ReceiveBankMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.ReceiveMenuController;
import org.jsynthlib.utils.ctrlr.controller.lua.ReceivePatchMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.SaveBankMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.SaveMenuController;
import org.jsynthlib.utils.ctrlr.controller.lua.SavePatchMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.SetNameMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.WriteMenuController;
import org.jsynthlib.utils.ctrlr.controller.lua.WritePatchMethodController;
import org.jsynthlib.utils.ctrlr.domain.BankToPatchRelationBean;
import org.jsynthlib.utils.ctrlr.domain.WritePatchMessage;

import com.google.inject.Inject;

/**
 * @author Pascal Collberg
 */
public class LuaFactoryFacadeImpl implements LuaFactoryFacade {

    @Inject
    private AssembleValuesController.Factory assembleValuesFactory;

    @Inject
    private AssignValuesController.Factory assignValuesFactory;

    @Inject
    private GetNameMethodController.Factory getNameMethodFactory;

    @Inject
    private LoadMenuController.Factory loadMenuFactory;

    @Inject
    private LoadPatchMethodController.Factory loadMethodFactory;

    @Inject
    private ReceiveMenuController.Factory receiveMenuFactory;

    @Inject
    private ReceivePatchMethodController.Factory receiveMethodFactory;

    @Inject
    private SaveMenuController.Factory saveMenuFactory;

    @Inject
    private SavePatchMethodController.Factory saveMethodFactory;

    @Inject
    private SetNameMethodController.Factory setNameMethodFactory;

    @Inject
    private WriteMenuController.Factory writeMenuFactory;

    @Inject
    private WritePatchMethodController.Factory writeMethodFactory;

    @Inject
    private AssignValuesToBankController.Factory assignValuesToBankFactory;

    @Inject
    private LoadBankMethodController.Factory loadBankFactory;

    @Inject
    private AssembleValuesFromBankController.Factory assembleValuesFromBankFactory;

    @Inject
    private ReceiveBankMethodController.Factory receiveBankFactory;

    @Inject
    private SaveBankMethodController.Factory saveBankFactory;

    @Override
    public AssembleValuesController newAssembleValuesController() {
        AssembleValuesController controller =
                assembleValuesFactory.newAssembleValuesController();
        controller.init();
        return controller;
    }

    @Override
    public AssignValuesController newAssignValuesController() {
        AssignValuesController controller =
                assignValuesFactory.newAssignValuesController();
        controller.init();
        return controller;
    }

    @Override
    public GetNameMethodController newGetNameMethodController(String[] chars) {
        GetNameMethodController controller =
                getNameMethodFactory.newGetNameMethodController(chars);
        controller.init();
        return controller;
    }

    @Override
    public GetNameMethodController newGetNameMethodController() {
        GetNameMethodController controller =
                getNameMethodFactory.newGetNameMethodController();
        controller.init();
        return controller;
    }

    @Override
    public LoadMenuController newLoadMenuController() {
        LoadMenuController controller = loadMenuFactory.newLoadMenuController();
        controller.init();
        return controller;
    }

    @Override
    public LoadPatchMethodController newLoadPatchMethodController() {
        LoadPatchMethodController controller =
                loadMethodFactory.newLoadPatchMethodController();
        controller.init();
        return controller;
    }

    @Override
    public ReceiveMenuController newReceiveMenuController() {
        ReceiveMenuController controller =
                receiveMenuFactory.newReceiveMenuController();
        controller.init();
        return controller;
    }

    @Override
    public ReceivePatchMethodController newReceivePatchMethodController(
            List<String> midiMessages, List<String> popupList) {
        ReceivePatchMethodController controller =
                receiveMethodFactory.newReceivePatchMethodController(
                        midiMessages, popupList);
        controller.init();
        return controller;
    }

    @Override
    public SaveMenuController newSaveMenuController() {
        SaveMenuController controller = saveMenuFactory.newSaveMenuController();
        controller.init();
        return controller;
    }

    @Override
    public SavePatchMethodController newSavePatchMethodController() {
        SavePatchMethodController controller =
                saveMethodFactory.newSavePatchMethodController();
        controller.init();
        return controller;
    }

    @Override
    public SetNameMethodController newSetNameMethodController(
            String[] patchNameChars) {
        SetNameMethodController controller =
                setNameMethodFactory.newSetNameMethodController(patchNameChars);
        controller.init();
        return controller;
    }

    @Override
    public SetNameMethodController newSetNameMethodController() {
        SetNameMethodController controller =
                setNameMethodFactory.newSetNameMethodController();
        controller.init();
        return controller;
    }

    @Override
    public WriteMenuController newWriteMenuController() {
        WriteMenuController controller =
                writeMenuFactory.newWriteMenuController();
        controller.init();
        return controller;
    }

    @Override
    public WritePatchMethodController newWriteMethodController(
            boolean variableBanks, boolean variablePatches,
            List<WritePatchMessage> writeMsgList, List<String> popups) {
        WritePatchMethodController controller =
                writeMethodFactory.newWriteMethodController(variableBanks,
                        variablePatches, writeMsgList, popups);
        controller.init();
        return controller;
    }

    @Override
    public AssignValuesToBankController newAssignValuesToBankController(
            List<BankToPatchRelationBean> putPatchData) {
        AssignValuesToBankController controller =
                assignValuesToBankFactory
                .newAssignValuesToBankController(putPatchData);
        controller.init();
        return controller;
    }

    @Override
    public LoadBankMethodController newLoadBankMethodController() {
        LoadBankMethodController controller =
                loadBankFactory.newLoadBankMethodController();
        controller.init();
        return controller;
    }

    @Override
    public AssembleValuesFromBankController newAssembleValuesFromBankController(
            List<BankToPatchRelationBean> putPatchData) {
        AssembleValuesFromBankController controller =
                assembleValuesFromBankFactory
                .newAssembleValuesFromBankController(putPatchData);
        controller.init();
        return controller;
    }

    @Override
    public ReceiveBankMethodController newReceiveBankMethodController(
            List<String> midiMessages, List<String> popupList) {
        ReceiveBankMethodController controller =
                receiveBankFactory.newReceiveBankMethodController(midiMessages,
                        popupList);
        controller.init();
        return controller;
    }

    @Override
    public SaveBankMethodController newSaveBankMethodController() {
        SaveBankMethodController controller =
                saveBankFactory.newSaveBankMethodController();
        controller.init();
        return controller;
    }

}
