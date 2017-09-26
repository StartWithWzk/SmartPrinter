package com.qg.smartprinter.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.qg.common.logger.Log;
import com.qg.smartprinter.R;
import com.qg.smartprinter.util.SharedPreferencesUtils;
import com.squareup.picasso.Picasso;

/**
 * 商家信息编辑
 */
public class EditBusinessInfoActivity extends BaseActivity {
    private static final String TAG = "EditBusinessInfoActivity";

    private static final int REQUEST_IMAGE = 1;
    private String mURI;

    public static void start(Context context) {
        Intent starter = new Intent(context, EditBusinessInfoActivity.class);
        context.startActivity(starter);
    }

    private EditText mAdvertisementView;
    private EditText mQRCodeView;
    private EditText mNameView;
    private EditText mPhoneView;
    private EditText mAddressView;
    private ImageView mLogoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business_info);
        setupToolbar();
        setupView();
    }


    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    }

    private void setupView() {
        mLogoView = (ImageView) findViewById(R.id.logo);
        findViewById(R.id.logo_layout).setOnClickListener(this);

        mAdvertisementView = ((EditText) findViewById(R.id.advertisement));
        mQRCodeView = ((EditText) findViewById(R.id.qr_code_text));
        mNameView = (EditText) findViewById(R.id.store_name);
        mAddressView = (EditText) findViewById(R.id.store_address);
        mPhoneView = (EditText) findViewById(R.id.store_phone);

        SharedPreferencesUtils su = SharedPreferencesUtils.getInstance();
        mURI = su.getString(SharedPreferencesUtils.BUSINESS_LOGO_URI, "");
        String uri = mURI;
        Log.d(TAG, "updateView: uri" + uri);
        if (!TextUtils.isEmpty(uri)) {
            Picasso.with(this)
                    .load(uri)
                    .into(mLogoView);
        }
        mAdvertisementView.setText(su.getString(SharedPreferencesUtils.BUSINESS_AD, ""));
        mQRCodeView.setText(su.getString(SharedPreferencesUtils.BUSINESS_QR_CODE, ""));
        mNameView.setText(su.getString(SharedPreferencesUtils.BUSINESS_NAME, ""));
        mAddressView.setText(su.getString(SharedPreferencesUtils.BUSINESS_ADDRESS, ""));
        mPhoneView.setText(su.getString(SharedPreferencesUtils.BUSINESS_PHONE, ""));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logo_layout:
                requestLogo(REQUEST_IMAGE);
                break;
            case R.id.fab:
                saveInfo();
                break;
        }
    }

    private void requestLogo(int requestCode) {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_IMAGE:
                Uri uri = data.getData();
                mURI = uri.toString();
                Picasso.with(this)
                        .load(uri)
                        .into(mLogoView);
                break;
            default:
                break;
        }
    }

    /**
     * 保存商家信息
     */
    private void saveInfo() {
        SharedPreferencesUtils.getInstance().setString(
                SharedPreferencesUtils.BUSINESS_LOGO_URI, mURI,
                SharedPreferencesUtils.BUSINESS_NAME, mNameView.getText().toString(),
                SharedPreferencesUtils.BUSINESS_ADDRESS, mAddressView.getText().toString(),
                SharedPreferencesUtils.BUSINESS_PHONE, mPhoneView.getText().toString(),
                SharedPreferencesUtils.BUSINESS_AD, mAdvertisementView.getText().toString(),
                SharedPreferencesUtils.BUSINESS_QR_CODE, mQRCodeView.getText().toString()
        );
        finish();
    }
}
