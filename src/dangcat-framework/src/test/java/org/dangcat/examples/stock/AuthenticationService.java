package org.dangcat.examples.stock;

public interface AuthenticationService {
    SettleService getSettleService();

    UserInfoService getUserInfoService();
}
