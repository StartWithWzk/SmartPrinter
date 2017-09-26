package com.qg.smartprinter.localorder.device.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * A TCP socket of printer.
 */
public class TCPPrinterSocket implements PrinterSocket {

    private Socket mSocket;

    private String mHost;

    private int mPort;

    private TCPPrinterSocket(String host, int port) {
        this.mHost = host;
        this.mPort = port;
    }

    public static TCPPrinterSocket create(String host, int port) {
        return new TCPPrinterSocket(host, port);
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
        mSocket = new Socket(mHost, mPort);
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
