package com.qg.smartprinter.localorder.wifi;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.qg.smartprinter.localorder.DevicesManager;
import com.qg.smartprinter.localorder.PrinterTestFragment;
import com.qg.smartprinter.localorder.device.RemoteDevice;
import com.qg.smartprinter.localorder.device.WifiDevice;
import com.qg.smartprinter.localorder.event.Events;
import com.qg.smartprinter.util.SharedPreferencesUtils;
import com.qg.smartprinter.util.rxbus.RxBus;

/**
 * This fragment controls Wifi to communicate with other devices.
 */
public class WifiPrinterTestFragment extends PrinterTestFragment {
    private static final String TAG = "WifiPrinterFragment";

    private WifiManager mWifiManager = null;

    private boolean mIsSetup;

    public static WifiPrinterTestFragment newInstance() {
        return new WifiPrinterTestFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get local Wifi manager
        mWifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);

        // If the adapter is null, then Wifi is not supported
        if (mWifiManager == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Wifi is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }

    public RemoteDevice getDevice() {
        return DevicesManager.getInstance().getTCPDevice();
    }

    @Override
    protected void connect() {
        String ssid = mWifiManager.getConnectionInfo().getSSID();
        String ip = SharedPreferencesUtils.getInstance().getServerIP();
        int port = SharedPreferencesUtils.getInstance().getServerPort();
        WifiDevice device = new WifiDevice(ssid, ip, port);
        RxBus.getDefault().post(new Events.ConnectEvent(device));
    }

}
