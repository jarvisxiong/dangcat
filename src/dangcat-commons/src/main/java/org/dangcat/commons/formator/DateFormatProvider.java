package org.dangcat.commons.formator;

public class DateFormatProvider implements FormatProvider {
    private static final String DEFAULT_DATE_PATTERN = "DEFAULT_DATE_PATTERN";
    private String datePattern = null;
    private String format = null;

    public DateFormatProvider(DateType dateType) {
        this.format = DateFormator.getDatePattern(dateType);
        this.datePattern = DEFAULT_DATE_PATTERN + "_" + dateType.name().toUpperCase();
    }

    public String getFormat() {
        return System.getProperty(this.datePattern, this.format);
    }
}
