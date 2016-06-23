package org.dangcat.boot.service;

import org.dangcat.commons.timer.AlarmClock;
import org.dangcat.framework.service.ServiceControl;

/**
 * 定时服务接口。
 * @author dangcat
 * 
 */
public interface TimerService extends ServiceControl
{
    /**
     * 取消定时器，
     * @param alarmClock 定时器对象。
     */
    void cancelTimer(AlarmClock alarmClock);

    /**
     * 注册定时器，
     * @param alarmClock 闹钟定义。
     */
    void createTimer(AlarmClock alarmClock);

    /**
     * 定时执行接口。
     */
    void intervalExec();
}
