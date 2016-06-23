package org.dangcat.framework.service.xml;

import java.util.Collection;

import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.serialize.xml.XmlResolver;
import org.dangcat.commons.utils.ValueUtils;
import org.dom4j.Element;

/**
 * 拦截器对象解析器。
 * @author dangcat
 * 
 */
public class InterceptorsXmlResolver extends XmlResolver
{
    private static final String CHILDELEMENT_NAME = "Interceptor";
    protected static final String RESOLVER_NAME = "Interceptors";

    /**
     * 构建解析器。
     */
    public InterceptorsXmlResolver()
    {
        super(RESOLVER_NAME);
    }

    @SuppressWarnings("unchecked")
    public Collection<Class<?>> getInterceptors()
    {
        return (Collection<Class<?>>) this.getResolveObject();
    }

    /**
     * 开始解析子元素标签。
     * @param element 子元素名称。
     */
    protected void resolveChildElement(Element element)
    {
        if (CHILDELEMENT_NAME.equalsIgnoreCase(element.getName()) && !ValueUtils.isEmpty(element.getText()))
        {
            Class<?> interceptorClass = ReflectUtils.loadClass(element.getTextTrim());
            if (interceptorClass == null)
                logger.error("The interceptor class " + element.getTextTrim() + " is not found.");
            else
                this.getInterceptors().add(interceptorClass);
        }
    }
}
