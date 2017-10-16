package com.qg.smartprinter.localorder.device;

import android.support.annotation.IntDef;

import com.qg.common.logger.Log;
import com.qg.smartprinter.data.source.Order;
import com.qg.smartprinter.localorder.DevicesManager;
import com.qg.smartprinter.localorder.PrinterService;
import com.qg.smartprinter.localorder.device.socket.PrinterSocket;
import com.qg.smartprinter.localorder.event.Events;
import com.qg.smartprinter.localorder.messages.BOrder;
import com.qg.smartprinter.localorder.messages.BResponse;
import com.qg.smartprinter.localorder.util.CheckSumException;
import com.qg.smartprinter.localorder.util.CheckSumUtil;
import com.qg.smartprinter.localorder.util.DebugUtil;
import com.qg.smartprinter.util.rxbus.RxBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

import static com.qg.common.Preconditions.checkState;

public abstract class RemoteDevice {
    private static final String TAG = "RemoteDevice";

    private boolean mIdling = true;

    public static final int STATE_NONE = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    private BOrder mLastOrder;

    @IntDef({STATE_NONE, STATE_CONNECTING, STATE_CONNECTED})
    @interface State {
    }

    @State
    private int mState;

    private ConnectThread mConnectThread;

    private ConnectedThread mConnectedThread;

    /**
     * Return the address of the remote device.
     */
    public abstract String getAddress();

    /**
     * Return the name of the remote device.
     */
    public abstract String getName();

    /**
     * Return the connection type of the remote device.
     */
    public abstract int getType();

    /**
     * Attempt to create a socket with the remote device.
     */
    public abstract PrinterSocket createSocket() throws IOException;

    /**
     * Attempt to connect the remote device.
     */
    public void connect() {
        mConnectThread = new ConnectThread(this);
        mConnectThread.start();
    }

    public void stop() {
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        setState(STATE_NONE);

        // If the last order not finished, update data.
        if (mLastOrder != null) {
            PrinterService.getBus().post(new PrinterService.UpdateDBOrderStatusEvent(
                    mLastOrder.getOrderNumber(),
                    Order.UNFINISHED
            ));
        }
    }

    /**
     * 重要函数
     * 默认设备是处于空闲状态，当订单发送完毕，finish()函数会重新设置设备为空闲，并且会调用
     * DevicesManager.getInstance().nextOrder(this)，发送下一张订单
     * @param order 订单
     * @return 订单是否发送成功
     */
    public boolean order(final BOrder order) {
        if (!isIdling()) {
            return false;
        }
        setIdling(false);

        // Update the database
        PrinterService.getBus().post(new PrinterService.UpdateDBOrderStatusEvent(
                order.getOrderNumber(),
                Order.WAIT_RECEIVE
        ));

        // Record the order length and number.
        mLastOrder = order;

        internalOrder(order);
        return true;
    }

    void internalOrder(BOrder order) {
        // Save notifySend message.
        PrinterService.getBus().post(new PrinterService.InsertDBMessageEvent(
                order.orderNumber,
                order.toRealBytes(),
                Order.SEND_OR_RESEND
        ));
        write(order.toRealBytes());
    }

    public BOrder forceFinish() {
        BOrder order = mLastOrder;
        if (order != null) {
            onFinish();
        }
        return order;
    }
    public void finish(long responseNum) {
        // Filter
        if (mLastOrder != null && mLastOrder.getOrderNumber() == responseNum) {
            onFinish();
        }
    }

    private void onFinish() {
        // Set device idling delay.
        Observable.timer(getIdlingDelay(), TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        setIdling(true);
                    }
                });
        mLastOrder = null;
    }

    private int getIdlingDelay() {
//        int delay = (int) Math.ceil((float) mLastOrder.getBytesLength() / 1000);
//        return delay <= 0 ? 1 : delay;
        return 1;
    }


    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;

        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }

        // Perform the write asynchronously
        r.write(out);
    }

    public boolean isIdling() {
        return mIdling;
    }

    private void setIdling(boolean idling) {
        mIdling = idling;
        onIdlingChange(idling);
    }

    private void onIdlingChange(boolean idling) {
        if (idling) {
            // Check if any order to notifySend.
            BOrder bOrder = DevicesManager.getInstance().nextOrder(this);
            if (bOrder != null) {
                checkState(order(bOrder));
            }
        }
    }

    @State
    public int getState() {
        return mState;
    }

    private void setState(@State int state) {
        mState = state;
        RxBus.getDefault().post(new Events.StateChangeEvent(this, state));
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final PrinterSocket mmSocket;

        private final RemoteDevice mmDevice;

        ConnectThread(RemoteDevice device) {
            mmDevice = device;
            PrinterSocket tmp = null;

            // Get a PrinterSocket for a connection with the
            // given RemoteDevice
            try {
                tmp = device.createSocket();
            } catch (IOException e) {
                Log.e(TAG, "Socket create() failed", e);
            }
            mmSocket = tmp;
            setState(STATE_CONNECTING);
        }

        public void run() {
            // Make a connection to the PrinterSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                Log.e(TAG, "unable to createSocket()" +
                        " socket connection failure", e);
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "Encounter an exception when Socket.close() called", e2);
                }
                RxBus.getDefault().post(new Events.ConnectionFailedEvent(RemoteDevice.this, e));
                RemoteDevice.this.stop();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (RemoteDevice.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            mConnectedThread = new ConnectedThread(mmSocket, mmDevice);
            mConnectedThread.start();
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of createSocket socket failed", e);
            }
        }
    }

    /**
     * Maintain all input and output for a connected device.
     */
    private class ConnectedThread extends Thread {
        private final PrinterSocket mmSocket;

        private final InputStream mmInStream;

        private final OutputStream mmOutStream;

        ConnectedThread(PrinterSocket socket, RemoteDevice device) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // Get the PrinterSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();

                // Send the name of the connected device
                RxBus.getDefault().post(new Events.ConnectedEvent(device));

                setState(STATE_CONNECTED);
            } catch (IOException e) {
                Log.e(TAG, "Temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer;
            // Keep listening to the InputStream while connected
            while (mState == STATE_CONNECTED) {
                try {
                    // Read from the InputStream
                    try {
                        buffer = readBytes();
                        RxBus.getDefault().post(new Events.ReadEvent(RemoteDevice.this, buffer));
                    } catch (CheckSumException e) {
                        // Check failed.
                        RxBus.getDefault().post(new Events.CheckSumFailedEvent(RemoteDevice.this, e));
                    }
                } catch (IOException e) {
                    RxBus.getDefault().post(new Events.ConnectionLostEvent(RemoteDevice.this, e));
                    RemoteDevice.this.stop();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        synchronized void write(byte[] buffer) {
            try {
                Log.d(TAG, "Write start.");
                mmOutStream.write(buffer);
                Log.d(TAG, "Write" + DebugUtil.getBytesString(buffer));
                Log.d(TAG, "Write end. Length of notifySend bytes:" + buffer.length);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        private byte[] readBytes() throws IOException {
            // TODO: 2017/9/23  
            Log.v(TAG, "Read start.");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (baos.size() < BResponse.BYTE_COUNT) {
                int read = mmInStream.read();
                baos.write(read);
            }
            Log.v(TAG, "Read end.");
            byte[] checkSignalBytes;
            checkSignalBytes = baos.toByteArray();
            Log.d(TAG, "ReadBytes: " + DebugUtil.getBytesString(checkSignalBytes));
            if (0 != CheckSumUtil.checkSum(checkSignalBytes)) {
                throw new CheckSumException("Checksum not true!", checkSignalBytes);
            }
            return checkSignalBytes;
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of createSocket socket failed", e);
            }
        }
    }
}
