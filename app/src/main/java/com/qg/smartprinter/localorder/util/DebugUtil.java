package com.qg.smartprinter.localorder.util;

/**
 * 测试专用工具
 */
public final class DebugUtil {

    public static String getBytesString(byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for (int i = 0; i < bytes.length; i += 4) {
            byte[] test = new byte[4];
            System.arraycopy(bytes, i, test, 0, 4);
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < 4; j++) {
                builder.append(String.format("0x%02X, ", test[j] & 0xFF));
            }
            sb.append(String.format("%s\n", builder.toString()));
        }
        sb.append("}");
        return sb.toString();
    }

    public static String getByteString(byte b) {
        return String.format("0x%02X", b & 0xFF);
    }
}
