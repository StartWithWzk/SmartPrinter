package com.qg.smartprinter.localorder.messages;

import com.qg.smartprinter.localorder.util.BytesConvert;
import com.qg.smartprinter.util.BinaryUtil;

/**
 * 图片
 */
public final class BPhoto implements BDatagram{

    public static final short START = (short) 0xFF7E;
    public static final short END = (short) 0x7EFF;

    public int length;

    public byte[] data;

    public int contentLength; // 图片内容实际字节数

    public BPhoto(byte[] bytes) {
        this.contentLength = bytes.length;
        this.data = BytesConvert.getFilledBytes(bytes);
        this.length = this.data.length;
    }


    /**
     * 从RGB图像得到封装后的表单
     */
    public static BPhoto fromPixels(int[][] pixels) {
        return new BPhoto(BinaryUtil.getDatagram(pixels));
    }

    @Override
    public byte[] toBytes() {
        byte[] bytes = new byte[length + 8];
        int position = 0;
        position = BytesConvert.fill2BytesInt(START, bytes, position);
        position = BytesConvert.fill2BytesInt(length, bytes, position);
        position = BytesConvert.fillByte(data, bytes, position);
        position = BytesConvert.fill2BytesInt(contentLength, bytes, position);
        BytesConvert.fill2BytesInt(END, bytes, position);

        return bytes;
    }


}
