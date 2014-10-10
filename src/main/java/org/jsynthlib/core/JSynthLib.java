/*
 * JSynthLib - a Universal Synthesizer / Patch Editor in Java Copyright (C)
 * 2000-2004 Brian Klock et al.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 675 Mass
 * Ave, Cambridge, MA 02139, USA.
 *
 * Brian Klock- jsynthlib@overwhelmed.org
 */

package org.jsynthlib.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;

import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.patch.model.PatchEditFactory;
import org.jsynthlib.patch.model.impl.PatchEdit;

/**
 * This class launches an instance of the application.
 */
public class JSynthLib {

    /**
     * Launch an instance of the application.
     * @param args
     *            the command line arguments
     */
    public static void main(final String[] args) {
        int debugLevel = 0;
        File logfile = new File("jsynthlib.log");
        System.setProperty("log.path", logfile.getAbsolutePath());

        Platform.setImplicitExit(false);
        List<String> files = new ArrayList<String>();

        for (int i = 0; i < args.length; ++i) {
            if (args[i].startsWith("-D")) {
                try {
                    debugLevel = Integer.parseInt(args[++i]);
                } catch (RuntimeException exception) {
                    usage(1);
                }
            } else if (args[i].startsWith("-h")) {
                usage(0);
            } else if (args[i].startsWith("-")) {
                usage(1);
            } else {
                files.add(args[i]);
            }
        }

        PatchEditFactory patchEditFactory =
                JSynthLibInjector.getInstance(
                        PatchEditFactory.class);
        PatchEdit frame = patchEditFactory.newPatchEdit(files, debugLevel);
    }

    /**
     * Print usage to error stream and exit.
     * @param status
     *            the exit status
     */
    private static void usage(final int status) {
        System.err.println("usage: java JSynthLib [-D number] [filename...]");
        System.err.println("\t-D number\tset message flags as a bit mask");
        System.err.println("\t\t1\tdebug messages");
        System.err.println("\t\t2\tstack dump messages");
        System.exit(status);
    }
}
