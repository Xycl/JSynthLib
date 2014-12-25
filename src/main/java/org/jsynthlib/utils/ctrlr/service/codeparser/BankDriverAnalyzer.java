package org.jsynthlib.utils.ctrlr.service.codeparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaLexer;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser;
import main.java.org.jsynthlib.utils.ctrlr.service.codeparser.JavaParser.CompilationUnitContext;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.log4j.Logger;
import org.jsynthlib.device.model.XMLBankDriver;
import org.jsynthlib.utils.ctrlr.domain.CtrlrPanelModel;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class BankDriverAnalyzer {

    private final transient Logger log = Logger.getLogger(getClass());

    @Inject
    private CtrlrPanelModel panelModel;

    @Inject
    private BankDriverVisitor visitor;

    @Inject
    @Named("prefix")
    private String prefix;

    @Inject
    private BankDriverParserModel parserModel;

    private ArrayList<String> parsedClassesSimpleNames;

    Class<?> getMethodClass(String methodName,
            Class<? extends XMLBankDriver> bankDriverClass) {
        Class<?> currClass = bankDriverClass;
        while (currClass != Object.class) {
            Method[] methods = currClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    return currClass;
                }
            }
            currClass = currClass.getSuperclass();
        }
        throw new IllegalArgumentException("Could not find method "
                + methodName);
    }

    public synchronized void parseBankDriver(
            Class<? extends XMLBankDriver> bankDriverClass) throws IOException {
        MethodWrapper wrapper = MethodWrapper.newWrapper(prefix, "putPatch");
        Class<?> putPatchClass =
                getMethodClass(wrapper.getName(), bankDriverClass);
        parserModel.addMethodToParse(putPatchClass.getSimpleName(),
 wrapper);

        wrapper = MethodWrapper.newWrapper(prefix, "getPatch");
        Class<?> getPatchClass =
                getMethodClass(wrapper.getName(), bankDriverClass);
        parserModel.addMethodToParse(getPatchClass.getSimpleName(),
 wrapper);

        // Get the valid source files as a list
        List<File> filesFromParsedClass = getFilesAsList(bankDriverClass);
        List<File> files = new ArrayList<File>(filesFromParsedClass);
        if (files.size() > 0) {
            HashMap<String, Map<String, MethodWrapper>> methodsToParseBefore =
                    new HashMap<String, Map<String, MethodWrapper>>();
            methodsToParseBefore.putAll(parserModel.getMethodsToParse());
            Iterator<File> iterator = files.iterator();
            while (iterator.hasNext()) {
                File file = iterator.next();
                log.info("Parsing " + file.getName());
                CharStream charStream =
                        new ANTLRFileStream(file.getAbsolutePath());
                JavaLexer lexer = new JavaLexer(charStream);
                JavaParser parser =
                        new JavaParser(new CommonTokenStream(lexer));
                CompilationUnitContext compilationUnit =
                        parser.compilationUnit();
                visitor.visit(compilationUnit);
                if (parserModel.getMethodsToParse().isEmpty()) {
                    // When all methods are parsed exit loop
                    break;
                } else if (!iterator.hasNext()) {
                    // Reset if not all method were found
                    files.clear();
                    files.addAll(filesFromParsedClass);
                    Map<String, Class<?>> referencedClasses =
                            parserModel.getReferencedClasses();
                    Iterator<String> fileIterator =
                            parserModel.getMethodsToParse().keySet().iterator();
                    while (fileIterator.hasNext()) {
                        String simpleClassName = fileIterator.next();
                        if (referencedClasses.containsKey(simpleClassName)) {
                            log.info("Adding referenced class "
                                    + simpleClassName);
                            File classFile =
                                    getFileFromClass(referencedClasses
                                            .get(simpleClassName));
                            files.add(classFile);
                        } else if (!parsedClassesSimpleNames
                                .contains(simpleClassName)) {
                            throw new ParserException("Could not find "
                                    + simpleClassName
                                    + " among the referenced classes");
                        }
                    }
                    iterator = files.iterator();

                    // If the loop has run without any changes to the methods to
                    // parse set - break
                    Map<String, Map<String, MethodWrapper>> methodsToParseAfter =
                            parserModel.getMethodsToParse();
                    if (methodsToParseAfter.equals(methodsToParseBefore)) {
                        break;
                    } else {
                        methodsToParseBefore.clear();
                        methodsToParseBefore.putAll(methodsToParseAfter);
                    }
                }
            }

            if (!parserModel.getMethodsToParse().isEmpty()) {
                log.warn("Failed to parse all methods: "
                        + parserModel.getMethodsToParse().toString());
            }

            Map<String, Map<String, FieldWrapper>> fieldsToParse =
                    parserModel.getFieldsToParse();
            for (Entry<String, Map<String, FieldWrapper>> fieldToParse : fieldsToParse
                    .entrySet()) {
                Map<String, FieldWrapper> fieldsList = fieldToParse.getValue();
                for (FieldWrapper fieldWrapper : fieldsList.values()) {
                    BufferedReader br =
                            new BufferedReader(new InputStreamReader(System.in));
                    System.out.print("Enter value for variable "
                            + fieldWrapper.getName() + " in class "
                            + fieldToParse.getKey());
                    String s = br.readLine();
                    panelModel.putGlobalVariable(fieldWrapper.getLuaName(), s);
                }
            }
        } else {
            log.warn("No valid source files to process. "
                    + "Extiting from the program");
        }

    }

    boolean methodsToParseEquals(Map<String, List<MethodWrapper>> before,
            Map<String, List<MethodWrapper>> after) {
        Set<String> beforeKeys = before.keySet();
        if (!beforeKeys.equals(after.keySet())) {
            return false;
        }
        for (String beforeKey : beforeKeys) {
            List<MethodWrapper> beforeList = before.get(beforeKey);
            List<MethodWrapper> afterList = after.get(beforeKey);
            if (!beforeList.equals(afterList)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method accepts the comma-separated file names, splits it using the
     * defined delimiter. A list of valid file objects will be created and
     * returned to main method.
     * @param bankDriverClass
     *            Comma-separated file names
     * @return List of valid source file objects
     */
    List<File> getFilesAsList(Class<?> bankDriverClass) {
        List<File> files = new ArrayList<File>();
        parsedClassesSimpleNames = new ArrayList<String>();
        // split the filenames using the delimiter
        Class<?> currClass = bankDriverClass;
        while (!currClass.equals(Object.class)) {
            parsedClassesSimpleNames.add(currClass.getSimpleName());
            File file = getFileFromClass(currClass);
            files.add(file);
            currClass = currClass.getSuperclass();
        }

        return files;
    }

    File getFileFromClass(Class<?> klass) {
        StringBuilder sb = new StringBuilder();
        sb.append("src").append(File.separatorChar).append("main")
        .append(File.separatorChar).append("java")
        .append(File.separatorChar)
        .append(klass.getName().replace('.', File.separatorChar))
        .append(".java");
        File sourceFile = new File(sb.toString());
        if (sourceFile != null && sourceFile.exists()) {
            return sourceFile;
        } else {
            throw new ParserException(sourceFile.getAbsolutePath()
                    + " is not a valid file. " + "Ignoring the file ");
        }
    }

    BankDriverVisitor getVisitor() {
        return visitor;
    }

    void setVisitor(BankDriverVisitor visitor) {
        this.visitor = visitor;
    }

    CtrlrPanelModel getPanelModel() {
        return panelModel;
    }

    void setPanelModel(CtrlrPanelModel panelModel) {
        this.panelModel = panelModel;
    }

}
