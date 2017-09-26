package com.qg.smartprinter.localorder.util;

import android.support.annotation.VisibleForTesting;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 由于传输的数据都应该是无符号数，所以使用更大的类型作为存储类型
 */
public class BytesConvert {

    public static int intFrom2Bytes(byte[] bytes) {
        if (bytes.length < 2) {
            return -1;
        }
        return ByteBuffer.allocate(4).putShort((short) 0).put(bytes, 0, 2).getInt(0);
    }

    public static long longFrom4Bytes(byte[] bytes) {
        if (bytes.length < 4) {
            return -1;
        }
        return ByteBuffer.allocate(8).putInt(0).put(bytes, 0, 4).getLong(0);
    }

    @VisibleForTesting
    public
    static byte[] intTo4Bytes(int number) {
        return ByteBuffer.allocate(4).putInt(number).array();
    }

    @VisibleForTesting
    public
    static byte[] shortTo2Bytes(short number) {
        return ByteBuffer.allocate(2).putShort(number).array();
    }

    public static int fill2BytesInt(int number, byte[] bytes, int start) {
        byte[] b = shortTo2Bytes((short) number);

        System.arraycopy(b, 0, bytes, start, b.length);

        return start + b.length;
    }

    public static int fill4BytesLong(long number, byte[] bytes, int start) {
        byte[] b = intTo4Bytes((int) number);

        System.arraycopy(b, 0, bytes, start, b.length);

        return start + b.length;
    }

    public static int fillByte(byte[] srcByte, byte[] bytes, int start) {
        System.arraycopy(srcByte, 0, bytes, start, srcByte.length);

        return start + srcByte.length;
    }

    /**
     * 将byte数组按4字节对齐
     */
    public static byte[] getFilledBytes(byte[] bytes) {
        //获取内容的长度
        int length = bytes.length;
        //因为要字节对齐,以4字节为为单位,所以计算要填充多少位字节
        int fillLength = 4 - (length % 4);
        fillLength %= 4;
        //创建字节数组,大小为数据长度
        byte[] data = new byte[length + fillLength];
        Arrays.fill(data, (byte) 0);
        //填充数据
        BytesConvert.fillByte(bytes, data, 0);
        return data;
    }

}
