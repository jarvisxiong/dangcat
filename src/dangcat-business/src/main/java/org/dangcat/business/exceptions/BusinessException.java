package org.dangcat.business.exceptions;

import org.dangcat.persistence.validator.exception.DataValidateException;

/**
 * 业务服务异常。
 *
 * @author dangcat
 */
public class BusinessException extends DataValidateException {
    /**
     * 有特殊权限才能修改。
     */
    public static final Integer ADVANCED_MODIFY = 59;
    /**
     * 新增数据失败。
     */
    public static final Integer CREATE_ERROR = 53;
    /**
     * 数据不存在。
     */
    public static final Integer DATA_NOTEXISTS = 56;
    /**
     * 没有找到符合条件的结果。
     */
    public static final Integer DATA_NOTFOUND = 54;
    /**
     * 数据重复。
     */
    public static final Integer DATA_REPEAT = 55;
    /**
     * 数据状态不正确。
     */
    public static final Integer DATASTATE_INCORRECT = 60;
    /**
     * 删除数据错误。
     */
    public static final Integer DELETE_ERROR = 50;
    /**
     * 字段不存在。
     */
    public static final Integer FIELD_NOTEXISTS = 58;
    /**
     * 执行错误，请查看后台日志。
     */
    public static final Integer INVOKE_ERROR = 60;
    /**
     * 加载数据错误。
     */
    public static final Integer LOAD_ERROR = 51;
    /**
     * 没有执行权限。
     */
    public static final Integer PERMISSION_DENY = 57;
    /**
     * 保存数据失败。
     */
    public static final Integer SAVE_ERROR = 52;
    private static final long serialVersionUID = 1L;

    public BusinessException(Class<?> classType, String fieldName, Integer messageId, Object... params) {
        super(classType, fieldName, messageId, params);
    }

    public BusinessException(Integer messageId, Object... params) {
        super(messageId, params);
    }
}
