package org.dangcat.boot.menus.xml;

import org.dangcat.boot.menus.Menu;
import org.dangcat.boot.menus.Menus;
import org.dangcat.commons.serialize.xml.XmlResolver;

/**
 * 系统菜单对象解析器。
 *
 * @author dangcat
 */
public class MenusXmlResolver extends XmlResolver {
    /**
     * 属性列表。
     */
    private Menus menus;

    /**
     * 构建解析器。
     */
    public MenusXmlResolver() {
        super(Menus.class.getSimpleName());
        this.addChildXmlResolver(new MenuXmlResolver());
    }

    /**
     * 产生子元素对象。
     *
     * @param elementName 子元素名称。
     * @param child       子元素对象。
     */
    protected void afterChildCreate(String elementName, Object child) {
        if (child instanceof Menu)
            this.menus.getData().add((Menu) child);
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement() {
        this.menus = new Menus();
        this.setResolveObject(this.menus);
    }
}
