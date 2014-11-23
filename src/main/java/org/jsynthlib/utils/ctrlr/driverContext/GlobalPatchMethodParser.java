package org.jsynthlib.utils.ctrlr.driverContext;

import org.jsynthlib.utils.ctrlr.builder.method.MethodBuilder;

public interface GlobalPatchMethodParser {

    MethodBuilder getPatchRequestBuilder();

    MethodBuilder getPatchStoreBuilder();

    MethodBuilder getPatchLoadBuilder();

    MethodBuilder getPatchSaveBuilder();
}
