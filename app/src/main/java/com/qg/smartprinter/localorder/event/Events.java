package com.qg.smartprinter.localorder.event;

import com.qg.smartprinter.localorder.device.RemoteDevice;
import com.qg.smartprinter.localorder.messages.BOrder;
import com.qg.smartprinter.localorder.util.CheckSumException;

import java.io.IOException;

/**
 * @author TZH
 * @version 1.0
 */
public abstract class Events {
    private Events() {
    }

    public static class RemoteEvent {
        private RemoteDevice mDevice;

        private RemoteEvent(RemoteDevice device) {
            mDevice = device;
        }

        public RemoteDevice getDevice() {
            return mDevice;
        }
    }

    public static class CheckSumFailedEvent extends RemoteEvent {
        private CheckSumException mException;

        public CheckSumFailedEvent(RemoteDevice device, CheckSumException e) {
            super(device);
            mException = e;
        }
    }

    public static class ConnectionLostEvent extends RemoteEvent {
        private IOException mException;

        public ConnectionLostEvent(RemoteDevice device, IOException e) {
            super(device);
            mException = e;
        }
    }

    public static class ConnectionFailedEvent extends RemoteEvent {
        private IOException mException;

        public ConnectionFailedEvent(RemoteDevice device, IOException e) {
            super(device);
            mException = e;
        }
    }

    public static class StateChangeEvent extends RemoteEvent {

        private int mState;

        public StateChangeEvent(RemoteDevice device, int state) {
            super(device);
            mState = state;
        }

        public int getState() {
            return mState;
        }
    }

    public static class ConnectEvent extends RemoteEvent {
        public ConnectEvent(RemoteDevice device) {
            super(device);
        }
    }

    public static class ConnectedEvent extends RemoteEvent {
        public ConnectedEvent
                (RemoteDevice device) {
            super(device);
        }
    }

    public static class OrderEvent extends RemoteEvent {
        private BOrder mOrder;
        private boolean mCorrected;

        public OrderEvent(RemoteDevice device, BOrder order) {
            this(device, order, false);
        }

        public OrderEvent(RemoteDevice device, BOrder order, boolean corrected) {
            super(device);
            mOrder = order;
            mCorrected = corrected;
        }

        /**
         * 是否为异常修正订单？
         */
        public boolean isCorrected() {
            return mCorrected;
        }

        public BOrder getOrder() {
            return mOrder;
        }

    }

    public static class OrderLostEvent {
        private BOrder mOrder;

        public OrderLostEvent(OrderEvent event) {
            mOrder = event.getOrder();
        }
    }

    public static class DisconnectEvent extends RemoteEvent {
        public DisconnectEvent(RemoteDevice device) {
            super(device);
        }
    }

    public static class ReadEvent extends RemoteEvent {
        private byte[] mBytes;

        public ReadEvent(RemoteDevice device, byte[] bytes) {
            super(device);
            mBytes = bytes;
        }

        public byte[] getBytes() {
            return mBytes;
        }
    }
}
