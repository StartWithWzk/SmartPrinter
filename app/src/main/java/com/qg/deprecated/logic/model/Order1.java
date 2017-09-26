package com.qg.deprecated.logic.model;

/**
 * Created by 攀登 on 2016/7/31.
 */
public class Order1 {
    private int id;
    /**
     * 0-打印成功
     * 1-打印失败
     * 2-进入打印队列
     * 3-开始打印
     * 4-数据错误
     * 5-打印成功-之前的异常订单
     * 6-新来的订单还未发送
     */
    private String orderStatus;

    public Order1() {
    }

    public Order1(int id, String orderStatus) {
        this.id = id;
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
