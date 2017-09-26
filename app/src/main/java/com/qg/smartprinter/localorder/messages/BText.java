package com.qg.smartprinter.localorder.messages;

import com.qg.smartprinter.localorder.util.BytesConvert;
import com.qg.smartprinter.localorder.util.Charsets;

/**
 * 文本
 */
public final class BText implements BDatagram{

    public static final int START= (short) 0x7EFF;
    public static final int END= (short) 0xFF7E;
    private String mText;

    public int length;

    public byte[] data;

    public int padding;

    public BText(String text) {
        this((text + "\n").getBytes(Charsets.PRINTER_CHARSET));
        mText = text;
    }

    private BText(byte[] bytes) {
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

        position = BytesConvert.fill2BytesInt(padding, bytes, position);

        BytesConvert.fill2BytesInt(END, bytes, position);

        return bytes;
    }

    public String getText() {
        return mText;
    }
}
