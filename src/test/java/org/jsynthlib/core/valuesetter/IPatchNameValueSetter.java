package org.jsynthlib.core.valuesetter;

public interface IPatchNameValueSetter extends IValueSetter {

    String[] VALUES = {
            "a", "A", "Apa", "apa", "Text T", "t t tt" };

    final class ValueProvider {
        private ValueProvider() {

        }

        public static int getValueIndex(String value) {
            for (int i = 0; i < VALUES.length; i++) {
                String val = VALUES[i];
                if (val.equals(value)) {
                    return i;
                }
            }
            throw new IllegalArgumentException("Could not find value " + value);
        }
    }
}
