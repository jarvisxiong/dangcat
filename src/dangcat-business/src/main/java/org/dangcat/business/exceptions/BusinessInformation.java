package org.dangcat.business.exceptions;

import org.dangcat.persistence.validator.exception.DataValidateInformation;

/**
 * 业务服务信息。
 *
 * @author dangcat
 */
public class BusinessInformation extends DataValidateInformation {
    /**
     * 没有找到符合条件的结果。
     */
    public static final Integer DATA_NOTFOUND = 50;
    /**
     * 成功删除数据。
     */
    public static final Integer DELETE_SUCCESS = 51;
    private static final long serialVersionUID = 1L;

    public BusinessInformation(Class<?> classType, String fieldName, Integer messageId, Object... params) {
        super(classType, fieldName, messageId, params);
    }

    public BusinessInformation(Integer messageId, Object... params) {
        super(messageId, params);
    }
}
