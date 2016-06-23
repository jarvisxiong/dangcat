package org.dangcat.boot.menus;

import java.util.Collection;
import java.util.LinkedList;

public class MenuDataCollection extends MenuBase {
    private Collection<MenuData> menuDataCollection = null;

    public void addMenuData(MenuData menuData) {
        if (this.menuDataCollection == null)
            this.menuDataCollection = new LinkedList<MenuData>();
        if (menuData != null && !this.menuDataCollection.contains(menuData))
            this.menuDataCollection.add(menuData);
    }

    protected Collection<MenuData> getDataCollection() {
        return this.menuDataCollection;
    }
}
