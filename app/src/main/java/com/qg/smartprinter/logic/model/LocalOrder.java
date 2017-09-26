package com.qg.smartprinter.logic.model;

import android.service.voice.AlwaysOnHotwordDetector;

import com.qg.smartprinter.util.DateUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.qg.smartprinter.util.TextUtil.nullable;

public class LocalOrder implements Serializable {

    public static final String SEPARATOR = "------------------\n";

    private long orderId; //订单编号
    private int tableNumber; // 桌号
    private Date orderTime; // 下单时间
    private ArrayList<OrderContent> contents; // 订单内容 -- 菜名、数量、小计
    private float discount; // 折扣
    private Business business; // 商家信息
    private String paymentMethod; // 支付方式

    private LocalOrder(Builder builder) {
        orderId = builder.orderId;
        business = builder.business;
        tableNumber = builder.tableNumber;
        orderTime = builder.orderTime;
        contents = builder.contents;
        discount = builder.discount;
        paymentMethod = builder.paymentMethod;
    }

    public long getOrderId() {
        return orderId;
    }

    private String format(String name, String count, String m, boolean head) {
        final int l1 = 20;
        final int l2 = 5;
        int length = name.length() * 2;

        int s1 = l1 - length;
        int s2 = head ? l2 - 2 : l2 + 1;

        StringBuilder sb = new StringBuilder();
        sb.append(name);
        space(sb, s1);
        sb.append(count);
        space(sb, s2);
        sb.append(m);
        return sb.toString();
    }

    private void space(StringBuilder sb, int length) {
        for (int i = 0; i < length; i++) {
            sb.append(" ");
        }
    }

    public String getPrintString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("(来自智能打印机APP端)\n")
                .append(SEPARATOR)
                .append("\n")
                .append("商家名称: ").append(nullable(business.getName())).append("\n")
                .append("商家地址: ").append(nullable(business.getAddress())).append("\n")
                .append("商家电话: ").append(nullable(business.getPhone())).append("\n")
                .append(SEPARATOR)
                .append("桌号：").append(tableNumber).append("\n")
                .append("订单编号: ").append(orderId).append("\n")
                .append("餐段：").append(getPeriod()).append("\n")
                .append("下单时间：").append(DateUtils.getDateString(orderTime)).append("\n")
                .append(SEPARATOR);

        buffer.append(format("菜单名", "数量", "小计", true))
                .append("\n");

        int sum = 0;
        for (OrderContent item : contents) {
            buffer.append(format(item.getDishes(), String.valueOf(item.getCount()), String.valueOf(item.getSubtotal()), false));
            buffer.append("\n");
            sum += item.getSubtotal();
        }
        sum -= discount;

        buffer.append(SEPARATOR)
                .append("优惠额: ").append(discount).append("\n")
                .append("合 计: ").append(sum).append("\n")
                .append(SEPARATOR)
                .append("支付方式：").append(nullable(paymentMethod)).append("\n")
                .append(SEPARATOR)
                .append(business.getAdvertisement());

        return buffer.toString();
    }

    private String getPeriod() {
        int hour = DateUtils.getCalendar(orderTime).get(Calendar.HOUR_OF_DAY);
        if (hour < 3 || hour > 22) {
            return "宵夜";
        } else if (hour < 10) {
            return "早餐";
        } else if (hour < 16) {
            return "午餐";
        } else if (hour <= 22) {
            return "晚餐";
        } else {
            return "";
        }
    }

    public static final class Builder {
        private int orderId;
        private Business business;
        private int tableNumber;
        private Date orderTime;
        private ArrayList<OrderContent> contents;
        private float discount;
        private String paymentMethod;

        public Builder() {
            // Default values.
            business = new Business();
            business.setName("");
            business.setAddress("");
            business.setPhone("");
            business.setAdvertisement("");
            orderTime = new Date();
            contents = new ArrayList<>();
            paymentMethod = "";
        }

        public Builder orderId(int val) {
            orderId = val;
            return this;
        }

        public Builder business(Business val) {
            business = val;
            return this;
        }

        public Builder tableNumber(int val) {
            tableNumber = val;
            return this;
        }

        public Builder orderTime(Date val) {
            orderTime = val;
            return this;
        }

        public Builder contents(ArrayList<OrderContent> val) {
            contents = val;
            return this;
        }

        public Builder discount(float val) {
            discount = val;
            return this;
        }

        public Builder paymentMethod(String val) {
            paymentMethod = val;
            return this;
        }

        public LocalOrder build() {
            if (orderId == 0) throw new IllegalStateException("orderId == 0");
            return new LocalOrder(this);
        }
    }

}
