package com.qg.deprecated.logic.param;

import com.qg.deprecated.logic.model.PrinterId;

import java.util.ArrayList;

/**
 * Created by 攀登 on 2016/7/30.
 */
public class RegisterParam extends Param {

    private String userName; // 店主名字
    private String userAccount; // 账户
    private String userPassword; // 密码
    private String userStore; // 商店名
    private String userAddress; // 商家地址
    private String userPhone; // 商家电话
    private ArrayList<PrinterId> printers;

    public RegisterParam(String name, String account, String password, String store, String address, String phone, ArrayList<String> printerId) {
        this.userName = name;
        this.userAccount = account;
        this.userPassword = password;
        this.userStore = store;
        this.userAddress = address;
        this.userPhone = phone;
        ArrayList<PrinterId> ids = new ArrayList<>(printerId.size());
        for (int i = 0; i < printerId.size(); i++) {
            PrinterId id = new PrinterId(printerId.get(i));
            ids.add(id);
        }
        this.printers = ids;
    }
}
