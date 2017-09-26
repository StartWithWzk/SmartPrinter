package com.qg.smartprinter.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qg.smartprinter.R;
import com.qg.smartprinter.ui.cookbook.CookbookActivity;
import com.qg.smartprinter.localorder.status.localstatus.LocalStatusActivity;
import com.qg.smartprinter.util.SharedPreferencesUtils;
import com.squareup.picasso.Picasso;

/**
 * 商家信息
 */
public class BusinessInfoActivity extends BaseActivity {

    private TextView mAdvertisementView;
    private TextView mQRCodeTextView;
    private TextView mNameView;
    private TextView mPhoneView;
    private TextView mAddressView;
    private ImageView mLogoView;

    public static void start(Context context) {
        Intent starter = new Intent(context, BusinessInfoActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_info);
        setupToolbar();
        setupView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupView() {
        mLogoView = (ImageView) findViewById(R.id.logo);
        mQRCodeTextView = (TextView) findViewById(R.id.qr_code_text);
        mAdvertisementView = (TextView) findViewById(R.id.advertisement);
        mNameView = (TextView) findViewById(R.id.store_name);
        mAddressView = (TextView) findViewById(R.id.store_address);
        mPhoneView = (TextView) findViewById(R.id.store_phone);

        findViewById(R.id.order).setOnClickListener(this);
    }

    private void updateView() {
        SharedPreferencesUtils su = SharedPreferencesUtils.getInstance();
        String uri = su.getString(SharedPreferencesUtils.BUSINESS_LOGO_URI, "");
        if (!TextUtils.isEmpty(uri)) {
            Picasso.with(this)
                    .load(uri)
                    .into(mLogoView);
        }
        mNameView.setText(su.getString(SharedPreferencesUtils.BUSINESS_NAME, ""));
        mAddressView.setText(su.getString(SharedPreferencesUtils.BUSINESS_ADDRESS, ""));
        mPhoneView.setText(su.getString(SharedPreferencesUtils.BUSINESS_PHONE, ""));
        mAdvertisementView.setText(su.getString(SharedPreferencesUtils.BUSINESS_AD, ""));
        mQRCodeTextView.setText(su.getString(SharedPreferencesUtils.BUSINESS_QR_CODE, ""));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_business_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                EditBusinessInfoActivity.start(this);
                return true;
            case R.id.status:
                LocalStatusActivity.start(this);
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.order:
                CookbookActivity.start(this);
                break;
        }
    }
}
