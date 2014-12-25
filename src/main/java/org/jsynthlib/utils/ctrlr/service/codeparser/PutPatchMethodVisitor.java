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

import java.util.List;

import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.FormalParameterContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.FormalParameterListContext;

import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.utils.ctrlr.service.codeparser.FieldWrapper.FieldType;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * @author Pascal Collberg
 */
public class PutPatchMethodVisitor extends MethodVisitorBase {

    public interface Factory {
        PutPatchMethodVisitor newPutPatchMethodVisitor(Class<?> parsedClass,
                MethodWrapper parsedMethod);
    }

    private final String bankDataVarName;

    @Inject
    public PutPatchMethodVisitor(DriverModel model,
            @Assisted Class<?> parsedClass, @Assisted MethodWrapper parsedMethod) {
        super(parsedMethod, parsedClass);
        bankDataVarName = model.getBankDataVarName();
        setIgnoreReturnStatement(true);
    }

    @Override
    public Void visitFormalParameterList(FormalParameterListContext ctx) {
        List<FormalParameterContext> formalParameter = ctx.formalParameter();
        FormalParameterContext bankContext = formalParameter.get(0);
        FieldWrapper field = new FieldWrapper();
        field.setName(bankContext.variableDeclaratorId().getText());
        field.setLuaName(bankDataVarName);
        field.setType(FieldType.PATCH);
        putLocalVariable(field.getName(), field);

        FormalParameterContext patchContext = formalParameter.get(1);
        field = new FieldWrapper();
        field.setName(patchContext.variableDeclaratorId().getText());
        field.setLuaName(patchContext.variableDeclaratorId().getText());
        field.setType(FieldType.PATCH);
        putLocalVariable(field.getName(), field);

        getCode().append(field.getLuaName());

        FormalParameterContext patchNumContext = formalParameter.get(2);
        field = new FieldWrapper();
        field.setName(patchNumContext.variableDeclaratorId().getText());
        field.setLuaName("patchNum");
        field.setType(FieldType.INT);
        putLocalVariable(field.getName(), field);

        getCode().append(", ").append(field.getLuaName());

        return null;
    }
}
