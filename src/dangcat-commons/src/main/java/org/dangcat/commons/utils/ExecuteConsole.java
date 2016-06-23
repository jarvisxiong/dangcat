package org.dangcat.commons.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

public class ExecuteConsole extends Thread
{
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final Logger LOGGER = Logger.getLogger(CommandExecutor.class);
    private boolean error = false;
    private InputStream inputStream;
    private Logger logger = null;
    private OutputStream outputStream;

    public ExecuteConsole(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    public Logger getLogger()
    {
        return this.logger == null ? LOGGER : this.logger;
    }

    public OutputStream getOutputStream()
    {
        return outputStream;
    }

    public boolean isError()
    {
        return error;
    }

    private void log(String message)
    {
        if (this.logger != null)
        {
            if (this.isError())
                this.logger.error(message);
            else
                this.logger.info(message);
        }
    }

    public void run()
    {
        BufferedReader bufferedReader = null;
        try
        {
            PrintWriter printWriter = null;
            if (this.outputStream != null)
                printWriter = new PrintWriter(this.outputStream);

            bufferedReader = new BufferedReader(new InputStreamReader(this.inputStream));
            String message = null;
            while ((message = bufferedReader.readLine()) != null)
            {
                if (printWriter != null)
                    printWriter.println(message);

                this.log(message);
            }
            if (printWriter != null)
                printWriter.flush();
        }
        catch (IOException e)
        {
            this.getLogger().error(this, e);
        }
        finally
        {
            try
            {
                if (bufferedReader != null)
                    bufferedReader.close();
            }
            catch (IOException e)
            {
            }
        }
    }

    public void setError(boolean error)
    {
        this.error = error;
    }

    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    public void setOutputStream(OutputStream outputStream)
    {
        this.outputStream = outputStream;
    }
}
