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
package org.jsynthlib.utils.ctrlr.lua;

import java.util.ArrayList;
import java.util.List;

import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;

import com.google.inject.Singleton;

/**
 * @author Pascal Collberg
 */
@Singleton
public class DriverLuaBean {

    private String driverPrefix;
    private String nameModulatorName;
    private String receiveMethodName;
    private String writeMethodName;
    private String loadMethodName;
    private String saveMethodName;
    private String getNameMethodName;
    private String setNameMethodName;
    private String assignValuesMethodName;
    private String assembleValuesMethodName;
    private String infoLabelName;
    private String[] patchNameChars;
    private int panelWidth;
    private int panelHeight;

    private final List<String> receiveMidiMessages;
    private final List<String> receivePopupList;
    private final List<WritePatchMessage> writeMsgList;
    private boolean variablePatches;
    private boolean variableBanks;
    private XmlSingleDriverDefinition singleDriverDef;

    public DriverLuaBean() {
        receiveMidiMessages = new ArrayList<String>();
        receivePopupList = new ArrayList<String>();
        writeMsgList = new ArrayList<WritePatchMessage>();
    }


    public void setDriverPrefix(String driverPrefix) {
        this.driverPrefix = driverPrefix;
        nameModulatorName = driverPrefix + "_patchNameModulator";
        infoLabelName = driverPrefix + "_infoModulator";
        receiveMethodName = driverPrefix + "_ReceivePatch";
        writeMethodName = driverPrefix + "_WritePatch";
        loadMethodName = driverPrefix + "_LoadPatch";
        saveMethodName = driverPrefix + "_SavePatch";

        getNameMethodName = driverPrefix + "_GetPatchName";
        setNameMethodName = driverPrefix + "_SetPatchName";
        assignValuesMethodName = driverPrefix + "_AssignValues";
        assembleValuesMethodName = driverPrefix + "_AssembleValues";
    }

    public String getDriverPrefix() {
        return driverPrefix;
    }

    public String getNameModulatorName() {
        return nameModulatorName;
    }

    public String getReceiveMethodName() {
        return receiveMethodName;
    }

    public String getWriteMethodName() {
        return writeMethodName;
    }

    public String getLoadMethodName() {
        return loadMethodName;
    }

    public String getSaveMethodName() {
        return saveMethodName;
    }

    public String getGetNameMethodName() {
        return getNameMethodName;
    }

    public String getSetNameMethodName() {
        return setNameMethodName;
    }

    public String getAssignValuesMethodName() {
        return assignValuesMethodName;
    }

    public String getAssembleValuesMethodName() {
        return assembleValuesMethodName;
    }

    public String getInfoLabelName() {
        return infoLabelName;
    }

    public String getAssignValuesCall(String dataAttrName,
            String sendMidiAttrName) {
        return new StringBuilder().append(assignValuesMethodName).append("(")
                .append(dataAttrName).append(", ").append(sendMidiAttrName)
                .append(")").toString();

    }

    public String getSetNameCall(String dataAttrName, String nameAttrName) {
        return new StringBuilder().append(setNameMethodName).append("(")
                .append(dataAttrName).append(", ").append(nameAttrName)
                .append(")").toString();
    }

    public String getGetNameCall(String dataAttrName) {
        return new StringBuilder().append(getNameMethodName).append("(")
                .append(dataAttrName).append(")").toString();
    }

    public String getAssembleValuesCall(String dataAttrName) {
        return new StringBuilder().append(assembleValuesMethodName).append("(")
                .append(dataAttrName).append(")").toString();
    }

    public List<StringPair> getReceiveMenuOptions() {
        return new ArrayList<StringPair>();
    }

    public List<StringPair> getLoadMenuOptions() {
        return new ArrayList<StringPair>();
    }

    public List<StringPair> getSaveMenuOptions() {
        return new ArrayList<StringPair>();
    }

    public List<StringPair> getWriteMenuOptions() {
        return new ArrayList<StringPair>();
    }

    public String[] getPatchNameChars() {
        return patchNameChars;
    }

    public void setPatchNameChars(String[] patchNameChars) {
        this.patchNameChars = patchNameChars;
    }

    public int getPatchNameSize() {
        return singleDriverDef.getPatchNameSize();
    }

    public int getPatchNameStart() {
        return singleDriverDef.getPatchNameStart();
    }

    public String getSysexID() {
        System.out.println(toString() + " getSysexID");
        return singleDriverDef.getSysexID();
    }

    public int getPatchSize() {
        return singleDriverDef.getPatchSize();
    }

    public List<String> getReceiveMidiMessages() {
        return receiveMidiMessages;
    }

    public List<String> getReceivePopupList() {
        return receivePopupList;
    }

    public static class WritePatchMessage {
        private final String message;
        private final boolean patchDataMsg;
        private int bankNbrOffset = -1;
        private int patchNbrOffset = -1;

        public WritePatchMessage(String message, boolean patchDataMsg) {
            super();
            this.message = message;
            this.patchDataMsg = patchDataMsg;
        }

        public String getMessage() {
            return message;
        }

        public boolean isPatchDataMsg() {
            return patchDataMsg;
        }

        public boolean containsBankNbr() {
            return bankNbrOffset != -1;
        }

        public boolean containsPatchNbr() {
            return patchNbrOffset != -1;
        }

        public int getBankNbrOffset() {
            return bankNbrOffset;
        }

        public void setBankNbrOffset(int bankNbrOffset) {
            this.bankNbrOffset = bankNbrOffset;
        }

        public int getPatchNbrOffset() {
            return patchNbrOffset;
        }

        public void setPatchNbrOffset(int patchNbrOffset) {
            this.patchNbrOffset = patchNbrOffset;
        }
    }

    public List<WritePatchMessage> getWriteMsgList() {
        return writeMsgList;
    }

    public boolean isVariableBanks() {
        return variableBanks;
    }

    public void setVariableBanks(boolean variableBanks) {
        this.variableBanks = variableBanks;
    }

    public boolean isVariablePatches() {
        return variablePatches;
    }

    public void setVariablePatches(boolean variablePatches) {
        this.variablePatches = variablePatches;
    }

    public XmlSingleDriverDefinition getSingleDriverDef() {
        return singleDriverDef;
    }

    public void setSingleDriverDef(XmlSingleDriverDefinition singleDriverDef) {
        System.out.println(toString() + " setSingleDriverDef");
        this.singleDriverDef = singleDriverDef;
    }

    public int getPanelWidth() {
        return panelWidth;
    }

    public void setPanelWidth(int panelWidth) {
        this.panelWidth = panelWidth;
    }

    public int getPanelHeight() {
        return panelHeight;
    }

    public void setPanelHeight(int panelHeight) {
        this.panelHeight = panelHeight;
    }

}
