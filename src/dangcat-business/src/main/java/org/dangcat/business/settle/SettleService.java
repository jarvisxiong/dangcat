package org.dangcat.business.settle;

/**
 * 结算服务。
 * @author dangcat
 * 
 */
public interface SettleService
{
    /**
     * 添加结算对象。
     * @param SettleUnit 结算对象。
     */
    public void addSettleUnit(SettleUnit settleUnit);

    /**
     * 执行结算。
     */
    public void execute();

    /**
     * 删除结算对象。
     * @param settleUnit 结算对象。
     */
    public void removeSettleUnit(SettleUnit settleUnit);
}
