package org.jsynthlib.utils.ctrlr.builder;

import org.jsynthlib.xmldevice.CombinedGroup;
import org.jsynthlib.xmldevice.CombinedIntPatchParam;
import org.jsynthlib.xmldevice.EnvelopeParamSpec;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.MidiSenderReference;
import org.jsynthlib.xmldevice.ParamModelReference;

public interface SliderSpecWrapper {

    final class Factory {

        private Factory() {
        }

        public static SliderSpecWrapper newWrapper(
                CombinedIntPatchParam paramSpec, CombinedGroup cg) {
            SliderSpecWrapperImpl wrapper = new SliderSpecWrapperImpl();
            wrapper.max = paramSpec.getMax();
            wrapper.min = paramSpec.getMin();
            wrapper.name = paramSpec.getName();
            wrapper.msRef = cg.getMidiSender();
            wrapper.pmRef = cg.getParamModel();
            return wrapper;
        }

        public static SliderSpecWrapper newWrapper(EnvelopeParamSpec paramSpec,
                int index) {
            SliderSpecWrapperImpl wrapper = new SliderSpecWrapperImpl();
            wrapper.max = paramSpec.getMax();
            wrapper.min = paramSpec.getMin();
            if (paramSpec.isSetName()) {
                wrapper.name = paramSpec.getName();
            } else {
                wrapper.name = Integer.toString(index);
            }
            wrapper.msRef = paramSpec.getMidiSender();
            wrapper.pmRef = paramSpec.getParamModel();
            return wrapper;
        }

        public static SliderSpecWrapper newWrapper(IntParamSpec paramSpec) {
            SliderSpecWrapperImpl wrapper = new SliderSpecWrapperImpl();
            wrapper.max = paramSpec.getMax();
            wrapper.min = paramSpec.getMin();
            wrapper.name = paramSpec.getName();
            wrapper.msRef = paramSpec.getMidiSender();
            wrapper.pmRef = paramSpec.getParamModel();
            return wrapper;
        }

        private static class SliderSpecWrapperImpl implements SliderSpecWrapper {

            private String name;
            private int min;
            private int max;
            private MidiSenderReference msRef;
            private ParamModelReference pmRef;

            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getMin() {
                return min;
            }

            @Override
            public int getMax() {
                return max;
            }

            @Override
            public MidiSenderReference getMidiSender() {
                return msRef;
            }

            @Override
            public boolean isSetMidiSender() {
                return msRef != null;
            }

            @Override
            public boolean isSetParamModel() {
                return pmRef != null;
            }

            @Override
            public ParamModelReference getParamModel() {
                return pmRef;
            }
        }
    }

    String getName();

    int getMin();

    int getMax();

    MidiSenderReference getMidiSender();

    boolean isSetMidiSender();

    boolean isSetParamModel();

    ParamModelReference getParamModel();
}
