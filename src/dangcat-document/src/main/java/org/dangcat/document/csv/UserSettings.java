package org.dangcat.document.csv;

public class UserSettings {
    /**
     * Use a backslash character before the text qualifier to represent an
     * occurance of the text qualifier.
     */
    public static final int ESCAPE_MODE_BACKSLASH = 2;
    /**
     * Double up the text qualifier to represent an occurance of the text
     * qualifier.
     */
    public static final int ESCAPE_MODE_DOUBLED = 1;
    public char Comment = Letters.POUND;
    public char Delimiter = Letters.COMMA;
    public int EscapeMode = ESCAPE_MODE_DOUBLED;
    public char RecordDelimiter = Letters.NULL;
    public char TextQualifier = Letters.QUOTE;
    public boolean UseTextQualifier = true;
}
