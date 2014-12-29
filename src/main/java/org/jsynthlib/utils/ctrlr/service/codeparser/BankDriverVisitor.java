package org.jsynthlib.utils.ctrlr.service.codeparser;

import java.util.List;
import java.util.Map;

import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaBaseVisitor;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.ClassDeclarationContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.ExpressionContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.FieldDeclarationContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.FormalParameterContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.FormalParametersContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.ImportDeclarationContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.MethodBodyContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.MethodDeclarationContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.PackageDeclarationContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.PrimaryContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.QualifiedNameContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.VariableDeclaratorContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.VariableDeclaratorIdContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.VariableInitializerContext;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.log4j.Logger;
import org.jsynthlib.utils.ctrlr.service.codeparser.FieldWrapper.FieldType;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class BankDriverVisitor extends JavaBaseVisitor<Void> {

    private final transient Logger log = Logger.getLogger(getClass());

    private final VisitorFactoryFacade visitorFacade;

    @Inject
    @Named("prefix")
    private String prefix;
    @Inject
    private BankDriverParserModel parserModel;

    private QualifiedNameContext packageName;

    private Class<?> currClass;

    @Inject
    public BankDriverVisitor(VisitorFactoryFacade visitorFacade) {
        this.visitorFacade = visitorFacade;
    }

    @Override
    public Void visitImportDeclaration(ImportDeclarationContext ctx) {
        try {
            String qualifiedName = ctx.qualifiedName().getText();
            if (ctx.getChild(1) instanceof TerminalNode) {
                // static import
                qualifiedName =
                        qualifiedName.substring(0,
                                qualifiedName.lastIndexOf('.'));
            }
            if (qualifiedName.startsWith("org.jsynthlib.xmldevice")) {
                log.info("Skipping XML class " + qualifiedName);
            } else {
                Class<?> klass = Class.forName(qualifiedName);
                parserModel.addReferencedClass(klass.getSimpleName(), klass);
            }
        } catch (ClassNotFoundException e) {
            log.warn(e.getMessage(), e);
        }
        return super.visitImportDeclaration(ctx);
    }

    @Override
    public Void visitFieldDeclaration(FieldDeclarationContext ctx) {
        FieldWrapper wrapper = new FieldWrapper();
        try {

            FieldType fieldType = FieldType.getFromString(ctx.type().getText());
            wrapper.setType(fieldType);
            VariableDeclaratorContext declarator =
                    ctx.variableDeclarators().variableDeclarator(0);
            VariableDeclaratorIdContext variableDeclaratorId =
                    declarator.variableDeclaratorId();
            VariableInitializerContext variableInitializer =
                    declarator.variableInitializer();
            wrapper.setName(variableDeclaratorId.getText());
            wrapper.setLuaName(prefix + variableDeclaratorId.getText());
            if (variableInitializer != null) {
                ExpressionContext expression = variableInitializer.expression();
                if (MethodVisitorBase.compareClassArrays(new Class<?>[] {
                        PrimaryContext.class }, expression.children)) {
                    wrapper.setValue(expression.getText());
                    parserModel.addDeclaredField(currClass.getSimpleName(),
                            wrapper);
                } else if (MethodVisitorBase.compareClassArrays(new Class<?>[] {
                        PrimaryContext.class, TerminalNode.class,
                        PrimaryContext.class }, expression.children)) {
                    wrapper.setValue(expression.getText());
                    parserModel.addDeclaredField(currClass.getSimpleName(),
                            wrapper);
                }
            }
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
        }
        return null;
    }

    @Override
    public Void visitClassDeclaration(ClassDeclarationContext ctx) {
        try {
            String className = ctx.getChild(1).getText();
            currClass = Class.forName(packageName.getText() + "." + className);
            return super.visitClassDeclaration(ctx);
        } catch (ClassNotFoundException e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Void visitPackageDeclaration(PackageDeclarationContext ctx) {
        packageName = ctx.qualifiedName();
        return super.visitPackageDeclaration(ctx);
    }

    @Override
    public Void visitMethodDeclaration(MethodDeclarationContext ctx) {
        String methodName = ctx.Identifier().getText();

        Map<String, Map<String, MethodWrapper>> methodsToParse =
                parserModel.getMethodsToParse();
        if (methodsToParse.containsKey(currClass.getSimpleName())) {
            Map<String, MethodWrapper> map =
                    methodsToParse.get(currClass.getSimpleName());
            if (map.containsKey(methodName)) {
                MethodWrapper methodWrapper = map.get(methodName);
                MethodVisitorBase visitor =
                        visitorFacade
                        .newMethodVisitor(currClass, methodWrapper);
                log.info("Visiting method " + methodName);
                visitor.visit(ctx);
                visitor.getCode().setJavaParserDone(true);
                map.remove(methodName);
                if (map.isEmpty()) {
                    methodsToParse.remove(currClass.getSimpleName());
                }
            }
        } else if (!parserModel.isChecksumSet()
                && methodName.equals("calculateChecksum")) {
            FormalParametersContext formalParameters = ctx.formalParameters();
            List<FormalParameterContext> formalParameter =
                    formalParameters.formalParameterList().formalParameter();
            if (formalParameter.size() == 4
                    && formalParameter.get(0).type().getText().equals("byte[]")) {
                MethodWrapper wrapper = new MethodWrapper();
                wrapper.setName("calculateChecksum");
                wrapper.setLuaName(prefix + "_CalculateChecksum");
                MethodVisitorBase visitor =
                        visitorFacade.newMethodVisitor(currClass, wrapper);
                log.info("Visiting method " + methodName);
                visitor.visit(ctx);
                visitor.getCode().setJavaParserDone(true);
                parserModel.setChecksumMethod(wrapper);
                methodsToParse.remove(Object.class.getSimpleName());
            }
        }
        return null;
    }

    @Override
    public Void visitMethodBody(MethodBodyContext ctx) {
        return super.visitMethodBody(ctx);
    }
}
