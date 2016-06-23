package org.dangcat.install.database.task;

public abstract class DatabaseAccess {
    protected static final String NAME = "name";
    protected static final String PASSWORD = "password";
    protected static final String PORT = "port";
    protected static final String SERVER = "server";
    protected static final String USER = "user";
    private boolean enabled = true;

    protected String getDatabaseKey(String name, String key) {
        return "database." + name + "." + key;
    }

    protected String getDatabaseName(String name) {
        return this.getDatabaseKey(name, NAME);
    }

    protected String getPasswordKey(String name) {
        return this.getDatabaseKey(name, PASSWORD);
    }

    protected String getPortKey(String name) {
        return this.getDatabaseKey(name, PORT);
    }

    protected String getServerKey(String name) {
        return this.getDatabaseKey(name, SERVER);
    }

    protected String getUserKey(String name) {
        return this.getDatabaseKey(name, USER);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
