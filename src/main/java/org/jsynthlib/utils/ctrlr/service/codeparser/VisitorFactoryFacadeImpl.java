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

import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.device.model.XMLBankDriver;
import org.jsynthlib.utils.ctrlr.domain.DriverTypeModel;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Pascal Collberg
 */
@Singleton
public class VisitorFactoryFacadeImpl implements VisitorFactoryFacade {

    @Inject
    private DriverTypeModel driverTypeModel;

    @Inject
    private DefaultMethodVisitor.Factory defaultMethodFactory;

    @Inject
    private GetPatchMethodVisitor.Factory getPatchMethodFactory;

    @Inject
    private PutPatchMethodVisitor.Factory putPatchMethodFactory;

    @Inject
    private CalculateChecksumMethodVisitor.Factory checksumMethodFactory;

    @Override
    public DefaultMethodVisitor newDefaultMethodVisitor(Class<?> parsedClass,
            MethodWrapper method) {
        return defaultMethodFactory
                .newDefaultMethodVisitor(parsedClass, method);
    }

    @Override
    public GetPatchMethodVisitor newGetPatchMethodVisitor(Class<?> parsedClass,
            MethodWrapper parsedMethod) {
        return getPatchMethodFactory.newGetPatchMethodVisitor(parsedClass,
                parsedMethod);
    }

    @Override
    public PutPatchMethodVisitor newPutPatchMethodVisitor(Class<?> parsedClass,
            MethodWrapper parsedMethod) {
        return putPatchMethodFactory.newPutPatchMethodVisitor(parsedClass,
                parsedMethod);
    }

    @Override
    public MethodVisitorBase newMethodVisitor(Class<?> currClass,
            MethodWrapper method) {
        String methodName = method.getName();
        if (methodName.equals("getPatch")
                && XMLBankDriver.class.isAssignableFrom(currClass)) {
            return newGetPatchMethodVisitor(currClass, method);
        } else if (methodName.equals("putPatch")
                && XMLBankDriver.class.isAssignableFrom(currClass)) {
            return newPutPatchMethodVisitor(currClass, method);
        } else if (methodName.equals("calculateChecksum")
                && IDriver.class.isAssignableFrom(currClass)) {
            return newCalculateChecksumMethodVisitor(currClass, method);
        } else {
            return newDefaultMethodVisitor(currClass, method);
        }
    }

    @Override
    public CalculateChecksumMethodVisitor newCalculateChecksumMethodVisitor(
            Class<?> parsedClass, MethodWrapper parsedMethod) {
        driverTypeModel.setCalculateChecksumMethodName(parsedMethod
                .getLuaName());
        return checksumMethodFactory.newCalculateChecksumMethodVisitor(
                parsedClass, parsedMethod);
    }

}
