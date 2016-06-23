package org.dangcat.net.rfc.template.xml;

import org.dangcat.commons.serialize.xml.XmlResolver;

/**
 * 属性规则解析器。
 *
 * @author dangcat
 */
public class RulesXmlResolver extends XmlResolver {
    private static final String RESOLVER_NAME = "Rules";
    private Rules rules = null;

    /**
     * 构建解析器。
     */
    public RulesXmlResolver() {
        super(RESOLVER_NAME);
        this.addChildXmlResolver(new RuleXmlResolver());
    }

    /**
     * 产生子元素对象。
     *
     * @param elementName 子元素名称。
     * @param child       子元素对象。
     */
    protected void afterChildCreate(String elementName, Object child) {
        Rule rule = (Rule) child;
        if (rule != null)
            this.rules.add(rule);
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement() {
        this.rules = new Rules();
        this.setResolveObject(this.rules);
    }
}
