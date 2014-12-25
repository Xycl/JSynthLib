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

import org.jsynthlib.utils.ctrlr.domain.CtrlrPanelModel;
import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.xmldevice.XmlBankDriverDefinitionDocument.XmlBankDriverDefinition;

class TestVisitorFactoryFacade implements VisitorFactoryFacade {

    private static final String TEST = "test";
    private final TestLuaFactoryFacade luaFactoryFacade;
    private final XmlBankDriverDefinition bankDriverDef;
    private final DriverModel model;
    private final CtrlrPanelModel panelModel;

    public TestVisitorFactoryFacade(TestLuaFactoryFacade luaFactoryFacade,
            XmlBankDriverDefinition bankDriverDef, CtrlrPanelModel panelModel) {
        this.luaFactoryFacade = luaFactoryFacade;
        this.panelModel = panelModel;
        this.bankDriverDef = bankDriverDef;
        model = new DriverModel(TEST);
    }

    @Override
    public PutPatchMethodVisitor newPutPatchMethodVisitor(Class<?> parsedClass,
            MethodWrapper methodName) {
        PutPatchMethodVisitor visitor =
                new PutPatchMethodVisitor(model, parsedClass, methodName);
        initVisitor(visitor);
        return visitor;
    }

    void initVisitor(MethodVisitorBase visitor) {
        visitor.setPrefix(TEST);
        visitor.setLuaFactory(luaFactoryFacade);
        visitor.setPanelModel(panelModel);
        visitor.setDriverDef(bankDriverDef);
    }

    @Override
    public GetPatchMethodVisitor newGetPatchMethodVisitor(Class<?> parsedClass,
            MethodWrapper methodName) {
        GetPatchMethodVisitor visitor =
                new GetPatchMethodVisitor(methodName, parsedClass, model);
        initVisitor(visitor);
        return visitor;
    }

    @Override
    public DefaultMethodVisitor newDefaultMethodVisitor(Class<?> parsedClass,
            MethodWrapper methodName) {
        DefaultMethodVisitor visitor =
                new DefaultMethodVisitor(methodName, parsedClass);
        initVisitor(visitor);
        return visitor;
    }

    @Override
    public MethodVisitorBase newMethodVisitor(Class<?> currClass,
            MethodWrapper method) {
        // TODO Auto-generated method stub
        return null;
    }
}