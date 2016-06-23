package org.dangcat.boot.permission;

import org.apache.log4j.Logger;
import org.dangcat.boot.service.impl.ServiceCalculator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Module extends JndiName {
    protected static Logger logger = Logger.getLogger(Module.class);
    private Map<String, JndiName> jndiNameMap = new HashMap<String, JndiName>();

    public void addJndiName(JndiName jndiName) {
        if (this.jndiNameMap.containsKey(jndiName.getName()))
            logger.error("The permission congfig is error: " + jndiName);
        else
            this.jndiNameMap.put(jndiName.getName(), jndiName);
    }

    public JndiName findJndiName(String jndiName) {
        String name = null;
        int index = jndiName.indexOf("/");
        if (index != -1)
            name = jndiName.substring(index + 1);
        return this.jndiNameMap.get(name);
    }

    public Collection<JndiName> getJndiNameCollection() {
        return this.jndiNameMap.values();
    }

    public Integer getPermission() {
        return ServiceCalculator.create(this.getId());
    }
}
