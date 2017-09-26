package com.qg.smartprinter.localorder;

import android.support.annotation.Nullable;

import com.qg.smartprinter.Injection;
import com.qg.smartprinter.data.source.Order;
import com.qg.smartprinter.data.source.PrintersRepository;
import com.qg.smartprinter.localorder.device.BluetoothDeviceWrapper;
import com.qg.smartprinter.localorder.device.RemoteDevice;
import com.qg.smartprinter.localorder.device.WifiDevice;
import com.qg.smartprinter.localorder.messages.BOrder;
import com.qg.smartprinter.localorder.messages.BPrinterStatus;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.qg.common.Preconditions.checkState;

public class DevicesManager {

    private Map<RemoteDevice, Integer> mWifiDeviceIPMap;

    private Map<RemoteDevice, Integer> mDeviceStatusCountMap;

    private Map<RemoteDevice, Integer> mOrderStatusCountMap;

    private Map<RemoteDevice, LinkedList<BOrder>> mOrderQueueMap;

    private DevicesManager() {
        mWifiDeviceIPMap = new HashMap<>();
        mDeviceStatusCountMap = new HashMap<>();
        mOrderStatusCountMap = new HashMap<>();
        mOrderQueueMap = new HashMap<>();
    }

    private static final DevicesManager INSTANCE = new DevicesManager();

    public static DevicesManager getInstance() {
        return INSTANCE;
    }

    /**
     * 发送订单到队列
     */
    public boolean sendOrder(RemoteDevice device, BOrder order) {
        if (!mOrderQueueMap.containsKey(device)) {
            mOrderQueueMap.put(device, new LinkedList<BOrder>());
        }
        if (device.isIdling()) {
            // 这份订单直接发送，不需要加入队列
            checkState(device.order(order));
            return true;
        }
        // 将未发送的订单添加到队列中
        return mOrderQueueMap.get(device).offer(order);
    }

    @Nullable
    public BOrder nextOrder(RemoteDevice device) {
        if (!mOrderQueueMap.containsKey(device)) {
            return null;
        }
        return mOrderQueueMap.get(device).poll();
    }

    public void clear() {
        mWifiDeviceIPMap.clear();
        mDeviceStatusCountMap.clear();
        mOrderStatusCountMap.clear();
        mOrderQueueMap.clear();
    }

    public int getNextDeviceStatusNumber(RemoteDevice device) {
        if (!mDeviceStatusCountMap.containsKey(device)) {
            return 1;
        }
        int num = mDeviceStatusCountMap.get(device);
        mDeviceStatusCountMap.put(device, num + 1);
        return num;
    }

    public int getNextOrderStatusNumber(RemoteDevice device) {
        if (!mOrderStatusCountMap.containsKey(device)) {
            return 1;
        }
        int num = mOrderStatusCountMap.get(device);
        mOrderStatusCountMap.put(device, num + 1);
        return num;
    }

    private void internalAddDevice(RemoteDevice device) {
        mDeviceStatusCountMap.put(device, 1);
        mOrderStatusCountMap.put(device, 1);
    }

    private void internalRemoveDevice(RemoteDevice device) {
        mDeviceStatusCountMap.remove(device);
        mOrderStatusCountMap.remove(device);
        mWifiDeviceIPMap.remove(device);
        // 将队列中的订单移除，并更新数据库。
        LinkedList<BOrder> removeOrders = mOrderQueueMap.remove(device);
        if (removeOrders != null) {
            for (BOrder removeOrder : removeOrders) {
                long orderNumber = removeOrder.getOrderNumber();
                PrinterService.getBus().post(new PrinterService.UpdateDBOrderStatusEvent(
                        orderNumber,
                        Order.UNFINISHED
                ));
            }
        }
    }

    // ------------------------------------
    // 辣鸡项目，整天改需求，浪费我时间，随便写
    private RemoteDevice mBTDevice;
    private RemoteDevice mTCPDevice;
    private Printer mBTPrinter;
    private Printer mTCPPrinter;

    void addDevice(RemoteDevice device) {
        if (device instanceof BluetoothDeviceWrapper) {
            if (mBTDevice != null) {
                // 蓝牙设备连接中，断开原连接
                mBTDevice.stop();
                removeDevice(mBTDevice);
            }
            mBTDevice = device;
        } else if (device instanceof WifiDevice) {
            if (mTCPDevice != null) {
                // TCP设备连接中，断开原连接
                mTCPDevice.stop();
                removeDevice(mTCPDevice);
            }
            mTCPDevice = device;
        }
        internalAddDevice(device);
    }

    void addPrinter(RemoteDevice device) {
        if (device instanceof BluetoothDeviceWrapper) {
            Printer printer = new Printer(
                    BluetoothDeviceWrapper.TYPE,
                    device.getName(),
                    device.getAddress(),
                    device
            );
            mBTPrinter = printer;
            Injection.providePrintersRepository()
                    .addPrinter(printer);
        } else if (device instanceof WifiDevice) {
            Printer printer = new Printer(
                    WifiDevice.TYPE,
                    device.getName(),
                    device.getAddress(),
                    device
            );
            mTCPPrinter = printer;
            Injection.providePrintersRepository()
                    .addPrinter(printer);
        }
    }

    void removeDevice(RemoteDevice device) {
        if (device == null) {
            return;
        }
        if (device.equals(mBTDevice)) {
            mBTDevice = null;
            if (mBTPrinter == null) {
                return;
            }
            PrintersRepository.getInstance().removePrinter(mBTPrinter.getId());
            mBTPrinter.setDevice(null);
            mBTPrinter = null;
        } else if (device.equals(mTCPDevice)) {
            mTCPDevice = null;
            if (mTCPPrinter == null) {
                return;
            }
            PrintersRepository.getInstance().removePrinter(mTCPPrinter.getId());
            mTCPPrinter.setDevice(null);
            mTCPPrinter = null;
        }
        internalRemoveDevice(device);
    }

    void updateDeviceStatus(RemoteDevice device, long printerId, BPrinterStatus status) {
        Printer printer;
        if (device.equals(mBTDevice)) {
            printer = mBTPrinter;
        } else if (device.equals(mTCPDevice)) {
            printer = mTCPPrinter;
        } else {
            return;
        }
        printer.setPrinterId(printerId);
        if (status != null) {
            printer.updateStatus(status);
        }
    }

    public RemoteDevice getBTDevice() {
        return mBTDevice;
    }

    public RemoteDevice getTCPDevice() {
        return mTCPDevice;
    }

}
