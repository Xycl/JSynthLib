package org.jsynthlib.device.model.handler

class ClosureParamModel extends ClosureHandlerBase implements IParamModel {

    public static class Builder extends ClosureHandlerBuilderBase {

        @Override
        public ClosureHandlerBase newInstance() {
            return new ClosureParamModel()
        }
    }

    @Override
    public void set(int value) {
        getClosure().call(value)
    }

    @Override
    public int get() {
        // TODO Auto-generated method stub
        return 0;
    }
}
