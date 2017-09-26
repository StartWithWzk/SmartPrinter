package com.qg.smartprinter.localorder.messages;

import com.qg.smartprinter.localorder.util.BytesConvert;

import java.util.ArrayList;

/**
 * Created by TZH on 2016/8/7.
 */
public class BOrderData {
    private ArrayList<BDatagram> mBDatagram;
    private String mText;

    public BOrderData() {
        mBDatagram = new ArrayList<>();
    }

    public BOrderData add(BDatagram datagram) {
        if (datagram instanceof BText) {
            mText = ((BText)datagram).getText();
        }
        mBDatagram.add(datagram);
        return this;
    }

    public String getText() {
        return mText;
    }

    public byte[] toBytes() {
        ArrayList<byte[]> list = new ArrayList<>();
        int length = 0;
        for (BDatagram d : mBDatagram) {
            byte[] bytes = d.toBytes();
            list.add(bytes);
            length += bytes.length;
        }
        byte[] result = new byte[length];
        int p = 0;
        for (byte[] bs: list) {
            p = BytesConvert.fillByte(bs, result, p);
        }
        return result;
    }
}
