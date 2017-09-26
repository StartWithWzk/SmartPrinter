package com.qg.smartprinter.localorder.messages;

import com.qg.smartprinter.localorder.util.BytesConvert;

import java.util.Arrays;

public class AbstractMessage {
    public int start = 0xCFFC;

    public int flag;

    public long line1;

    public long line2;

    public long line3;

    public int checkSum;

    public int end = 0xFCCF;

    AbstractMessage() {
    }

    public static AbstractMessage bytesToAbstractStatus(byte[] bytes) {
        return new AbstractMessage(bytes);
    }

    public AbstractMessage(byte[] bytes) {
        start = BytesConvert.intFrom2Bytes(Arrays.copyOfRange(bytes, 0, 2));
        flag = BytesConvert.intFrom2Bytes(Arrays.copyOfRange(bytes, 2, 4));
        line1 = BytesConvert.longFrom4Bytes(Arrays.copyOfRange(bytes, 4, 8));
        line2 = BytesConvert.longFrom4Bytes(Arrays.copyOfRange(bytes, 8, 12));
        line3 = BytesConvert.longFrom4Bytes(Arrays.copyOfRange(bytes, 12, 16));
        checkSum = BytesConvert.intFrom2Bytes(Arrays.copyOfRange(bytes, 16, 18));
        end = BytesConvert.intFrom2Bytes(Arrays.copyOfRange(bytes, 18, 20));
    }

    public byte getStatusToken() {
        return (byte) ((flag & 0xFF00) >> (5 + 8)); // >> 13bit
    }

    public void setStatusToken(byte token) {
        flag &= 0x1fff; // 13bit
        flag |= token << (5 + 8); // << 13bit
    }

}
