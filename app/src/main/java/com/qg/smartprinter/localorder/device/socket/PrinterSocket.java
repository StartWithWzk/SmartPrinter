package com.qg.smartprinter.localorder.device.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Provides a client-side printer socket.
 */
public interface PrinterSocket {

    InputStream getInputStream() throws IOException;

    OutputStream getOutputStream() throws IOException;

    void connect() throws IOException;

    void close() throws IOException;

    boolean isConnected();
}
