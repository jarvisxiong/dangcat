package org.dangcat.web.tags;

import org.apache.log4j.Logger;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityMetaData;
import org.dangcat.web.serialize.json.EntityJsonSerializer;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * 输出实体定义结构。
 *
 * @author dangcat
 */
public class EntityTag extends SimpleTagSupport {
    protected static final Logger logger = Logger.getLogger(EntityTag.class);
    private String classType = null;
    private String name = null;

    @Override
    public void doTag() throws JspTagException {
        if (ValueUtils.isEmpty(this.getClassType()))
            throw new JspTagException("The entity tag class type can't be empty.");

        Class<?> classType = ReflectUtils.loadClass(this.getClassType());
        if (classType == null)
            throw new JspTagException("The class type " + this.getClassType() + " can't be found.");

        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(classType);
        if (entityMetaData == null)
            throw new JspTagException("The class type " + this.getClassType() + " is not entity type.");

        try {
            PageContext pageContext = (PageContext) this.getJspContext();
            EntityJsonSerializer entityJsonSerializer = new EntityJsonSerializer();
            entityJsonSerializer.serialize(pageContext.getOut(), classType, this.getName());
        } catch (Exception e) {
            logger.error(this, e);

            Throwable rootCause = null;
            if (e instanceof ServletException)
                rootCause = ((ServletException) e).getRootCause();
            throw new JspTagException(e.getMessage(), rootCause);
        }
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
