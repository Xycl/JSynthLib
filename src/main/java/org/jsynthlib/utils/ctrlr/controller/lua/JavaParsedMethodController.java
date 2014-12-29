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

import java.util.ArrayList;
import java.util.List;

import org.jsynthlib.utils.ctrlr.controller.ReplacableListener;
import org.jsynthlib.utils.ctrlr.controller.Replaceable;
import org.jsynthlib.utils.ctrlr.domain.PreConditionsNotMetException;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * @author Pascal Collberg
 */
public class JavaParsedMethodController extends EditorLuaMethodControllerBase
implements ReplacableListener {

    public interface Factory {
        JavaParsedMethodController newJavaParsedMethodController(
                String methodName);
    }

    private final List<Object> items;

    private boolean javaParserDone;

    @Inject
    public JavaParsedMethodController(@Assisted String methodName) {
        super(methodName);
        items = new ArrayList<Object>();
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
        StringBuilder code = new StringBuilder();
        for (Object object : items) {
            // if (object instanceof Replaceable) {
            // throw new IllegalStateException(
            // "Unreplaced Replaceable detected");
            // }
            code.append(object);
        }
        setLuaMethodCode(code.toString());
    }

    public boolean isJavaParserDone() {
        return javaParserDone;
    }

    public void setJavaParserDone(boolean javaParserDone) {
        this.javaParserDone = javaParserDone;
        init();
    }

    public JavaParsedMethodController append(Replaceable replaceable) {
        items.add(replaceable);
        replaceable.addListener(this);
        return this;
    }

    public JavaParsedMethodController append(Object obj) {
        items.add(obj);
        return this;
    }

    public JavaParsedMethodController append(String str) {
        items.add(str);
        return this;
    }

    public JavaParsedMethodController append(CharSequence s) {
        items.add(s);
        return this;
    }

    public JavaParsedMethodController append(char[] str) {
        items.add(str);
        return this;
    }

    public JavaParsedMethodController append(boolean b) {
        items.add(b);
        return this;
    }

    public JavaParsedMethodController append(char c) {
        items.add(c);
        return this;
    }

    public JavaParsedMethodController append(int i) {
        items.add(i);
        return this;
    }

    public JavaParsedMethodController append(long lng) {
        items.add(lng);
        return this;
    }

    public JavaParsedMethodController append(float f) {
        items.add(f);
        return this;
    }

    public JavaParsedMethodController append(double d) {
        items.add(d);
        return this;
    }

    @Override
    public void onReplace(Replaceable replacable, String replacement) {
        int index = items.indexOf(replacable);
        items.set(index, replacement);
        replacable.removeListener(this);
        writeLuaMethodCode();
    }
}
