package com.qg.smartprinter.localorder;

import com.qg.smartprinter.localorder.device.BluetoothDeviceWrapper;
import com.qg.smartprinter.localorder.device.RemoteDevice;
import com.qg.smartprinter.localorder.device.WifiDevice;
import com.qg.smartprinter.localorder.messages.BPrinterStatus;

import java.util.HashMap;
import java.util.Map;

public class Printer {

    private int id;

    private long printerId;

    private String name;

    private String address;

    private Map<Long, Integer> unitsStatus = new HashMap<>();

    private RemoteDevice device;

    public Printer(int id, String name, String address, RemoteDevice device) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.device = device;
    }

    public RemoteDevice getDevice() {
        return device;
    }

    public void setDevice(RemoteDevice device) {
        this.device = device;
    }

    public void setPrinterId(long printerId) {
        this.printerId = printerId;
    }

    public long getPrinterId() {
        return printerId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getId() {
        return id + device.getAddress();
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getStatusString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Long, Integer> status : unitsStatus.entrySet()) {
            sb.append("打印单元")
                    .append(status.getKey())
                    .append(":")
                    .append(BPrinterStatus.PRINTER_STATUS_MAP.get(status.getValue()))
                    .append("\n");
        }
        return sb.toString();
    }

    public void updateStatus(BPrinterStatus status) {
        if (unitsStatus == null) {
            unitsStatus = new HashMap<>();
        }
        unitsStatus.put(status.number, status.sta);
    }

    public String getConnectMethod() {
        if (device instanceof BluetoothDeviceWrapper) {
            return "蓝牙";
        } else if (device instanceof WifiDevice) {
            return "WIFI";
        } else {
            return "未知";
        }
    }
}
