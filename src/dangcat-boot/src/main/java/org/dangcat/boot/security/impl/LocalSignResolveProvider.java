package org.dangcat.boot.security.impl;

import org.dangcat.boot.security.SecurityUtils;
import org.dangcat.commons.crypto.AESUtils;
import org.dangcat.commons.crypto.MD5Utils;
import org.dangcat.commons.formator.DateType;
import org.dangcat.commons.serialize.json.JsonDeserializer;
import org.dangcat.commons.serialize.json.JsonSerializer;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.ValueUtils;

import java.util.Date;
import java.util.Map;

/**
 * 本地认证签名计算器。
 * 
 */
public class LocalSignResolveProvider implements SignResolveProvider
{
    private static final String SECURITY_NAME = "LocalSecurity";
    private Map<String, LoginUser> localeUserMap = null;

    public LocalSignResolveProvider(Map<String, LoginUser> localeUserMap)
    {
        this.localeUserMap = localeUserMap;
    }

    private String createPassWord(String content)
    {
        return content + DateUtils.format(new Date(), DateType.Hour);
    }

    @Override
    public String createSignId(LoginUser loginUser)
    {
        String remoteHost = SecurityUtils.getRemoteHost();
        SignInfo signInfo = new SignInfo();
        signInfo.setNo(loginUser.getNo());
        signInfo.setKey(this.createSignKey(loginUser, remoteHost));
        String serializeResult = JsonSerializer.serialize(signInfo);
        String password = this.createPassWord(remoteHost);
        return AESUtils.encrypt(serializeResult, password);
    }

    private String createSignKey(LoginUser loginUser, String remoteHost)
    {
        return MD5Utils.encrypt(loginUser.getNo(), remoteHost, SECURITY_NAME, loginUser.getPassword());
    }

    @Override
    public LoginUser parseLoginUser(String signId)
    {
        String remoteHost = SecurityUtils.getRemoteHost();
        LoginUser loginUser = null;
        if (!ValueUtils.isEmpty(remoteHost))
        {
            String password = this.createPassWord(remoteHost);
            String serializeResult = AESUtils.decrypt(signId, password);
            if (!ValueUtils.isEmpty(serializeResult))
            {
                SignInfo signInfo = JsonDeserializer.deserializeObject(serializeResult, SignInfo.class);
                if (signInfo != null && signInfo.isValid())
                {
                    LoginUser localLoginUser = this.localeUserMap.get(signInfo.getNo());
                    if (localLoginUser != null)
                    {
                        String sourcePassword = this.createSignKey(localLoginUser, remoteHost);
                        if (ValueUtils.compare(sourcePassword, signInfo.getKey()) == 0)
                            loginUser = localLoginUser;
                    }
                }
            }
        }
        return loginUser;
    }
}
