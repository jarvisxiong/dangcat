package org.dangcat.framework.service;

import org.apache.log4j.Logger;
import org.dangcat.framework.event.Event;
import org.dangcat.framework.event.EventHandler;

import java.util.*;

/**
 * 服务基类
 *
 * @author dangcat
 */
public abstract class ServiceBase implements ServiceProvider, EventHandler {
    static {
        ServiceHelper.addInjectProvider(new ServiceInjectProvider());
    }

    public final Logger logger = Logger.getLogger(this.getClass());
    private List<Object> childrenList = new LinkedList<Object>();
    private boolean isEnabled = true;
    private ServiceProvider parent;
    private Map<Class<?>, Object> serviceContainer = new LinkedHashMap<Class<?>, Object>();

    /**
     * 构造服务对象。
     *
     * @param parent 所属服务。
     */
    public ServiceBase(ServiceProvider parent) {
        this.parent = parent;
    }

    /**
     * 添加服务。
     */
    public void addService(Class<?> classType, Object service) {
        if (service != this) {
            this.serviceContainer.put(classType, service);
            if (!this.childrenList.contains(service))
                this.childrenList.add(service);
        }
    }

    /**
     * 读取子服务列表。
     *
     * @return 子服务集合。
     */
    public Collection<Object> getChildren() {
        return this.childrenList;
    }

    protected Logger getLogger() {
        return this.logger;
    }

    /**
     * 访问父服务。
     *
     * @return
     */
    public ServiceProvider getParent() {
        return this.parent;
    }

    /**
     * 获取指定类型的服务。
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> classType) {
        if (this.serviceContainer.containsKey(classType))
            return (T) this.serviceContainer.get(classType);
        if (this.parent != null)
            return this.parent.getService(classType);
        return null;
    }

    /**
     * 处理事件。
     */
    public Object handle(Event event) {
        Object result = null;
        if (this.isEnabled()) {
            for (Object serviceObject : this.childrenList) {
                if (event.isCancel() || event.isHandled())
                    break;

                if (serviceObject instanceof EventHandler) {
                    EventHandler eventHandler = (EventHandler) serviceObject;
                    result = eventHandler.handle(event);
                }
            }
        }
        return result;
    }

    /**
     * 初始化服务。
     */
    public void initialize() {
        // 由配置文件载入子服务。
        ServiceHelper.loadFromServiceXml(this);
        // 注入服务。
        this.inject();
    }

    protected void inject() {
        // 注入服务。
        ServiceHelper.inject(this, this);
        // 注入子服务中的
        for (Object childService : this.getChildren()) {
            if (childService instanceof ServiceBase) {
                ServiceBase serviceBase = (ServiceBase) childService;
                serviceBase.inject();
            } else
                ServiceHelper.inject(this, childService);
        }
    }

    /**
     * 服务是否启动。
     */
    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * 删除服务。
     */
    public void removeService(Object service) {
        Class<?> classType = null;
        for (Class<?> key : this.serviceContainer.keySet()) {
            if (this.serviceContainer.get(key) == service) {
                classType = key;
                break;
            }
        }
        if (classType != null)
            this.serviceContainer.remove(classType);
        if (this.childrenList.contains(service))
            this.childrenList.remove(service);
    }
}
