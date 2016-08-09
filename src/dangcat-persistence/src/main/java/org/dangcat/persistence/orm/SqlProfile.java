package org.dangcat.persistence.orm;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.ValueUtils;

import java.util.Collection;

/**
 * SQL跟踪
 *
 * @author dangcat
 */
public class SqlProfile {
    private static final String CHANGE_LINE = "\r\n";
    private static final Boolean DEFAULT_DEBUG_ENABLED = Boolean.FALSE;
    private static final Long DEFAULT_MINCOSTTIME = 30000l;
    private static final Logger logger = Logger.getLogger(SqlProfile.class);
    private static Boolean debugEnabled = null;
    private static Long minCostTime = null;
    private long beginTime = System.currentTimeMillis();
    private StringBuilder sqlCache = null;

    public static long getMinCostTime() {
        if (minCostTime == null) {
            String value = System.getProperty("SqlProfile.MinCostTime");
            minCostTime = ValueUtils.parseLong(value, DEFAULT_MINCOSTTIME);
        }
        return minCostTime.longValue();
    }

    public static void setMinCostTime(long value) {
        minCostTime = value;
    }

    public static boolean isDebugEnabled() {
        if (debugEnabled == null) {
            String value = System.getProperty("SqlProfile.DebugEnabled");
            debugEnabled = ValueUtils.parseBoolean(value, DEFAULT_DEBUG_ENABLED);
        }
        return debugEnabled.booleanValue();
    }

    public static void setDebugEnabled(boolean value) {
        debugEnabled = value;
    }

    public void appendSql(Collection<String> sqls) {
        if (sqls != null && !sqls.isEmpty()) {
            for (String sql : sqls)
                this.appendSql(sql);
        }
    }

    public void appendSql(String sql) {
        if (!ValueUtils.isEmpty(sql)) {
            StringBuilder sqlCache = this.getSqlCache();
            if (sqlCache.length() > 0)
                sqlCache.append(CHANGE_LINE);
            sqlCache.append(sql);
        }
    }

    public void begin() {
        this.beginTime = System.currentTimeMillis();
    }

    public void end() {
        if (this.sqlCache == null || this.sqlCache.length() == 0)
            return;

        long costTime = System.currentTimeMillis() - this.beginTime;
        if (costTime > 0l) {
            this.sqlCache.append(" cost time ");
            this.sqlCache.append(costTime);
            this.sqlCache.append("(ms)");
        }

        if (costTime > getMinCostTime())
            logger.warn(this.sqlCache);
        else if (isDebugEnabled())
            logger.info(this.sqlCache);
        this.sqlCache = null;
    }

    private StringBuilder getSqlCache() {
        if (this.sqlCache == null)
            this.sqlCache = new StringBuilder();
        return this.sqlCache;
    }
}
