package com.qg.smartprinter.localorder.device;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.qg.smartprinter.localorder.ResendTaskManager;
import com.qg.smartprinter.localorder.device.socket.BTPrinterSocket;
import com.qg.smartprinter.localorder.device.socket.PrinterSocket;
import com.qg.smartprinter.localorder.messages.BOrder;

import java.io.IOException;

public class BluetoothDeviceWrapper extends RemoteDevice {
    public static final int TYPE = 1001;

    // 是否启用重传功能
    private static boolean IS_RESEND_IF_LOST = true;

    private ResendTaskManager mResendTaskManager = new ResendTaskManager();

    private BluetoothDevice mDevice;

    public BluetoothDeviceWrapper(BluetoothDevice device) {
        mDevice = device;
    }

    @Override
    public String getAddress() {
        return mDevice.getAddress();
    }

    @Override
    public String getName() {
        return mDevice.getName();
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public PrinterSocket createSocket() throws IOException {
        return BTPrinterSocket.create(mDevice, BluetoothAdapter.getDefaultAdapter());
    }

    @Override
    public void finish(long responseNum) {
        if (IS_RESEND_IF_LOST) {
            mResendTaskManager.finishTask(responseNum);
        }
        super.finish(responseNum);
    }

    @Override
    void internalOrder(final BOrder order) {
        if (IS_RESEND_IF_LOST) {
            mResendTaskManager.startTask(new ResendTaskManager.Task(order) {
                @Override
                protected void run() {
                    BluetoothDeviceWrapper.super.internalOrder(order);
                }
            });
        } else {
            super.internalOrder(order);
        }
    }

    @Override
    public void stop() {
        // Stop all tasks.
        mResendTaskManager.clearTasks();
        super.stop();
    }
}
