package org.dangcat.boot.menus.xml;

import org.dangcat.boot.menus.Menu;
import org.dangcat.boot.menus.MenuData;
import org.dangcat.boot.menus.Separator;
import org.dangcat.commons.serialize.xml.XmlResolver;
import org.dom4j.Element;

/**
 * 模块对象解析器。
 * @author dangcat
 * 
 */
public class MenuXmlResolver extends XmlResolver
{
    /** 模块。 */
    private Menu menu = null;

    /**
     * 构建解析器。
     */
    public MenuXmlResolver()
    {
        super(Menu.class.getSimpleName());
        this.addChildXmlResolver(new MenuItemXmlResolver());
        this.addChildXmlResolver(new SubmenuXmlResolver());
    }

    /**
     * 产生子元素对象。
     * @param elementName 子元素名称。
     * @param child 子元素对象。
     */
    protected void afterChildCreate(String elementName, Object child)
    {
        if (child instanceof MenuData)
            this.menu.addMenuData((MenuData) child);
    }

    /**
     * 开始解析子元素标签。
     * @param element 子元素名称。
     */
    protected void resolveChildElement(Element element)
    {
        if (Separator.class.getSimpleName().equalsIgnoreCase(element.getName()))
            this.menu.addMenuData(new Separator());
        else
            super.resolveChildElement(element);
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement()
    {
        this.menu = new Menu();
        this.setResolveObject(this.menu);
    }
}
