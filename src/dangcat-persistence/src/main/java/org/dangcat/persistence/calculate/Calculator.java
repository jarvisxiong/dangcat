package org.dangcat.persistence.calculate;

import java.util.Collection;

/**
 * 计算器接口。
 *
 * @author dangcat
 */
public interface Calculator {
    /**
     * 计算实体集合。
     *
     * @param entityCollection 实体对象集合。
     */
    void calculate(Collection<?> entityCollection);

    /**
     * 计算单个实体。
     *
     * @param entity 实体对象。
     */
    void calculate(Object entity);
}
