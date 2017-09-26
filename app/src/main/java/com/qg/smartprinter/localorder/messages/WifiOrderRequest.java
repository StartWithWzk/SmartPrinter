package com.qg.smartprinter.localorder.messages;

import android.support.annotation.VisibleForTesting;

import com.qg.smartprinter.localorder.util.BytesConvert;
import com.qg.smartprinter.localorder.util.CheckSumUtil;

import static com.qg.smartprinter.localorder.messages.BResponse.BYTE_COUNT;

/**
 * @author TZH
 * @version 1.0
 */
public class WifiOrderRequest extends AbstractMessage {

    public static final byte TYPE_TOKEN = (byte) 0b110; // 类型标志

    private long orderTime; // 订单生成时间

    private int ip;

    private long orderLength; // 请求订单长度

    public WifiOrderRequest(long orderTime, int ip, long orderLength) {
        this.orderTime = orderTime;
        this.ip = ip;
        this.orderLength = orderLength;
        setStatusToken(TYPE_TOKEN);
    }

    public static WifiOrderRequest fromOrder(BOrder order, int ip) {
        return new WifiOrderRequest(order.getGenerateTime(), ip, order.getBytesLength());
    }

    public static byte[] newRequestSignal(BOrder order, int ip) {
        WifiOrderRequest request = fromOrder(order, ip);
        return (request.toRealBytes());
    }


    public byte[] toRealBytes() {
        checkSum = 0;
        checkSum = CheckSumUtil.checkSum(toBytes());
        return toBytes();
    }

    @VisibleForTesting
    public byte[] toBytes() {
        byte[] bytes = new byte[BYTE_COUNT];
        int position = 0;
        position = BytesConvert.fill2BytesInt(start, bytes, position);
        position = BytesConvert.fill2BytesInt(flag, bytes, position);
        position = BytesConvert.fill4BytesLong(orderTime, bytes, position);
        position = BytesConvert.fill4BytesLong(ip, bytes, position);
        position = BytesConvert.fill4BytesLong(orderLength, bytes, position);
        position = BytesConvert.fill2BytesInt(checkSum, bytes, position);
        BytesConvert.fill2BytesInt(end, bytes, position);
        return bytes;
    }

}
