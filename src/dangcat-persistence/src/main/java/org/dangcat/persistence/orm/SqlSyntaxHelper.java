package org.dangcat.persistence.orm;

import org.dangcat.persistence.entity.SqlBuilderBase;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.model.Range;

import java.util.Map;

/**
 * 标准表达式构建器。
 *
 * @author dangcat
 */
public interface SqlSyntaxHelper {
    /**
     * 构建建表表达式。
     *
     * @param sqlBuilder     数据库名。
     * @param sqlBuilderBase 表名。
     */
    void buildCreateStatement(SqlBuilder sqlBuilder, SqlBuilderBase sqlBuilderBase);

    /**
     * 构建表存在表达式。
     *
     * @param databaseName 数据库名。
     * @param tableName    表名。
     * @return 查询语句。
     */
    String buildExistsStatement(String databaseName, String tableName);

    /**
     * 构建含有范围载入的语句表达式。
     *
     * @param sql   查询语句。
     * @param range 查询范围。
     * @return 载入的表达语句。
     */
    String buildRangeLoadStatement(String sql, Range range);

    /**
     * 产生默认的参数配置。
     */
    void createDefaultParams(Map<String, String> params);

    /**
     * 转换SQL栏位数据类型。
     *
     * @param column 栏位对象。
     * @return SQL数据类型。
     */
    String getSqlType(Column column);
}
