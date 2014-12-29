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

import org.jsynthlib.utils.ctrlr.service.codeparser.FieldWrapper.FieldType;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * @author Pascal Collberg
 */
public class CalculateChecksumMethodVisitor extends MethodVisitorBase {

    public interface Factory {
        CalculateChecksumMethodVisitor newCalculateChecksumMethodVisitor(
                Class<?> parsedClass, MethodWrapper parsedMethod);
    }

    private FieldWrapper startField;
    private FieldWrapper endField;
    private FieldWrapper ofsField;

    @Inject
    public CalculateChecksumMethodVisitor(@Assisted Class<?> parsedClass,
            @Assisted MethodWrapper method) {
        super(method, parsedClass);
        setIgnoreReturnStatement(true);
    }

    @Override
    public Void visitFormalParameterList(FormalParameterListContext ctx) {
        List<FormalParameterContext> formalParameters = ctx.formalParameter();
        FormalParameterContext sysex = formalParameters.get(0);
        FieldWrapper field = new FieldWrapper();
        String sysexName = sysex.variableDeclaratorId().getText();
        field.setName(sysexName);
        field.setLuaName(sysexName);
        field.setType(FieldType.BYTE_ARRAY);
        putLocalVariable(field.getName(), field);

        FormalParameterContext start = formalParameters.get(1);
        startField = new FieldWrapper();
        String startName = start.variableDeclaratorId().getText();
        startField.setName(startName);
        startField.setLuaName("csStart");
        startField.setType(FieldType.INT);
        putLocalVariable(startField.getName(), startField);

        FormalParameterContext end = formalParameters.get(2);
        endField = new FieldWrapper();
        String endName = end.variableDeclaratorId().getText();
        endField.setName(endName);
        endField.setLuaName("csEnd");
        endField.setType(FieldType.INT);
        putLocalVariable(endField.getName(), endField);

        FormalParameterContext ofs = formalParameters.get(3);
        ofsField = new FieldWrapper();
        String ofsName = ofs.variableDeclaratorId().getText();
        ofsField.setName(ofsName);
        ofsField.setLuaName("csOfs");
        ofsField.setType(FieldType.INT);
        putLocalVariable(ofsField.getName(), ofsField);

        getCode().append(field.getLuaName()).append(", ")
        .append(startField.getLuaName()).append(", ")
        .append(endField.getLuaName()).append(", ")
        .append(ofsField.getLuaName());
        return null;
    }
}
