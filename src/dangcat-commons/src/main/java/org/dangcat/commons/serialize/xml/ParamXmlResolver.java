package org.dangcat.commons.serialize.xml;

import org.dangcat.commons.reflect.ReflectUtils;

/**
 * 参数对象解析器。
 *
 * @author dangcat
 */
public class ParamXmlResolver extends XmlResolver {
    /**
     * 参数对象。
     */
    private Param param = null;

    /**
     * 构建解析器。
     */
    public ParamXmlResolver() {
        this(Param.class.getSimpleName());
    }

    /**
     * 构建解析器。
     */
    public ParamXmlResolver(String name) {
        super(name == null ? Param.class.getSimpleName() : name);
    }

    /**
     * 属性文本。
     *
     * @param value 文本值。
     */
    @Override
    protected void resolveElementText(String value) {
        this.param.setValue(ReflectUtils.parseValue(this.param.getClassType(), value));
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement() {
        this.param = new Param();
        this.setResolveObject(this.param);
    }
}
