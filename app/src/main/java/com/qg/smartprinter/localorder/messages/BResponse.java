package com.qg.smartprinter.localorder.messages;

import com.qg.smartprinter.localorder.util.BytesConvert;
import com.qg.smartprinter.localorder.util.CheckSumUtil;
import com.qg.smartprinter.util.NetworkUtils;

/**
 * 状态应答（本地应答）
 * Created by TZH on 2016/7/30.
 */
public class BResponse extends AbstractMessage {

    public static final byte TYPE_TOKEN = (byte) 0b101; // 类型标志

    public static final int TOKEN_PRINTER_STATUS = 1;

    public static final int TOKEN_ORDER_STATUS = 0;

    public static final int BYTE_COUNT = 5 * 4;

    public long printerId; // 主控板id

    public long responseType;// 应答类型

    public long responseNum; //应答序号

    public BResponse(long printerId, long responseType, long responseNum) {
        this.printerId = printerId;
        this.responseType = responseType;
        this.responseNum = responseNum;
        this.checkSum = 0;
        setStatusToken(TYPE_TOKEN);
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
        position = BytesConvert.fill4BytesLong(responseType, bytes, position);
        position = BytesConvert.fill4BytesLong(responseNum, bytes, position);
        position = BytesConvert.fill2BytesInt(checkSum, bytes, position);
        BytesConvert.fill2BytesInt(end, bytes, position);
        return bytes;
    }

    public static BResponse bytesToResponse(byte[] bytes) {
        AbstractMessage status = AbstractMessage.bytesToAbstractStatus(bytes);
        BResponse response = new BResponse(status.line1, status.line2, status.line3);
        response.checkSum = status.checkSum;
        return response;

    }

    public static byte[] newCheckedSignal(long printerId, long responseType, long responseNum) {
        BResponse response = new BResponse(printerId, responseType, responseNum);
        return (response.toRealBytes());
    }

    @Override
    public String toString() {
        return "{ "
                + "主控板ID:" + printerId + ", "
                + "应答类型:" + getResponseTypeString() + ", "
                + "应答编号:" + responseNum
                + " }";
    }

    public long getResponseType() {
        return responseType;
    }

    public long getResponseNum() {
        return responseNum;
    }

    public String getResponseTypeString() {
        switch ((int) responseType) {
            case TOKEN_ORDER_STATUS:
                return "订单状态";
            case TOKEN_PRINTER_STATUS:
                return "打印机状态";
            default:
                return "IP?" + NetworkUtils.intToInetAddress((int) responseType);
        }
    }

}
