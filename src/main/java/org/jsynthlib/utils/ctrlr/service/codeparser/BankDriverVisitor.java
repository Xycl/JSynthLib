package org.jsynthlib.utils.ctrlr.service.codeparser;

import java.util.Map;

import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaBaseVisitor;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.ClassBodyContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.ClassDeclarationContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.ImportDeclarationContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.MethodBodyContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.MethodDeclarationContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.PackageDeclarationContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.QualifiedNameContext;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.log4j.Logger;

import com.google.inject.Inject;

public class BankDriverVisitor extends JavaBaseVisitor<Void> {

    private final transient Logger log = Logger.getLogger(getClass());

    private final VisitorFactoryFacade visitorFacade;

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
    public Void visitClassBody(ClassBodyContext ctx) {
        return super.visitClassBody(ctx);
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

        Map<String, Map<String, MethodWrapper>> methodsToParse = parserModel.getMethodsToParse();
        if (methodsToParse.containsKey(currClass.getSimpleName())) {
            Map<String, MethodWrapper> map = methodsToParse.get(currClass.getSimpleName());
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
        }
        return null;
    }

    @Override
    public Void visitMethodBody(MethodBodyContext ctx) {
        return super.visitMethodBody(ctx);
    }
}
