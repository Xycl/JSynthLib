package org.jsynthlib.device.model.handler

class ClosurePatchNameSender extends ClosureHandlerBase implements IPatchStringSender {

    public static class Builder extends ClosureHandlerBuilderBase {

        @Override
        public ClosureHandlerBase newInstance() {
            return new ClosurePatchNameSender()
        }
    }

    @Override
    public void send(String name) {
        def c = getClosure()
        c(name)
    }
}
