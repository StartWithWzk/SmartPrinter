package com.qg.smartprinter.localorder.messages;

import com.qg.smartprinter.localorder.util.BytesConvert;
import com.qg.smartprinter.localorder.util.Charsets;

/**
 * 二维码
 */
public final class BQRCode implements BDatagram {

    public static final int START = (short) 0x7FFF;
    public static final int PADDING = (short) 0;
    public static final int END = (short) 0xFF7F;

    private int length;
    private byte[] data;

    public BQRCode(String text) {
        this(text.getBytes(Charsets.URL_QR_CODE_CHARSET));
    }

    public BQRCode(byte[] bytes) {
        this.data = BytesConvert.getFilledBytes(bytes);
        this.length = (short) this.data.length;
    }

    @Override
    public byte[] toBytes() {
        byte[] bytes = new byte[length + 8];

        int position = 0;
        position = BytesConvert.fill2BytesInt(START, bytes, position);
        position = BytesConvert.fill2BytesInt(length, bytes, position);
        position = BytesConvert.fillByte(data, bytes, position);
        position = BytesConvert.fill2BytesInt(PADDING, bytes, position);
        BytesConvert.fill2BytesInt(END, bytes, position);

        return bytes;
    }

}
