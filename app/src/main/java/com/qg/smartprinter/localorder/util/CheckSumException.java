package com.qg.smartprinter.localorder.util;

import java.io.IOException;

/**
 * 校验和异常
 */
public class CheckSumException extends IOException {
    private byte[] mBytes;

    public CheckSumException(byte[] bytes) {
        this(null, bytes);
    }

    public CheckSumException(String detailMessage, byte[] bytes) {
        this(detailMessage, null, bytes);
    }

    public CheckSumException(String message, Throwable cause, byte[] bytes) {
        super(message, cause);
        this.mBytes = bytes;
    }

    public CheckSumException(Throwable cause) {
        super(cause);
    }

    public byte[] getBytes() {
        return mBytes;
    }

    public void setBytes(byte[] bytes) {
        mBytes = bytes;
    }
}
