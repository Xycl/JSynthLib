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

/**
 * @author Pascal Collberg
 */
public interface LuaFactoryFacade
extends
org.jsynthlib.utils.ctrlr.controller.lua.AssembleValuesController.Factory,
org.jsynthlib.utils.ctrlr.controller.lua.AssignValuesController.Factory,
org.jsynthlib.utils.ctrlr.controller.lua.GetNameMethodController.Factory,
org.jsynthlib.utils.ctrlr.controller.lua.LoadMenuController.Factory,
org.jsynthlib.utils.ctrlr.controller.lua.LoadPatchMethodController.Factory,
org.jsynthlib.utils.ctrlr.controller.lua.ReceiveMenuController.Factory,
org.jsynthlib.utils.ctrlr.controller.lua.ReceivePatchMethodController.Factory,
org.jsynthlib.utils.ctrlr.controller.lua.SaveMenuController.Factory,
org.jsynthlib.utils.ctrlr.controller.lua.SavePatchMethodController.Factory,
org.jsynthlib.utils.ctrlr.controller.lua.SetNameMethodController.Factory,
org.jsynthlib.utils.ctrlr.controller.lua.WriteMenuController.Factory,
org.jsynthlib.utils.ctrlr.controller.lua.WritePatchMethodController.Factory,
org.jsynthlib.utils.ctrlr.controller.lua.AssignValuesToBankController.Factory,
org.jsynthlib.utils.ctrlr.controller.lua.LoadBankMethodController.Factory,
org.jsynthlib.utils.ctrlr.controller.lua.AssembleValuesFromBankController.Factory,
org.jsynthlib.utils.ctrlr.controller.lua.ReceiveBankMethodController.Factory,
org.jsynthlib.utils.ctrlr.controller.lua.SaveBankMethodController.Factory,
org.jsynthlib.utils.ctrlr.controller.lua.SelectPatchMethodController.Factory,
org.jsynthlib.utils.ctrlr.controller.lua.JavaParsedMethodController.Factory {

}
