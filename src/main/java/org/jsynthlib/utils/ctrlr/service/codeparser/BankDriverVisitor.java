package org.jsynthlib.utils.ctrlr.service.codeparser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaBaseVisitor;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.ClassBodyContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.ClassDeclarationContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.MethodBodyContext;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.MethodDeclarationContext;

import com.google.inject.Inject;

public class BankDriverVisitor extends JavaBaseVisitor<Void> {

    private final Map<String, MethodVisitorBase> methodsToParse;
    private final Set<Field> fieldsToParse;
    private final VisitorFactoryFacade visitorFacade;

    @Inject
    public BankDriverVisitor(VisitorFactoryFacade visitorFacade) {
        methodsToParse = new HashMap<String, MethodVisitorBase>();
        fieldsToParse = new HashSet<Field>();
        this.visitorFacade = visitorFacade;
        methodsToParse
        .put("getPatch", visitorFacade.newGetPatchMethodVisitor());
        methodsToParse
        .put("putPatch", visitorFacade.newPutPatchMethodVisitor());
    }

    @Override
    public Void visitClassBody(ClassBodyContext ctx) {
        return super.visitClassBody(ctx);
    }

    @Override
    public Void visitClassDeclaration(ClassDeclarationContext ctx) {
        return super.visitClassDeclaration(ctx);
    }

    @Override
    public Void visitMethodDeclaration(MethodDeclarationContext ctx) {
        String methodName = ctx.Identifier().getText();

        if (methodsToParse.containsKey(methodName)) {
            System.out.println("Method " + methodName);
            MethodVisitorBase visitor =
                    methodsToParse.get(methodName);
            visitor.visit(ctx);
            visitor.getCode().setJavaParserDone(true);

            fieldsToParse.addAll(visitor.getFieldsToParse());
            Set<Method> newMethodsToParse = visitor.getMethodsToParse();
            if (newMethodsToParse != null) {
                for (Method methodToParse : newMethodsToParse) {
                    DefaultMethodVisitor methodVisitor =
                            visitorFacade
                            .newDefaultMethodVisitor(methodToParse);
                    methodsToParse.put(methodToParse.getName(), methodVisitor);
                }
            }
            methodsToParse.remove(methodName);
        }
        return null;
    }

    @Override
    public Void visitMethodBody(MethodBodyContext ctx) {
        return super.visitMethodBody(ctx);
    }

    public Set<String> getMethodsToParse() {
        return methodsToParse.keySet();
    }

    public Set<Field> getFieldsToParse() {
        return fieldsToParse;
    }
}
