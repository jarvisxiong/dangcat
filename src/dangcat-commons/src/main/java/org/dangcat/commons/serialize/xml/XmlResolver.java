package org.dangcat.commons.serialize.xml;

import org.apache.log4j.Logger;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultAttribute;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Xml文件解析器。
 * @author dangcat
 * 
 */
public abstract class XmlResolver
{
    /** 跟踪日志。 */
    protected static final Logger logger = Logger.getLogger(XmlResolver.class);
    /** 元素解析影射表. */
    private Map<String, XmlResolver> childXmlResolverMap = new HashMap<String, XmlResolver>();
    /** Xml文档。 */
    private Document document = null;
    /** 解析器名称。 */
    private String name;
    /** 解析对象。 */
    private Object resolveObject = null;

    /**
     * 构建解析器。
     */
    public XmlResolver(String name)
    {
        this.name = name;
    }

    /**
     * 增加子元素解析器。
     * @param xmlResolver 解析器。
     */
    protected void addChildXmlResolver(XmlResolver xmlResolver)
    {
        if (xmlResolver != null)
        {
            String elementName = xmlResolver.getName();
            if (containsChildXmlResolver(elementName))
                childXmlResolverMap.remove(elementName);
            childXmlResolverMap.put(elementName, xmlResolver);
        }
    }

    /**
     * 产生子元素对象。
     * @param elementName 子元素名称。
     * @param child 子元素对象。
     */
    protected void afterChildCreate(String elementName, Object child)
    {
        ReflectUtils.setProperty(this.resolveObject, elementName, child);
    }

    /**
     * 解析子元素之前。
     * @param name 属性名称。
     * @param xmlResolver 解析器。
     */
    protected void beforeChildResolve(String elementName, XmlResolver xmlResolver)
    {
    }

    /**
     * 是否已经存在解析器。
     * @param elementName 元素名称。
     */
    protected boolean containsChildXmlResolver(String elementName)
    {
        return childXmlResolverMap.containsKey(elementName);
    }

    /**
     * 解析元素标签结束。
     * @return 解析对象。
     */
    protected Object endElement()
    {
        return this.getResolveObject();
    }

    public String getName()
    {
        return this.name;
    }

    public Object getResolveObject()
    {
        return this.resolveObject;
    }

    public void setResolveObject(Object resolveObject) {
        this.resolveObject = resolveObject;
    }

    /**
     * 反向解析对象。
     * @param xmlFile Xml文档。
     * @return 解析对象。
     * @throws XMLStreamException 解析异常。
     * @throws DocumentException
     */
    public void open(File xmlFile) throws DocumentException
    {
        if (xmlFile != null && xmlFile.exists())
        {
            SAXReader xmlReader = new SAXReader();
            xmlReader.setEntityResolver(new NullEntityResolver());
            this.document = xmlReader.read(xmlFile);
        }
    }

    /**
     * 反向解析对象。
     * @param inputStream 输入流。
     * @return 解析对象。
     * @throws XMLStreamException 解析异常。
     * @throws DocumentException
     */
    public void open(InputStream inputStream) throws DocumentException
    {
        if (inputStream != null)
        {
            SAXReader xmlReader = new SAXReader();
            xmlReader.setEntityResolver(new NullEntityResolver());
            this.document = xmlReader.read(inputStream);
        }
    }

    /**
     * 解析对象。
     * @return 解析对象。
     * @throws XMLStreamException 解析异常。
     */
    public Object resolve()
    {
        return this.resolve(this.document.getRootElement());
    }

    /**
     * 解析元素。
     * @param parent 父元素。
     * @return 解析对象。
     */
    public Object resolve(Element parent)
    {
        String elementName = parent.getName();

        if (!this.getName().equals(elementName))
            return null;

        // 开始解析元素标签。
        logger.debug("StartElement: " + elementName);
        this.startElement();

        // 解析属性。
        for (Object attributeObject : parent.attributes())
        {
            DefaultAttribute chileElement = (DefaultAttribute) attributeObject;
            logger.debug("Attribute " + chileElement.getName() + ": " + chileElement.getValue());
            this.resolveAttribute(chileElement.getName(), chileElement.getValue());
        }

        // 解析文字。
        String text = parent.getTextTrim();
        if (text != null && !text.equals(""))
        {
            logger.debug("ElementText : " + text);
            this.resolveElementText(parent.getText());
        }

        // 解析子元素。
        for (Object elementObject : parent.elements())
        {
            Element childElement = (Element) elementObject;
            String childElementName = childElement.getName();
            logger.debug("Resolve ChildElement: " + childElementName);
            this.resolveChildElement(childElement);
        }

        // 返回解析对象。
        logger.debug("EndElement: " + elementName);
        return this.endElement();
    }

    /**
     * 解析属性。
     * @param name 属性名称。
     * @param value 属性值。
     */
    protected void resolveAttribute(String name, String value)
    {
        ReflectUtils.setProperty(this.resolveObject, name, value);
    }

    /**
     * 开始解析子元素标签。
     * @param element 子元素名称。
     */
    protected void resolveChildElement(Element element)
    {
        String elementName = element.getName();
        if (this.childXmlResolverMap.containsKey(elementName))
        {
            XmlResolver xmlResolver = this.childXmlResolverMap.get(elementName);
            this.beforeChildResolve(elementName, xmlResolver);
            Object propertyValue = ReflectUtils.getProperty(this.resolveObject, elementName);
            if (propertyValue != null)
                xmlResolver.setResolveObject(propertyValue);
            Object result = xmlResolver.resolve(element);
            if (result != null)
                this.afterChildCreate(elementName, result);
        }
    }

    /**
     * 属性文本。
     * @param value 文本值。
     */
    protected void resolveElementText(String value)
    {
    }

    /**
     * 开始解析元素标签。
     */
    protected void startElement()
    {
    }
}
