package org.dangcat.business.staff.security;

import org.dangcat.boot.security.SecurityUtils;
import org.dangcat.boot.security.impl.LoginUser;
import org.dangcat.boot.security.impl.SignInfo;
import org.dangcat.boot.security.impl.SignResolveProvider;
import org.dangcat.commons.crypto.AESUtils;
import org.dangcat.commons.crypto.MD5Utils;
import org.dangcat.commons.formator.DateType;
import org.dangcat.commons.serialize.json.JsonDeserializer;
import org.dangcat.commons.serialize.json.JsonSerializer;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.ValueUtils;

import java.util.Date;

/**
 * 操作员认证签名计算器。
 */
public class OperatorSignResolveProvider implements SignResolveProvider {
    private static final String SECURITY_NAME = "OperatorSecurity";
    private OperatorSecurityServiceImpl operatorSecurityService = null;

    public OperatorSignResolveProvider(OperatorSecurityServiceImpl operatorSecurityService) {
        this.operatorSecurityService = operatorSecurityService;
    }

    private String createPassWord(String content) {
        return content + DateUtils.format(new Date(), DateType.Hour);
    }

    @Override
    public String createSignId(LoginUser loginUser) {
        String remoteHost = SecurityUtils.getRemoteHost();
        SignInfo signInfo = new SignInfo();
        signInfo.setNo(loginUser.getNo());
        signInfo.setKey(this.createSignKey(loginUser.getNo(), loginUser.getPassword(), remoteHost));
        String serializeResult = JsonSerializer.serialize(signInfo);
        String password = this.createPassWord(remoteHost);
        return AESUtils.encrypt(serializeResult, password);
    }

    private String createSignKey(String no, String password, String clientIp) {
        return MD5Utils.encrypt(no, clientIp, SECURITY_NAME, password);
    }

    @Override
    public LoginUser parseLoginUser(String signId) {
        String remoteHost = SecurityUtils.getRemoteHost();
        LoginUser loginUser = null;
        if (!ValueUtils.isEmpty(remoteHost)) {
            String password = this.createPassWord(remoteHost);
            String serializeResult = AESUtils.decrypt(signId, password);
            if (!ValueUtils.isEmpty(serializeResult)) {
                SignInfo signInfo = JsonDeserializer.deserializeObject(serializeResult, SignInfo.class);
                if (signInfo != null && signInfo.isValid()) {
                    LoginOperator loginOperator = this.operatorSecurityService.loadLoginOperator(signInfo.getNo());
                    if (loginOperator != null) {
                        String sourcePassword = this.createSignKey(loginOperator.getNo(), loginOperator.getPassword(), remoteHost);
                        if (ValueUtils.compare(sourcePassword, signInfo.getKey()) == 0)
                            loginUser = this.operatorSecurityService.createLoginUser(loginOperator);
                    }
                }
            }
        }
        return loginUser;
    }
}
