package com.qg.smartprinter.util;

import java.util.regex.Pattern;

public class FormatChecker {
    /**
     * 联系方式验证
     */
    public boolean isContact(String s) {
        return isMobile(s) || isPhone(s) || checkEmail(s);
    }

    /**
     * 手机号验证
     */
    private boolean isMobile(String str) {
        return match(str, "^[1][3,4,5,7,8][0-9]{9}$");
    }

    /**
     * 电话号码验证
     */
    private boolean isPhone(String str) {
        return match(str, "^[0,8][0-9]{2,3}-[0-9]{5,10}$"); // 验证带区号的
    }

    /**
     * 验证输入的邮箱格式是否符合
     */
    private boolean checkEmail(String email) {
        return match(email, "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    }

    private boolean match(String str, String pattern) {
        return Pattern.compile(pattern).matcher(str).matches();
    }
}
