package org.dangcat.document.csv;

class DataBuffer {
    protected char[] Buffer = new char[StaticSettings.MAX_BUFFER_SIZE];
    // / <summary>
    // / The position of the cursor in the buffer when the
    // / current column was started or the last time data
    // / was moved out to the column buffer.
    // / </summary>
    protected int ColumnStart = 0;
    // / <summary>
    // / How much usable data has been read into the stream,
    // / which will not always be as long as Buffer.Length.
    // / </summary>
    protected int Count = 0;
    protected int LineStart = 0;
    protected int Position = 0;
}
