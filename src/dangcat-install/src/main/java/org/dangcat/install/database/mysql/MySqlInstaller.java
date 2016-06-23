package org.dangcat.install.database.mysql;

import org.dangcat.commons.io.FileMarker;
import org.dangcat.commons.utils.OSType;
import org.dangcat.install.database.DatabaseInstaller;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class MySqlInstaller extends DatabaseInstaller
{
    private static final String BASEDIR = "basedir";
    private static final String DATADIR = "datadir";
    private static final String DEFAULT_CHARACTERSET = System.getProperty("file.encoding");
    private static final int DEFAULT_PORT = 3306;
    private static final String DEFAULT_USER = "root";

    public MySqlInstaller()
    {
        this.setDefaultPort(DEFAULT_PORT);
        this.setDefaultUser(DEFAULT_USER);
        this.setCharacterSet(DEFAULT_CHARACTERSET);
    }

    @Override
    public void config() throws Exception
    {
        this.logService("service.config");

        String myTemplateName = this.getMyTemplateFile().getName();
        File dataDir = new File(this.getDataDir());
        this.processFileMarker(dataDir, myTemplateName, this.getMyFile());

        String serviceTemplateName = this.getServiceTemplateFile().getName();
        File binDir = new File(this.getBinDir());
        this.processFileMarker(binDir, serviceTemplateName, this.getServiceFile());
    }

    @Override
    public void createDatabase() throws Exception
    {
        this.sortScripts();

        this.logDatabase("database.create", this.getDatabaseName());
        FileMarker fileMarker = new FileMarker(MySqlInstaller.class);
        fileMarker.setEncoding(this.getCharacterSet());
        fileMarker.getDataMap().putAll(this.getParams());
        File scriptFile = File.createTempFile("mysql", ".sql");
        try
        {
            fileMarker.process(MySqlInstaller.class.getSimpleName() + ".sql", scriptFile);

            StringBuilder params = new StringBuilder();
            params.append("-u root -S \"");
            params.append(this.getSocketFile());
            params.append("\" -P ");
            params.append(this.getPort().toString());
            params.append(" < \"");
            params.append(scriptFile.getAbsolutePath());
            params.append("\"");

            this.execute(this.getMySqlFile(), params.toString());
            this.logDatabase("database.success", this.getDatabaseName());
            scriptFile.delete();
        }
        catch (Exception e)
        {
            this.logDatabase("database.error", this.getDatabaseName(), e.getMessage());
            this.logger.error(this.getDatabaseName(), e);
        }
    }

    public String getBaseDir()
    {
        return (String) this.getParams().get(BASEDIR);
    }

    public void setBaseDir(String baseDir) {
        this.getParams().put(BASEDIR, baseDir);
    }

    private String getBinDir()
    {
        return this.getBaseDir() + File.separator + "bin";
    }

    public String getDataDir()
    {
        return (String) this.getParams().get(DATADIR);
    }

    public void setDataDir(String dataDir) {
        this.getParams().put(DATADIR, dataDir);
    }

    private File getMyFile()
    {
        return new File(this.getDataDir() + File.separator + this.getMyFileName());
    }

    private String getMyFileName()
    {
        String myFileName = null;
        OSType osType = OSType.getOSType();
        if (OSType.Linux.equals(osType))
            myFileName = "my.cnf";
        else if (OSType.Windows.equals(osType))
            myFileName = "my.ini";
        return myFileName;
    }

    private File getMySqlFile()
    {
        String mySqlFile = this.getBinDir() + File.separator + "mysql";
        if (OSType.Windows.equals(OSType.getOSType()))
            mySqlFile += ".exe";
        return new File(mySqlFile);
    }

    private File getMyTemplateFile()
    {
        return new File(this.getMyFile().getAbsolutePath() + ".template");
    }

    @Override
    protected File getServiceFile()
    {
        return new File(this.getBinDir() + File.separator + this.getServiceFileName());
    }

    private String getServiceFileName()
    {
        String serviceFileName = "service";
        OSType osType = OSType.getOSType();
        if (OSType.Linux.equals(osType))
            serviceFileName += ".sh";
        else if (OSType.Windows.equals(osType))
            serviceFileName += ".bat";
        return serviceFileName;
    }

    private File getServiceTemplateFile()
    {
        return new File(this.getServiceFile().getAbsolutePath() + ".template");
    }

    private String getSocketFile()
    {
        String socketFile = this.getDataDir() + File.separator + "mysql.sock";
        return socketFile.replace("\\", "/");
    }

    private void processFileMarker(File templatePath, String templateName, File outputFile) throws Exception
    {
        FileMarker fileMarker = new FileMarker(templatePath);
        fileMarker.getDataMap().putAll(this.getParams());
        fileMarker.getDataMap().put(DATADIR, this.getDataDir().replace("\\", "/"));
        fileMarker.getDataMap().put(BASEDIR, this.getBaseDir().replace("\\", "/"));
        fileMarker.process(templateName, outputFile);
    }

    protected void sortScripts()
    {
        List<String> scripts = this.getScripts();
        List<String> processScripts = new LinkedList<String>();
        for (String script : scripts)
            processScripts.add(script.replace("\\", "/"));
        scripts.clear();
        scripts.addAll(processScripts);
        Collections.sort(scripts, new Comparator<String>()
        {
            @Override
            public int compare(String src, String dest)
            {
                return this.getSortValue(src) - this.getSortValue(dest);
            }

            private int getSortValue(String value)
            {
                value = value.toLowerCase();
                int sortValue = value.hashCode();
                if (value.endsWith("create.sql"))
                    sortValue = 0;
                else if (value.endsWith("init.sql"))
                    sortValue = 5;
                return sortValue;
            }
        });
    }
}
