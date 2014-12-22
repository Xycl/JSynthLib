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
package org.jsynthlib.utils.ctrlr.service.codeparser;

import java.util.ArrayList;
import java.util.List;

import org.jsynthlib.utils.ctrlr.controller.LuaFactoryFacade;
import org.jsynthlib.utils.ctrlr.controller.lua.AssembleValuesController;
import org.jsynthlib.utils.ctrlr.controller.lua.AssembleValuesFromBankController;
import org.jsynthlib.utils.ctrlr.controller.lua.AssignValuesController;
import org.jsynthlib.utils.ctrlr.controller.lua.AssignValuesToBankController;
import org.jsynthlib.utils.ctrlr.controller.lua.GetNameMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.JavaParsedMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.LoadBankMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.LoadMenuController;
import org.jsynthlib.utils.ctrlr.controller.lua.LoadPatchMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.ReceiveBankMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.ReceiveMenuController;
import org.jsynthlib.utils.ctrlr.controller.lua.ReceivePatchMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.SaveBankMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.SaveMenuController;
import org.jsynthlib.utils.ctrlr.controller.lua.SavePatchMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.SelectPatchMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.SetNameMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.WriteMenuController;
import org.jsynthlib.utils.ctrlr.controller.lua.WritePatchMethodController;
import org.jsynthlib.utils.ctrlr.domain.BankToPatchRelationBean;
import org.jsynthlib.utils.ctrlr.domain.WritePatchMessage;

class TestLuaFactoryFacade implements LuaFactoryFacade {

    private final List<JavaParsedMethodController> list;

    public TestLuaFactoryFacade() {
        list = new ArrayList<JavaParsedMethodController>();
    }

    @Override
    public AssembleValuesController newAssembleValuesController() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AssignValuesController newAssignValuesController() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GetNameMethodController newGetNameMethodController(String[] chars) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GetNameMethodController newGetNameMethodController() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LoadMenuController newLoadMenuController() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LoadPatchMethodController newLoadPatchMethodController() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReceiveMenuController newReceiveMenuController() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReceivePatchMethodController newReceivePatchMethodController(
            List<String> midiMessages, List<String> popupList) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SaveMenuController newSaveMenuController() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SavePatchMethodController newSavePatchMethodController() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SetNameMethodController newSetNameMethodController(
            String[] patchNameChars) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SetNameMethodController newSetNameMethodController() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public WriteMenuController newWriteMenuController() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public WritePatchMethodController newWriteMethodController(
            boolean variableBanks, boolean variablePatches,
            List<WritePatchMessage> writeMsgList, List<String> popups) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AssignValuesToBankController newAssignValuesToBankController(
            List<BankToPatchRelationBean> putPatchData) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LoadBankMethodController newLoadBankMethodController() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AssembleValuesFromBankController newAssembleValuesFromBankController(
            List<BankToPatchRelationBean> putPatchData) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReceiveBankMethodController newReceiveBankMethodController(
            List<String> midiMessages, List<String> popupList) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SaveBankMethodController newSaveBankMethodController() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SelectPatchMethodController newSelectPatchMethodController() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JavaParsedMethodController newJavaParsedMethodController(
            String methodName) {
        JavaParsedMethodController controller =
                new JavaParsedMethodController(methodName);
        list.add(controller);
        return controller;
    }

    public List<JavaParsedMethodController> getList() {
        return list;
    }

}