package org.dangcat.commons.serialize.xml;

import java.util.HashMap;
import java.util.Map;

/**
 * 参数列表对象解析器。
 *
 * @author dangcat
 */
public class ParamsXmlResolver extends XmlResolver {
    private static final String RESOLVER_NAME = "Params";
    /**
     * 参数列表对象。
     */
    private Map<String, Object> params;

    /**
     * 构建解析器。
     */
    public ParamsXmlResolver() {
        this(RESOLVER_NAME, null);
        this.addChildXmlResolver(new ParamXmlResolver());
    }

    public ParamsXmlResolver(String resolverName, String elementName) {
        super(resolverName);
        this.addChildXmlResolver(new ParamXmlResolver(elementName));
    }

    /**
     * 产生子元素对象。
     *
     * @param elementName 子元素名称。
     * @param child       子元素对象。
     */
    protected void afterChildCreate(String elementName, Object child) {
        Param param = (Param) child;
        if (param != null)
            this.params.put(param.getName(), param.getValue());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setResolveObject(Object resolveObject) {
        this.params = (Map<String, Object>) resolveObject;
        super.setResolveObject(resolveObject);
    }

    @Override
    protected void startElement() {
        if (this.params == null) {
            this.params = new HashMap<String, Object>();
            this.setResolveObject(this.params);
        }
    }
}
