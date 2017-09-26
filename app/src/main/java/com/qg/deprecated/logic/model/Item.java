package com.qg.deprecated.logic.model;

/**
 * 订单内容
 */
public class Item {
    private String name;
    private String price;
    private String count;

    public Item() {
    }

    public Item(String name, String price, String count) {
        this.name = name;
        this.price = price;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public float getCost() {
        return Integer.valueOf(price) * Integer.valueOf(count);
    }

    @Override
    public String toString() {
        return getName() + "   " + getCount() + "  " + getCost();
    }
}