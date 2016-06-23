package org.dangcat.commons.os.service;

import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.CommandExecutor;
import org.dangcat.commons.utils.ValueUtils;

class LinuxSystemService extends SystemService
{
    /** 删除服务。 */
    private static final String CMD_DELETE_CMD = "chkconfig --del {0}";
    /** 判断服务是否存在。 */
    private static final String CMD_EXISTS_CMD = "chkconfig --list {0}";
    /** 判断服务是否在运行。 */
    private static final String CMD_RUNNING = "service {0} status";
    private static final Logger logger = Logger.getLogger(LinuxSystemService.class);

    @Override
    protected boolean exists(String name)
    {
        if (ValueUtils.isEmpty(name))
            return false;

        String command = MessageFormat.format(CMD_EXISTS_CMD, name);
        String info = CommandExecutor.execute(command);
        if (logger.isDebugEnabled())
            logger.debug(command + ":" + info);
        return !ValueUtils.isEmpty(info) && info.contains("0:");
    }

    @Override
    protected String getRemoveCMD()
    {
        return CMD_DELETE_CMD;
    }

    @Override
    protected boolean isRunning(String name)
    {
        if (ValueUtils.isEmpty(name))
            return false;

        String command = MessageFormat.format(CMD_RUNNING, name);
        String info = CommandExecutor.execute(command);
        return !ValueUtils.isEmpty(info) && (info.contains("running") || info.contains("pid"));
    }
}
