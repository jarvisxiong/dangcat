package org.dangcat.commons.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class CommandExecutor
{
    private static final Logger LOGGER = Logger.getLogger(CommandExecutor.class);

    public static String execute(String command)
    {
        CommandExecutor commandExecutor = new CommandExecutor();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        commandExecutor.setOutputStream(byteArrayOutputStream);
        commandExecutor.exec(command);
        return byteArrayOutputStream.toString();
    }

    private File dirFile = null;
    private Map<String, String> envParams = null;
    private File exeFile = null;
    private Logger logger = null;
    private OutputStream outputStream = null;

    /**
     * 执行外部命令。
     * @param params 命令字串。
     * @return 执行结果。
     */
    public boolean exec(String... params)
    {
        boolean result = false;
        try
        {
            String[] commands = this.getCommands(params);
            String[] envArray = this.getEnvArray();
            File workDirectory = this.getWorkDirectory();
            this.getLogger().info(ValueUtils.join(commands, " "));
            Process process = Runtime.getRuntime().exec(commands, envArray, workDirectory);

            ExecuteConsole standConsole = new ExecuteConsole(process.getInputStream());
            standConsole.setOutputStream(this.getOutputStream());
            standConsole.setLogger(this.logger);
            standConsole.start();

            ExecuteConsole errorConsole = new ExecuteConsole(process.getErrorStream());
            errorConsole.setOutputStream(this.getOutputStream());
            errorConsole.setLogger(this.logger);
            errorConsole.setError(true);
            errorConsole.start();

            if (process.waitFor() == 0)
            {
                if (standConsole != null)
                    standConsole.join();
                if (errorConsole != null)
                    errorConsole.join();
                process.destroy();
            }
            result = true;
        }
        catch (Exception e)
        {
            this.getLogger().error(this, e);
        }
        return result;
    }

    private String[] getCommands(String... params)
    {
        Collection<String> commands = new ArrayList<String>();
        OSType osType = OSType.getOSType();
        if (OSType.Linux.equals(osType))
        {
            commands.add("/bin/sh");
            commands.add("-c");
        }
        else if (OSType.Windows.equals(osType))
        {
            commands.add("cmd");
            commands.add("/C");
        }
        StringBuilder command = new StringBuilder();
        String execFileCommand = this.getExecFileCommand();
        if (!ValueUtils.isEmpty(execFileCommand))
        {
            command.append(" \"");
            command.append(execFileCommand);
            command.append(" \"");
        }
        if (params != null)
        {
            for (String param : params)
            {
                command.append(" ");
                command.append(param);
            }
        }
        commands.add(command.toString());
        return commands.toArray(new String[0]);
    }

    public File getDirFile()
    {
        return this.dirFile;
    }

    private String[] getEnvArray()
    {
        Collection<String> paramCollection = null;
        if (this.envParams != null)
        {
            paramCollection = new ArrayList<String>();
            for (Entry<String, String> entry : this.envParams.entrySet())
                paramCollection.add(entry.getKey() + "=" + entry.getValue());
        }
        if (paramCollection == null || paramCollection.size() == 0)
            return null;
        return paramCollection.toArray(new String[0]);
    }

    private String getExecFileCommand()
    {
        String execFileCommand = null;
        if (this.exeFile != null)
        {
            if (this.exeFile.exists())
                execFileCommand = this.exeFile.getAbsolutePath();
            else
                execFileCommand = this.exeFile.getName();
        }
        return execFileCommand;
    }

    public File getExeFile()
    {
        return this.exeFile;
    }

    public Logger getLogger()
    {
        return this.logger == null ? LOGGER : this.logger;
    }

    public OutputStream getOutputStream()
    {
        if (this.outputStream == null)
            this.outputStream = new ByteArrayOutputStream();
        return this.outputStream;
    }

    private File getWorkDirectory()
    {
        if (this.dirFile == null || !this.dirFile.exists())
            return null;
        return this.dirFile;
    }

    public void putEnv(String name, String value)
    {
        if (name != null && value != null)
        {
            if (this.envParams == null)
                this.envParams = new HashMap<String, String>();
            this.envParams.put(name, value);
        }
    }

    public void setDirFile(File dirFile)
    {
        this.dirFile = dirFile;
    }

    public void setExeFile(File exeFile)
    {
        this.exeFile = exeFile;
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
