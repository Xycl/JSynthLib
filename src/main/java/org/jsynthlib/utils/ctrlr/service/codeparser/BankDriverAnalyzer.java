package org.jsynthlib.utils.ctrlr.service.codeparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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

public class BankDriverAnalyzer {

    private final transient Logger log = Logger.getLogger(getClass());

    @Inject
    private CtrlrPanelModel panelModel;

    @Inject
    private BankDriverVisitor visitor;

    public void parseBankDriver(Class<? extends XMLBankDriver> bankDriverClass)
            throws IOException {
        // Get the valid source files as a list
        List<File> files = getFilesAsList(bankDriverClass);
        if (files.size() > 0) {
            Set<String> methodsToParseBefore = new HashSet<String>();
            methodsToParseBefore.addAll(visitor.getMethodsToParse());
            Iterator<File> iterator = files.iterator();
            while (iterator.hasNext()) {
                File file = iterator.next();
                log.debug("Parsing " + file.getAbsolutePath());
                CharStream charStream =
                        new ANTLRFileStream(file.getAbsolutePath());
                JavaLexer lexer = new JavaLexer(charStream);
                JavaParser parser =
                        new JavaParser(new CommonTokenStream(lexer));
                CompilationUnitContext compilationUnit =
                        parser.compilationUnit();
                visitor.visit(compilationUnit);
                if (visitor.getMethodsToParse().isEmpty()) {
                    // When all methods are parsed exit loop
                    break;
                } else if (!iterator.hasNext()) {
                    // Reset if not all method were found
                    iterator = files.iterator();

                    // If the loop has run without any changes to the methods to
                    // parse set - break
                    Set<String> methodsToParseAfter =
                            visitor.getMethodsToParse();
                    if (methodsToParseAfter.equals(methodsToParseBefore)) {
                        break;
                    } else {
                        methodsToParseBefore.clear();
                        methodsToParseBefore.addAll(methodsToParseAfter);
                    }
                }
            }

            if (!visitor.getMethodsToParse().isEmpty()) {
                log.warn("Failed to parse all methods: "
                        + visitor.getMethodsToParse().toString());
            }

            Set<Field> fieldsToParse = visitor.getFieldsToParse();
            for (Field fieldToParse : fieldsToParse) {
                BufferedReader br =
                        new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Enter value for variable "
                        + fieldToParse.getName() + " in driver "
                        + bankDriverClass.getSimpleName());
                String s = br.readLine();
                panelModel.putGlobalVariable(fieldToParse.getLuaName(), s);
            }
        } else {
            log.warn("No valid source files to process. "
                    + "Extiting from the program");
        }
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
        List<File> files = new LinkedList<File>();
        // split the filenames using the delimiter
        List<String> classList = new ArrayList<String>();
        Class<?> currClass = bankDriverClass;
        while (!currClass.equals(Object.class)) {
            StringBuilder sb = new StringBuilder();
            sb.append("src")
            .append(File.separatorChar)
            .append("main")
            .append(File.separatorChar)
            .append("java")
            .append(File.separatorChar)
            .append(currClass.getName()
                    .replace('.', File.separatorChar)).append(".java");
            classList.add(sb.toString());
            currClass = currClass.getSuperclass();
        }

        String[] filesArr = classList.toArray(new String[classList.size()]);
        File sourceFile = null;
        for (String fileName : filesArr) {
            sourceFile = new File(fileName);
            if (sourceFile != null && sourceFile.exists()) {
                files.add(sourceFile);
            } else {
                log.warn(fileName + " is not a valid file. "
                        + "Ignoring the file ");
            }
        }
        return files;
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
