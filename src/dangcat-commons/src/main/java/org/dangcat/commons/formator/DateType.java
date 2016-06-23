package org.dangcat.commons.formator;

public enum DateType {
    Day(4), Full(0), GMT(4), Hour(3), Minute(2), Second(1);

    private static final DateType[] parseValues = new DateType[]{Full, Second, Minute, Day};
    private final int value;

    DateType(int value) {
        this.value = value;
    }

    public static DateType[] getParseValues() {
        return parseValues;
    }

    public int getValue() {
        return this.value;
    }
}
