package com.qg.smartprinter.logic.model;

import java.io.Serializable;

/**
 * 订单内容
 */
public class OrderContent implements Serializable {
    private String dishes;
    private int count;
    private int subtotal;

    public OrderContent(String dishes, int count, int subtotal) {
        this.dishes = dishes;
        this.count = count;
        this.subtotal = subtotal;
    }

    public String getDishes() {
        return dishes;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSubtotal() {
        return subtotal;
    }

    @Override
    public String toString() {
        return dishes + TAB1 + count + TAB2 + subtotal;
    }

    public static final String TAB1 = "       ";
    public static final String TAB2 = "       ";
}
