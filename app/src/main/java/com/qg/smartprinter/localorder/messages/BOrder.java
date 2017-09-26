package com.qg.smartprinter.localorder.messages;

import android.support.annotation.IntDef;

import com.google.gson.Gson;
import com.qg.smartprinter.data.source.Order;
import com.qg.smartprinter.localorder.util.BytesConvert;
import com.qg.smartprinter.localorder.util.CheckSumUtil;

import java.io.Serializable;

/**
 * 订单
 */
public class BOrder implements Serializable {

    public static final int START_ORDER = 0x3E11;
    public int length;
    public long printerId;   // 主控板id
    /**
     * 时间戳/IP地址
     */
    public long stamp;
    public long orderNumber;
    public int bulkId;
    public int inNumber;
    public int checkNum;
    /**
     * 保留字段，代表订单的类型
     */
    public int retainField;

    public byte[] data;
    public int padding;
    public static final int END_ORDER = 0x11E3;

    // 生成时间
    private long generateTime;

    private BOrder() {
        generateTime = System.currentTimeMillis();
    }

    public static BOrder fromOrder(Order order, long printerId) {
        long orderNumber = Integer.valueOf(order.getId());
        byte[] data = new Gson().fromJson(order.getContent(), byte[].class);
        return fromOrderData(orderNumber, data, printerId);
    }

    public static BOrder fromOrderData(long orderNumber, BOrderData orderData, long printerId) {
        byte[] data = orderData.toBytes();
        return fromOrderData(orderNumber, data, printerId);
    }

    public static BOrder fromOrderData(long orderNumber, byte[] data, long printerId) {
        BOrder bo = new BOrder();
        //设置主控板id
        bo.setPrinterId(printerId);
        //设置时间戳
        bo.setStamp(System.currentTimeMillis());
        //设置订单序号
        bo.setOrderNumber(orderNumber);
        //设置批次,批次内序号
        bo.setBulkId((int) (orderNumber >> 16));
        bo.setInNumber((int) orderNumber);
        //设置校验和
        bo.setCheckNum(0);
        //设置数据域,数据长度
        int length = data.length;
        bo.setData(data);
        bo.setLength(length);
        //设置填充
        bo.setPadding(0);
        return bo;
    }


    /**
     * 计算校验和，并转换为字节数组
     */
    public byte[] toRealBytes() {
        checkNum = 0;
        checkNum = CheckSumUtil.checkSum(toBytes());
        return toBytes();
    }

    /**
     * 不作任何处理，转换成字节数组
     */
    private byte[] toBytes() {
        byte[] bytes = new byte[length + 28];
        int position = 0;
        position = BytesConvert.fill2BytesInt(START_ORDER, bytes, position);
        position = BytesConvert.fill2BytesInt(length, bytes, position);
        position = BytesConvert.fill4BytesLong(printerId, bytes, position);
        position = BytesConvert.fill4BytesLong(stamp, bytes, position);
        position = BytesConvert.fill4BytesLong(orderNumber, bytes, position);
        position = BytesConvert.fill2BytesInt(bulkId, bytes, position);
        position = BytesConvert.fill2BytesInt(inNumber, bytes, position);
        position = BytesConvert.fill2BytesInt(checkNum, bytes, position);
        position = BytesConvert.fill2BytesInt(retainField, bytes, position);
        position = BytesConvert.fillByte(data, bytes, position);
        position = BytesConvert.fill2BytesInt(padding, bytes, position);
        BytesConvert.fill2BytesInt(END_ORDER, bytes, position);
        return bytes;
    }

    public long getGenerateTime() {
        return generateTime;
    }

    public long getBytesLength() {
        return length + 28;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getPrinterId() {
        return printerId;
    }

    public void setPrinterId(long id) {
        this.printerId = id;
    }

    public long getStamp() {
        return stamp;
    }

    public void setStamp(long stamp) {
        this.stamp = stamp;
    }

    public long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getBulkId() {
        return bulkId;
    }

    public void setBulkId(int bulkId) {
        this.bulkId = bulkId;
    }

    public int getInNumber() {
        return inNumber;
    }

    public void setInNumber(int inNumber) {
        this.inNumber = inNumber;
    }

    public int getCheckNum() {
        return checkNum;
    }

    public void setCheckNum(int checkNum) {
        this.checkNum = checkNum;
    }


    @IntDef({NETWORK_ORDER, BT_ORDER, NETWORK_CORR_ORDER, BT_CORR_ORDER, WIFI_ORDER, WIFI_CORR_ORDER})
    @interface RetainField {
    }

    public static final int NETWORK_ORDER = 0x00;
    public static final int BT_ORDER = 0x01;
    public static final int NETWORK_CORR_ORDER = 0x02;
    public static final int BT_CORR_ORDER = 0x03;
    public static final int WIFI_ORDER = 0x04;
    public static final int WIFI_CORR_ORDER = 0x05;

    public static final String NETWORK_ORDER_STR = "网络初始订单";
    public static final String BT_ORDER_STR = "蓝牙初始订单";
    public static final String NETWORK_CORR_ORDER_STR = "网络异常修正订单";
    public static final String BT_CORR_ORDER_STR = "蓝牙异常修正订单";
    public static final String WIFI_ORDER_STR = "Wi-fi初始订单";
    public static final String WIFI_CORR_ORDER_STR = "Wi-fi异常修正订单";
    public static final String UNKNOWN_STR = "未知";

    public void setRetainField(@RetainField int retainField) {
        this.retainField = retainField;
    }

    @RetainField
    public int getRetainField() {
        return retainField;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding1) {
        this.padding = padding1;
    }


    public static String getTypeString(int retainField) {
        switch (retainField) {
            case NETWORK_ORDER:
                return NETWORK_ORDER_STR;
            case BT_ORDER:
                return BT_ORDER_STR;
            case WIFI_ORDER:
                return WIFI_ORDER_STR;
            case NETWORK_CORR_ORDER:
                return NETWORK_CORR_ORDER_STR;
            case BT_CORR_ORDER:
                return BT_CORR_ORDER_STR;
            case WIFI_CORR_ORDER:
                return WIFI_CORR_ORDER_STR;
            default:
                return UNKNOWN_STR;
        }
    }

    public String getType() {
        return getTypeString(retainField);
    }
}

