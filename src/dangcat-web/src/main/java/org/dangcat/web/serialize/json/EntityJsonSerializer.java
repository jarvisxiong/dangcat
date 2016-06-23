package org.dangcat.web.serialize.json;

import com.google.gson.stream.JsonWriter;
import org.apache.log4j.Logger;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.annotation.JndiName;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityMetaData;
import org.dangcat.persistence.entity.EntityUtils;
import org.dangcat.persistence.entity.Relation;
import org.dangcat.persistence.model.Table;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;

/**
 * 栏位对象序列化。
 *
 * @author dangcat
 */
public class EntityJsonSerializer {
    protected static final Logger logger = Logger.getLogger(EntityJsonSerializer.class);
    private static final String CHILDREN_NAMES = "childrenNames";
    private static final String JNDI_NAME = "jndiName";

    public void serialize(Writer writer, Class<?> classType, String name) throws IOException {
        JsonWriter jsonWriter = null;
        try {
            StringWriter stringWriter = new StringWriter();
            jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.beginObject();
            this.serializeEntity(jsonWriter, classType, name);
            jsonWriter.endObject();

            String result = stringWriter.toString();
            int firstIndex = result.indexOf("{");
            result = result.substring(firstIndex + 1);
            int lastIndex = result.lastIndexOf("}");
            result = result.substring(0, lastIndex);
            writer.write(result);
        } catch (Exception e) {
            logger.error("serialize the classType " + classType + " is error: ", e);
        } finally {
            try {
                if (jsonWriter != null)
                    jsonWriter.close();
            } catch (IOException e) {
            }
        }
    }

    private void serializeEntity(JsonWriter jsonWriter, Class<?> classType, String name) throws IOException {
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(classType);
        if (ValueUtils.isEmpty(name))
            name = ReflectUtils.toPropertyName(classType.getSimpleName());
        this.serializeTable(entityMetaData, jsonWriter, name);
        this.serializeRelations(classType, jsonWriter);
    }

    private void serializeJndiName(EntityMetaData entityMetaData, JsonWriter jsonWriter) throws IOException {
        JndiName jndiName = ReflectUtils.findAnnotation(entityMetaData.getEntityClass(), JndiName.class);
        if (jndiName != null)
            jsonWriter.name(JNDI_NAME).value(jndiName.module() + "/" + jndiName.name());
    }

    private void serializeRelationNames(Class<?> classType, JsonWriter jsonWriter) throws IOException {
        Collection<Relation> relationCollection = EntityUtils.getSerializeRelations(classType);
        if (relationCollection != null && relationCollection.size() > 0) {
            JsonWriter childrenNamesWriter = jsonWriter.name(CHILDREN_NAMES);
            childrenNamesWriter.beginArray();
            for (Relation relation : relationCollection)
                childrenNamesWriter.value(ReflectUtils.toPropertyName(relation.getName()));
            childrenNamesWriter.endArray();
        }
    }

    private void serializeRelations(Class<?> classType, JsonWriter jsonWriter) throws IOException {
        Collection<Relation> relationCollection = EntityUtils.getSerializeRelations(classType);
        if (relationCollection != null && relationCollection.size() > 0) {
            for (Relation relation : relationCollection)
                this.serializeEntity(jsonWriter, relation.getMemberType(), ReflectUtils.toPropertyName(relation.getName()));
        }
    }

    private void serializeTable(EntityMetaData entityMetaData, JsonWriter jsonWriter, String name) throws IOException {
        Table table = entityMetaData.getTable();
        JsonWriter tableJsonWriter = jsonWriter.name(name);
        tableJsonWriter.beginObject();
        this.serializeJndiName(entityMetaData, tableJsonWriter);
        this.serializeRelationNames(entityMetaData.getEntityClass(), tableJsonWriter);
        ColumnJsonSerializer columnJsonSerializer = new ColumnJsonSerializer(entityMetaData);
        columnJsonSerializer.serializeColumns(tableJsonWriter, table.getColumns());
        tableJsonWriter.endObject();
    }
}