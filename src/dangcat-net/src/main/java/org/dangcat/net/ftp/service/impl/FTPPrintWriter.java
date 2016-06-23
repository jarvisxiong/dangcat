package org.dangcat.net.ftp.service.impl;

import java.io.PrintWriter;

import org.apache.log4j.Logger;

public class FTPPrintWriter extends PrintWriter
{
    private static final String CRYPTO_TEXT = " ******\r\n";
    private static final String PASS = "PASS";
    private static final String USER = "USER";
    private StringBuilder debug = null;
    private Logger logger = null;

    public FTPPrintWriter(Logger logger)
    {
        super(System.out);
        this.logger = logger;
    }

    @Override
    public void flush()
    {
        if (this.logger.isDebugEnabled())
        {
            StringBuilder debug = this.debug;
            this.debug = null;
            if (debug != null)
            {
                this.removeEnter(debug);
                if (debug.length() > 0)
                    this.logger.debug(debug);
            }
            super.flush();
        }
    }

    @Override
    public void print(String value)
    {
        if (this.logger.isDebugEnabled())
        {
            value = this.translateLog(value);
            if (this.debug == null)
                this.debug = new StringBuilder();
            this.debug.append(value);
            super.print(value);
        }
    }

    @Override
    public void println(String value)
    {
        if (this.logger.isDebugEnabled())
        {
            value = this.translateLog(value);
            StringBuilder debug = this.debug;
            this.debug = null;
            if (debug != null)
                debug.append(value);
            else
                debug = new StringBuilder(value);
            this.removeEnter(debug);
            if (debug.length() > 0)
                this.logger.debug(debug);
            super.print(value);
        }
    }

    private void removeEnter(StringBuilder debug)
    {
        while (debug.length() > 0)
        {
            char charValue = debug.charAt(debug.length() - 1);
            if (charValue == '\r' || charValue == '\n')
                debug.deleteCharAt(debug.length() - 1);
            else
                break;
        }
    }

    private String translateLog(String value)
    {
        if (value.startsWith(USER))
            return USER + CRYPTO_TEXT;
        if (value.startsWith(PASS))
            return PASS + CRYPTO_TEXT;
        return value;
    }
}
