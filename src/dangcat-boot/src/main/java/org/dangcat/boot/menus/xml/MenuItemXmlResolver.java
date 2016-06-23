package org.dangcat.boot.menus.xml;

import java.util.Map;

import org.dangcat.boot.menus.MenuItem;
import org.dangcat.commons.serialize.xml.ParamsXmlResolver;
import org.dangcat.commons.serialize.xml.XmlResolver;

/**
 * 模块对象解析器。
 * @author dangcat
 * 
 */
public class MenuItemXmlResolver extends XmlResolver
{
    /** 模块。 */
    private MenuItem menuItem = null;

    /**
     * 构建解析器。
     */
    public MenuItemXmlResolver()
    {
        super(MenuItem.class.getSimpleName());
        this.addChildXmlResolver(new ParamsXmlResolver());
    }

    /**
     * 产生子元素对象。
     * @param elementName 子元素名称。
     * @param child 子元素对象。
     */
    @SuppressWarnings("unchecked")
    protected void afterChildCreate(String elementName, Object child)
    {
        if (child instanceof Map)
            this.menuItem.setParams((Map<String, Object>) child);
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement()
    {
        this.menuItem = new MenuItem();
        this.setResolveObject(this.menuItem);
    }
}
