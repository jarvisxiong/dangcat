package org.dangcat.framework.service;

/**
 * 服务状态。
 *
 * @author dangcat
 */
public enum ServiceStatus {
    /**
     * 暂停。
     */
    Pause(4),
    /**
     * 已经启动。
     */
    Started(2),
    /**
     * 正在启动。
     */
    Starting(1),
    /**
     * 已经停止。
     */
    Stopped(0),
    /**
     * 正在停止。
     */
    Stopping(3);

    private final int value;

    ServiceStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
