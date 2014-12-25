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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaBaseVisitor;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.CatchClauseContext;
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
import org.jsynthlib.utils.ctrlr.service.codeparser.FieldWrapper.FieldType;
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

    protected final AtomicInteger indent = new AtomicInteger(0);

    @Inject
    private XmlDriverDefinition driverDef;

    @Inject
    private LuaFactoryFacade luaFactory;

    @Inject
    private CtrlrPanelModel panelModel;

    private final MethodWrapper parsedMethod;

    private JavaParsedMethodController code;

    private final Map<String, FieldWrapper> parsedLocalVariables;

    private final List<String> terminals;

    private boolean ignoreReturnStatement;

    @Inject
    @Named("prefix")
    private String prefix;

    @Inject
    private BankDriverParserModel model;
    private final Class<?> parsedClass;

    public MethodVisitorBase(MethodWrapper method, Class<?> parsedClass) {
        this.parsedMethod = method;
        parsedLocalVariables = new HashMap<String, FieldWrapper>();
        terminals = Arrays.asList(TERMINALS);
        this.parsedClass = parsedClass;
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
        code =
                luaFactory.newJavaParsedMethodController(parsedMethod
                        .getLuaName());

        code.append(code.indent(indent.getAndIncrement()));
        code.append("function ").append(parsedMethod.getLuaName()).append(" (");
        visit(ctx.formalParameters());
        code.append(")").append(code.newLine());
        code.append(code.getPanelInitCheck(indent)).append(code.newLine())
        .append(code.indent(indent));

        visit(ctx.methodBody());
        code.append(code.newLine()).append("end").append(code.newLine());

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
                        TerminalNode.class }, ctx.children)
                        && ctx.getChild(1).equals("(")) {
            return true;
        } else {
            return false;
        }
    }

    FieldWrapper getFieldFromVarName(ParseTree tree, boolean global) {
        String varName = tree.getText();
        if (varName.endsWith(".sysex")) {
            varName = varName.replace(".sysex", "");
        }

        FieldWrapper field = null;
        if (parsedLocalVariables.containsKey(varName)) {
            field = parsedLocalVariables.get(varName);
        } else {
            String fieldValue = getFieldValue(varName);
            field = new FieldWrapper();
            field.setName(varName);
            if (global) {
                field.setLuaName(prefix + "_" + varName);
                if (fieldValue != null) {
                    panelModel
                    .putGlobalVariable(field.getLuaName(), fieldValue);
                } else {
                    // TODO: implement!
                    // fieldsToParse.add(field);
                }
            } else {
                field.setLuaName(varName);
            }
            parsedLocalVariables.put(varName, field);
        }
        return field;
    }

    void handleSystemArrayCopy(ExpressionContext ctx) {
        List<ExpressionContext> expression = ctx.expressionList().expression();
        ExpressionContext src = expression.get(0);
        ExpressionContext srcPos = expression.get(1);
        ExpressionContext dest = expression.get(2);
        ExpressionContext destPos = expression.get(3);
        ExpressionContext length = expression.get(4);

        FieldWrapper srcField = getFieldFromVarName(src, true);
        code.append("local trimmedData = ").append(srcField.getLuaName())
        .append(":getRange(");
        visit(srcPos);
        code.append(", ");
        visit(length);
        code.append(")").append(code.newLine()).append(code.indent(indent));

        FieldWrapper destField = getFieldFromVarName(dest, true);
        code.append(destField.getLuaName()).append(":copyFrom(trimmedData, ");
        visit(destPos);
        code.append(", ");
        visit(length);
        code.append(")").append(code.newLine()).append(code.indent(indent));

    }

    @Override
    public Void visitExpression(ExpressionContext ctx) {
        if (isMethodCall(ctx)) {
            String methodName = ctx.getChild(0).getText();
            if (methodName.equals("System.arraycopy")) {
                handleSystemArrayCopy(ctx);
                return null;
            } else if (methodName.equals("getSingleDriver().createPatch")) {
                // TODO: fix single size
                code.append("MemoryBlock(").append("1").append(", false)");
                return null;
            } else {
                handleGenericMethodCall(ctx);
                return null;
            }
        } else if (compareClassArrays(new Class<?>[] {
                ExpressionContext.class, TerminalNode.class,
                ExpressionContext.class, TerminalNode.class }, ctx.children)
                && ctx.getChild(1).getText().equals("[")) {
            // Array value expression
            FieldWrapper field = getFieldFromVarName(ctx.expression(0), true);
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
                TerminalNode.class, ExpressionContext.class, }, ctx.children)
                && ctx.getChild(1).getText().equals(">")
                && ctx.getChild(2).getText().equals(">")) {
            // Bitwise signed right shift
            code.append("bit.rshift(");
            visit(ctx.getChild(0));
            code.append(",");
            visit(ctx.getChild(3));
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

    void handleGenericMethodCall(ExpressionContext ctx) {
        ExpressionContext callExpression = ctx.expression(0);
        if (callExpression.getChildCount() == 1) {
            Class<?> currClass = parsedClass;
            boolean foundMethod = false;
            while (!currClass.equals(Object.class)) {
                Method[] methods = parsedClass.getMethods();

                for (Method method : methods) {
                    if (method.getName().equals(callExpression.getText())) {
                        foundMethod = true;
                        break;
                    }
                }
                if (foundMethod) {
                    MethodWrapper method = new MethodWrapper();
                    method.setName(callExpression.getText());
                    method.setLuaName(prefix + "_" + callExpression.getText());
                    model.addMethodToParse(currClass.getSimpleName(), method);
                    code.append(ctx.getText().replace(method.getName(),
                            method.getLuaName()));
                    break;
                }
            }

            if (!foundMethod) {
                log.warn("Could not find implementation for method "
                        + parsedMethod);
            }
        } else if (callExpression.getChildCount() == 3) {
            ExpressionContext instanceName = callExpression.expression(0);
            MethodWrapper methodWrapper = new MethodWrapper();
            methodWrapper.setName(callExpression.getChild(2).getText());
            methodWrapper
            .setLuaName(callExpression.getText().replace('.', '_'));
            model.addMethodToParse(instanceName.getText(), methodWrapper);
            code.append(methodWrapper.getLuaName()).append("(");
            visit(ctx.expressionList());
            code.append(")");
        } else {
            log.warn("Unsupported method call " + callExpression.getText());
        }
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
            // currField.setType(FieldType.BYTE_ARRAY);
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
        FieldWrapper field =
                getFieldFromVarName(variableDeclarator.variableDeclaratorId(),
                        false);
        String initializer = variableDeclarator.variableInitializer().getText();
        if (isByteArrayDeclaration(variableDeclarator)) {
            code.append(varName).append(" = ");
            return super.visitLocalVariableDeclaration(ctx);
        } else if (initializer.startsWith("getPatchFactory")) {
            String sysexVar =
                    variableDeclarator.variableInitializer().expression()
                    .expressionList().expression(0).getText();

            String declaratorId = field.getLuaName();
            code.append("local ").append(declaratorId)
            .append(" = MemoryBlock(").append(sysexVar)
            .append(":getSize(), false)").append(code.newLine())
            .append(code.indent(indent));
            code.append(declaratorId).append(":copyFrom(").append(sysexVar)
            .append(", 0, ").append(sysexVar).append(":getSize())");
            field.setType(FieldType.PATCH);
        } else {
            FieldType type = FieldType.getFromString(ctx.type().getText());
            field.setType(type);
            code.append("local ").append(varName).append(" = ");
            return super.visitLocalVariableDeclaration(ctx);
        }
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
            FieldWrapper field = getFieldFromVarName(ctx, true);
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
            FieldWrapper field =
                    getFieldFromVarName(expression.expression(0), true);
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
                // TODO handle while
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

    @Override
    public Void visitCatchClause(CatchClauseContext ctx) {
        return null;
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
        code.append(
                getFieldFromVarName(variableDeclarator.variableDeclaratorId(),
                        false).getLuaName()).append(" = ");
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
        code.append(code.newLine())
        .append(code.indent(indent.decrementAndGet())).append("end")
        .append(code.newLine()).append(code.indent(indent));
    }

    protected FieldWrapper putLocalVariable(String key, FieldWrapper value) {
        return parsedLocalVariables.put(key, value);
    }

    LuaFactoryFacade getLuaFactory() {
        return luaFactory;
    }

    void setLuaFactory(LuaFactoryFacade luaFactory) {
        this.luaFactory = luaFactory;
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
