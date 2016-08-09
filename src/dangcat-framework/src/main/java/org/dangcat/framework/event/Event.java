package org.dangcat.framework.event;

import org.dangcat.commons.utils.Environment;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息对象。
 *
 * @author dangcat
 */
public class Event implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 是否取消事件路由。
     */
    private boolean cancel = false;
    /**
     * 事件已经处理。
     */
    private boolean handled = false;
    /**
     * 消息码。
     */
    private String id;
    /**
     * 参数表。
     */
    private Map<String, Object> params = new HashMap<String, Object>();
    /**
     * 事件时间
     */
    private Date timeStamp;

    public Event() {

    }

    public Event(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder();
        info.append("ID: " + this.getId());
        info.append(", ");
        info.append("Cancel: " + this.isCancel());
        info.append(", ");
        info.append("Handled: " + this.isHandled());
        for (String paramName : this.getParams().keySet()) {
            info.append(Environment.LINE_SEPARATOR);
            info.append("Param " + paramName + " = " + this.getParams().get(paramName));
        }
        return info.toString();
    }
}
