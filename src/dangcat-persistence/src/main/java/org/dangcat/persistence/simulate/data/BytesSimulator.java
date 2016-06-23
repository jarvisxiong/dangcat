package org.dangcat.persistence.simulate.data;

/**
 * 字节数组模拟器。
 * @author dangcat
 * 
 */
public class BytesSimulator extends ValueSimulator
{
    private static final int DEFAULT_LENGTH = 256;
    private static final char INIT_VALUE = 'A';
    private int length = 0;

    public BytesSimulator(int length)
    {
        super(byte[].class);
        this.length = length;
    }

    protected Object createValue(int index)
    {
        byte[] bytes = new byte[this.getLength()];
        for (int i = 0; i < this.getLength(); i++)
            bytes[i] = (byte) (INIT_VALUE + index);
        return bytes;
    }

    public int getLength()
    {
        return this.length == 0 ? DEFAULT_LENGTH : this.length;
    }
}
