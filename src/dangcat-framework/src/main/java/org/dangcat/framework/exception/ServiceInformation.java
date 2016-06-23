package org.dangcat.framework.exception;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.dangcat.commons.resource.ResourceUtils;
import org.dangcat.commons.serialize.json.JsonSerializer;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.serialize.json.ServiceInformationSerializer;

/**
 * 服务信息。
 * @author dangcat
 * 
 */
public class ServiceInformation implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;

    static
    {
        JsonSerializer.addSerialize(new ServiceInformationSerializer());
    }

    private List<ServiceInformation> informationList = null;
    /** 消息国际化。 */
    private Locale locale = null;
    /** The ID to the bundle message. */
    private Integer messageId = null;
    /** The key to the bundle message. */
    private String messageKey = null;
    /** 异常参数。 */
    private List<Object> paramList = null;

    public ServiceInformation()
    {
        super();
    }

    public ServiceInformation(Integer messageId, Object... params)
    {
        this.messageId = messageId;
        this.addParams(params);
    }

    public ServiceInformation(String messageKey, Object... params)
    {
        this.messageKey = messageKey;
        this.addParams(params);
    }

    public void addInformation(ServiceInformation serviceInformation)
    {
        if (serviceInformation != null)
        {
            if (this.informationList == null)
                this.informationList = new ArrayList<ServiceInformation>();
            if (!this.informationList.contains(serviceInformation))
                this.informationList.add(serviceInformation);
        }
    }

    public void addParams(Object... params)
    {
        if (params != null && params.length > 0)
        {
            if (this.paramList == null)
                this.paramList = new ArrayList<Object>();
            this.paramList.addAll(Arrays.asList(params));
        }
    }

    protected String format(String message)
    {
        if (this.paramList != null && this.paramList.size() > 0)
            message = MessageFormat.format(message, this.paramList.toArray());
        return message;
    }

    public List<ServiceInformation> getChildInformationList()
    {
        return this.informationList;
    }

    public Locale getLocale()
    {
        return this.locale == null ? Environment.getDefaultLocale() : this.locale;
    }

    public String getMessage()
    {
        if (this.getMessageId() != null)
        {
            String message = ResourceUtils.getText(this.getClass(), this.getLocale(), this.getMessageId().toString());
            if (ValueUtils.isEmpty(message))
                return this.getMessageId().toString();
            return this.format(message);
        }
        return this.messageKey;
    }

    public Integer getMessageId()
    {
        return messageId;
    }

    public String getMessageKey()
    {
        return messageKey;
    }

    /**
     * 参数列表。
     */
    public Object[] getParams()
    {
        return this.paramList == null ? null : this.paramList.toArray();
    }

    @Override
    public String toString()
    {
        return this.getMessage();
    }
}
