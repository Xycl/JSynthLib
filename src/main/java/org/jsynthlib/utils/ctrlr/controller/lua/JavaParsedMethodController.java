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
package org.jsynthlib.utils.ctrlr.controller.lua;

import org.jsynthlib.utils.ctrlr.domain.PreConditionsNotMetException;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * @author Pascal Collberg
 *
 */
public class JavaParsedMethodController extends EditorLuaMethodControllerBase {

    public interface Factory {
        JavaParsedMethodController newJavaParsedMethodController(
                String methodName);
    }

    private boolean javaParserDone;
    /**
     * @param methodName
     */
    private final StringBuilder code;

    @Inject
    public JavaParsedMethodController(@Assisted String methodName) {
        super(methodName);
        code = new StringBuilder();
    }


    @Override
    protected void checkPreconditions() throws PreConditionsNotMetException {
        if (!javaParserDone) {
            throw new PreConditionsNotMetException();
        }
        super.checkPreconditions();
    }

    @Override
    protected void writeLuaMethodCode() {
        setLuaMethodCode(code.toString());
    }

    public boolean isJavaParserDone() {
        return javaParserDone;
    }

    public void setJavaParserDone(boolean javaParserDone) {
        this.javaParserDone = javaParserDone;
        init();
    }

    public StringBuilder append(Object obj) {
        return code.append(obj);
    }

    public StringBuilder append(String str) {
        return code.append(str);
    }

    public StringBuilder append(CharSequence s) {
        return code.append(s);
    }

    public StringBuilder append(char[] str) {
        return code.append(str);
    }

    public StringBuilder append(boolean b) {
        return code.append(b);
    }

    public StringBuilder append(char c) {
        return code.append(c);
    }

    public StringBuilder append(int i) {
        return code.append(i);
    }

    public StringBuilder append(long lng) {
        return code.append(lng);
    }

    public StringBuilder append(float f) {
        return code.append(f);
    }

    public StringBuilder append(double d) {
        return code.append(d);
    }

    @Override
    public String toString() {
        return code.toString();
    }

}
