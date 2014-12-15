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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Set;

import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.controller.lua.MidiReceivedDriverPart;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;

import com.google.inject.Singleton;

/**
 * @author Pascal Collberg
 *
 */
@Singleton
public class CtrlrPanelModel extends Observable {

    private PanelType panel;
    private XmlDeviceDefinition xmldevice;
    private int panelHeight;
    private int panelWidth;
    private final List<MidiReceivedDriverPart> midiReceivedParts;
    private String panelLoadedName;
    private final Map<String, String> globalVariables;

    public CtrlrPanelModel() {
        midiReceivedParts = new ArrayList<MidiReceivedDriverPart>();
        globalVariables = new HashMap<String, String>();
    }

    public XmlDeviceDefinition getXmldevice() {
        return xmldevice;
    }

    public void setXmldevice(XmlDeviceDefinition xmldevice) {
        this.xmldevice = xmldevice;
        setChanged();
        notifyObservers(xmldevice);
    }

    public PanelType getPanel() {
        return panel;
    }

    public void setPanel(PanelType panel) {
        this.panel = panel;
        setChanged();
        notifyObservers(panel);
    }

    public int getPanelHeight() {
        return panelHeight;
    }

    public int getPanelWidth() {
        return panelWidth;
    }

    public void setPanelHeight(int panelHeight) {
        this.panelHeight = panelHeight;
        setChanged();
        notifyObservers(panelHeight);
    }

    public void setPanelWidth(int panelWidth) {
        this.panelWidth = panelWidth;
        setChanged();
        notifyObservers(panelWidth);
    }

    public boolean addMidiReceivedDriverPart(MidiReceivedDriverPart e) {
        boolean add = midiReceivedParts.add(e);
        if (add) {
            setChanged();
            notifyObservers(midiReceivedParts);
        }
        return add;
    }

    public List<MidiReceivedDriverPart> getMidiReceivedParts() {
        return midiReceivedParts;
    }

    public String getPanelLoadedName() {
        return panelLoadedName;
    }

    public void setPanelLoadedName(String panelLoadedName) {
        this.panelLoadedName = panelLoadedName;
        setChanged();
        notifyObservers(panelLoadedName);
    }

    public String putGlobalVariable(String key, String value) {
        String put = globalVariables.put(key, value);
        setChanged();
        notifyObservers(globalVariables);
        return put;
    }

    public Set<Entry<String, String>> getGlobalVariableEntries() {
        return globalVariables.entrySet();
    }

}
