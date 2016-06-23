package org.dangcat.framework.service.xml;

import org.dangcat.commons.serialize.xml.PropertiesXmlResolver;
import org.dangcat.commons.serialize.xml.XmlResolver;
import org.dangcat.framework.service.impl.ServiceInfo;

/**
 * 栏位对象解析器。
 *
 * @author dangcat
 */
class ServiceXmlResolver extends XmlResolver {
    private static final String RESOLVER_NAME = "Service";

    /**
     * 服务信息。
     */
    private ServiceInfo serviceInfo = null;

    /**
     * 构建解析器。
     */
    ServiceXmlResolver() {
        super(RESOLVER_NAME);
        this.addChildXmlResolver(new PropertiesXmlResolver());
        this.addChildXmlResolver(new InterceptorsXmlResolver());
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement() {
        this.serviceInfo = new ServiceInfo();
        this.setResolveObject(this.serviceInfo);
    }
}
