package org.dangcat.examples.stock;

public interface AuthenticationService
{
    public SettleService getSettleService();

    public UserInfoService getUserInfoService();
}
