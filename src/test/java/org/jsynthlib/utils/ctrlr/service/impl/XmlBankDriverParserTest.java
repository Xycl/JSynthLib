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
package org.jsynthlib.utils.ctrlr.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Pascal Collberg
 *
 */
public class XmlBankDriverParserTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        int increment = 1;
        List<byte[]> resultList = new ArrayList<byte[]>();
        for (int i = 0; i < 50 * 3000; i += increment) {
            byte[] bs = new byte[2];
            if (i / 128 > 127) {
                System.out.println("Max number reached");
                break;
            }
            bs[0] = (byte) (i / 128);
            bs[1] = (byte) (i % 128);
            System.out.println(i + " -> " + Arrays.toString(bs));
            resultList.add(bs);
        }

        int i = 0;
        for (byte[] bs : resultList) {
            int val = bs[0] * 128;
            val += bs[1];
            assertEquals(i, val);
            i += increment;
        }

    }

}
