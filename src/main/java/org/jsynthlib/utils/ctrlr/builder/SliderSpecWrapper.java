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
            DefaultSliderSpecWrapper wrapper = new DefaultSliderSpecWrapper();
            wrapper.setMax(paramSpec.getMax());
            wrapper.setMin(paramSpec.getMin());
            wrapper.setName(paramSpec.getName());
            wrapper.setMsRef(cg.getMidiSender());
            wrapper.setPmRef(cg.getParamModel());
            return wrapper;
        }

        public static SliderSpecWrapper newWrapper(EnvelopeParamSpec paramSpec,
                int index) {
            DefaultSliderSpecWrapper wrapper = new DefaultSliderSpecWrapper();
            wrapper.setMax(paramSpec.getMax());
            wrapper.setMin(paramSpec.getMin());
            if (paramSpec.isSetName()) {
                wrapper.setName(paramSpec.getName());
            } else {
                wrapper.setName(Integer.toString(index));
            }
            wrapper.setMsRef(paramSpec.getMidiSender());
            wrapper.setPmRef(paramSpec.getParamModel());
            return wrapper;
        }

        public static SliderSpecWrapper newWrapper(IntParamSpec paramSpec) {
            DefaultSliderSpecWrapper wrapper = new DefaultSliderSpecWrapper();
            wrapper.setMax(paramSpec.getMax());
            wrapper.setMin(paramSpec.getMin());
            wrapper.setName(paramSpec.getName());
            wrapper.setMsRef(paramSpec.getMidiSender());
            wrapper.setPmRef(paramSpec.getParamModel());
            return wrapper;
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
