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

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaBaseVisitor;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.CreatorContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.ExpressionContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.ExpressionListContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.ForControlContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.ForInitContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.LiteralContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.LocalVariableDeclarationContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.MethodDeclarationContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.ParExpressionContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.PrimaryContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.StatementContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.StatementExpressionContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.TypeContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.VariableDeclaratorContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.VariableDeclaratorIdContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.VariableInitializerContext;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.jsynthlib.utils.ctrlr.controller.LuaFactoryFacade;
import org.jsynthlib.utils.ctrlr.controller.lua.JavaParsedMethodController;
import org.jsynthlib.utils.ctrlr.domain.CtrlrPanelModel;
import org.jsynthlib.utils.ctrlr.service.codeparser.Field.FieldType;
import org.jsynthlib.xmldevice.Property;
import org.jsynthlib.xmldevice.XmlDriverDefinition;
import org.jsynthlib.xmldevice.XmlDriverDefinition.CustomProperties;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author Pascal Collberg
 */
public abstract class MethodVisitorBase extends JavaBaseVisitor<Void> {

    private static final String[] TERMINALS = {
        "+", "-", "*", "/", "|", "&", "+=", "-=" };
    private static final String[] IGNORED_STATEMENTS = {
        "ErrorMsg.report", "calculateChecksum(", "!canHoldPatch(" };

    private final transient Logger log = Logger.getLogger(getClass());

    private final Set<Method> methodsToParse;
    private final Set<Field> fieldsToParse;
    protected final AtomicInteger indent = new AtomicInteger(0);

    @Inject
    private XmlDriverDefinition driverDef;

    @Inject
    private LuaFactoryFacade luaFactory;

    @Inject
    private CtrlrPanelModel panelModel;

    private final String methodName;

    private JavaParsedMethodController code;

    private final Map<String, Field> parsedLocalVariables;

    private Field currField;

    private final List<String> terminals;

    private boolean ignoreReturnStatement;

    @Inject
    @Named("prefix")
    private String prefix;

    public MethodVisitorBase(String methodName) {
        methodsToParse = new HashSet<Method>();
        fieldsToParse = new HashSet<Field>();
        this.methodName = methodName;
        parsedLocalVariables = new HashMap<String, Field>();
        terminals = Arrays.asList(TERMINALS);
    }

    protected String getFieldValue(String fieldName) {
        BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
        try {
            PropertyDescriptor propertyDescriptor =
                    PropertyUtils.getPropertyDescriptor(driverDef, fieldName);
            if (propertyDescriptor != null) {
                return beanUtilsBean.getSimpleProperty(driverDef, fieldName);
            }

            CustomProperties customProperties = driverDef.getCustomProperties();
            if (customProperties != null) {
                Property[] propertyArray =
                        customProperties.getCustomPropertyArray();
                for (Property property : propertyArray) {
                    if (property.getName().equals(fieldName)) {
                        return property.getValue();
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
            log.warn(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Void visitMethodDeclaration(MethodDeclarationContext ctx) {
        code = luaFactory.newJavaParsedMethodController(methodName);
        code.append(code.indent(indent.getAndIncrement()));
        code.append("function ").append(methodName).append(" (");
        visit(ctx.formalParameters());
        code.append(")").append(code.newLine());
        code.append(code.getPanelInitCheck(indent)).append(code.newLine())
        .append(code.indent(indent));

        visit(ctx.methodBody());
        code.append(code.newLine()).append("end")
        .append(code.newLine());

        return null;
    }

    protected boolean compareClassArrays(Class<?>[] cArr,
            List<ParseTree> parseTree) {
        ArrayList<ClassAdapter> list = new ArrayList<ClassAdapter>();
        for (int i = 0; i < cArr.length; i++) {
            Class<?> class1 = cArr[i];
            list.add(new ClassAdapter(class1));
        }
        ClassAdapter[] adapterArray =
                list.toArray(new ClassAdapter[list.size()]);
        return Arrays.equals(adapterArray,
                parseTree.toArray(new ParseTree[parseTree.size()]));
    }

    static class ClassAdapter {

        private final Class<?> klass;

        public ClassAdapter(Class<?> klass) {
            this.klass = klass;
        }

        @Override
        public int hashCode() {
            int result = 1;
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
            if (!klass.isAssignableFrom(obj.getClass())) {
                return false;
            }
            return true;
        }
    }

    protected boolean isMethodCall(ExpressionContext ctx) {
        if (compareClassArrays(new Class<?>[] {
                ExpressionContext.class, TerminalNode.class,
                ExpressionListContext.class, TerminalNode.class }, ctx.children)) {
            return true;
        } else if (compareClassArrays(
                new Class<?>[] {
                        ExpressionContext.class, TerminalNode.class,
                        TerminalNode.class }, ctx.children)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Void visitExpression(ExpressionContext ctx) {
        if (isMethodCall(ctx)) {
            Method method = new Method();
            method.setName(ctx.getChild(0).getText());
            method.setLuaName(prefix + "_" + ctx.getChild(0).getText());
            methodsToParse.add(method);
            code.append(ctx.getText().replace(method.getName(),
                    method.getLuaName()));
            return null;
        } else if (compareClassArrays(new Class<?>[] {
                ExpressionContext.class, TerminalNode.class,
                ExpressionContext.class, TerminalNode.class }, ctx.children)
                && ctx.getChild(1).getText().equals("[")) {
            // Array value expression
            String varName = ctx.getChild(0).getText();
            if (varName.endsWith(".sysex")) {
                varName = varName.replace(".sysex", "");
            }

            Field field = null;
            if (parsedLocalVariables.containsKey(varName)) {
                field = parsedLocalVariables.get(varName);
            } else {
                field = handleVariable(varName);
            }
            code.append(field.getLuaName()).append(":getByte(");
            Void expression =
                    super.visitExpression((ExpressionContext) ctx.getChild(2));
            code.append(")");
            return expression;
        } else if (compareClassArrays(new Class<?>[] {
                TerminalNode.class, TypeContext.class, TerminalNode.class,
                ExpressionContext.class, }, ctx.children)) {
            // Casted type
            return super.visit(ctx.getChild(3));
        } else if (compareClassArrays(new Class<?>[] {
                ExpressionContext.class, TerminalNode.class,
                ExpressionContext.class, }, ctx.children)
                && ctx.getChild(1).getText().equals("&")) {
            // Bitwise and
            code.append("bit.band(");
            visit(ctx.getChild(0));
            code.append(",");
            visit(ctx.getChild(2));
            code.append(")");
            return null;
        } else if (compareClassArrays(new Class<?>[] {
                ExpressionContext.class, TerminalNode.class,
                ExpressionContext.class, }, ctx.children)
                && ctx.getChild(1).getText().equals("|")) {
            // Bitwise or
            code.append("bit.bor(");
            visit(ctx.getChild(0));
            code.append(",");
            visit(ctx.getChild(2));
            code.append(")");
            return null;
        } else if (compareClassArrays(new Class<?>[] {
                ExpressionContext.class, TerminalNode.class,
                ExpressionContext.class, }, ctx.children)
                && ctx.getChild(1).getText().equals("+=")) {
            // increment
            visit(ctx.getChild(0));
            code.append(" = ");
            visit(ctx.getChild(0));
            code.append(" + ");
            visit(ctx.getChild(2));
            return null;
        } else if (compareClassArrays(new Class<?>[] {
                ExpressionContext.class, TerminalNode.class,
                ExpressionContext.class, }, ctx.children)
                && ctx.getChild(1).getText().equals("-=")) {
            // increment
            visit(ctx.getChild(0));
            code.append(" = ");
            visit(ctx.getChild(0));
            code.append(" - ");
            visit(ctx.getChild(2));
            return null;
        } else {
            return super.visitExpression(ctx);
        }
    }

    Field handleVariable(String varName) {
        Field field;
        String fieldValue = getFieldValue(varName);
        field = new Field();
        field.setName(varName);
        field.setLuaName(prefix + "_" + varName);
        if (fieldValue != null) {
            panelModel.putGlobalVariable(field.getLuaName(), fieldValue);
        } else {
            fieldsToParse.add(field);
        }
        return field;
    }

    @Override
    public Void visitTerminal(TerminalNode node) {
        if (terminals.contains(node.getText())) {
            code.append(" ").append(node.getText()).append(" ");
        }
        if (node.getText().equals("return") && !ignoreReturnStatement) {
            code.append(node.getText()).append(" ");
        }
        if (node.getText().equals(";")) {
            code.append(code.newLine()).append(code.indent(indent));
        }
        return super.visitTerminal(node);
    }

    @Override
    public Void visitCreator(CreatorContext ctx) {
        if (ctx.arrayCreatorRest() != null
                && ctx.createdName().getText().equals("byte")) {
            code.append("MemoryBlock(");
            visit(ctx.arrayCreatorRest());
            code.append(", false)");
            currField.setType(FieldType.BYTE_ARRAY);
            return null;
        }
        return super.visitCreator(ctx);
    }

    @Override
    public Void visitLocalVariableDeclaration(
            LocalVariableDeclarationContext ctx) {
        VariableDeclaratorContext variableDeclarator =
                ctx.variableDeclarators().variableDeclarator(0);
        String varName = variableDeclarator.variableDeclaratorId().getText();
        currField = new Field();
        currField.setName(varName);
        currField.setLuaName(varName);
        String initializer = variableDeclarator.variableInitializer().getText();
        if (isByteArrayDeclaration(variableDeclarator)) {
            code.append(varName).append(" = ");
            parsedLocalVariables.put(currField.getName(), currField);
            return super.visitLocalVariableDeclaration(ctx);
        } else if (initializer.startsWith("getPatchFactory")) {
            String sysexVar =
                    variableDeclarator.variableInitializer().expression()
                    .expressionList().expression(0).getText();
            code.append("patchData:copyFrom(")
            .append(sysexVar).append(", 0, ").append(sysexVar)
            .append(":getSize())");
            currField.setType(FieldType.PATCH);
        } else {
            FieldType type = FieldType.getFromString(ctx.type().getText());
            currField.setType(type);
            code.append("local ").append(varName)
            .append(" = ").append(initializer);
        }
        parsedLocalVariables.put(currField.getName(), currField);
        return null;
    }

    Pattern byteArrayPattern = Pattern.compile("newbyte\\[([^\\]]+)\\]");

    protected boolean isByteArrayDeclaration(VariableDeclaratorContext ctx) {
        Class<?>[] cArr =
            {
                VariableDeclaratorIdContext.class, TerminalNode.class,
                VariableInitializerContext.class };
        if (compareClassArrays(cArr, ctx.children)) {
            VariableInitializerContext variableInitializer =
                    ctx.variableInitializer();
            Matcher matcher =
                    byteArrayPattern.matcher(variableInitializer.getText());
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    boolean isArrayElementAssignment(StatementExpressionContext ctx) {
        ExpressionContext expression = ctx.expression().expression(0);
        if (expression == null) {
            return false;
        }
        Class<?>[] cArr =
            {
                ExpressionContext.class, TerminalNode.class,
                ExpressionContext.class, TerminalNode.class };
        if (compareClassArrays(cArr, expression.children)) {
            return expression.getChild(1).getText().equals("[")
                    && expression.getChild(3).getText().equals("]");
        }

        return false;
    }

    @Override
    public Void visitPrimary(PrimaryContext ctx) {
        if (ctx.getChildCount() > 1) {
            code.append("(");
        }
        LiteralContext literal = ctx.literal();
        if (literal != null) {
            code.append(literal.getText());
        } else if (ctx.Identifier() != null) {
            String varName = ctx.getText();
            Field field = null;
            if (parsedLocalVariables.containsKey(varName)) {
                field = parsedLocalVariables.get(varName);
            } else {
                field = handleVariable(varName);
            }
            code.append(field.getLuaName());
        }
        Void visitPrimary = super.visitPrimary(ctx);
        if (ctx.getChildCount() > 1) {
            code.append(")");
        }
        return visitPrimary;
    }

    @Override
    public Void visitStatementExpression(StatementExpressionContext ctx) {
        for (String string : IGNORED_STATEMENTS) {
            if (ctx.getText().contains(string)) {
                return null;
            }
        }

        if (isArrayElementAssignment(ctx)) {

            ExpressionContext expression = ctx.expression().expression(0);

            String varName = expression.expression(0).getText();
            if (varName.endsWith(".sysex")) {
                varName = varName.replace(".sysex", "");
            }

            Field field = null;
            if (parsedLocalVariables.containsKey(varName)) {
                field = parsedLocalVariables.get(varName);
            } else {
                field = handleVariable(varName);
            }
            code.append(field.getLuaName()).append(":setByte(");

            visit(expression.expression(1));
            code.append(", ");
            visit(ctx.expression().expression(1));

            code.append(")");
            return null;
        }
        return super.visitStatementExpression(ctx);
    }

    @Override
    public Void visitStatement(StatementContext ctx) {
        if (ctx.getText().startsWith("return") && ignoreReturnStatement) {
            return null;
        } else if (compareClassArrays(new Class<?>[] {
                StatementExpressionContext.class, TerminalNode.class },
                ctx.children)) {
            return super.visitStatement(ctx);
        } else if (compareClassArrays(new Class<?>[] {
                TerminalNode.class, TerminalNode.class,
                ForControlContext.class, TerminalNode.class,
                StatementContext.class }, ctx.children)) {
            appendFor(ctx);
            Void visit = visit(ctx.getChild(4));
            appendEnd();
            return visit;
        } else if (compareClassArrays(new Class<?>[] {
                TerminalNode.class, ParExpressionContext.class,
                StatementContext.class }, ctx.children)) {
            TerminalNode node = (TerminalNode) ctx.getChild(0);
            if (node.getText().equals("if")) {
                if (appendIf(ctx)) {
                    Void visit = visit(ctx.getChild(2));
                    appendEnd();
                    return visit;
                } else {
                    return null;
                }
            } else {
                log.info("WHILE!");
            }
            return visit(ctx.getChild(2));
        } else if (compareClassArrays(new Class<?>[] {
                TerminalNode.class, ParExpressionContext.class,
                StatementContext.class, TerminalNode.class,
                StatementContext.class }, ctx.children)) {
            log.info("IF_ELSE!");
            if (appendIf(ctx)) {
                Void visit = visit(ctx.getChild(2));
                // TODO handle else
                appendEnd();
                return visit; // visit(ctx.getChild(4));
            } else {
                return null;
            }
        } else {
            return super.visitStatement(ctx);
        }

    }

    boolean appendIf(StatementContext ctx) {
        ParExpressionContext parExpression = ctx.parExpression();
        ExpressionContext expression = parExpression.expression();
        for (String string : IGNORED_STATEMENTS) {
            if (ctx.getText().contains(string)) {
                return false;
            }
        }

        code.append("if ");
        code.append(expression.getText()).append(" then")
        .append(code.newLine())
        .append(code.indent(indent.incrementAndGet()));
        return true;
    }

    void appendFor(StatementContext ctx) {
        code.append("for ");
        ForControlContext child = (ForControlContext) ctx.getChild(2);
        ForInitContext forInit = child.forInit();
        VariableDeclaratorContext variableDeclarator =
                forInit.localVariableDeclaration().variableDeclarators()
                .variableDeclarator(0);
        code.append(variableDeclarator.variableDeclaratorId().getText())
        .append(" = ");
        code.append(variableDeclarator.variableInitializer().getText()).append(
                ", ");

        ExpressionContext expression = child.expression();
        TerminalNode lt = (TerminalNode) expression.getChild(1);
        if (!lt.getText().equals("<")) {
            throw new RuntimeException("Cannot handle for loops of this kind: "
                    + lt.getText());
        }
        code.append("(").append(expression.getChild(2).getText()).append("-1)")
        .append(" do").append(code.newLine())
        .append(code.indent(indent.incrementAndGet()));
    }

    void appendEnd() {
        code.append(code.indent(indent.decrementAndGet())).append("end")
        .append(code.newLine()).append(code.indent(indent));
    }

    protected Field putLocalVariable(String key, Field value) {
        return parsedLocalVariables.put(key, value);
    }

    LuaFactoryFacade getLuaFactory() {
        return luaFactory;
    }

    void setLuaFactory(LuaFactoryFacade luaFactory) {
        this.luaFactory = luaFactory;
    }

    public Set<Method> getMethodsToParse() {
        return methodsToParse;
    }

    public Set<Field> getFieldsToParse() {
        return fieldsToParse;
    }

    XmlDriverDefinition getDriverDef() {
        return driverDef;
    }

    void setDriverDef(XmlDriverDefinition driverDef) {
        this.driverDef = driverDef;
    }

    CtrlrPanelModel getPanelModel() {
        return panelModel;
    }

    void setPanelModel(CtrlrPanelModel panelModel) {
        this.panelModel = panelModel;
    }

    public JavaParsedMethodController getCode() {
        return code;
    }

    String getPrefix() {
        return prefix;
    }

    void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    protected boolean isIgnoreReturnStatement() {
        return ignoreReturnStatement;
    }

    protected void setIgnoreReturnStatement(boolean ignoreReturnStatement) {
        this.ignoreReturnStatement = ignoreReturnStatement;
    }

}
