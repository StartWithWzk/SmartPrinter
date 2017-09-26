package com.qg.smartprinter.data.source;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.qg.smartprinter.localorder.messages.BOrder;
import com.qg.smartprinter.util.DateUtils;

/**
 * 发送的订单
 *
 * @author TZH
 * @version 1.0
 */
public class Order {

    // 订单编号
    private String mId;

    // 订单内容
    private String mContent;

    // 订单时间信息
    private String mTime;

    // 订单类型
    private String mType;

    // 订单状态
    private String mStatus;

    //订单发送报文
    private byte[] mMessage;

    public Order(String id, String content, String time, String type, byte[] message, String status) {
        mId = id;
        mContent = content;
        mTime = time;
        mType = type;
        mMessage = message;
        mStatus = status;
    }

    @Override
    public String toString() {
        return "ID：" + mId + "\n" +
                "时间：" + mTime + "\n" +
                "类型：" + getType() + "\n" +
                "状态：" + mStatus + "\n"
                ;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public byte[] getMessage() {
        return mMessage;
    }

    public void setMessage(byte[] message) {
        mMessage = message;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public static Order fromBOrder(BOrder o) {
        return new Order(
                String.valueOf(o.getOrderNumber()),
                new Gson().toJson(o.getData()),
                DateUtils.getDateString(),
                o.getType(),
                o.toRealBytes(),
                WAIT_SEND
        );
    }

    public static final String REQUEST = "请求发送";
    public static final String SEND_OR_RESEND = "发送/重传";
    public static final String WAIT_SEND = "等待发送";
    public static final String WAIT_RECEIVE = "发送中(等待应答)";
    public static final String RECEIVE_SUCCESS = "发送成功(收到应答)";
    public static final String UNFINISHED = "打印未完成(异常断开)";


    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_BT = 1;
    public static final int TYPE_WIFI = 2;

    public static int getTargetDeviceType(@NonNull String type) {
        switch (type) {
            case BOrder.BT_ORDER_STR:
            case BOrder.BT_CORR_ORDER_STR:
                return TYPE_BT;
            case BOrder.WIFI_ORDER_STR:
            case BOrder.WIFI_CORR_ORDER_STR:
                return TYPE_WIFI;
            default:
                return TYPE_UNKNOWN;
        }
    }
}


