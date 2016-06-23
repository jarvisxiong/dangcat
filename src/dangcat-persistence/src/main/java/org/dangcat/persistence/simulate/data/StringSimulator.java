package org.dangcat.persistence.simulate.data;

import org.dangcat.commons.utils.ValueUtils;

/**
 * ×Ö´®Ä£ÄâÆ÷¡£
 *
 * @author dangcat
 */
public class StringSimulator extends ValueSimulator {
    private static final char FILL_CHAR = '0';
    private int fillLength = 0;
    private String fillText = null;
    private int length = 0;
    private String postfix = null;
    private String prefix = null;

    public StringSimulator(int length) {
        this(null, null, length);
    }

    public StringSimulator(String prefix, int length) {
        this(prefix, null, length);
    }

    public StringSimulator(String prefix, String postfix, int length) {
        super(String.class);
        this.prefix = prefix;
        this.postfix = postfix;
        this.length = length;
    }

    protected Object createValue(int index) {
        StringBuilder value = new StringBuilder();
        if (!ValueUtils.isEmpty(this.prefix))
            value.append(this.prefix);
        value.append(this.getFillText(index));
        if (!ValueUtils.isEmpty(this.postfix))
            value.append(this.postfix);
        return value.toString();
    }

    private String getFillText(int index) {
        if (this.fillText == null) {
            StringBuilder text = new StringBuilder();
            if (!ValueUtils.isEmpty(this.prefix))
                text.append(this.prefix);
            if (!ValueUtils.isEmpty(this.postfix))
                text.append(this.postfix);
            int fillLength = this.length - text.length();
            if (fillLength <= 0) {
                this.prefix = null;
                this.postfix = null;
                fillLength = this.length;
            }
            text = new StringBuilder();
            for (int i = 0; i < fillLength; i++)
                text.append(FILL_CHAR);
            this.fillLength = fillLength;
            this.fillText = text.toString();
        }
        String result = this.fillText + index;
        return result.substring(result.length() - this.fillLength);
    }

    public int getLength() {
        return length;
    }

    public String getPostfix() {
        return postfix;
    }

    public void setPostfix(String postfix) {
        this.postfix = postfix;
        this.fillText = null;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        this.fillText = null;
    }
}
