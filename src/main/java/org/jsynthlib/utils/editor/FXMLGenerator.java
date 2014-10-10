package org.jsynthlib.utils.editor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.jsynthlib.fxml.AlignmentType;
import org.jsynthlib.fxml.AnchorPane;
import org.jsynthlib.fxml.AnchorPaneDocument;
import org.jsynthlib.fxml.CheckBox;
import org.jsynthlib.fxml.Children;
import org.jsynthlib.fxml.ComboBox;
import org.jsynthlib.fxml.Content;
import org.jsynthlib.fxml.Envelope;
import org.jsynthlib.fxml.FlowPane;
import org.jsynthlib.fxml.FlowPaneMargin;
import org.jsynthlib.fxml.FlowPaneMargin.Insets;
import org.jsynthlib.fxml.GridPane;
import org.jsynthlib.fxml.Knob;
import org.jsynthlib.fxml.Label;
import org.jsynthlib.fxml.Layoutable;
import org.jsynthlib.fxml.Pane;
import org.jsynthlib.fxml.TextField;
import org.jsynthlib.fxml.TitledPane;
import org.jsynthlib.xmldevice.CombinedGroup;
import org.jsynthlib.xmldevice.CombinedIntPatchParam;
import org.jsynthlib.xmldevice.EnvelopeNodeSpec;
import org.jsynthlib.xmldevice.EnvelopeSpec;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamGroup;
import org.jsynthlib.xmldevice.PatchParamValues;
import org.jsynthlib.xmldevice.PatchParams;
import org.jsynthlib.xmldevice.StringParamSpec;
import org.jsynthlib.xmldevice.XEnvelopeParamSpec;
import org.jsynthlib.xmldevice.XmlPatchDriverSpecDocument;
import org.jsynthlib.xmldevice.XmlPatchDriverSpecDocument.XmlPatchDriverSpec;
import org.jsynthlib.xmldevice.YEnvelopeParamSpec;

/*
 * TODO: Add fx: in front of id attributes.
 */
public class FXMLGenerator {

    private static final int MAX_NUM_PARAMS_IN_GROUP = 20;

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            String packageName = System.getProperty("packageName");
            String fileNamePrefix = System.getProperty("fileNamePrefix");
            FXMLGenerator editorGenerator = new FXMLGenerator(packageName, fileNamePrefix);
            editorGenerator.generateClasses();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final String packageName;
    private final String fileNamePrefix;
    private AnchorPaneDocument paneDocument;
    private AnchorPane anchorPane;
    private boolean driverDocEdited;

    /**
     * @param packageName
     * @param fileNamePrefix
     */
    public FXMLGenerator(String packageName, String fileNamePrefix) {
        this.packageName = packageName;
        this.fileNamePrefix = fileNamePrefix;
    }

    void generateClasses() throws XmlException, IOException {

        InputStream stream =
                getClass().getClassLoader().getResourceAsStream(
                        packageName.replace('.', '/') + "/" + fileNamePrefix
                                + ".xml");
        XmlPatchDriverSpecDocument document =
                XmlPatchDriverSpecDocument.Factory.parse(stream);
        XmlPatchDriverSpec driverSpec = document.getXmlPatchDriverSpec();
        PatchParams patchParams = driverSpec.getPatchParams();

        paneDocument = AnchorPaneDocument.Factory.newInstance();
        anchorPane = paneDocument.addNewAnchorPane();
        anchorPane.setPrefHeight((float) 800.0);
        anchorPane.setPrefWidth((float) 1200.0);
        Children children = anchorPane.addNewChildren();
        FlowPane flowPane = children.addNewFlowPane();
        flowPane.setAnchorPaneBottomAnchor((float) 0.0);
        flowPane.setAnchorPaneLeftAnchor((float) 0.0);
        flowPane.setAnchorPaneRightAnchor((float) 0.0);
        flowPane.setAnchorPaneTopAnchor((float) 0.0);
        flowPane.setStyleClass("bordered-titled-content");
        generateParamsRecursive(flowPane, patchParams, "");
        XmlOptions options = new XmlOptions();
        options.setSavePrettyPrint();
        // HashMap<String, String> substitutes = new HashMap<String, String>();
        // substitutes.put("http://www.jsynthlib.org/fxml",
        // "http://javafx.com/javafx/2.2");
        // options.setLoadSubstituteNamespaces(substitutes);
        options.setUseDefaultNamespace();
        // paneDocument.save(new File("output/test.fxml"), options);
        String xmlText = paneDocument.xmlText(options);
        String xml =
                xmlText.replace("xmlns=\"http://www.jsynthlib.org/fxml\"",
                        "xmlns=\"http://javafx.com/javafx/2.2\"");
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n")
                .append("<?import java.lang.*?>\n")
                .append("<?import javafx.collections.*?>\n")
                .append("<?import javafx.geometry.*?>\n")
                .append("<?import javafx.scene.control.*?>\n")
                .append("<?import javafx.scene.control.Slider?>\n")
                .append("<?import javafx.scene.layout.*?>\n")
                .append("<?import javafx.scene.layout.AnchorPane?>\n")
                .append("<?import org.jsynthlib.device.view.*?>\n")
                .append("<?scenebuilder-classpath-element ../../../../../../../target/classes?>\n")
                .append("<?scenebuilder-stylesheet ../../../../application.css?>\n\n");
        xml = xml.replace("TextField id=\"", "TextField fx:id=\"");
        xml = xml.replace("Label id=\"", "Label fx:id=\"");
        xml = xml.replace("ComboBox id=\"", "ComboBox fx:id=\"");
        xml = xml.replace("Knob id=\"", "Knob fx:id=\"");
        xml = xml.replace("Envelope id=\"", "Envelope fx:id=\"");
//        xml = xml.replace(" id=\"", " fx:id=\"");
//        xml = xml.replace(" id=\"", " fx:id=\"");
//        xml = xml.replace(" id=\"", " fx:id=\"");
//        xml = xml.replace(" id=\"", " fx:id=\"");
        sb.append(xml);
        FileOutputStream fos =
                new FileOutputStream(new File(fileNamePrefix + "Editor.fxml"));
        fos.write(sb.toString().getBytes());
        fos.flush();
        fos.close();

        if (driverDocEdited) {
            document.save(new File(fileNamePrefix + "XmlUpdate.xml"));
        }
    }

    void generateParamsRecursive(Pane panel, PatchParams group, String groupPath) {
        Children children = panel.addNewChildren();
        Children paramChildren = null;
        if (isSingleGroup(group) || isWrapperGroup(group)) {
            paramChildren = children;
        } else {
            GridPane gridPane = children.addNewGridPane();
            paramChildren = gridPane.addNewChildren();
        }

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
                String uuid = paramSpec.getUuid();
                if (uuid == null || uuid.isEmpty()) {
                    paramSpec.setUuid(generateUuid());
                    driverDocEdited = true;
                }
                PatchParamValues paramValues = paramSpec.getPatchParamValues();
                if (paramValues == null) {
                    if (paramSpec.getMin() == 0 && paramSpec.getMax() == 1) {
                        CheckBox checkBox = paramChildren.addNewCheckBox();
                        generateParam(paramChildren, paramSpec.getName(),
                                paramSpec.getUuid(), checkBox, row, col);
                    } else {
                        Knob knob = paramChildren.addNewKnob();
                        // slider.setPrefWidth((float) 85.0);
                        knob.setStyleClass("knobStyle");
                        generateParam(paramChildren, paramSpec.getName(),
                                paramSpec.getUuid(), knob, row, col);
                    }
                } else {
                    ComboBox comboBox = paramChildren.addNewComboBox();
                    comboBox.setGridPaneHalignment(AlignmentType.CENTER);
                    generateParam(paramChildren, paramSpec.getName(),
                            paramSpec.getUuid(), comboBox, row, col);
                }
            } else if (xmlObject instanceof PatchParamGroup) {
                PatchParamGroup subGroup = (PatchParamGroup) xmlObject;
                Pane p = null;
                TitledPane titledPane = children.addNewTitledPane();
                titledPane.setText(subGroup.getName());
                FlowPaneMargin margin = titledPane.addNewFlowPaneMargin();
                Insets insets = margin.addNewInsets();
                insets.setBottom(2);
                insets.setLeft(2);
                insets.setRight(2);
                insets.setTop(2);

                Content content = titledPane.addNewContent();
                AnchorPane subAnchorPane = content.addNewAnchorPane();
                Children anchorChildren = subAnchorPane.addNewChildren();
                if (isSingleGroup(subGroup)) {
                    p = anchorChildren.addNewGridPane();
                    GridPane gp = (GridPane) p;
                    gp.setAnchorPaneBottomAnchor(0);
                    gp.setAnchorPaneLeftAnchor(0);
                    gp.setAnchorPaneRightAnchor(0);
                    gp.setAnchorPaneTopAnchor(0);
                } else {
                    p = anchorChildren.addNewFlowPane();
                    FlowPane fp = (FlowPane) p;
                    fp.setAnchorPaneBottomAnchor(0);
                    fp.setAnchorPaneLeftAnchor(0);
                    fp.setAnchorPaneRightAnchor(0);
                    fp.setAnchorPaneTopAnchor(0);
                }
                p.setStyleClass("bordered-titled-content");

                generateParamsRecursive(p, subGroup,
                        groupPath + "/" + subGroup.getName());
            } else if (xmlObject instanceof StringParamSpec) {
                StringParamSpec paramSpec = (StringParamSpec) xmlObject;
                String uuid = paramSpec.getUuid();
                if (uuid == null || uuid.isEmpty()) {
                    paramSpec.setUuid(generateUuid());
                    driverDocEdited = true;
                }
                TextField textField = paramChildren.addNewTextField();
                textField.setPrefWidth(100);
                generateParam(paramChildren, paramSpec.getName(),
                        paramSpec.getUuid(), textField, row, col);
            } else if (xmlObject instanceof EnvelopeSpec) {
                 EnvelopeSpec envelopeSpec = (EnvelopeSpec) xmlObject;
                 generateEnvelopeUuids(envelopeSpec);
                 String uuid = envelopeSpec.getUuid();
                 if (uuid == null || uuid.isEmpty()) {
                     envelopeSpec.setUuid(generateUuid());
                     driverDocEdited = true;
                 }
                 Envelope envelope = paramChildren.addNewEnvelope();
                 generateParam(children, "", envelopeSpec.getUuid(), envelope, row, col);
            } else if (xmlObject instanceof CombinedGroup) {
                CombinedGroup combGroup = (CombinedGroup) xmlObject;
                CombinedIntPatchParam[] paramArray = combGroup.getParamArray();
                for (CombinedIntPatchParam intPatchParam : paramArray) {
                    String uuid = intPatchParam.getUuid();
                    if (uuid == null || uuid.isEmpty()) {
                        intPatchParam.setUuid(generateUuid());
                        driverDocEdited = true;
                    }

                    PatchParamValues paramValues = intPatchParam.getPatchParamValues();
                    if (paramValues == null) {
                        CheckBox cb = paramChildren.addNewCheckBox();
                        generateParam(paramChildren, intPatchParam.getName(),
                                intPatchParam.getUuid(), cb, row, col);
                    } else {
                        ComboBox comboBox = paramChildren.addNewComboBox();
                        comboBox.setGridPaneHalignment(AlignmentType.CENTER);
                        generateParam(paramChildren, intPatchParam.getName(),
                                intPatchParam.getUuid(), comboBox, row, col);
                    }
                }
            }
            col++;
            if (col >= numCols) {
                col = 0;
                row += 2;
            }
        }
    }

    void generateEnvelopeUuids(EnvelopeSpec envelopeSpec) {
        EnvelopeNodeSpec[] envelopeNodeSpecs = envelopeSpec.getEnvelopeNodeSpecArray();
        for (EnvelopeNodeSpec envelopeNodeSpec : envelopeNodeSpecs) {
            XEnvelopeParamSpec xParam = envelopeNodeSpec.getXParam();
            String uuid = xParam.getUuid();
            if (uuid == null || uuid.isEmpty()) {
                xParam.setUuid(generateUuid());
                driverDocEdited = true;
            }

            YEnvelopeParamSpec yParam = envelopeNodeSpec.getYParam();
            uuid = yParam.getUuid();
            if (uuid == null || uuid.isEmpty()) {
                yParam.setUuid(generateUuid());
                driverDocEdited = true;
            }
        }
    }

    String generateUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("\\-", "");
    }

    boolean isWrapperGroup(PatchParams patchParams) {
        return getNumParamsInSingleGroup(patchParams) == 0;
    }

    boolean isSingleGroup(PatchParams patchParams) {
        return patchParams.getPatchParamGroupArray() == null
                || patchParams.getPatchParamGroupArray().length == 0;
    }

    boolean isClassGroup(PatchParams patchParams) {
        return getNumParamsIngroup(patchParams) >= MAX_NUM_PARAMS_IN_GROUP
                || patchParams.getPatchParamGroupArray() == null
                || patchParams.getPatchParamGroupArray().length == 0;
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

    protected void generateParam(Children children, String paramName,
            String uuid, Layoutable layoutable, int row, int col) {
        // float x = 5 + col * X_OFFSET;
        // float y = 5 + row * Y_OFFSET * 2;
        layoutable.setId(uuid);
        layoutable.setGridPaneColumnIndex(col);
        layoutable.setGridPaneRowIndex(row);
        // layoutable.setLayoutX(x);
        // layoutable.setLayoutY(y);
        // float lblY = 5 + (row + 1) * Y_OFFSET * 2;
        Label label = children.addNewLabel();
        label.setPrefWidth((float) 100.0);
        label.setWrapText(true);
        label.setText(paramName);
        label.setId("lbl" + uuid);
        label.setAlignment(AlignmentType.CENTER);
        // label.setTextAlignment(AlignmentType.CENTER);
        // label.setLayoutX(x);
        // label.setLayoutY(lblY);
        label.setGridPaneColumnIndex(col);
        label.setGridPaneRowIndex(row + 1);
    }

    static String getJavaName(String name) {
        return name.replaceAll("[ /\\-]|(<[^>]+>)", "").trim();
    }
}
