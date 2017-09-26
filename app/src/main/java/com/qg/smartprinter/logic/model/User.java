package com.qg.smartprinter.logic.model;

import com.qg.deprecated.logic.model.Printer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by 攀登 on 2016/7/25.
 */
public class User implements Serializable{

    private String userId; // 用户id
    private String userName; // 店主名字
    private String userAccount; // 账户
    private String userPassword; // 密码
    private Integer userPrinters;
    private String userLogo;
    private String userQrcode;
    private String userStore; // 商店名
    private String userAddress; // 商家地址
    private String userPhone; // 商家电话
    private ArrayList<Printer> printers;
    private ArrayList<Integer> printerId;

    public User(String id) {
        this.userId = id;
    }

    public User(String name, String account, String password, String store, String address, String phone, ArrayList<Integer> printerId) {
        this.userName = name;
        this.userAccount = account;
        this.userPassword = password;
        this.userStore = store;
        this.userAddress = address;
        this.userPhone = phone;
        this.printerId = printerId;
    }

    public String getId() {
        return userId;
    }

    public void setId(String id) {
        this.userId = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Integer getUserPrinters() {
        return userPrinters;
    }

    public void setUserPrinters(Integer userPrinters) {
        this.userPrinters = userPrinters;
    }

    public String getUserLogo() {
        return userLogo;
    }

    public void setUserLogo(String userLogo) {
        this.userLogo = userLogo;
    }

    public String getUserQrcode() {
        return userQrcode;
    }

    public void setUserQrcode(String userQrcode) {
        this.userQrcode = userQrcode;
    }

    public String getUserStore() {
        return userStore;
    }

    public void setUserStore(String userStore) {
        this.userStore = userStore;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public ArrayList<Printer> getPrinters() {
        return printers;
    }

    public void setPrinters(ArrayList<Printer> printers) {
        this.printers = printers;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof User) {
            User user = (User) o;
            return userPhone.equals(user.getUserPhone());
        }
        return false;
    }

    @Override
    public String toString() {
        return "userId->" + userId + ";" + "userName->" + userName + ";" + "userPhone->" + userPhone + ";";
    }

    public ArrayList<Integer> getPrinterId() {
        return printerId;
    }

    public void setPrinterId(ArrayList<Integer> printerId) {
        this.printerId = printerId;
    }
}