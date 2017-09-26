package com.qg.smartprinter.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.qg.common.logger.Log;
import com.qg.smartprinter.R;
import com.qg.smartprinter.localorder.DevicesManager;
import com.qg.smartprinter.localorder.OrderManager;
import com.qg.smartprinter.localorder.device.BluetoothDeviceWrapper;
import com.qg.smartprinter.localorder.device.RemoteDevice;
import com.qg.smartprinter.localorder.device.WifiDevice;
import com.qg.smartprinter.localorder.event.Events;
import com.qg.smartprinter.localorder.messages.BOrder;
import com.qg.smartprinter.localorder.messages.BOrderData;
import com.qg.smartprinter.localorder.messages.BPhoto;
import com.qg.smartprinter.localorder.messages.BQRCode;
import com.qg.smartprinter.localorder.messages.BText;
import com.qg.smartprinter.localorder.selectdevice.SelectDeviceActivity;
import com.qg.smartprinter.logic.model.LocalOrder;
import com.qg.smartprinter.util.BinaryUtil;
import com.qg.smartprinter.util.BitmapUtil;
import com.qg.smartprinter.util.SharedPreferencesUtils;

public class LocalPrintPreviewActivity extends BaseActivity {

    private static final String TAG = "LocalPrintPreviewActivity";

    public static final String EXTRA_ORDER = "order";
    private static final int REQUEST_IMAGE = 1;
    private static final int REQUEST_DEVICE = 2;

    private ImageView mAddLogo;

    private boolean hasPhoto;
    private String mQRCodeText;
    private BPhoto mBPhoto;
    private LocalOrder mOrder;

    private int mPrintDeviceType = -1;

    public static void start(Context context, LocalOrder order) {
        Intent starter = new Intent(context, LocalPrintPreviewActivity.class);
        starter.putExtra(EXTRA_ORDER, order);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_print_preview);

        mOrder = (LocalOrder) getIntent().getSerializableExtra(EXTRA_ORDER);
        SharedPreferencesUtils su = SharedPreferencesUtils.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.print).setOnClickListener(this);
        findViewById(R.id.select_device).setOnClickListener(this);

        TextView printTextView = (TextView) findViewById(R.id.print_text);
        mAddLogo = (ImageView) findViewById(R.id.add_logo);
        mQRCodeText = su.getString(SharedPreferencesUtils.BUSINESS_QR_CODE, "");
        String logoUri = su.getString(SharedPreferencesUtils.BUSINESS_LOGO_URI, "");
        hasPhoto = !TextUtils.isEmpty(logoUri);
        if (hasPhoto) {
            loadUri(Uri.parse(logoUri));
        }

        printTextView.setText(mOrder.getPrintString());
        if (!TextUtils.isEmpty(mQRCodeText)) {
            ImageView qrCodeView = (ImageView) findViewById(R.id.qr_code);
            try {
                qrCodeView.setImageBitmap(BinaryUtil.encodeAsBitmap(mQRCodeText, 2 * qrCodeView.getHeight()));
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.print:
                onPrintClick();
                break;
            case R.id.select_device:
                SelectDeviceActivity.start(this, REQUEST_DEVICE);
                break;
        }
    }

    private void onPrintClick() {
        if (hasPhoto && mBPhoto == null) {
            Toast.makeText(LocalPrintPreviewActivity.this, R.string.loading_logo, Toast.LENGTH_SHORT).show();
            return;
        }
        BOrderData bOrderData = new BOrderData();
        if (mBPhoto != null) {
            bOrderData.add(mBPhoto);
        }
        bOrderData.add(new BText(mOrder.getPrintString()));
        String url = mQRCodeText;
        if (!TextUtils.isEmpty(url)) {
            bOrderData.add(new BQRCode(url));
        }
        BOrder printOrder = BOrder.fromOrderData(mOrder.getOrderId(), bOrderData, 0);
        if (mPrintDeviceType == -1) {
            Toast.makeText(this, R.string.not_select_device, Toast.LENGTH_SHORT).show();
            return;
        }
        RemoteDevice device;
        if (mPrintDeviceType == WifiDevice.TYPE) {
            device = DevicesManager.getInstance().getTCPDevice();
        } else if (mPrintDeviceType == BluetoothDeviceWrapper.TYPE) {
            device = DevicesManager.getInstance().getBTDevice();
        } else {
            Toast.makeText(this, R.string.unknown_device, Toast.LENGTH_SHORT).show();
            return;
        }
        if (device == null) {
            Toast.makeText(this, R.string.reselect_device, Toast.LENGTH_SHORT).show();
            return;
        }
        OrderManager.getBus().post(new Events.OrderEvent(device, printOrder));
        Toast.makeText(this, R.string.printing, Toast.LENGTH_SHORT).show();

        // Go back to business info screen.
        Intent intent = new Intent(LocalPrintPreviewActivity.this, BusinessInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_IMAGE:
                Uri data1 = data.getData();
                loadUri(data1);
                break;
            case REQUEST_DEVICE:
                // Return a device type.
                mPrintDeviceType = data.getIntExtra(SelectDeviceActivity.EXTRA_DEVICE_TYPE, -1);
                break;
            default:
                break;
        }
    }

    private void loadUri(Uri data1) {
        new BitmapUtil.BWBitmapTask(this, data1, new BitmapUtil.BWBitmapTask.Handler() {
            @Override
            public void pre() {
                showLoadingDialog();
            }

            @Override
            public void post() {
                dismissMyDialog();
            }

            @Override
            public void success(BitmapUtil.Result result) {
                mAddLogo.setImageBitmap(result.bitmap);
                mBPhoto = new BPhoto(result.bytes);
                Log.v(TAG, "onPostExecute: success");
            }

            @Override
            public void failure() {
                Toast.makeText(LocalPrintPreviewActivity.this, getString(R.string.error_add_logo), Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }
}
