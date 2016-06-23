package org.dangcat.framework.service.xml;

import org.dangcat.commons.serialize.xml.XmlResolver;
import org.dangcat.commons.utils.Environment;
import org.dangcat.framework.service.impl.ServiceInfo;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * 服务对象解析器。
 * @author dangcat
 * 
 */
public class ServicesXmlResolver extends XmlResolver
{
    private static final String RESOLVER_NAME = "Services";
    /** 拦截器配置。 */
    private Collection<Class<?>> interceptors = new LinkedHashSet<Class<?>>();
    /** 配置的子服务列表。 */
    private List<ServiceInfo> serviceInfos = new LinkedList<ServiceInfo>();

    /**
     * 构建解析器。
     */
    public ServicesXmlResolver()
    {
        super(RESOLVER_NAME);
        this.addChildXmlResolver(new ServiceXmlResolver());
        this.addChildXmlResolver(new InterceptorsXmlResolver());
    }

    /**
     * 产生子元素对象。
     * @param elementName 子元素名称。
     * @param child 子元素对象。
     */
    @Override
    protected void afterChildCreate(String elementName, Object child)
    {
        if (child instanceof ServiceInfo)
            this.serviceInfos.add((ServiceInfo) child);
    }

    /**
     * 解析子元素之前。
     * @param name 属性名称。
     * @param xmlResolver 解析器。
     */
    @Override
    protected void beforeChildResolve(String elementName, XmlResolver xmlResolver)
    {
        if (InterceptorsXmlResolver.RESOLVER_NAME.equalsIgnoreCase(xmlResolver.getName()))
            xmlResolver.setResolveObject(this.getInterceptors());
    }

    public Collection<Class<?>> getInterceptors()
    {
        return this.interceptors;
    }

    public List<ServiceInfo> getServiceInfos()
    {
        return this.serviceInfos;
    }

    @Override
    public String toString()
    {
        StringBuilder info = new StringBuilder();
        info.append("Services: ");
        for (ServiceInfo serviceInfo : this.serviceInfos)
        {
            info.append(Environment.LINETAB_SEPARATOR);
            info.append(serviceInfo);
        }
        info.append(Environment.LINE_SEPARATOR);
        info.append("Interceptors: ");
        for (Class<?> interceptor : this.interceptors)
        {
            info.append(Environment.LINETAB_SEPARATOR);
            info.append(interceptor.getName());
        }
        return info.toString();
    }
}
