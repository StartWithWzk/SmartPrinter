package com.qg.deprecated.logic.model;

import java.io.Serializable;

/**
 * Created by 攀登 on 2016/8/1.
 */
public class DoneOrder implements Serializable {
    public UserOrderInView order;
    public String company;
    public String expectTime;
    public String orderTime;
    public String orderRemark;
    public int orderMealFee;
    public int orderDisFee;
    public int orderPreAmount;
    public String userName;
    public String userAddress;
    public String userTelephone;

    public DoneOrder() {
    }

    public DoneOrder(UserOrderInView order, String company, String expectTime, String orderTime, String orderRemark, int orderMealFee, int orderDisFee, int orderPreAmount, String
            userName, String userAddress, String userTelephone) {
        this.order = order;
        this.company = company;
        this.expectTime = expectTime;
        this.orderTime = orderTime;
        this.orderRemark = orderRemark;
        this.orderMealFee = orderMealFee;
        this.orderDisFee = orderDisFee;
        this.orderPreAmount = orderPreAmount;
        this.userName = userName;
        this.userAddress = userAddress;
        this.userTelephone = userTelephone;
    }
}
