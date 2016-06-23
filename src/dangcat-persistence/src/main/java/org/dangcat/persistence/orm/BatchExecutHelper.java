package org.dangcat.persistence.orm;

import java.util.List;

import org.dangcat.commons.utils.ValueUtils;

public class BatchExecutHelper
{
    /**
     * 分析语句中的参数。
     * @param sql 原始语句。
     * @param filedNameList 产生参数顺序。
     * @return 分析后的JDBC语句。
     */
    public static String analyze(String sql, List<String> filedNameList)
    {
        StringBuilder sqlBuilder = new StringBuilder();
        if (!ValueUtils.isEmpty(sql))
        {
            for (int beginIndex = 0; beginIndex < sql.length(); beginIndex++)
            {
                String value = sql.substring(beginIndex, beginIndex + 1);
                if (isParams(value))
                {
                    String paramName = analyzeParamName(sql.substring(beginIndex + 1));
                    beginIndex += paramName.length();
                    filedNameList.add(paramName);
                    sqlBuilder.append(" ? ");
                }
                else
                    sqlBuilder.append(value);

            }
        }
        return sqlBuilder.toString();
    }

    /**
     * 分析参数名。
     * @param sql SQL字句。
     * @return 参数名。
     */
    private static String analyzeParamName(String sql)
    {
        StringBuilder paramName = new StringBuilder();
        for (char charValue : sql.toCharArray())
        {
            if (Character.isLetterOrDigit(charValue) || charValue == '_')
                paramName.append(charValue);
            else
                break;
        }
        return paramName.toString();
    }

    /**
     * 是否包含参数设置。
     * @param sqlBatchList 批量SQL语句。
     * @return 包含带设定参数。
     */
    public static boolean isContainsParams(List<String> sqlBatchList)
    {
        boolean result = false;
        if (sqlBatchList.size() > 0)
        {
            for (String sql : sqlBatchList)
            {
                if (isContainsParams(sql))
                {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 是否包含参数设置。
     * @param sql SQL语句。
     * @return 包含带设定参数。
     */
    public static boolean isContainsParams(String sql)
    {
        boolean result = false;
        if (!ValueUtils.isEmpty(sql))
        {
            sql = replaceAll(sql, "'");
            sql = replaceAll(sql, "\"");
            for (String paramFlag : SqlBuilder.PARAMS_FLAG)
            {
                if (sql.indexOf(paramFlag) != -1)
                {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 是否包含参数设置。
     * @param sql SQL语句。
     * @return 包含带设定参数。
     */
    private static boolean isParams(String value)
    {
        boolean result = false;
        for (String paramFlag : SqlBuilder.PARAMS_FLAG)
        {
            if (paramFlag.equalsIgnoreCase(value))
            {
                result = true;
                break;
            }
        }
        return result;
    }

    private static String replaceAll(String text, String flag)
    {
        int beginIndex = text.indexOf(flag);
        while (text.length() > 0 && beginIndex != -1)
        {
            int endIndex = text.indexOf(flag, beginIndex + 1);
            if (endIndex != -1)
            {
                if (beginIndex == 0)
                    text = text.substring(endIndex + 1);
                else if (endIndex == text.length() - 1)
                    text = text.substring(0, beginIndex);
                else
                    text = text.substring(0, beginIndex) + text.substring(endIndex + 1);
            }
            beginIndex = text.indexOf(flag);
        }
        return text;
    }
}
