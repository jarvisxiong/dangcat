package org.dangcat.persistence.model;

/**
 * 数据表状态。
 *
 * @author dangcat
 */
public enum TableState {
    /**
     * 设置全表为插入状态。
     */
    Insert,
    /**
     * 正在在入。
     */
    Loading,
    /**
     * 正常。
     */
    Normal,
    /**
     * 正在存档。
     */
    Saving
}
