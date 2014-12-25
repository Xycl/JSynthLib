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
public class MethodWrapper {

    public static MethodWrapper newWrapper(String prefix, String javaName) {
        MethodWrapper wrapper = new MethodWrapper();
        wrapper.setName(javaName);
        wrapper.setLuaName(prefix + "_" + javaName);
        return wrapper;
    }

    private String name;
    private String luaName;

    public String getName() {
        return name;
    }

    public String getLuaName() {
        return luaName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLuaName(String luaName) {
        this.luaName = luaName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (luaName == null ? 0 : luaName.hashCode());
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
        MethodWrapper other = (MethodWrapper) obj;
        if (luaName == null) {
            if (other.luaName != null) {
                return false;
            }
        } else if (!luaName.equals(other.luaName)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

}
