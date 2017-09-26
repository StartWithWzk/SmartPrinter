package com.qg.smartprinter.localorder.device;

import android.os.Parcel;
import android.os.Parcelable;

import com.qg.smartprinter.data.source.Order;
import com.qg.smartprinter.localorder.PrinterService;
import com.qg.smartprinter.localorder.device.socket.PrinterSocket;
import com.qg.smartprinter.localorder.device.socket.TCPPrinterSocket;
import com.qg.smartprinter.localorder.messages.BOrder;
import com.qg.smartprinter.localorder.messages.WifiOrderRequest;

import java.io.IOException;

public class WifiDevice extends RemoteDevice implements Parcelable {
    public static final int TYPE = 1002;

    /**
     * The network name
     */
    private String SSID;

    private String dstName;

    private int dstPort;

    private BOrder mKeepOrder;

    private int ip;

    public WifiDevice(String SSID, String dstName, int dstPort) {
        this.SSID = SSID;
        this.dstName = dstName;
        this.dstPort = dstPort;
    }

    private WifiDevice(Parcel in) {
        SSID = in.readString();
        dstName = in.readString();
        dstPort = in.readInt();
    }

    public String getAddress() {
        return dstName;
    }

    public String getName() {
        return SSID;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public PrinterSocket createSocket() throws IOException {
        return TCPPrinterSocket.create(dstName, dstPort);
    }

    public static final Creator<WifiDevice> CREATOR = new Creator<WifiDevice>() {
        @Override
        public WifiDevice createFromParcel(Parcel in) {
            return new WifiDevice(in);
        }

        @Override
        public WifiDevice[] newArray(int size) {
            return new WifiDevice[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(SSID);
        dest.writeString(dstName);
        dest.writeInt(dstPort);
    }

    @Override
    public void finish(long responseNum) {
        super.finish(responseNum);
    }

    @Override
    void internalOrder(BOrder order) {
        // Keep the order until notify.
        mKeepOrder = order;
        // Request notifySend order;
        write(WifiOrderRequest.newRequestSignal(order, getIP()));
        PrinterService.getBus().post(new PrinterService.InsertDBMessageEvent(
                order.getOrderNumber(),
                order.toRealBytes(),
                Order.REQUEST));
    }

    public void notifySend() {
        if (mKeepOrder == null) {
            return;
        }
        // Send order.
        super.internalOrder(mKeepOrder);
        mKeepOrder = null;
    }

    public void setIP(int ip) {
        this.ip = ip;
    }

    public int getIP() {
        return ip;
    }

}
