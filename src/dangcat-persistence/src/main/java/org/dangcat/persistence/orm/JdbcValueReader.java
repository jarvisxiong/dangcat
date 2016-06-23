package org.dangcat.persistence.orm;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;

class JdbcValueReader {
    /**
     * 由数据集解析数据行。
     *
     * @param fieldName 字段名。
     * @param resultSet 原始数据集合。
     * @throws SQLException 异常对象。
     * @throws IOException
     */
    protected static Object read(String fieldName, ResultSet resultSet, Class<?> targetClass) throws SQLException {
        Object value = null;
        // 数据库类型
        int columnIndex = resultSet.findColumn(fieldName);
        int sqlType = resultSet.getMetaData().getColumnType(columnIndex);
        switch (sqlType) {
            case Types.NCHAR:
            case Types.NVARCHAR:
            case Types.CHAR:
            case Types.VARCHAR:
                value = readString(sqlType, fieldName, resultSet, targetClass);
                break;
            case Types.TINYINT:
            case Types.BIGINT:
            case Types.SMALLINT:
            case Types.INTEGER:
            case Types.NUMERIC:
            case Types.FLOAT:
            case Types.REAL:
            case Types.DOUBLE:
            case Types.DECIMAL:
            case Types.BOOLEAN:
                value = readNumber(fieldName, resultSet, targetClass);
                break;
            case Types.TIMESTAMP:
            case Types.DATE:
                value = readDateTime(fieldName, resultSet, targetClass);
                break;
            case Types.BINARY:
            case Types.VARBINARY:
                value = readBytes(fieldName, resultSet, targetClass);
                break;
            case Types.LONGNVARCHAR:
            case Types.LONGVARCHAR:
            case Types.NCLOB:
            case Types.CLOB:
                value = readClobStream(sqlType, fieldName, resultSet, targetClass);
                break;
            case Types.LONGVARBINARY:
            case Types.BLOB:
                value = readBytesStream(fieldName, resultSet, targetClass);
                break;
            case Types.BIT:
                value = resultSet.getBoolean(fieldName);
                break;
            default:
                value = resultSet.getObject(fieldName);
                break;
        }
        return value;
    }

    /**
     * 转换字节类型。
     *
     * @param fieldName   字段名。
     * @param resultSet   结果集合。
     * @param targetClass 目标类型。
     * @return 解析结果。
     * @throws SQLException 运行异常。
     */
    private static Object readBytes(String fieldName, ResultSet resultSet, Class<?> targetClass) throws SQLException {
        Object value = null;
        if (targetClass.equals(byte[].class) || targetClass.equals(Byte[].class))
            value = resultSet.getBytes(fieldName);
        return value;
    }

    /**
     * 转换字节类型。
     *
     * @param fieldName   字段名。
     * @param resultSet   结果集合。
     * @param targetClass 目标类型。
     * @return 解析结果。
     * @throws SQLException 运行异常。
     */
    private static Object readBytesStream(String fieldName, ResultSet resultSet, Class<?> targetClass) throws SQLException {
        Object value = null;
        if (targetClass.equals(byte[].class) || targetClass.equals(Byte[].class)) {
            InputStream inputStream = resultSet.getBinaryStream(fieldName);
            if (inputStream != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                try {
                    IOUtils.copy(inputStream, outputStream);
                    value = outputStream.toByteArray();
                } catch (IOException e) {
                    throw new SQLException(e);
                }
            }
        }
        return value;
    }

    /**
     * 转换字符类型。
     *
     * @param fieldName   字段名。
     * @param resultSet   结果集合。
     * @param targetClass 目标类型。
     * @return 解析结果。
     * @throws SQLException 运行异常。
     */
    private static Object readClobStream(int sqlType, String fieldName, ResultSet resultSet, Class<?> targetClass) throws SQLException {
        Object value = null;
        if (targetClass.equals(String.class) || targetClass.equals(char[].class) || targetClass.equals(Character[].class)) {
            Reader reader = null;
            if (sqlType == Types.NCLOB || sqlType == Types.LONGNVARCHAR)
                reader = resultSet.getNCharacterStream(fieldName);
            else if (sqlType == Types.CLOB || sqlType == Types.LONGVARCHAR)
                reader = resultSet.getCharacterStream(fieldName);
            if (reader != null) {
                CharArrayWriter writer = new CharArrayWriter();
                try {
                    IOUtils.copy(reader, writer);
                    if (targetClass.equals(String.class))
                        value = writer.toString();
                    else if (targetClass.equals(char[].class) || targetClass.equals(Character[].class))
                        value = writer.toCharArray();
                } catch (IOException e) {
                    throw new SQLException(e);
                }
            }
        }
        return value;
    }

    /**
     * 转换日期类型。
     *
     * @param fieldName   字段名。
     * @param resultSet   结果集合。
     * @param targetClass 目标类型。
     * @return 解析结果。
     * @throws SQLException 运行异常。
     */
    private static Object readDateTime(String fieldName, ResultSet resultSet, Class<?> targetClass) throws SQLException {
        Object value = null;
        if (targetClass.equals(Timestamp.class) || targetClass.equals(Date.class)) {
            Date date = resultSet.getTimestamp(fieldName);
            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                value = calendar.getTime();
            }
        }
        return value;
    }

    /**
     * 转换数字类型。
     *
     * @param fieldName   字段名。
     * @param resultSet   结果集合。
     * @param targetClass 目标类型。
     * @return 解析结果。
     * @throws SQLException 运行异常。
     */
    private static Object readNumber(String fieldName, ResultSet resultSet, Class<?> targetClass) throws SQLException {
        Object value = resultSet.getObject(fieldName);
        if (value == null) {
            if (targetClass.equals(byte.class))
                value = resultSet.getByte(fieldName);
            else if (targetClass.equals(short.class))
                value = resultSet.getShort(fieldName);
            else if (targetClass.equals(int.class))
                value = resultSet.getInt(fieldName);
            else if (targetClass.equals(long.class))
                value = resultSet.getLong(fieldName);
            else if (targetClass.equals(double.class))
                value = resultSet.getDouble(fieldName);
            else if (targetClass.equals(boolean.class))
                value = resultSet.getBoolean(fieldName);
        } else if (!targetClass.equals(value.getClass())) {
            if (targetClass.equals(Byte.class) || targetClass.equals(byte.class))
                value = resultSet.getByte(fieldName);
            else if (targetClass.equals(Short.class) || targetClass.equals(short.class))
                value = resultSet.getShort(fieldName);
            else if (targetClass.equals(Integer.class) || targetClass.equals(int.class))
                value = resultSet.getInt(fieldName);
            else if (targetClass.equals(Long.class) || targetClass.equals(long.class))
                value = resultSet.getLong(fieldName);
            else if (targetClass.equals(Double.class) || targetClass.equals(double.class))
                value = resultSet.getDouble(fieldName);
            else if (targetClass.equals(Boolean.class) || targetClass.equals(boolean.class))
                value = resultSet.getBoolean(fieldName);
        }
        return value;
    }

    /**
     * 转换字串类型。
     *
     * @param fieldName   字段名。
     * @param resultSet   结果集合。
     * @param targetClass 目标类型。
     * @return 解析结果。
     * @throws SQLException 运行异常。
     */
    private static Object readString(int sqlType, String fieldName, ResultSet resultSet, Class<?> targetClass) throws SQLException {
        String value = null;
        if (targetClass.equals(String.class) || targetClass.equals(char[].class)) {
            if (sqlType == Types.NCHAR || sqlType == Types.NVARCHAR)
                value = resultSet.getNString(fieldName);
            else if (sqlType == Types.CHAR || sqlType == Types.VARCHAR)
                value = resultSet.getString(fieldName);
            if (value != null && targetClass.equals(char[].class))
                return value.toCharArray();
        }
        return value;
    }
}
