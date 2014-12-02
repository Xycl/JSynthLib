package org.jsynthlib.utils.ctrlr;

import java.io.File;

import com.google.inject.assistedinject.Assisted;

public interface CtrlrSynthGeneratorFactory {

    CtrlrSynthGenerator newPanelGenerator(
            @Assisted("packageName") String packageName,
            @Assisted("fileNamePrefix") String fileNamePrefix, File outDir);
}
