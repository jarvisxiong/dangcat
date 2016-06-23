package org.dangcat.persistence.exception;

/**
 * 数据表产生的异常。
 *
 * @author dangcat
 */
public class EntityException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EntityException() {
    }

    public EntityException(Exception cause) {
        super(cause);
    }

    public EntityException(String message) {
        super(message);
    }

    public EntityException(String message, Exception cause) {
        super(message, cause);
    }
}
