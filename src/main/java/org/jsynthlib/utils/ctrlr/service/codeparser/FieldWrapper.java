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

/**
 * @author Pascal Collberg
 */
public class FieldWrapper {

    public static FieldWrapper newFieldWrapper(String prefix, String fieldName) {
        return newFieldWrapper(prefix, fieldName, null);
    }

    public static FieldWrapper newFieldWrapper(String prefix, String fieldName,
            String type) {
        return newFieldWrapper(prefix, fieldName, type, null);
    }

    public static FieldWrapper newFieldWrapper(String prefix, String fieldName, String type, String value) {
        FieldWrapper wrapper = new FieldWrapper();
        wrapper.setName(fieldName);
        if (prefix == null) {
            wrapper.setLuaName(fieldName);
        } else {
            wrapper.setLuaName(prefix + "_" + fieldName);
        }
        try {
            FieldType fieldType = FieldType.getFromString(type);
            wrapper.setType(fieldType);
        } catch (IllegalArgumentException e) {
        }

        wrapper.setValue(value);
        return wrapper;
    }

    enum FieldType {
        INT, BYTE, BYTE_ARRAY, STRING, CHAR, PATCH, BOOLEAN;

        public static FieldType getFromString(String type) {
            if ("int".equals(type)) {
                return INT;
            } else if ("byte".equals(type)) {
                return BYTE;
            } else if ("byte[]".equals(type)) {
                return BYTE_ARRAY;
            } else if ("String".equals(type)) {
                return STRING;
            } else if ("char".equals(type)) {
                return CHAR;
            } else if ("Patch".equals(type)) {
                return PATCH;
            } else if ("boolean".equals(type)) {
                return BOOLEAN;
            }
            throw new IllegalArgumentException("Unsupported type " + type);
        }

    }

    private String name;
    private String luaName;
    private FieldType type;
    private String value;

    public String getName() {
        return name;
    }

    public String getLuaName() {
        return luaName;
    }

    public FieldType getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLuaName(String luaName) {
        this.luaName = luaName;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FieldWrapper other = (FieldWrapper) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
