package org.dangcat.commons.os.service;

import org.dangcat.commons.utils.CommandExecutor;
import org.dangcat.commons.utils.ValueUtils;

import java.text.MessageFormat;

class WindowsSystemService extends SystemService
{
    /** 删除服务。 */
    private static final String CMD_DELETE_CMD = "sc delete {0}";
    /** 判断服务是否存在。 */
    private static final String CMD_EXISTS_CMD = "sc query {0} | find \"STATE\"";

    @Override
    protected boolean exists(String name)
    {
        if (ValueUtils.isEmpty(name))
            return false;

        String command = MessageFormat.format(CMD_EXISTS_CMD, name);
        String info = CommandExecutor.execute(command);
        return !ValueUtils.isEmpty(info) && info.contains("STATE");
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

        String command = MessageFormat.format(CMD_EXISTS_CMD, name);
        String info = CommandExecutor.execute(command);
        return !ValueUtils.isEmpty(info) && info.contains("STATE") && info.contains("RUNNING");
    }
}
