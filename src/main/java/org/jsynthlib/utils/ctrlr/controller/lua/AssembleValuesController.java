package org.jsynthlib.utils.ctrlr.controller.lua;

import java.util.Observable;
import java.util.Observer;

import org.jsynthlib.utils.SysexUtils;
import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.utils.ctrlr.domain.DriverTypeModel;
import org.jsynthlib.utils.ctrlr.domain.PreConditionsNotMetException;
import org.jsynthlib.xmldevice.XmlDriverDefinition;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class AssembleValuesController extends EditorLuaMethodControllerBase
implements Observer {

    public interface Factory {
        AssembleValuesController newAssembleValuesController();
    }

    @Inject
    private XmlDriverDefinition driverDef;

    private final String prefix;

    private final DriverTypeModel driverTypeModel;

    private String checksumMethodName;

    /**
     * @param group
     * @param methodName
     */
    @Inject
    public AssembleValuesController(@Named("prefix") String prefix,
            DriverModel model, DriverTypeModel driverTypeModel) {
        super(model.getAssembleValuesMethodName());
        this.prefix = prefix;
        this.driverTypeModel = driverTypeModel;
        driverTypeModel.addObserver(this);
    }

    @Override
    protected void checkPreconditions() throws PreConditionsNotMetException {
        if (checksumMethodName == null) {
            throw new PreConditionsNotMetException();
        }
        super.checkPreconditions();
    }


    @Override
    protected void writeLuaMethodCode() {
        String sysexID = driverDef.getSysexID().replaceAll("\\*{2}", "00");
        byte[] header = SysexUtils.stringToSysex(sysexID);
        byte[] buf = new byte[driverDef.getPatchSize()];
        System.arraycopy(header, 0, buf, 0, header.length);
        buf[buf.length - 1] = (byte) 0xF7;

        int indent = 0;
        StringBuilder code = new StringBuilder();
        code.append(indent(indent))
        .append("-- This method assembles the param values from ")
        .append(newLine());
        code.append(indent(indent))
        .append("-- all modulators and stores them in a memory block")
        .append(newLine());
        code.append(indent(indent++)).append(getMethodDecl("data"))
        .append(newLine());
        code.append(indent(indent)).append("data:createFromTable({")
        .append(SysexUtils.byteToHexStringArray(buf)).append("})")
        .append(newLine());
        code.append(indent(indent)).append("local headerSize = ")
        .append(header.length).append(newLine());
        code.append(indent(indent)).append("local patchSize = ")
        .append(driverDef.getPatchSize()).append(newLine());

        code.append(indent(indent++))
        .append("for i = headerSize, patchSize do")
        .append(" -- run through all modulators and fetch their value")
        .append(newLine());
        code.append(indent(indent)).append("name = &quot;").append(prefix)
        .append("&quot;..i").append(newLine());
        code.append(indent(indent))
        .append("mod = panel:getModulatorByName(name)")
        .append(newLine());
        code.append(indent(indent++)).append("if mod ~= nil then")
        .append(newLine());
        code.append(indent(indent)).append("data:setByte(i, mod:getValue())")
        .append(newLine());
        code.append(indent(--indent)).append("end").append(newLine());

        code.append(indent(--indent)).append("end").append(newLine());

        // Call calculateChecksum
        String csStart = Integer.toString(driverTypeModel.getSingleCsStart());
        String csEnd = Integer.toString(driverTypeModel.getSingleCsEnd());
        String csOfs = Integer.toString(driverTypeModel.getSingleCsOfs());
        code.append(indent(indent))
        .append(getMethodCall(checksumMethodName, "data", csStart,
                csEnd, csOfs)).append(newLine());
        code.append(indent(--indent)).append("end").append(newLine());

        setLuaMethodCode(code.toString());
        driverTypeModel.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        checksumMethodName = driverTypeModel.getCalculateChecksumMethodName();
        init();
    }
}
