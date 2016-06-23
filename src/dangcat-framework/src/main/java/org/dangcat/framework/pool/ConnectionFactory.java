package org.dangcat.framework.pool;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.conf.Configure;
import org.dangcat.framework.conf.ConfigureCollection;
import org.dangcat.framework.conf.ConfigureManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ConnectionFactory<T extends ConnectionPool<?>, K> {
    public static final String DEFAULT = "default";
    protected static final Logger logger = Logger.getLogger(ConnectionFactory.class);
    private Map<String, T> connectionPools = new HashMap<String, T>();
    private String defaultName = null;

    public void close() {
        for (T connectionPool : this.connectionPools.values())
            this.close(connectionPool);
    }

    protected abstract void close(T connectionPool);

    public boolean containsKey(String key) {
        return this.connectionPools.containsKey(key);
    }

    protected abstract T createConnectionPool(String name, Map<String, String> params) throws SessionException;

    protected abstract K createSession(T connectionPool);

    public synchronized T get(String name) {
        if (name != null)
            name = name.toLowerCase();
        if (name == null || !this.containsKey(name))
            name = DEFAULT;
        if (DEFAULT.equalsIgnoreCase(name)) {
            if (!this.connectionPools.containsKey(DEFAULT) && this.connectionPools.size() > 0)
                this.connectionPools.put(DEFAULT, this.connectionPools.values().iterator().next());
        }
        return this.connectionPools.get(name);
    }

    public String getDefaultName() {
        return this.defaultName;
    }

    public void setDefaultName(String name) {
        if (this.connectionPools.containsKey(name)) {
            this.connectionPools.put(DEFAULT, this.connectionPools.get(name));
            this.defaultName = name;
        }
    }

    public String[] getResourceNames() {
        List<String> resourceNameList = new ArrayList<String>();
        for (String resourceName : this.connectionPools.keySet()) {
            if (DEFAULT.equalsIgnoreCase(resourceName))
                continue;
            resourceNameList.add(resourceName);
        }
        return resourceNameList.toArray(new String[0]);
    }

    public abstract String getResourceType();

    /**
     * 初始化服务。
     *
     * @throws SessionException
     */
    public void initialize() throws SessionException {
        ConfigureCollection configureCollection = ConfigureManager.getInstance().getConfigureCollection(this.getResourceType());
        if (configureCollection != null) {
            Map<String, Configure> configureMap = configureCollection.getConfigureMap();
            for (String key : configureMap.keySet()) {
                if (this.connectionPools.containsKey(key)) {
                    this.close(this.connectionPools.get(key));
                    this.connectionPools.remove(key);
                }
                T connectionPool = this.createConnectionPool(key, configureMap.get(key).getParams());
                if (connectionPool != null)
                    this.put(key, connectionPool);
            }
        }
    }

    /**
     * 开启一个会话对象。
     *
     * @throws SessionException 会话异常。
     */
    public K openSession() {
        return this.openSession(DEFAULT);
    }

    /**
     * 开启一个会话对象。
     *
     * @throws SessionException 会话异常。
     */
    public K openSession(String name) {
        return this.createSession(this.get(name));
    }

    protected synchronized void put(String name, T connectionPool) {
        this.connectionPools.put(name, connectionPool);
        if (ValueUtils.isEmpty(this.defaultName))
            this.setDefaultName(name);
        else if (connectionPool.isDefault())
            this.setDefaultName(name);
    }
}
