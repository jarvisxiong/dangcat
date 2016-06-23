package org.dangcat.document.json;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.document.DocumentBase;
import org.dangcat.persistence.DataReader;
import org.dangcat.persistence.DataWriter;
import org.dangcat.persistence.model.Column;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Json文档输入输出。
 * @author dangcat
 * 
 */
public class JsonDocument extends DocumentBase
{
    protected static final String INDENT_SPACE = "    ";
    protected static final String JSON_ENCODING = "UTF-8";

    /**
     * 从数据流加载数据。
     * @param dataWriter 数据输出接口。
     * @return 读入的行数。
     */
    @Override
    public int read(DataWriter dataWriter)
    {
        JsonReader jsonReader = null;
        try
        {
            jsonReader = new JsonReader(new InputStreamReader(this.getInputStream(), JSON_ENCODING));
            jsonReader.beginArray();
            while (jsonReader.hasNext())
                this.readObject(dataWriter, jsonReader, dataWriter.size());
            jsonReader.endArray();

        }
        catch (Exception e)
        {
            logger.error("The object " + dataWriter.size() + " is error: ", e);
        }
        finally
        {
            try
            {
                if (jsonReader != null)
                    jsonReader.close();
            }
            catch (IOException e)
            {
            }
        }
        return dataWriter.size();
    }

    private void readObject(DataWriter dataWriter, JsonReader jsonReader, int index) throws IOException
    {
        jsonReader.beginObject();
        while (jsonReader.hasNext())
        {
            String fieldName = jsonReader.nextName();
            Column column = dataWriter.getColumns().find(fieldName);
            if (column != null)
            {
                JsonToken jsonToken = jsonReader.peek();
                if (jsonToken == JsonToken.STRING || jsonToken == JsonToken.NUMBER)
                {
                    Object value = column.parse(jsonReader.nextString());
                    dataWriter.setValue(index, fieldName, value);
                }
                else if (jsonToken == JsonToken.BOOLEAN)
                    dataWriter.setValue(index, fieldName, jsonReader.nextBoolean());
                else
                    jsonReader.skipValue();
            }
            else
                jsonReader.skipValue();
        }
        jsonReader.endObject();
    }

    /**
     * 导出数据到数据流。
     * @param dataReader 数据输入接口。
     * @return 写入的行数。
     */
    @Override
    public int write(DataReader dataReader)
    {
        JsonWriter jsonWriter = null;
        int result = 0;
        try
        {
            jsonWriter = new JsonWriter(new OutputStreamWriter(this.getOutputStream(), JSON_ENCODING));
            jsonWriter.setIndent(INDENT_SPACE);
            jsonWriter.beginArray();
            for (int index = 0; index < dataReader.size(); index++)
            {
                this.writeObject(dataReader, jsonWriter, index);
                result++;
            }
            jsonWriter.endArray();
        }
        catch (Exception e)
        {
            logger.error("The object " + result + " is error: ", e);
        }
        finally
        {
            try
            {
                if (jsonWriter != null)
                    jsonWriter.close();
            }
            catch (IOException e)
            {
            }
        }
        return result;
    }

    private void writeObject(DataReader dataReader, JsonWriter jsonWriter, int index) throws IOException
    {
        jsonWriter.beginObject();
        for (Column column : dataReader.getColumns())
        {
            String name = column.getName();
            Object value = dataReader.getValue(index, name);
            if (value == null)
                jsonWriter.name(name).nullValue();
            else if (ValueUtils.isNumber(column.getFieldClass()))
                jsonWriter.name(name).value((Number) value);
            else
                jsonWriter.name(name).value(column.toString(value));
        }
        jsonWriter.endObject();
    }
}
