package org.dangcat.persistence.simulate.data;

/**
 * 字符模拟器。
 *
 * @author dangcat
 */
public class CharsSimulator extends ValueSimulator {
    private static final char INIT_VALUE = 'A';
    private int length = 0;

    public CharsSimulator(int length) {
        super(char[].class);
        this.length = length;
    }

    protected Object createValue(int index) {
        char[] chars = new char[this.length];
        for (int i = 0; i < this.length; i++)
            chars[i] = (char) (INIT_VALUE + (index % 26));
        return chars;
    }
}
