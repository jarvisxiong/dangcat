package org.dangcat.boot;

import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.ServiceBase;
import org.dangcat.framework.service.ServiceStatus;

import java.io.IOException;

/**
 * 服务启动类。
 *
 * @author dangcat
 */
public class Launcher {
    private static final String COMMAND_RESTART = "restart";
    private static final String COMMAND_START = "start";
    private static final String COMMAND_STOP = "stop";
    private static final String DANGCAT_HOME = "DANGCAT_HOME";
    private static ApplicationContext applicationContext = null;

    /**
     * 初始化服务。
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static void initialize(String[] args) {
        if (applicationContext != null)
            return;

        applicationContext = ApplicationContext.getInstance();
        // 设置HOME路径。
        if (ValueUtils.isEmpty(System.getProperty(DANGCAT_HOME)))
            System.setProperty(DANGCAT_HOME, FileUtils.getCanonicalPath("."));

        applicationContext.setName(args[1]);
    }

    public static void main(String[] args) {
        initialize(args);

        System.out.print("Receive system message : ");
        for (String arg : args)
            System.out.print(" " + arg);
        System.out.println();

        if (COMMAND_START.equalsIgnoreCase(args[0]))
            start();
        else if (COMMAND_STOP.equalsIgnoreCase(args[0]))
            stop();
        else if (COMMAND_RESTART.equalsIgnoreCase(args[0])) {
            start();
            stop();
        } else
            System.err.println("The system message is unknown.");
    }

    /**
     * 启动服务。
     */
    public static void start() {
        if (applicationContext.getServiceStatus().equals(ServiceStatus.Stopped)) {
            applicationContext.initialize();
            applicationContext.start();
        }
    }

    /**
     * 启动服务。
     */
    public static ServiceBase start(Class<?> mainClassType, String serviceName, boolean isTestMode) {
        if (isTestMode)
            Environment.setModuleEnabled("test", true);
        Environment.setHomePath(mainClassType);
        main(new String[]{COMMAND_START, serviceName});
        return applicationContext.getMainService();
    }

    /**
     * 停止服务。
     */
    public static void stop() {
        if (applicationContext.getServiceStatus().equals(ServiceStatus.Started))
            applicationContext.stop();
        applicationContext = null;
        System.exit(0);
    }
}
