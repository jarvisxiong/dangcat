package org.dangcat.commons.resource;

import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

/**
 * 资源读取接口。
 *
 * @author dangcat
 */
public abstract class ResourceReader {
    public String format(String message, Object... params) {
        if (params != null && params.length > 0 && !ValueUtils.isEmpty(message))
            message = MessageFormat.format(message, params);
        return message;
    }

    /**
     * 读取对象。
     *
     * @param locale 地区对象。
     * @param key    关键字。
     * @return 资源对象。
     */
    public abstract Object getObject(Locale locale, String key);

    /**
     * 读取对象。
     *
     * @param key 关键字。
     * @return 资源对象。
     */
    public Object getObject(String key) {
        return this.getObject(Environment.getCurrentLocale(), key);
    }

    /**
     * 读取文本。
     *
     * @param locale 地区对象。
     * @param key    关键字。
     * @return 资源文本。
     */
    public abstract String getText(Locale locale, String key, Object... params);

    /**
     * 读取文本。
     *
     * @param key 关键字。
     * @return 资源文本。
     */
    public String getText(String key, Object... params) {
        return this.getText(Environment.getCurrentLocale(), key, params);
    }

    /**
     * 读取映射表。
     *
     * @param locale 地区对象。
     * @param key    关键字。
     * @return 资源映射表。
     */
    public abstract Map<Integer, String> getValueMap(Locale locale, String key);

    /**
     * 读取映射表。
     *
     * @param key 关键字。
     * @return 资源映射表。
     */
    public Map<Integer, String> getValueMap(String key) {
        return this.getValueMap(Environment.getCurrentLocale(), key);
    }
}
