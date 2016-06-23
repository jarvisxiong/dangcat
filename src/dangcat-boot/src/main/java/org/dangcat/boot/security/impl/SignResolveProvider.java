package org.dangcat.boot.security.impl;

/**
 * 签名解析接口。
 */
public interface SignResolveProvider
{
    /**
     * 登录信息转化成签名字串。
     * @param loginUser 登录信息。
     * @param clientIp 客户端地址。
     * @return 签名字串。
     */
    String createSignId(LoginUser loginUser);

    /**
     * 签名字串转换成登录信息。
     * @param signId 签名字串。
     * @param clientIp 客户端地址。
     * @return 登录信息。
     */
    LoginUser parseLoginUser(String signId);
}
