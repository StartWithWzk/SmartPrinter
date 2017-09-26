package com.qg.smartprinter.localorder.messages;

import com.qg.smartprinter.localorder.util.BytesConvert;
import com.qg.smartprinter.localorder.util.CheckSumUtil;

import static com.qg.smartprinter.localorder.messages.BResponse.BYTE_COUNT;

/**
 * @author TZH
 * @version 1.0
 */
public class OrderAcceptableResponse extends AbstractMessage {

    public static final byte TYPE_TOKEN = (byte) 0b111; // 类型标志

    private long printerId; // 主控板ID

    private long ip;

    private long retain; // 保留字段

    public OrderAcceptableResponse(byte[] bytes) {
        super(bytes);
        printerId = line1;
        ip = line2;
        retain = line3;
    }

    public byte[] toRealBytes() {
        checkSum = 0;
        checkSum = CheckSumUtil.checkSum(toBytes());
        return toBytes();
    }

    private byte[] toBytes() {
        byte[] bytes = new byte[BYTE_COUNT];
        int position = 0;
        position = BytesConvert.fill2BytesInt(start, bytes, position);
        position = BytesConvert.fill2BytesInt(flag, bytes, position);
        position = BytesConvert.fill4BytesLong(printerId, bytes, position);
        position = BytesConvert.fill4BytesLong(ip, bytes, position);
        position = BytesConvert.fill4BytesLong(retain, bytes, position);
        position = BytesConvert.fill2BytesInt(checkSum, bytes, position);
        BytesConvert.fill2BytesInt(end, bytes, position);
        return bytes;
    }

    public static OrderAcceptableResponse bytesToResponse(byte[] readBytes) {
        return new OrderAcceptableResponse(readBytes);
    }

    public int getIP() {
        return (int) ip;
    }
}
