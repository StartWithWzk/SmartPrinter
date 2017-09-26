package com.qg.smartprinter.localorder.device.socket;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * A bluetooth socket of printer.
 */
public class BTPrinterSocket implements PrinterSocket {

    private static final UUID WELL_KNOWN_SPP_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothSocket mSocket;

    private BluetoothDevice mDevice;

    private BluetoothAdapter mAdapter;

    private BTPrinterSocket() {
    }

    public static BTPrinterSocket create(BluetoothDevice device, BluetoothAdapter adapter) {
        BTPrinterSocket socket = new BTPrinterSocket();
        socket.mDevice = device;
        socket.mAdapter = adapter;
        return socket;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return mSocket.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return mSocket.getOutputStream();
    }

    @Override
    public void connect() throws IOException {
        mAdapter.cancelDiscovery();
        mSocket = mDevice.createRfcommSocketToServiceRecord(WELL_KNOWN_SPP_UUID);
        mSocket.connect();
    }

    @Override
    public void close() throws IOException {
        if (mSocket != null) {
            mSocket.close();
        }
    }

    @Override
    public boolean isConnected() {
        return mSocket.isConnected();
    }
}
