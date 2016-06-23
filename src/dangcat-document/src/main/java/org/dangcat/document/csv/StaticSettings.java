package org.dangcat.document.csv;

class StaticSettings
{
    protected static final int INITIAL_COLUMN_BUFFER_SIZE = 50;
    protected static final int INITIAL_COLUMN_COUNT = 10;
    // these are static instead of final so they can be changed in unit test
    // isn't visible outside this class and is only accessed once during
    // CsvReader construction
    protected static final int MAX_BUFFER_SIZE = 1024;
    protected static final int MAX_FILE_BUFFER_SIZE = 4 * 1024;
}
