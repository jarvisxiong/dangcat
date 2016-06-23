package org.dangcat.document.csv;

class ColumnBuffer {
    protected char[] Buffer = new char[StaticSettings.INITIAL_COLUMN_BUFFER_SIZE];
    protected int Position = 0;
}
