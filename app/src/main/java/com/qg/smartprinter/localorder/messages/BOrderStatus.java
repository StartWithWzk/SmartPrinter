package com.qg.smartprinter.localorder.messages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单状态
 */
public final class BOrderStatus extends AbstractMessage implements Serializable {

    public static final byte TYPE_TOKEN = (byte) 0b001;  // 类型标志

    private int sta; // 订单状态

    private long printerId;   // 主控板id

    public long seconds;

    public long bulkId;    // 批次id ; 低16bit
    public long inNumber;  // 批次内序号; 高16bit
    public long orderId;

    public static BOrderStatus bytesToOrderStatus(byte[] bytes) {
        AbstractMessage status = AbstractMessage.bytesToAbstractStatus(bytes);
        BOrderStatus bos = new BOrderStatus();
        bos.sta = (bytes[3] & 0xFF);
        bos.flag = status.flag;
        bos.printerId = status.line1;
        bos.seconds = status.line2;
        bos.bulkId = (status.line3 & 0xFFFF);
        bos.inNumber = ((status.line3 >> 16) & 0xFFFF);
        bos.orderId = status.line3;
        bos.checkSum = status.checkSum;
        return bos;
    }

    @Override
    public String toString() {
        return "{ "
                + "主控板ID:" + printerId + ", \n"
                + "订单ID:" + orderId + ", \n"
                + "状态:" + getStatusString() + "\n"
                + " }";
    }

    public long getPrinterId() {
        return printerId;
    }

    public int getStatus() {
        return sta;
    }

    public String getStatusString() {
        return ORDER_STATUS_MAP.get(sta);
    }

    private static final Map<Integer, String> ORDER_STATUS_MAP = new HashMap<>();

    /*
     * a)0为普通订单打印成功
     * b)1为普通订单打印出错：打印机异常
     * c)2为普通订单进入打印队列
     * d)3为普通订单开始打印
     * e)4为普通订单订单数据解析错误
     * f)5为前异常订单打印成功
     * g)6为前异常订单打印出错：打印机异常
     * h)7为前异常订单进入打印队列
     * i)8为前异常订单开始打印
     * j)9为前异常订单数据解析错误
     */
    public static final int PRINT_SUCCESS = 0x00;
    public static final int PRINT_ERROR = 0x01;
    public static final int PRINT_ENQUEUE = 0x02;
    public static final int PRINT_START = 0x03;
    public static final int ORDER_DATA_ERROR = 0x04;
    public static final int PRINT_SUCCESS_PRE_EXCEPTION_ORDER = 0x05;
    public static final int PRINT_ERROR_PRE_EXCEPTION_ORDER = 0x06;
    public static final int PRINT_ENQUEUE_PRE_EXCEPTION_ORDER = 0x07;
    public static final int PRINT_START_PRE_EXCEPTION_ORDER = 0x08;
    public static final int ORDER_DATA_ERROR_PRE_EXCEPTION_ORDER = 0x09;

    static {
        ORDER_STATUS_MAP.put(PRINT_SUCCESS, "打印成功");
        ORDER_STATUS_MAP.put(PRINT_ERROR, "打印出错");
        ORDER_STATUS_MAP.put(PRINT_ENQUEUE, "进入打印队列");
        ORDER_STATUS_MAP.put(PRINT_START, "开始打印");
        ORDER_STATUS_MAP.put(ORDER_DATA_ERROR, "订单数据解析错误");

        ORDER_STATUS_MAP.put(PRINT_SUCCESS_PRE_EXCEPTION_ORDER, "前异常订单打印成功");
        ORDER_STATUS_MAP.put(PRINT_ERROR_PRE_EXCEPTION_ORDER, "前异常订单打印出错");
        ORDER_STATUS_MAP.put(PRINT_ENQUEUE_PRE_EXCEPTION_ORDER, "前异常订单进入打印队列");
        ORDER_STATUS_MAP.put(PRINT_START_PRE_EXCEPTION_ORDER, "前异常订单开始打印");
        ORDER_STATUS_MAP.put(ORDER_DATA_ERROR_PRE_EXCEPTION_ORDER, "前异常订单数据解析错误");
    }

}
