package org.dangcat.document.csv;

import java.io.StringReader;

class CsvReaderUtils
{
    protected static char hexToDec(char hex)
    {
        char result;

        if (hex >= 'a')
            result = (char) (hex - 'a' + 10);
        else if (hex >= 'A')
            result = (char) (hex - 'A' + 10);
        else
            result = (char) (hex - '0');
        return result;
    }

    // these are all more or less global loop variables
    // to keep from needing to pass them all into various
    // methods during parsing

    /**
     * Creates a {@link com.csvreader.CsvReader CsvReader} object using a string
     * of data as the source.&nbsp;Uses ISO-8859-1 as the
     * {@link java.nio.charset.Charset Charset}.
     * 
     * @param data The String of data to use as the source.
     * @return A {@link com.csvreader.CsvReader CsvReader} object using the
     *         String of data as the source.
     */
    protected static CsvReader parse(String data)
    {
        if (data == null)
            throw new IllegalArgumentException("Parameter data can not be null.");
        return new CsvReader(new StringReader(data));
    }
}
