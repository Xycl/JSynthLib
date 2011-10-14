/*
 * Copyright 2009 Peter Geirnaert
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
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

package synthdrivers.YamahaCS2x;
import core.*;

import java.io.*;
import java.text.*;
import javax.swing.*;
/**
 * There is no bank sysex format for the CS2x so 
 * the author decided to put all 128 performances of one bank together.
 * @author Peter Geirnaert
 */
public class YamahaCS2xBank2Driver extends BankDriver {
    
    static final int PATCHNAME_SIZE = 8; //name of a single performance
    static final int BANK_NB_PATCHES = 128;
    static final int FOOTER_SIZE = PATCHNAME_SIZE;
    static final int DATA_START = 9+PATCHNAME_SIZE;
    static final int PATCHNAME_OFFSET = 9;
    static final int BANK_SIZE = 128*339;
    static final int NB_COLUMNS = 4;
    static final int NB_ROWS = BANK_NB_PATCHES/NB_COLUMNS;
    static final int com1=63; //size of common sysex part 1
    static final int com2=com1+32; //size of common sysex part 1+2
    static final int com3=com2+20; //size of common sysex part 1+2+3
    static final int lyr=56; //size of a layer sysex part

    //static private YamahaCS2xBank2Driver mInstance;
    final static SysexHandler SYS_REQ_COMMON = new SysexHandler
            // 0  1  2  3     4         5      6  7  
            ("F0 43 20 63 *banknum* *patchnum* *part* F7"); //from commondriver
    final static SysexHandler SYS_REQ_LAYER = new SysexHandler
            // 0  1  2  3     4         5      6  7  
            ("F0 43 20 63 *banknum* *patchnum* 00 F7"); //FROM LAYERSDRIVER

    /**
     *  Constructor for the YamahaFS1RBankDriver object */
    public YamahaCS2xBank2Driver() {
        super ("Bank","Peter Geirnaert",BANK_NB_PATCHES,NB_COLUMNS);
	sysexID = "F0430*";
	deviceIDoffset = 2;
	singleSize = -1;
	bankNumbers = new String[]{"User Bank 1", "User Bank 2"};
	patchNumbers = DriverUtil.generateNumbers(1, 128, "u##");
	patchNameStart = PATCHNAME_OFFSET;	
	patchNameSize = 8; //name of the bank is based on sysex[6] of the first performance
	patchSize = BANK_SIZE;
    }

    /**
    static YamahaCS2xBank2Driver getInstance() {
	if (mInstance == null) {
            mInstance = new YamahaCS2xBank2Driver();
        }
        return mInstance;
	}
    */
    public void requestPatchDump(int bankNum, int patchNum) {
        //core.PatchEdit.showWaitDialog("requesting all performances of the selected bank,\n wait 3 minutes please,\n there's a lot to request and the CS2x is limited in speed.");
        int i;
        int pN = 0x00;
        int bN = 0x70;
        int pT;
        for (i=0;i<CS2x.numPerfInBank;i++) {
            /** trying to get a wait dialog showing when the request is being done.
            switch(i) {
                case 1: core.PatchEdit.showWaitDialog("requesting all performances of the selected bank,\n wait 3 minutes please,\n there's a lot to request and the CS2x is limited in speed.");
                break;
                case 127: core.PatchEdit.hideWaitDialog();
                break;
                //default: break;
            }
            */
            switch (bankNum) {
                case 0: bN = 0x70; pN = (byte)i;
                break;
                case 1: bN = 0x78; pN = (byte)i;
                break;
            }
            pT = (byte)0x00;
            final SysexHandler.NameValue[] nameValues = {
                new SysexHandler.NameValue("patchnum", pN),
                new SysexHandler.NameValue("banknum", bN),
                new SysexHandler.NameValue("part", pT)
            };
            send(SYS_REQ_COMMON.toSysexMessage(getDeviceID(), nameValues));
            CS2x.sleep();
            pT = (byte)0x40;
            final SysexHandler.NameValue[] nameValues2 = {
                new SysexHandler.NameValue("patchnum", pN),
                new SysexHandler.NameValue("banknum", bN),
                new SysexHandler.NameValue("part", pT)
            };
            send(SYS_REQ_COMMON.toSysexMessage(getDeviceID(), nameValues2));
            CS2x.sleep();
            pT = (byte)0x60;
            final SysexHandler.NameValue[] nameValues3 = {
                new SysexHandler.NameValue("patchnum", pN),
                new SysexHandler.NameValue("banknum", bN),
                new SysexHandler.NameValue("part", pT)
            };
            send(SYS_REQ_COMMON.toSysexMessage(getDeviceID(), nameValues3));
            CS2x.sleep();
            final SysexHandler.NameValue[] nameValues4 = {
                new SysexHandler.NameValue("patchnum", pN),
                new SysexHandler.NameValue("banknum", bN+1)
            };
            send(SYS_REQ_LAYER.toSysexMessage(getDeviceID(), nameValues4));
            CS2x.sleep();
            final SysexHandler.NameValue[] nameValues5 = {
                new SysexHandler.NameValue("patchnum", pN),
                new SysexHandler.NameValue("banknum", bN+2)
            };
            send(SYS_REQ_LAYER.toSysexMessage(getDeviceID(), nameValues5));
            CS2x.sleep();
            final SysexHandler.NameValue[] nameValues6 = {
                new SysexHandler.NameValue("patchnum", pN),
                new SysexHandler.NameValue("banknum", bN+3)
            };
            send(SYS_REQ_LAYER.toSysexMessage(getDeviceID(), nameValues6));
            CS2x.sleep();
            final SysexHandler.NameValue[] nameValues7 = {
                new SysexHandler.NameValue("patchnum", pN),
                new SysexHandler.NameValue("banknum", bN+4)
            };
            send(SYS_REQ_LAYER.toSysexMessage(getDeviceID(), nameValues7));
            CS2x.sleep();
        }
    }
    public String getPatchName(Patch ip) {
        return (ip.sysex[6] == 0x70 ? "User Bank 1":"User Bank 2");
    }

    /** Gets the start of a performance range of massages (7) */
    public int getPatchStart(int patchNum) {
        int start = patchNum*339;
	return start;
    }
    
    /**Gets a patch from the bank, converting it as needed*/
    public Patch getPatch(Patch bank,int patchNum) {
        int oStart = getPatchStart(patchNum);
        int oSize = 339;
        byte oPatch[] = new byte[oSize];
        System.arraycopy(((Patch)bank).sysex, oStart, oPatch, 0, oSize);
        return new Patch(oPatch);
    }

    /**Puts a patch into the bank, converting it as needed*/
    public void putPatch(Patch bank,Patch p, int patchNum) {
	int oStart = getPatchStart(patchNum);
	int oSize = 339;
        int csize = 341;
        if (oSize == ((Patch)p).sysex.length) {
            System.arraycopy(((Patch)p).sysex, 0, ((Patch)bank).sysex, oStart, oSize);
        } else if (csize == ((Patch)p).sysex.length && ((Patch)p).sysex[6] == (byte)0x60) {
            System.arraycopy(((Patch)p).sysex, 0, ((Patch)bank).sysex, oStart, 95);
            System.arraycopy(((Patch)p).sysex, 97, ((Patch)bank).sysex, 95, 20);
        }
    }
    
    /** Gets the patchName of a patch in the bank
     * @param  ip        bank sysex
     *@param  patchNum  number of the patch in the bank
     *@return           The patchName */
    public String getPatchName(Patch ip, int patchNum) {
        Patch p = (Patch)ip;
        int oPatchStart = getPatchStart(patchNum);
	//System.out.println("getPatchName "+patchNum+" start = "+oPatchStart);
        try { 
            String name = new String(p.sysex, oPatchStart+PATCHNAME_OFFSET, patchNameSize, "US-ASCII");
            //System.out.println(name+" "+patchNum);
            return name;
        } catch (UnsupportedEncodingException ex) { 
            return "no name? :(";
        }
        
    }
    
    /** Sets the name of a performance in this bank
     *@param  ip        bank patch
     *@param  patchNum  patch, in the bank, to give new name
     *@param  name      new patchName value */
    public void setPatchName(Patch ip, int patchNum, String name) {
        Patch p = (Patch)ip;
        int oPatchStart = getPatchStart(patchNum);
        while (name.length() < 8) 
            name = name + " ";
        byte[] namebytes = new byte[8];
        try {
            namebytes = name.getBytes("US-ASCII");
            for (int i = 0; i < 8; i++)
                p.sysex[oPatchStart+PATCHNAME_OFFSET + i] = namebytes[i];
        } catch (UnsupportedEncodingException ex) { return; 
        }
    }

    /** calculate checksum of each sysexmessage inside the bankpatch:
     * 128*7 messages have to be checked. */
    public void calculateChecksum(Patch p)
    {
        int i;
        int oPatchStart;
        for (i=0;i<128;i++) { // for each performance, do this:
            oPatchStart = getPatchStart(i); //find the start of the performance in the bank
            calculateChecksum(p,oPatchStart+4,oPatchStart+60,oPatchStart+61); //common part1
            calculateChecksum(p,oPatchStart+com1+4,oPatchStart+com1+29,oPatchStart+com1+30); //common part2
            calculateChecksum(p,oPatchStart+com2+4,oPatchStart+com2+17,oPatchStart+com2+18); //common part3
            calculateChecksum(p,oPatchStart+com3+4,oPatchStart+com3+53,oPatchStart+com3+54); //layer 1
            calculateChecksum(p,oPatchStart+com3+lyr+4,oPatchStart+com3+lyr+53,oPatchStart+com3+lyr+54); //layer 2
            calculateChecksum(p,oPatchStart+com3+(2*lyr)+4,oPatchStart+com3+(2*lyr)+53,oPatchStart+com3+(2*lyr)+54); //layer 3
            calculateChecksum(p,oPatchStart+com3+(3*lyr)+4,oPatchStart+com3+(3*lyr)+53,oPatchStart+com3+(3*lyr)+54); //layer 4
        }
    }
        
    /** 
     * Store the bankPatch in the synth
     * @param bankNum the bank to store this bank in.
     */
    public void storePatch (Patch p, int bankNum,int patchNum) {
        //setup some bytes to use later
        byte bc=(byte)0x70; byte bl1; byte bl2; byte bl3; byte bl4; //the bytes about the bank we're working with
        byte bs = p.sysex[6]; //the source byte about the bank
        switch (bankNum) { //what's the destination ?
            case 0: // it's User Bank 1
                bc=0x70; bl1=0x71; bl2=0x72; bl3=0x73; bl4=0x74; //initialize the bytes for later
                break;
            case 1: // it's User Bank 2
                bc=0x78; bl1=0x79; bl2=0x7A; bl3=0x7B; bl4=0x7C; //initialize the bytes for later
                break;
        }
        //setup some byte arrays to use as buffers
        byte[] cmn1 = new byte[63];
        byte[] cmn2 = new byte[32];
        byte[] cmn3 = new byte[20];
        byte[] lyrX = new byte[56];
        //now the action:
        int i;
        int oPatchStart;
        if (bc == bs) { //the source bank is the same as the destination bank, just send all messages
            for (i=0;i<128;i++) { // for each performance, do this:
                oPatchStart = getPatchStart(i); //find the start of the performance in the bank
            //common part1
                System.arraycopy(p, oPatchStart, cmn1, 0, com1);
                send(cmn1);
                CS2x.sleep();
            //common part2
                System.arraycopy(p, oPatchStart+com1, cmn2, 0, com2-com1);
                send(cmn2);
                CS2x.sleep();
            //common part3
                System.arraycopy(p, oPatchStart+com2, cmn3, 0, com3-com2);
                send(cmn3);
                CS2x.sleep();
            //layer 1
                System.arraycopy(p, oPatchStart+com3, lyrX, 0, lyr);
                send(lyrX);
                CS2x.sleep();
            //layer 2
                System.arraycopy(p, oPatchStart+com3+lyr, lyrX, 0, lyr);
                send(lyrX);
                CS2x.sleep();
            //layer 3
                System.arraycopy(p, oPatchStart+com3+(2*lyr), lyrX, 0, lyr);
                send(lyrX);
                CS2x.sleep();
            //layer 4
                System.arraycopy(p, oPatchStart+com3+(3*lyr), lyrX, 0, lyr);
                send(lyrX);
                CS2x.sleep();
            }
        } else { //replace the bytes about the bank
            for (i=0;i<128;i++) { // for each performance, do this:
                oPatchStart = getPatchStart(i); //find the start of the performance in the bank
            //common part1
                System.arraycopy(p, oPatchStart, cmn1, 0, com1);
                cmn1[6] = bc;
                DriverUtil.calculateChecksum(cmn1,4,60,61);
                send(cmn1);
                CS2x.sleep();
            //common part2
                System.arraycopy(p, oPatchStart+com1, cmn2, 0, com2-com1);
                cmn2[6] = bc;
                DriverUtil.calculateChecksum(cmn2,4,29,30);
                send(cmn2);
                CS2x.sleep();
            //common part3
                System.arraycopy(p, oPatchStart+com2, cmn3, 0, com3-com2);
                cmn3[6] = bc;
                DriverUtil.calculateChecksum(cmn3,4,17,18);
                send(cmn3);
                CS2x.sleep();
            //layer 1
                System.arraycopy(p, oPatchStart+com3, lyrX, 0, lyr);
                lyrX[6] = bc;
                DriverUtil.calculateChecksum(lyrX,4,53,54);
                send(lyrX);
                CS2x.sleep();
            //layer 2
                System.arraycopy(p, oPatchStart+com3+lyr, lyrX, 0, lyr);
                lyrX[6] = bc;
                DriverUtil.calculateChecksum(lyrX,4,53,54);
                send(lyrX);
                CS2x.sleep();
            //layer 3
                System.arraycopy(p, oPatchStart+com3+(2*lyr), lyrX, 0, lyr);
                lyrX[6] = bc;
                DriverUtil.calculateChecksum(lyrX,4,53,54);
                send(lyrX);
                CS2x.sleep();
            //layer 4
                System.arraycopy(p, oPatchStart+com3+(3*lyr), lyrX, 0, lyr);
                lyrX[6] = bc;
                DriverUtil.calculateChecksum(lyrX,4,53,54);
                send(lyrX);
                CS2x.sleep();
            }
        }
        
    }

    /**
	 *  Bank factory
	 *
	 * @return    the new "empty" bank
	 */
	public Patch createNewPatch()
	{
	//System.out.println("createNewPatch");
		byte[] sysex = new byte[128*339];
                byte[] initPerf = new byte[339];
                InputStream fileIn= getClass().getResourceAsStream("InitPerf.syx");
                try {
                    fileIn.read(initPerf);
                    fileIn.close();
                } catch (Exception e) {
                    ErrorMsg.reportError("Error","Unable to find InitPerf.syx",e);
                }
                int oPatchStart;
		for (int i = 0; i < 128; i++) {
                    oPatchStart = getPatchStart(i);
                    System.arraycopy(initPerf,0,sysex,oPatchStart,339);
                }
		return new Patch(sysex, this);
	}

	/**
		FS1R bank holds 2 types of patch, performance and voice.
	*/
    protected boolean canHoldPatch(Patch p) {
        //if (p.sysex[1]==(byte)0x43 && p.sysex[3]==(byte)0x63 && p.sysex[4]==(byte)0x00 && p.sysex[5]==(byte)0x34) {// check header
        return (((Patch)p).sysex.length == 339 || ((Patch)p).sysex.length == 341);//check size
        //} else {
            //return false;
        //}
    }

}



