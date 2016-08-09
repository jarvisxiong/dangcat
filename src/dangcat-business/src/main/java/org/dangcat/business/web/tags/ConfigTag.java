package org.dangcat.business.web.tags;

import org.apache.log4j.Logger;
import org.dangcat.business.config.BusinessConfig;
import org.dangcat.commons.serialize.json.JsonSerializer;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.conf.ConfigProvider;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.impl.ServiceInfo;
import org.dangcat.web.serialize.json.EntityJsonSerializer;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * 配置参数输出。
 *
 * @author dangcat
 */
public class ConfigTag extends SimpleTagSupport {
    protected static final Logger logger = Logger.getLogger(ConfigTag.class);
    private static final String PROPERTY_NAME = "config";

    @Override
    public void doTag() throws JspTagException {
        try {
            ServiceContext serviceContext = ServiceContext.getInstance();
            if (serviceContext != null) {
                ServiceInfo serviceInfo = serviceContext.getServiceInfo();
                ConfigProvider configProvider = serviceInfo.getConfigProvider();
                if (configProvider instanceof BusinessConfig && !configProvider.getConfigValueMap().isEmpty()) {
                    BusinessConfig businessConfig = (BusinessConfig) configProvider;
                    JspWriter jspWriter = this.getJspContext().getOut();
                    jspWriter.write(PROPERTY_NAME);
                    jspWriter.write(":{");

                    EntityJsonSerializer entityJsonSerializer = new EntityJsonSerializer();
                    Object configEntity = businessConfig.getCurrentEntity();
                    entityJsonSerializer.serialize(jspWriter, configEntity.getClass(), "table");

                    String data = JsonSerializer.serialize(configEntity);
                    if (!ValueUtils.isEmpty(data)) {
                        jspWriter.write(",data:");
                        jspWriter.write(data);
                    }
                    jspWriter.write("},");
                }
            }
        } catch (Exception e) {
            logger.error(this, e);

            Throwable rootCause = null;
            if (e instanceof ServletException)
                rootCause = ((ServletException) e).getRootCause();
            throw new JspTagException(e.getMessage(), rootCause);
        }
    }
}
