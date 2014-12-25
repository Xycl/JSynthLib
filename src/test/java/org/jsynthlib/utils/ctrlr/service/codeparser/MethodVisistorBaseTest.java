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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Pascal Collberg
 */
public class MethodVisistorBaseTest {

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
    public void testCompareClassArrays() {
        MethodWrapper wrapper = MethodWrapper.newWrapper("test", "pelle");
        MethodVisitorBase tested = new MethodVisitorBase(wrapper, null) {

        };
        Class<?>[] cArr = {
                A.class, B.class, C.class, B.class };
        ArrayList<ParseTree> parseTree = new ArrayList<ParseTree>();
        parseTree.add(new AImpl());
        parseTree.add(new BImpl());
        parseTree.add(new CImpl());
        parseTree.add(new CImpl());
        assertTrue(tested.compareClassArrays(cArr, parseTree));

        parseTree.remove(parseTree.size() - 1);
        assertFalse(tested.compareClassArrays(cArr, parseTree));
    }

    public interface A extends ParseTree {

    }

    public class AImpl implements A {

        @Override
        public ParseTree getParent() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ParseTree getChild(int i) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getText() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String toStringTree(Parser parser) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Interval getSourceInterval() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Object getPayload() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getChildCount() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public String toStringTree() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    public interface B extends ParseTree {

    }

    public class BImpl implements B {

        @Override
        public ParseTree getParent() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ParseTree getChild(int i) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getText() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String toStringTree(Parser parser) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Interval getSourceInterval() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Object getPayload() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getChildCount() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public String toStringTree() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    public interface C extends B {

    }

    public class CImpl implements C {

        @Override
        public ParseTree getParent() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ParseTree getChild(int i) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getText() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String toStringTree(Parser parser) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Interval getSourceInterval() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Object getPayload() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getChildCount() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public String toStringTree() {
            // TODO Auto-generated method stub
            return null;
        }

    }
}
