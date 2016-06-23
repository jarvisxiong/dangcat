package org.dangcat.document.csv;

import java.io.IOException;
import java.io.Reader;

class FailingReader extends Reader
{
    public boolean DisposeCalled = false;

    FailingReader()
    {
        super("");
    }

    @Override
    public void close()
    {
        this.DisposeCalled = true;
    }

    @Override
    public int read(char[] buffer, int index, int count) throws IOException
    {
        throw new IOException("Read failed.");
    }
}
