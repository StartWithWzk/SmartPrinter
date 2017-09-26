package com.qg.smartprinter.localorder.messages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 打印机状态
 */
public class BPrinterStatus extends AbstractMessage implements Serializable {

    public static final byte TYPE_TOKEN = (byte) 0b010;  // 类型标志

    public int sta;
    // line1
    public long printerId; // 主控板id
    // line2
    public long seconds;   // 发送给服务器的时间戳
    // line3
    public long number;    // 主控板打印单元序号

    public static BPrinterStatus bytesToPrinterStatus(byte[] bytes) {
        AbstractMessage status = AbstractMessage.bytesToAbstractStatus(bytes);

        BPrinterStatus bps = new BPrinterStatus();

        bps.sta = (short) (bytes[3] & (short) 0xFF);

        bps.printerId = status.line1;

        bps.seconds = status.line2;

        bps.number = status.line3;

        bps.checkSum = status.checkSum;

        return bps;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        return sb.append("{ ")
                .append("主控板ID:").append(printerId).append(", ").append("\n")
                .append("打印单元序号:").append(number).append(", ").append("\n")
                .append("状态:").append(getStatusString()).append("\n")
                .append(" }")
                .toString();
    }

    public String getStatusString() {
        return PRINTER_STATUS_MAP.get(sta);
    }


    public static final Map<Integer, String> PRINTER_STATUS_MAP = new HashMap<>();

    public static final int KNIFE_ERR = 0x01;
    public static final int BOX_OPEN = 0x02;
    public static final int PAPER_GOING_END = 0x03;
    public static final int PAPER_IN = 0x04;
    public static final int MOVEMENT_HIGH_TEMP = 0x05;
    public static final int MOVEMENT_BURN = 0x06;
    public static final int NORMAL = 0x07;
    public static final int NORMAL_BUF_FULL = 0x0C;
    public static final int FAST_BUF_FULL = 0x0D;
    public static final int HEALTH = 0x0E;
    public static final int SECOND_HEALTH = 0x0F;
    public static final int NOT_HEALTH = 0x10;

    static {
        PRINTER_STATUS_MAP.put(KNIFE_ERR, "切刀错误");
        PRINTER_STATUS_MAP.put(BOX_OPEN, "机盒打开");
        PRINTER_STATUS_MAP.put(PAPER_GOING_END, "纸将用尽");
        PRINTER_STATUS_MAP.put(PAPER_IN, "正在进纸");
        PRINTER_STATUS_MAP.put(MOVEMENT_HIGH_TEMP, "机芯高温");
        PRINTER_STATUS_MAP.put(MOVEMENT_BURN, "机芯烧毁");
        PRINTER_STATUS_MAP.put(NORMAL, "正常状态");
        PRINTER_STATUS_MAP.put(0x08, "待定");
        PRINTER_STATUS_MAP.put(0x09, "待定");
        PRINTER_STATUS_MAP.put(0x0A, "待定");
        PRINTER_STATUS_MAP.put(0x0B, "待定 ");
        PRINTER_STATUS_MAP.put(NORMAL_BUF_FULL, "普通缓冲区满");
        PRINTER_STATUS_MAP.put(FAST_BUF_FULL, "加急缓冲区满");
        PRINTER_STATUS_MAP.put(HEALTH, "健康状态");
        PRINTER_STATUS_MAP.put(SECOND_HEALTH, "亚健康状态");
        PRINTER_STATUS_MAP.put(NOT_HEALTH, "不健康");
    }

}
