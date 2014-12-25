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

import org.apache.log4j.Logger;
import org.jsynthlib.utils.ctrlr.service.codeparser.FieldWrapper.FieldType;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * @author Pascal Collberg
 *
 */
public class DefaultMethodVisitor extends MethodVisitorBase {

    public interface Factory {
        DefaultMethodVisitor newDefaultMethodVisitor(Class<?> parsedClass,
                MethodWrapper parsedMethod);
    }

    private final transient Logger log = Logger.getLogger(getClass());

    @Inject
    public DefaultMethodVisitor(@Assisted MethodWrapper parsedMethod,
            @Assisted Class<?> parsedClass) {
        super(parsedMethod, parsedClass);
    }

    @Override
    public Void visitFormalParameterList(FormalParameterListContext ctx) {
        List<FormalParameterContext> formalParameter = ctx.formalParameter();
        boolean first = true;
        for (FormalParameterContext formalParamCtx : formalParameter) {
            FieldWrapper field = new FieldWrapper();
            String name = formalParamCtx.variableDeclaratorId().getText();
            field.setName(name);
            field.setLuaName(name);

            try {
                FieldType fieldType =
                        FieldType
                        .getFromString(formalParamCtx.type().getText());
                field.setType(fieldType);
            } catch (IllegalArgumentException e) {
                log.warn(e.getMessage(), e);
            }
            putLocalVariable(field.getName(), field);

            if (first) {
                first = false;
            } else {
                getCode().append(", ");
            }
            getCode().append(field.getLuaName());
        }
        return null;
    }

}
