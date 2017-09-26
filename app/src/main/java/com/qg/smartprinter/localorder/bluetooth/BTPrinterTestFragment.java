package com.qg.smartprinter.localorder.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.qg.common.logger.Log;
import com.qg.smartprinter.R;
import com.qg.smartprinter.localorder.DevicesManager;
import com.qg.smartprinter.localorder.PrinterTestFragment;
import com.qg.smartprinter.localorder.device.BluetoothDeviceWrapper;
import com.qg.smartprinter.localorder.device.RemoteDevice;
import com.qg.smartprinter.localorder.event.Events;
import com.qg.smartprinter.util.rxbus.RxBus;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BTPrinterTestFragment extends PrinterTestFragment {

    private static final String TAG = "BluetoothPrinterTestFragment";

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private BluetoothAdapter mBluetoothAdapter = null;

    private boolean mIsSetup;

    public static BTPrinterTestFragment newInstance() {

        Bundle args = new Bundle();

        BTPrinterTestFragment fragment = new BTPrinterTestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // If BT is ot on, request that it be enabled.
        // setupPrinter() will then be called during onActivityResult
        if (!mIsSetup) {
            setupPrinter();
        }
    }

    private void setupPrinter() {
        Log.d(TAG, "setupPrinter()");
        mIsSetup = true;
    }

    public RemoteDevice getDevice() {
        return DevicesManager.getInstance().getBTDevice();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When BTDeviceListActivity returns with a device to createSocket
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled
                    setupPrinter();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other divice
     *
     * @param data An {@link Intent} with {@link BTDeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     */
    private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(BTDeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        Log.d(TAG, "connectDevice: " + address);
        // Attempt to createSocket to the device
        RxBus.getDefault().post(new Events.ConnectEvent(new BluetoothDeviceWrapper(device)));
    }

    @Override
    protected void connect() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            return;
        }
        // Launch the WifiDeviceListActivity to see devices and do scan
        Intent serverIntent = new Intent(getActivity(), BTDeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }
}
