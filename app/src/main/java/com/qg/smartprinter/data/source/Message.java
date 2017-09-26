package com.qg.smartprinter.data.source;

import com.google.gson.Gson;

import java.util.UUID;

/**
 * 接收的报文
 *
 * @author TZH
 * @version 1.0
 */
public class Message {

    // 报文编号
    private String mId;

    // 报文内容
    private byte[] mContent;

    // 报文类型
    private String mType;

    // 接收时间
    private String mTime;

    // 对应的订单编号
    private String mOrderId;


    public Message(byte[] content, String type, String time, String orderId) {
        this(UUID.randomUUID().toString(), content, type, time, orderId);
    }

    public Message(String id, byte[] content, String type, String time, String orderId) {
        mId = id;
        mContent = content;
        mType = type;
        mTime = time;
        mOrderId = orderId;
    }

    public String getId() {
        return mId;
    }

    @Override
    public String toString() {
        return "订单ID：" + mOrderId + "\n"
                + "类型：" + mType + "\n"
                + "时间：" + mTime + "\n"
//              +  "报文：" + new Gson().toJson(mContent)
                ;
    }

    public void setId(String id) {
        mId = id;
    }

    public byte[] getContent() {
        return mContent;
    }

    public void setContent(byte[] content) {
        mContent = content;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getOrderId() {
        return mOrderId;
    }

    public void setOrderId(String orderId) {
        mOrderId = orderId;
    }
}

