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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Pascal Collberg
 */
public class BankDriverParserModel {

    private final Map<String, Map<String, FieldWrapper>> fieldsToParse;
    private final Map<String, Map<String, MethodWrapper>> methodsToParse;
    private final Map<String, Class<?>> referencedClasses;
    private final Map<String, Map<String, FieldWrapper>> declaredFields;
    private MethodWrapper checksumMethod;

    public BankDriverParserModel() {
        referencedClasses = new HashMap<String, Class<?>>();
        fieldsToParse = new HashMap<String, Map<String, FieldWrapper>>();
        methodsToParse = new HashMap<String, Map<String, MethodWrapper>>();
        declaredFields = new HashMap<String, Map<String, FieldWrapper>>();
    }

    public void addReferencedClass(String simpleName, Class<?> klass) {
        referencedClasses.put(simpleName, klass);
    }

    public Class<?> getClassBySimpleName(String simpleName) {
        return referencedClasses.get(simpleName);
    }

    public void addFieldToParse(String classSimpleName, FieldWrapper field) {
        Map<String, FieldWrapper> map = fieldsToParse.get(classSimpleName);
        if (map == null) {
            map = new HashMap<String, FieldWrapper>();
            fieldsToParse.put(classSimpleName, map);
        }
        if (!map.containsKey(field.getName())) {
            map.put(field.getName(), field);
        }
    }

    public void addDeclaredField(String classSimpleName, FieldWrapper field) {
        Map<String, FieldWrapper> map = declaredFields.get(classSimpleName);
        if (map == null) {
            map = new HashMap<String, FieldWrapper>();
            declaredFields.put(classSimpleName, map);
        }
        if (!map.containsKey(field.getName())) {
            map.put(field.getName(), field);
        }
    }

    public void addMethodToParse(String classSimpleName, MethodWrapper method) {
        Map<String, MethodWrapper> map = methodsToParse.get(classSimpleName);
        if (map == null) {
            map = new HashMap<String, MethodWrapper>();
            methodsToParse.put(classSimpleName, map);
        }
        if (!map.containsKey(method.getName())) {
            map.put(method.getName(), method);
        }
    }

    public Iterator<Entry<String, Map<String, FieldWrapper>>> fieldsIterator() {
        return fieldsToParse.entrySet().iterator();
    }

    public Iterator<Entry<String, Map<String, MethodWrapper>>> methodsIterator() {
        return methodsToParse.entrySet().iterator();
    }

    public Map<String, Map<String, FieldWrapper>> getFieldsToParse() {
        return fieldsToParse;
    }

    public Map<String, Map<String, MethodWrapper>> getMethodsToParse() {
        return methodsToParse;
    }

    public Map<String, Class<?>> getReferencedClasses() {
        return referencedClasses;
    }

    public Map<String, Map<String, FieldWrapper>> getDeclaredFields() {
        return declaredFields;
    }

    public MethodWrapper getChecksumMethod() {
        return checksumMethod;
    }

    public void setChecksumMethod(MethodWrapper checksumMethod) {
        this.checksumMethod = checksumMethod;
    }

    public boolean isChecksumSet() {
        return this.checksumMethod != null;
    }
}
