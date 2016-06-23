package org.dangcat.boot.menus.xml;

import org.dangcat.boot.menus.MenuData;
import org.dangcat.boot.menus.Separator;
import org.dangcat.boot.menus.Submenu;
import org.dangcat.commons.serialize.xml.XmlResolver;
import org.dom4j.Element;

/**
 * 模块对象解析器。
 * @author dangcat
 * 
 */
public class SubmenuXmlResolver extends XmlResolver
{
    private SubmenuXmlResolver childSubmenuXmlResolver = null;
    private Submenu submenu = null;

    /**
     * 构建解析器。
     */
    public SubmenuXmlResolver()
    {
        super(Submenu.class.getSimpleName());
        this.addChildXmlResolver(new MenuItemXmlResolver());
    }

    /**
     * 产生子元素对象。
     * @param elementName 子元素名称。
     * @param child 子元素对象。
     */
    protected void afterChildCreate(String elementName, Object child)
    {
        if (child instanceof MenuData)
            this.submenu.addMenuData((MenuData) child);
    }

    /**
     * 开始解析子元素标签。
     * @param element 子元素名称。
     */
    protected void resolveChildElement(Element element)
    {
        if (Separator.class.getSimpleName().equalsIgnoreCase(element.getName()))
            this.submenu.addMenuData(new Separator());
        else if (Submenu.class.getSimpleName().equalsIgnoreCase(element.getName()))
            this.resolveChildSubMenu(element);
        else
            super.resolveChildElement(element);
    }

    private void resolveChildSubMenu(Element element)
    {
        if (this.childSubmenuXmlResolver == null)
            this.childSubmenuXmlResolver = new SubmenuXmlResolver();
        this.childSubmenuXmlResolver.resolve(element);
        MenuData menuData = (MenuData) this.childSubmenuXmlResolver.getResolveObject();
        if (menuData != null)
            this.submenu.addMenuData(menuData);
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement()
    {
        this.submenu = new Submenu();
        this.setResolveObject(this.submenu);
    }
}
