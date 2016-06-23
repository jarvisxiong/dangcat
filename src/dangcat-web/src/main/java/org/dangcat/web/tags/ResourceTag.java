package org.dangcat.web.tags;

import org.apache.log4j.Logger;
import org.dangcat.commons.resource.ResourceReader;
import org.dangcat.commons.resource.ResourceUtils;
import org.dangcat.commons.serialize.json.JsonSerializer;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.ServiceContext;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.util.Map;

/**
 * ¶ÁÈ¡×ÊÔ´×Ö´®¡£
 *
 * @author dangcat
 */
public class ResourceTag extends SimpleTagSupport {
    protected static final Logger logger = Logger.getLogger(ResourceTag.class);
    private boolean isMap = false;
    private String key = null;

    @Override
    public void doTag() throws JspTagException {
        if (ValueUtils.isEmpty(this.getKey()))
            return;
        try {
            ServiceContext serviceContext = ServiceContext.getInstance();
            if (serviceContext != null) {
                ResourceReader resourceReader = serviceContext.getServiceInfo().getResourceReader();
                String message = resourceReader.getText(serviceContext.getLocale(), this.getKey());
                if (!ValueUtils.isEmpty(message)) {
                    if (this.isMap()) {
                        Map<Integer, String> map = ResourceUtils.createValueMap(message);
                        if (map != null)
                            message = JsonSerializer.serialize(map);
                    }
                    PageContext pageContext = (PageContext) this.getJspContext();
                    pageContext.getOut().print(message);
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

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isMap() {
        return this.isMap;
    }

    public void setMap(boolean isMap) {
        this.isMap = isMap;
    }
}
