package org.dangcat.boot.menus;

import java.util.Collection;

public class Submenu extends MenuDataCollection {
    public Collection<MenuData> getSubmenu() {
        return this.getDataCollection();
    }
}
