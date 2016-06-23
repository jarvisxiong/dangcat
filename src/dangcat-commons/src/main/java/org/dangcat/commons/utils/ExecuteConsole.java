package org.dangcat.commons.utils;

import org.apache.log4j.Logger;

import java.io.*;

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

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public OutputStream getOutputStream()
    {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public boolean isError()
    {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
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
}
