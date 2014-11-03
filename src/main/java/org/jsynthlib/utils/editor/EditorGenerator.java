package org.jsynthlib.utils.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.jsynthlib.device.model.PatchParam;
import org.jsynthlib.device.model.PatchParamLabel;
import org.jsynthlib.device.viewcontroller.widgets.Envelope;
import org.jsynthlib.xmldevice.EnvelopeSpec;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamGroup;
import org.jsynthlib.xmldevice.PatchParamValues;
import org.jsynthlib.xmldevice.PatchParams;
import org.jsynthlib.xmldevice.StringParamSpec;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;

import com.dreamfabric.DKnob;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JArray;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JVar;

public class EditorGenerator {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            EditorGenerator editorGenerator = new EditorGenerator();
            editorGenerator.generateClasses();
            File file = new File("src/main/java");
            editorGenerator.cm.build(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final JCodeModel cm;
    private final String packageName = "org.jsynthlib.synthdrivers.RolandD50";
    private final String fileNamePrefix = "D50SingleDriver";

    private static final int MAX_NUM_PARAMS_IN_GROUP = 20;
    private final IPanelContainerFactory containerFactory;

    EditorGenerator() {
        cm = new JCodeModel();
        containerFactory =
                new JavaContainerFactory(cm, packageName, fileNamePrefix);
    }

    void generateClasses() throws JClassAlreadyExistsException, XmlException,
            IOException {

        InputStream stream =
                getClass().getClassLoader().getResourceAsStream(
                        packageName.replace('.', '/') + "/" + fileNamePrefix
                                + ".xml");
        XmlSingleDriverDefinitionDocument document =
                XmlSingleDriverDefinitionDocument.Factory.parse(stream);
        XmlSingleDriverDefinition driverSpec = document.getXmlSingleDriverDefinition();
        PatchParams patchParams = driverSpec.getPatchParams();

        PanelContainer editorClass = containerFactory.createEditorClass();
        generateSuperPanelLayout(editorClass, "",
                patchParams.getPatchParamGroupArray().length == 0);

        generateParamsRecursive(editorClass, patchParams, "");
    }

    void generateParamsRecursive(PanelContainer panel, PatchParams group,
            String groupPath) throws JClassAlreadyExistsException {
        String query =
                "declare namespace jsl='http://www.jsynthlib.org/xmldevice';"
                        + "$this/*";
        XmlObject[] xmlObjects = group.selectPath(query);
        int col = 0;
        int row = 0;
        int numCols = 3;
        if (xmlObjects.length <= 4) {
            numCols = 2;
        }

        for (XmlObject xmlObject : xmlObjects) {
            if (xmlObject instanceof IntParamSpec) {
                IntParamSpec paramSpec = (IntParamSpec) xmlObject;
                PatchParamValues paramValues = paramSpec.getPatchParamValues();
                if (paramValues == null) {
                    generateParam(panel, paramSpec.getName(),
                            paramSpec.getUuid(), DKnob.class, row, col);
                } else {
                    generateParam(panel, paramSpec.getName(),
                            paramSpec.getUuid(), JComboBox.class, row, col);
                }
            } else if (xmlObject instanceof PatchParamGroup) {
                PatchParamGroup subGroup = (PatchParamGroup) xmlObject;
                PanelContainer panelContainer = null;
                if (isClassGroup(subGroup)) {
                    panelContainer =
                            containerFactory.generateGroupPanelClass(panel,
                                    groupPath, subGroup.getName());
                    generateSuperPanelLayout(panelContainer,
                            subGroup.getName(),
                            subGroup.getPatchParamGroupArray().length == 0);

                    generateParamsRecursive(panelContainer, subGroup, groupPath
                            + "/" + subGroup.getName());

                    PanelContainer caller = panelContainer.getCaller();
                    JFieldVar field =
                            caller.getPanelClass().field(JMod.PRIVATE,
                                    panelContainer.getPanelClass(),
                                    panelContainer.getName());
                    caller.getCreateMethod()
                            .body()
                            .assign(JExpr._this().ref(field),
                                    JExpr._new(panelContainer.getPanelClass()));
                    caller.getCreateMethod().body()
                            .invoke(JExpr._this(), "add").arg(field);
                } else {
                    panelContainer =
                            containerFactory.generateGroupPanel(panel,
                                    groupPath, subGroup.getName());
                    int numParams = getNumParamsInSingleGroup(subGroup);
                    generatePanelLayout(panelContainer, subGroup.getName(),
                            numParams);

                    generateParamsRecursive(panelContainer, subGroup, groupPath
                            + "/" + subGroup.getName());

                    PanelContainer caller = panelContainer.getCaller();
                    JExpression field = panelContainer.getField();
                    caller.getCreateMethod().body()
                            .invoke(JExpr._this(), "add").arg(field);
                }
            } else if (xmlObject instanceof StringParamSpec) {
                StringParamSpec paramSpec = (StringParamSpec) xmlObject;
                generateParam(panel, paramSpec.getName(), paramSpec.getUuid(),
                        JTextField.class, row, col);
            } else if (xmlObject instanceof EnvelopeSpec) {
                EnvelopeSpec envelope = (EnvelopeSpec) xmlObject;
                generateParam(panel, envelope.getName(), envelope.getUuid(),
                        Envelope.class, row, col);
            }
            col++;
            if (col >= numCols) {
                col = 0;
                row += 2;
            }
        }
    }

    boolean isClassGroup(PatchParams patchParams) {
        return getNumParamsIngroup(patchParams) >= MAX_NUM_PARAMS_IN_GROUP
                || patchParams.getPatchParamGroupArray() == null;
    }

    int getNumParamsIngroup(PatchParams group) {
        int numParams = 0;
        numParams += group.getEnvelopeSpecArray().length;
        numParams += group.getIntParamSpecArray().length;
        numParams += group.getStringParamSpecArray().length;
        PatchParamGroup[] subGroups = group.getPatchParamGroupArray();
        for (PatchParamGroup subGroup : subGroups) {
            numParams += getNumParamsIngroup(subGroup);
        }
        return numParams;
    }

    int getNumParamsInSingleGroup(PatchParams group) {
        int numParams = 0;
        numParams += group.getEnvelopeSpecArray().length;
        numParams += group.getIntParamSpecArray().length;
        numParams += group.getStringParamSpecArray().length;
        return numParams;
    }

    protected void generatePanelLayout(PanelContainer panel, String name, int numParams) {
        int numCols = 3;
        if (numParams <= 4) {
            numCols = 2;
        }
        int numRows = (numParams / numCols) * 2;

        // Layout constraints
        JMethod method = panel.getCreateMethod();
        JVar layout =
                method.body().decl(cm._ref(GridBagLayout.class),
                        panel.getName() + "Gbl");
        layout.init(JExpr._new(cm._ref(GridBagLayout.class)));

        JArray colWidthArrary = JExpr.newArray(cm.INT);
        for (int i = 0; i < numCols; i++) {
            colWidthArrary.add(JExpr.lit(0));
        }
        method.body().assign(layout.ref("columnWidths"), colWidthArrary);

        JArray rowWidthArrary = JExpr.newArray(cm.INT);
        for (int i = 0; i < numRows; i++) {
            rowWidthArrary.add(JExpr.lit(0));
        }
        method.body().assign(layout.ref("rowHeights"), rowWidthArrary);

        JInvocation color =
                JExpr._new(cm.ref(Color.class)).arg(JExpr.lit(0))
                        .arg(JExpr.lit(0)).arg(JExpr.lit(0));
        JInvocation lineBorder =
                JExpr._new(cm.ref(LineBorder.class)).arg(color)
                        .arg(JExpr.lit(1)).arg(JExpr.lit(true));
        JInvocation titledBorder =
                JExpr._new(cm.ref(TitledBorder.class)).arg(lineBorder)
                        .arg(JExpr.lit(name))
                        .arg(JExpr.direct("TitledBorder.LEADING"))
                        .arg(JExpr.direct("TitledBorder.TOP"))
                        .arg(JExpr._null()).arg(JExpr._null());
        method.body().invoke(panel.getField(), "setBorder").arg(titledBorder);
        method.body().invoke(panel.getField(), "setLayout").arg(layout);
    }

    protected void generateSuperPanelLayout(PanelContainer panel, String name,
            boolean gridbagLayout) {
        // Layout constraints
        JMethod method = panel.getCreateMethod();
        if (gridbagLayout) {
            JVar layout =
                    method.body().decl(cm._ref(GridBagLayout.class),
                            panel.getName() + "Gbl");
            layout.init(JExpr._new(cm._ref(GridBagLayout.class)));
            method.body().invoke(panel.getField(), "setLayout").arg(layout);
        } else {
            JVar layout =
                    method.body().decl(cm._ref(FlowLayout.class),
                            panel.getName() + "Fl");
            layout.init(JExpr._new(cm._ref(FlowLayout.class)));
            method.body().invoke(panel.getField(), "setLayout").arg(layout);
        }

        JInvocation color =
                JExpr._new(cm.ref(Color.class)).arg(JExpr.lit(0))
                        .arg(JExpr.lit(0)).arg(JExpr.lit(0));
        JInvocation lineBorder =
                JExpr._new(cm.ref(LineBorder.class)).arg(color)
                        .arg(JExpr.lit(1)).arg(JExpr.lit(true));
        JInvocation titledBorder =
                JExpr._new(cm.ref(TitledBorder.class)).arg(lineBorder)
                        .arg(JExpr.lit(name))
                        .arg(JExpr.direct("TitledBorder.LEADING"))
                        .arg(JExpr.direct("TitledBorder.TOP"))
                        .arg(JExpr._null()).arg(JExpr._null());
        method.body().invoke(panel.getField(), "setBorder").arg(titledBorder);
    }

    protected JFieldVar generateParam(PanelContainer panel, String paramName,
            String uuid, Class<?> class1, int row, int col) {
        String paramPath = panel.getName() + "/" + paramName;
        String javaName = getJavaName(paramPath);

        JDefinedClass panelClass = panel.getPanelClass();
        JFieldVar param = panelClass.field(JMod.PRIVATE, class1, javaName);
        JAnnotationUse annotationUse = param.annotate(PatchParam.class);
        annotationUse.param("uuid", uuid);

        JMethod paneMethod = panel.getCreateMethod();
        JBlock body = paneMethod.body();
        JVar constraints =
                body.decl(cm._ref(GridBagConstraints.class), javaName + "Gbc");
        constraints.init(JExpr._new(cm._ref(GridBagConstraints.class)));

        body.assign(constraints.ref("gridx"), JExpr.lit(col));
        body.assign(constraints.ref("gridy"), JExpr.lit(row));

        body.assign(JExpr._this().ref(param), JExpr._new(cm._ref(class1)));
        body.invoke(panel.getField(), "add").arg(param).arg(constraints);

        JFieldVar label =
                panelClass.field(JMod.PRIVATE, JLabel.class, javaName + "Lbl");
        JAnnotationUse annotationUse2 = label.annotate(PatchParamLabel.class);
        annotationUse2.param("uuid", uuid);

        body.assign(JExpr._this().ref(label), JExpr._new(cm._ref(JLabel.class))
                .arg("<html><center>" + paramName + "</center></html>"));
        body.invoke(label, "setLabelFor").arg(param);
        JVar dimension =
                body.decl(cm._ref(Dimension.class), javaName + "LblDim");
        dimension.init(body.invoke(label, "getPreferredSize"));

        JVar newDimension =
                body.decl(cm._ref(Dimension.class), javaName + "LblNewDim");
        newDimension
                .init(JExpr
                        ._new(cm.ref(Dimension.class))
                        .arg(JExpr.lit(70))
                        .arg(JOp.mul(
                                JExpr.cast(cm.INT,
                                        body.invoke(dimension, "getHeight")),
                                JExpr.lit(2))));
        body.invoke(label, "setPreferredSize").arg(newDimension);

        JVar labelConstraints =
                body.decl(cm._ref(GridBagConstraints.class), javaName
                        + "LblGbc");
        labelConstraints.init(JExpr._new(cm._ref(GridBagConstraints.class)));

        body.assign(labelConstraints.ref("gridx"), JExpr.lit(col));
        body.assign(labelConstraints.ref("gridy"), JExpr.lit(row + 1));
        body.invoke(panel.getField(), "add").arg(label).arg(labelConstraints);
        return param;
    }

    static String getJavaName(String name) {
        return name.replaceAll("[ /\\-]|(<[^>]+>)", "").trim();
    }
}
