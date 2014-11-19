package org.jsynthlib.utils.ctrlr;

import org.jsynthlib.utils.ctrlr.builder.CtrlrComponentBuilder;

public interface CtrlrComponentBuilderFactory {

    CtrlrComponentBuilder<? extends Object> newFactory(Object object);

}