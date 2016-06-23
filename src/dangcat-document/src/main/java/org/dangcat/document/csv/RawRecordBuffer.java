package org.dangcat.document.csv;

class RawRecordBuffer
{
    protected char[] Buffer = new char[StaticSettings.INITIAL_COLUMN_BUFFER_SIZE * StaticSettings.INITIAL_COLUMN_COUNT];
    protected int Position = 0;
}
