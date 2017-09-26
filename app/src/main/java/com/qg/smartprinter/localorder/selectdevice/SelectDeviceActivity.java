package com.qg.smartprinter.localorder.selectdevice;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.qg.smartprinter.Injection;
import com.qg.smartprinter.R;
import com.qg.smartprinter.localorder.DevicesManager;
import com.qg.smartprinter.localorder.bluetooth.BTDeviceListActivity;
import com.qg.smartprinter.localorder.device.BluetoothDeviceWrapper;
import com.qg.smartprinter.localorder.device.RemoteDevice;
import com.qg.smartprinter.localorder.device.WifiDevice;
import com.qg.smartprinter.localorder.event.Events;
import com.qg.smartprinter.ui.BaseActivity;
import com.qg.smartprinter.util.SharedPreferencesUtils;
import com.qg.smartprinter.util.rxbus.RxBus;
import com.qg.smartprinter.util.scheduler.BaseSchedulerProvider;

import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

import static com.qg.smartprinter.localorder.device.RemoteDevice.STATE_CONNECTED;
import static com.qg.smartprinter.localorder.device.RemoteDevice.STATE_CONNECTING;
import static com.qg.smartprinter.localorder.device.RemoteDevice.STATE_NONE;

// 乱写的Activity，混杂了所有层次
public class SelectDeviceActivity extends BaseActivity
        implements SelectDeviceContract.Presenter,
        SelectDeviceContract.View {

    private static final int REQUEST_CONNECT_BT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    public static final String EXTRA_DEVICE_TYPE = "DEVICE_TYPE";

    private SelectDeviceContract.Presenter mPresenter;
    private SelectDeviceContract.View mView;
    private CompositeSubscription mSubscriptions;

    private WifiManager mWifiManager;
    private BluetoothAdapter mBluetoothAdapter;

    private View mBTDeviceView;
    private TextView mBTStatusView;
    private Button mConnectBTButton;
    private Button mDisconnectBTButton;

    private View mWifiDeviceView;
    private TextView mWifiStatusView;
    private Button mConnectWifiButton;
    private Button mDisconnectWifiButton;

    public static void start(Activity context, int requestCode) {
        Intent starter = new Intent(context, SelectDeviceActivity.class);
        context.startActivityForResult(starter, requestCode);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_device_activity);

        mPresenter = this;
        mView = this;

        setupViews();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        BaseSchedulerProvider schedulerProvider = Injection.provideBaseSchedulerProvider();

        mSubscriptions = new CompositeSubscription();
        Subscription subscription = RxBus.getDefault().asObservable()
                .onBackpressureDrop() // Guard against uncontrollable frequency of upstream emissions.
                .observeOn(schedulerProvider.ui())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object e) {
                        if (e instanceof Events.StateChangeEvent) {
                            updateWifiStatus();
                            updateBTStatus();
                        } else if (e instanceof Events.ConnectionFailedEvent) {
                            Toast.makeText(SelectDeviceActivity.this, SelectDeviceActivity.this.getString(R.string.connect_fail),
                                    Toast.LENGTH_SHORT).show();
                        } else if (e instanceof Events.ConnectionLostEvent) {
                            Toast.makeText(SelectDeviceActivity.this, SelectDeviceActivity.this.getString(R.string.connect_lost),
                                    Toast.LENGTH_SHORT).show();
                        } else if (e instanceof Events.ConnectedEvent) {
                            String name = ((Events.ConnectedEvent) e).getDevice().getName();
                            Toast.makeText(SelectDeviceActivity.this, getString(R.string.title_connected_to, name),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter = null;
        mView = null;
        mSubscriptions.clear();
    }

    private void setupViews() {
        mBTDeviceView = findViewById(R.id.bt_device);
        mBTStatusView = (TextView) findViewById(R.id.bt_status);
        mConnectBTButton = (Button) findViewById(R.id.connect_bt_btn);
        mDisconnectBTButton = (Button) findViewById(R.id.disconnect_bt_btn);

        mWifiDeviceView = findViewById(R.id.wifi_device);
        mWifiStatusView = (TextView) findViewById(R.id.wifi_status);
        mConnectWifiButton = (Button) findViewById(R.id.connect_wifi_btn);
        mDisconnectWifiButton = (Button) findViewById(R.id.disconnect_wifi_btn);

        // Set up click events.
        RxView.clicks(mBTDeviceView)
                .filter(new Func1<Void, Boolean>() {
                    @Override
                    public Boolean call(Void aVoid) {
                        return hasBTDevice();
                    }
                })
                .filter(new Func1<Void, Boolean>() {
                    @Override
                    public Boolean call(Void aVoid) {
                        return getBTDevice().getState() == STATE_CONNECTED;
                    }
                })
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Intent data = new Intent();
                        data.putExtra(EXTRA_DEVICE_TYPE, BluetoothDeviceWrapper.TYPE);
                        setResult(Activity.RESULT_OK, data);
                        finish();
                    }
                });
        RxView.clicks(mWifiDeviceView)
                .filter(new Func1<Void, Boolean>() {
                    @Override
                    public Boolean call(Void aVoid) {
                        return hasWifiDevice();
                    }
                })
                .filter(new Func1<Void, Boolean>() {
                    @Override
                    public Boolean call(Void aVoid) {
                        return getWifiDevice().getState() == STATE_CONNECTED;
                    }
                })
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Intent data = new Intent();
                        data.putExtra(EXTRA_DEVICE_TYPE, WifiDevice.TYPE);
                        setResult(Activity.RESULT_OK, data);
                        finish();
                    }
                });

        updateBTStatus();
        updateWifiStatus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect_bt_btn:
                mPresenter.connectBT();
                break;
            case R.id.disconnect_bt_btn:
                mPresenter.disconnectBT();
                break;
            case R.id.connect_wifi_btn:
                mPresenter.connectWifi();
                break;
            case R.id.disconnect_wifi_btn:
                mPresenter.disconnectWifi();
                break;
            default:
                super.onClick(v);
        }
    }

    @Override
    public void connectWifi() {
        String ssid = mWifiManager.getConnectionInfo().getSSID();
        String ip = SharedPreferencesUtils.getInstance().getServerIP();
        int port = SharedPreferencesUtils.getInstance().getServerPort();
        WifiDevice device = new WifiDevice(ssid, ip, port);
        RxBus.getDefault().post(new Events.ConnectEvent(device));
        updateWifiStatus();
    }

    @Override
    public void disconnectWifi() {
        if (hasWifiDevice()) {
            RxBus.getDefault().post(new Events.DisconnectEvent(getWifiDevice()));
        }
    }

    @Override
    public void connectBT() {
        if (mBluetoothAdapter == null) {
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            mView.showEnableBT();
            return;
        }
        mView.showBTDevices();
    }

    @Override
    public void disconnectBT() {
        if (hasBTDevice()) {
            RxBus.getDefault().post(new Events.DisconnectEvent(getBTDevice()));
        }
    }

    @Override
    public void showBTDevices() {
        Intent serverIntent = new Intent(this, BTDeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_BT_DEVICE);
    }

    @Override
    public void showEnableBT() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }

    @Override
    public void updateWifiStatus() {
        int status = STATE_NONE;
        String name = null;
        if (hasWifiDevice()) {
            status = getWifiDevice().getState();
            name = getWifiDevice().getName();
        }
        switch (status) {
            case STATE_NONE:
                mConnectWifiButton.setVisibility(View.VISIBLE);
                mDisconnectWifiButton.setVisibility(View.GONE);
                mWifiStatusView.setText(R.string.not_connected);
                break;
            case STATE_CONNECTING:
                mConnectWifiButton.setVisibility(View.GONE);
                mDisconnectWifiButton.setVisibility(View.VISIBLE);
                mWifiStatusView.setText(R.string.title_connecting);
                break;
            case STATE_CONNECTED:
                mConnectWifiButton.setVisibility(View.GONE);
                mDisconnectWifiButton.setVisibility(View.VISIBLE);
                mWifiStatusView.setText(getString(R.string.title_connected_to, name));
                break;
        }
    }

    @Override
    public void updateBTStatus() {
        int status = STATE_NONE;
        String name = null;
        if (hasBTDevice()) {
            status = getBTDevice().getState();
            name = getBTDevice().getName();
        }
        switch (status) {
            case STATE_NONE:
                mConnectBTButton.setVisibility(View.VISIBLE);
                mDisconnectBTButton.setVisibility(View.GONE);
                mBTStatusView.setText(R.string.not_connected);
                break;
            case STATE_CONNECTING:
                mConnectBTButton.setVisibility(View.GONE);
                mDisconnectBTButton.setVisibility(View.VISIBLE);
                mBTStatusView.setText(R.string.title_connecting);
                break;
            case STATE_CONNECTED:
                mConnectBTButton.setVisibility(View.GONE);
                mDisconnectBTButton.setVisibility(View.VISIBLE);
                mBTStatusView.setText(getString(R.string.title_connected_to, name));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    mView.showBTDevices();
                }
                break;
            case REQUEST_CONNECT_BT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    connectBTDevice(data);
                    mView.updateBTStatus();
                }
                break;
        }
    }

    private void connectBTDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(BTDeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to createSocket to the device
        RxBus.getDefault().post(new Events.ConnectEvent(new BluetoothDeviceWrapper(device)));
    }

    public boolean hasBTDevice() {
        return getBTDevice() != null;
    }

    public boolean hasWifiDevice() {
        return getWifiDevice() != null;
    }

    public RemoteDevice getBTDevice() {
        return DevicesManager.getInstance().getBTDevice();
    }

    public RemoteDevice getWifiDevice() {
        return DevicesManager.getInstance().getTCPDevice();
    }
}
