package org.dangcat.net.rfc.template.xml;

import org.dangcat.commons.serialize.xml.XmlResolver;

/**
 * 属性规则解析器。
 *
 * @author dangcat
 */
public class RuleXmlResolver extends XmlResolver {
    private Rule rule = null;

    /**
     * 构建解析器。
     */
    public RuleXmlResolver() {
        this(Rule.class.getSimpleName());
    }

    /**
     * 构建解析器。
     */
    public RuleXmlResolver(String name) {
        super(name);
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement() {
        this.rule = new Rule();
        this.setResolveObject(this.rule);
    }
}
