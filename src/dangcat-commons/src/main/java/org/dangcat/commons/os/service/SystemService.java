package org.dangcat.commons.os.service;

import java.text.MessageFormat;

import org.dangcat.commons.utils.CommandExecutor;

abstract class SystemService
{
    protected abstract boolean exists(String name);

    protected abstract String getRemoveCMD();

    protected abstract boolean isRunning(String name);

    protected boolean remove(String name)
    {
        String command = MessageFormat.format(this.getRemoveCMD(), name);
        CommandExecutor.execute(command);
        return !exists(name);
    }
}
