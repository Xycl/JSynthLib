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
package org.jsynthlib.utils.ctrlr.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.device.model.XMLBankDriver;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.utils.SingletonMidiDeviceProvider.MidiRecordSession;
import org.jsynthlib.utils.ctrlr.CtrlrMidiService;
import org.jsynthlib.utils.ctrlr.DriverParseException;
import org.jsynthlib.utils.ctrlr.controller.LuaFactoryFacade;
import org.jsynthlib.utils.ctrlr.controller.lua.LoadBankMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.ReceiveBankMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.SaveBankMethodController;
import org.jsynthlib.utils.ctrlr.domain.BankToPatchRelationBean;
import org.jsynthlib.utils.ctrlr.domain.MethodDescriptionPair;
import org.jsynthlib.utils.ctrlr.service.LuaMethodProvider;
import org.jsynthlib.utils.ctrlr.service.PopupManager.PopupSession;
import org.jsynthlib.utils.ctrlr.service.XmlDriverParser;
import org.jsynthlib.utils.ctrlr.service.codeparser.BankDriverAnalyzer;
import org.jsynthlib.xmldevice.XmlBankDriverDefinitionDocument.XmlBankDriverDefinition;
import org.jsynthlib.xmldevice.XmlDriverDefinition;

import com.google.inject.Inject;

/**
 * @author Pascal Collberg
 */
public class XmlBankDriverParser extends XmlDriverParser {

    private static final int MAX_VALUE = 16380;

    private final XmlBankDriverDefinition driverDef;

    @Inject
    private BankDriverAnalyzer bankDriverAnalyzer;

    @Inject
    private CtrlrMidiService midiService;

    @Inject
    private LuaFactoryFacade luaFacade;

    @Inject
    @Named("editor")
    private LuaMethodProvider luaMethodProvider;

    @Inject
    @Named("className")
    private String driverClassName;

    @Inject
    public XmlBankDriverParser(XmlDriverDefinition driverDef) {
        this.driverDef = (XmlBankDriverDefinition) driverDef;
    }

    @Override
    protected void parseDriver() throws DriverParseException {
        String description = "Bank";
        // List<BankToPatchRelationBean> putPatchData = parsePutPatch();
        // List<BankToPatchRelationBean> putPatchData =
        // new ArrayList<BankToPatchRelationBean>();
        luaFacade.newAssignBankController();
        // luaFacade.newAssembleValuesFromBankController(putPatchData);

        try {
            Class<? extends XMLBankDriver> bankDriverClass =
                    (Class<? extends XMLBankDriver>) Class.forName(driverClassName);
            bankDriverAnalyzer.parseBankDriver(bankDriverClass);
        } catch (ClassNotFoundException e) {
            throw new DriverParseException(e);
        } catch (IOException e) {
        }
        LoadBankMethodController loadBankController =
                luaFacade.newLoadBankMethodController();
        luaMethodProvider.addLoadMenuOption(new MethodDescriptionPair(
                loadBankController.getMethodName(), description));
        SaveBankMethodController saveBankController =
                luaFacade.newSaveBankMethodController();
        luaMethodProvider.addSaveMenuOption(new MethodDescriptionPair(
                saveBankController.getMethodName(), description));
        ReceiveBankMethodController receiveBankController =
                parseBankDumpMethod();
        luaMethodProvider.addReceiveMenuOption(new MethodDescriptionPair(
                receiveBankController.getMethodName(), "Bank"));
    }

    ReceiveBankMethodController parseBankDumpMethod() {
        MidiRecordSession midiRecordSession = midiService.openSession();
        PopupSession popupSession = getPopupManager().openSession();
        getDriver().requestPatchDump(0, 0);
        String midiMessages = midiService.closeSession(midiRecordSession);
        List<String> popups = getPopupManager().closeSession(popupSession);
        List<String> msgList = new ArrayList<String>();

        String[] split = midiMessages.split(";");
        if (split.length > 1) {
            msgList.addAll(Arrays.asList(split));
            msgList.remove("");
        }

        return luaFacade.newReceiveBankMethodController(msgList, popups);

    }

    List<BankToPatchRelationBean> parsePutPatch() throws DriverParseException {
        XMLBankDriver xmlBankDriver = (XMLBankDriver) getDriver();
        IDriver singleDriver = xmlBankDriver.getSingleDriver();
        Patch patch = singleDriver.createPatch();
        if (patch.sysex.length > MAX_VALUE * 2) {
            throw new IllegalArgumentException("Patch size cannot be handled");
        }
        preparePatch(patch);

        Patch bankPatch = xmlBankDriver.createPatch();

        List<BankToPatchRelationBean> bankToPatchRelations =
                new ArrayList<BankToPatchRelationBean>();
        for (int i = 0; i < driverDef.getNumPatches(); i++) {
            byte[] bSysex = bankPatch.sysex;
            Arrays.fill(bSysex, (byte) 0);
            xmlBankDriver.putPatch(bankPatch, patch, i);

            BankToPatchRelationBean relationBean =
                    new BankToPatchRelationBean();
            relationBean.setPatchNumber(i);
            boolean foundNonZeroValue = false;
            for (int j = 0; j < bSysex.length; j++) {
                byte b = bSysex[j];
                if (foundNonZeroValue) {
                    // Check that two consecutive bytes are zero
                    if (b == 0 && bSysex[j + 1] == 0) {
                        relationBean.setDataSize(j
                                - relationBean.getBankDataOffset());
                        break;
                    }
                } else if (b != 0) {
                    foundNonZeroValue = true;
                    relationBean.setBankDataOffset(j);
                    byte[] pSysex = patch.sysex;

                    // Check that two consecutive bytes match
                    for (int k = 0; k < pSysex.length; k++) {
                        if (b == pSysex[k] && bSysex[j + 1] == pSysex[k + 1]) {
                            relationBean.setSingleDataOffset(k);
                            break;
                        }

                    }
                }
            }
            bankToPatchRelations.add(relationBean);
        }

        int defaultDataSize = -1;
        int defaultDataOffset = -1;
        for (BankToPatchRelationBean relationBean : bankToPatchRelations) {
            if (relationBean.getDataSize() == 0) {
                throw new DriverParseException("data size is 0");
            }
            if (defaultDataSize == -1) {
                defaultDataSize = relationBean.getDataSize();
            } else if (relationBean.getDataSize() != defaultDataSize) {
                throw new DriverParseException("data size mismatch");
            }

            if (defaultDataOffset == -1) {
                defaultDataOffset = relationBean.getSingleDataOffset();
            } else if (relationBean.getSingleDataOffset() != defaultDataOffset) {
                throw new DriverParseException("data offset mismatch");
            }
        }
        return bankToPatchRelations;
    }

    void preparePatch(Patch singlePatch) {
        byte[] sysex = singlePatch.sysex;
        boolean foundZeroByte = false;
        int increment = 1;
        for (int i = 0; i < sysex.length; i += increment) {
            if (foundZeroByte) {
                byte[] bytes = getBytes(MAX_VALUE - i);
                System.arraycopy(bytes, 0, sysex, i, bytes.length);
            } else {
                if (sysex[i] == 0 && sysex[i + 1] == 0) {
                    foundZeroByte = true;
                    increment = 2;
                    byte[] bytes = getBytes(MAX_VALUE - i);
                    System.arraycopy(bytes, 0, sysex, i, bytes.length);
                }
            }
        }
    }

    byte[] getBytes(int value) {
        byte[] bs = new byte[2];
        bs[0] = (byte) (value / 128);
        bs[1] = (byte) (value % 128);
        return bs;
    }

    int getInt(byte[] bytes) {
        int val = bytes[0] * 128;
        val += bytes[1];
        return val;
    }
}
