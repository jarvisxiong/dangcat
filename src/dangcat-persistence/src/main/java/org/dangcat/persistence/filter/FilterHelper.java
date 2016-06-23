package org.dangcat.persistence.filter;

import org.dangcat.persistence.model.Row;
import org.dangcat.persistence.model.TableStatementHelper;

import java.util.List;
import java.util.Map;

/**
 * 过滤组别转换工具。
 *
 * @author dangcat
 */
public class FilterHelper {
    /**
     * 检查数据行属于哪个过滤组别。
     *
     * @param row            数据行对象。
     * @param srcFilterGroup 来源过滤组别。
     * @return 如果存在所属过滤组别则返回组别名字，否则返回null。
     */
    public static Object checkGroup(Row row, FilterGroup srcFilterGroup) {
        if (srcFilterGroup != null && srcFilterGroup.getFilterExpressList().size() > 0) {
            for (FilterExpress filterExpress : srcFilterGroup.getFilterExpressList()) {
                FilterGroup filterGroup = (FilterGroup) filterExpress;
                if (filterGroup != null && filterGroup.isValid(row))
                    return filterGroup.getValue();
            }
        }
        return null;
    }

    /**
     * 判断是否可以转成CASE表达语句。
     *
     * @param srcFilterGroup 来源过滤组别。
     */
    private static boolean couldToCaseExpress(FilterGroup srcFilterGroup) {
        boolean result = false;
        if (srcFilterGroup != null && srcFilterGroup.getGroupType().equals(FilterGroupType.or)) {
            List<FilterExpress> filterExpressList = srcFilterGroup.getFilterExpressList();
            if (filterExpressList != null && filterExpressList.size() > 0) {
                result = true;
                // 必须是全部为对象组才可以转成 CASE表达式
                for (FilterExpress filterExpress : filterExpressList) {
                    if (!(filterExpress instanceof FilterGroup)) {
                        result = false;
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 判断是否可以转成CASE的ELSE表达语句。
     *
     * @param filterGroup 过滤组别。
     */
    private static boolean isElseGroup(FilterGroup filterGroup) {
        List<FilterExpress> filterExpressList = filterGroup.getFilterExpressList();
        if (filterExpressList != null && filterExpressList.size() > 0) {
            // 必须是全部为忽略的才可以转成 ELSE
            for (FilterExpress filterExpress : filterGroup.getFilterExpressList()) {
                if (filterExpress instanceof FilterUnit) {
                    FilterUnit filterUnit = (FilterUnit) filterExpress;
                    if (!filterUnit.getFilterType().equals(FilterType.ignore))
                        return false;
                } else if (filterExpress instanceof FilterGroup) {
                    if (!isElseGroup((FilterGroup) filterExpress))
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * 提取过滤中包含的字段内容。
     *
     * @param filterExpress 过滤单元。
     */
    public static void pickMap(FilterExpress filterExpress, Map<String, Object> params) {
        if (filterExpress instanceof FilterUnit) {
            FilterUnit filterUnit = (FilterUnit) filterExpress;
            params.put(filterUnit.getFieldName(), null);
        } else if (filterExpress instanceof FilterGroup) {
            FilterGroup filterGroup = (FilterGroup) filterExpress;
            for (FilterExpress childFilterExpress : filterGroup.getFilterExpressList())
                pickMap(childFilterExpress, params);
        }
    }

    /**
     * 转成CASE表达语句。
     *
     * @param srcFilterGroup 来源过滤组别。
     */
    public static String toCaseExpress(FilterGroup srcFilterGroup) {
        StringBuilder caseBuilder = new StringBuilder();
        if (couldToCaseExpress(srcFilterGroup) && srcFilterGroup.getFilterExpressList().size() > 0) {
            FilterGroup elseFilterGroup = null;
            for (FilterExpress filterExpress : srcFilterGroup.getFilterExpressList()) {
                FilterGroup filterGroup = (FilterGroup) filterExpress;
                // 如果符合ELSE表达则记录下来作为最后加入。
                if (isElseGroup(filterGroup))
                    elseFilterGroup = filterGroup;
                else {
                    String sql = filterGroup.toString();
                    if (sql != null && sql.length() > 0) {
                        if (caseBuilder.length() > 0)
                            caseBuilder.append("     ");
                        else
                            caseBuilder.append("CASE ");
                        caseBuilder.append("WHEN ");
                        caseBuilder.append(filterGroup);
                        caseBuilder.append(" THEN ");
                        caseBuilder.append(TableStatementHelper.toSqlString(filterGroup.getValue()));
                        caseBuilder.append("\n");
                    }
                }
            }
            if (caseBuilder.length() > 0) {
                if (elseFilterGroup != null) {
                    caseBuilder.append("     ELSE ");
                    caseBuilder.append(TableStatementHelper.toSqlString(elseFilterGroup.getValue()));
                }
                caseBuilder.append(" END");
            }
        }
        return caseBuilder.toString();
    }
}
