package org.jsynthlib.utils.ctrlr;

import org.jsynthlib.xmldevice.CombinedIntPatchParam;
import org.jsynthlib.xmldevice.EnvelopeParamSpec;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.MidiSenderReference;

public interface SliderSpecWrapper {

    final class Factory {

        private Factory() {
        }

        public static SliderSpecWrapper newWrapper(
                CombinedIntPatchParam paramSpec,
                MidiSenderReference midiSenderReference) {
            SliderSpecWrapperImpl wrapper = new SliderSpecWrapperImpl();
            wrapper.max = paramSpec.getMax();
            wrapper.min = paramSpec.getMin();
            wrapper.name = paramSpec.getName();
            wrapper.ref = midiSenderReference;
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
            wrapper.ref = paramSpec.getMidiSender();
            return wrapper;
        }

        public static SliderSpecWrapper newWrapper(IntParamSpec paramSpec) {
            SliderSpecWrapperImpl wrapper = new SliderSpecWrapperImpl();
            wrapper.max = paramSpec.getMax();
            wrapper.min = paramSpec.getMin();
            wrapper.name = paramSpec.getName();
            wrapper.ref = paramSpec.getMidiSender();
            return wrapper;
        }

        private static class SliderSpecWrapperImpl implements SliderSpecWrapper {

            private String name;
            private int min;
            private int max;
            private MidiSenderReference ref;

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
                return ref;
            }

            @Override
            public boolean isSetMidiSender() {
                return ref != null;
            }

        }
    }

    String getName();

    int getMin();

    int getMax();

    MidiSenderReference getMidiSender();

    boolean isSetMidiSender();
}
