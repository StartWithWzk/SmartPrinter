package com.qg.deprecated.logic.param;

import com.qg.deprecated.logic.model.Item;

import java.util.ArrayList;

/**
 * Created by 攀登 on 2016/7/31.
 */
public class PlaceOrderParam extends Param {
    private String company;
    //    private String userId;
    private String orderTime;
    private String expectTime;
    private ArrayList<Item> items;
    private String orderRemark;
    private String orderMealFee;
    private String orderDisFee;
    private String orderPreAmount;
    private String orderPayStatus; // 付款状态
    private String userName; // 顾客姓名
    private String userAddress; // 送餐地址
    private String userTelephone; // 顾客电话

    public PlaceOrderParam(String company, String orderTime, String expectTime, ArrayList<Item> items, String orderRemark, String orderMealFee, String orderDisFee,
                           String orderPreAmount, String orderPayStatus, String userName, String userAddress, String userTelephone) {
        this.company = company;
        this.orderTime = orderTime;
        this.expectTime = expectTime;
        this.items = items;
        this.orderRemark = orderRemark;
        this.orderMealFee = orderMealFee;
        this.orderDisFee = orderDisFee;
        this.orderPreAmount = orderPreAmount;
        this.orderPayStatus = orderPayStatus;
        this.userName = userName;
        this.userAddress = userAddress;
        this.userTelephone = userTelephone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getExpectTime() {
        return expectTime;
    }

    public void setExpectTime(String expectTime) {
        this.expectTime = expectTime;
    }

    public ArrayList<Item> getList() {
        return items;
    }

    public void setList(ArrayList<Item> items) {
        this.items = items;
    }

    public String getOrderRemark() {
        return orderRemark;
    }

    public void setOrderRemark(String orderRemark) {
        this.orderRemark = orderRemark;
    }

    public String getOrderMealFee() {
        return orderMealFee;
    }

    public void setOrderMealFee(String orderMealFee) {
        this.orderMealFee = orderMealFee;
    }

    public String getOrderDisFee() {
        return orderDisFee;
    }

    public void setOrderDisFee(String orderDisFee) {
        this.orderDisFee = orderDisFee;
    }

    public String getOrderPreAmount() {
        return orderPreAmount;
    }

    public void setOrderPreAmount(String orderPreAmount) {
        this.orderPreAmount = orderPreAmount;
    }

    public String getOrderPayStatus() {
        return orderPayStatus;
    }

    public void setOrderPayStatus(String orderPayStatus) {
        this.orderPayStatus = orderPayStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserTelephone() {
        return userTelephone;
    }

    public void setUserTelephone(String userTelephone) {
        this.userTelephone = userTelephone;
    }
}
