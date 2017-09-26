package com.qg.deprecated.logic.model;

/**
 * Created by 攀登 on 2016/7/31.
 */
public class PrinterStatus {
    private String id;
    private int printerStatus;

    public PrinterStatus() {}

    public PrinterStatus(String id, int printerStatus) {
        this.id = id;
        this.printerStatus = printerStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPrinterStatus() {
        return printerStatus;
    }

    public void setPrinterStatus(int printerStatus) {
        this.printerStatus = printerStatus;
    }
}
