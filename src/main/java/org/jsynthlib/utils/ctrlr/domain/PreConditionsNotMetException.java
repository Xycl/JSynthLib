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
package org.jsynthlib.utils.ctrlr.domain;

/**
 * @author Pascal Collberg
 */
public class PreConditionsNotMetException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public PreConditionsNotMetException() {
    }

    /**
     * @param message
     */
    public PreConditionsNotMetException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public PreConditionsNotMetException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public PreConditionsNotMetException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public PreConditionsNotMetException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
