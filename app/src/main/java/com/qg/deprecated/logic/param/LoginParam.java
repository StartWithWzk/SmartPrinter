package com.qg.deprecated.logic.param;

/**
 * Created by 攀登 on 2016/7/30.
 */
public class LoginParam extends Param {
    private String userAccount;
    private String userPassword;

    public LoginParam(String account, String password) {
        this.userAccount = account;
        this.userPassword = password;
    }
}
