package org.jsynthlib.device.model.handler

import org.jsynthlib.device.model.IDriver

class ClosureMidiSender extends ClosureHandlerBase implements ISender {

    public static class Builder extends ClosureHandlerBuilderBase {

        @Override
        public ClosureHandlerBase newInstance() {
            return new ClosureMidiSender()
        }
    }

    @Override
    public void send(IDriver driver, int value) {
        def c = getClosure()
        c(driver, value)
    }
}
