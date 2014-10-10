package org.jsynthlib.device.viewcontroller;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

public class WidgetDataFormat extends Format {

    private static final long serialVersionUID = 1L;

    private final int base;

    public WidgetDataFormat() {
        this(0);
    }

    public WidgetDataFormat(int base) {
        this.base = base;
    }

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo,
            FieldPosition pos) {
        String s = "";
        if (obj instanceof Long) {
            long longValue = (long) obj;
            s = Integer.toString((int) (longValue + base));
        } else if (obj instanceof Integer) {
            int longValue = (int) obj;
            s = Integer.toString(longValue + base);
        } else if (obj instanceof Number) {
            Number number = (Number) obj;
            s = Integer.toString(number.intValue() + base);
        }
        return toAppendTo.append(s, pos.getBeginIndex(), s.length());
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return null;
    }

}
