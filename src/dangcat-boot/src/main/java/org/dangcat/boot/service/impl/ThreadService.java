package org.dangcat.boot.service.impl;

import org.dangcat.commons.timer.AlarmClock;
import org.dangcat.commons.utils.Environment;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.ServiceStatus;
import org.dangcat.framework.service.impl.ServiceControlBase;

/**
 * 队列多线程服务基础类。
 *
 * @author dangcat
 */
public class ThreadService extends ServiceControlBase {
    private AlarmClock alarmClock = null;
    private int priority = Thread.NORM_PRIORITY;
    private Runnable runnable = null;
    private Object syncLock = new Object();
    private Thread thread;

    /**
     * 构造服务。
     *
     * @param parent 所属服务。
     */
    public ThreadService(ServiceProvider parent) {
        super(parent);
    }

    /**
     * 构造服务。
     *
     * @param parent 所属服务。
     * @param name   服务名称。
     */
    public ThreadService(ServiceProvider parent, String name) {
        super(parent, name);
    }

    /**
     * 构造服务。
     *
     * @param parent   所属服务。
     * @param name     服务名称。
     * @param runnable 线程执行接口。
     */
    public ThreadService(ServiceProvider parent, String name, Runnable runnable) {
        super(parent, name);
        this.runnable = runnable;
    }

    protected boolean executeAtStarting() {
        return false;
    }

    public AlarmClock getAlarmClock() {
        return this.alarmClock;
    }

    public void setAlarmClock(AlarmClock alarmClock) {
        this.alarmClock = alarmClock;
        if (alarmClock != null) {
            alarmClock.setTarget(this);
            if (this.getServiceStatus().equals(ServiceStatus.Started))
                TimerServiceImpl.getInstance().createTimer(alarmClock);
        }
    }

    /**
     * 线程优先级。
     */
    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Runnable getRunnable() {
        return this.runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    protected void innerExecute() {
        Runnable runnable = this.getRunnable();
        if (runnable != null)
            runnable.run();
    }

    /**
     * 唤醒执行。
     */
    public final void resume() {
        if (this.isEnabled()) {
            if (!Environment.isTestEnabled())
                this.wakeup();
            else
                this.innerExecute();
        }
    }

    /**
     * 启动守护线程。
     */
    @Override
    public void start() {
        if (this.isEnabled() && this.getServiceStatus().equals(ServiceStatus.Stopped)) {
            synchronized (this) {
                this.setServiceStatus(ServiceStatus.Starting);

                // 注册定时器。
                if (this.alarmClock != null)
                    TimerServiceImpl.getInstance().createTimer(this.alarmClock);

                this.thread = new Thread(new Runnable() {
                    public void run() {
                        boolean firstExecute = true;
                        while (ThreadService.this.isRunning() || ThreadService.this.getServiceStatus().equals(ServiceStatus.Starting)) {
                            try {
                                if (!ThreadService.this.executeAtStarting() || !firstExecute)
                                    ThreadService.this.waiting();
                                if (ThreadService.this.getServiceStatus().equals(ServiceStatus.Starting) || ThreadService.this.getServiceStatus().equals(ServiceStatus.Started))
                                    ThreadService.this.innerExecute();
                            } catch (Exception e) {
                                ThreadService.this.logger.error(this, e);
                            } finally {
                                firstExecute = false;
                            }
                        }
                    }
                }, this.getServiceName());
                this.thread.setPriority(this.getPriority());
                this.thread.start();
                super.start();
            }
        }
    }

    /**
     * 停止定时器
     */
    @Override
    public void stop() {
        if (this.getServiceStatus().equals(ServiceStatus.Started)) {
            synchronized (this) {
                this.setServiceStatus(ServiceStatus.Stopping);

                // 注册定时器。
                if (this.alarmClock != null)
                    TimerServiceImpl.getInstance().cancelTimer(this.alarmClock);

                if (this.thread != null) {
                    try {
                        this.stopping();
                        this.thread.join();
                        this.thread = null;
                    } catch (InterruptedException e) {
                        this.logger.info(this, e);
                    }
                    super.stop();
                }
            }
        }
    }

    /**
     * 停止线程运行。
     */
    protected void stopping() {
        this.wakeup();
    }

    /**
     * 进入等待。。
     *
     * @throws InterruptedException
     */
    private void waiting() throws InterruptedException {
        synchronized (this.syncLock) {
            this.syncLock.wait();
        }
    }

    private void wakeup() {
        synchronized (this.syncLock) {
            this.syncLock.notifyAll();
        }
    }
}
