package com.qg.deprecated.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qg.smartprinter.ui.BaseActivity;
import com.qg.smartprinter.R;
import com.qg.deprecated.logic.model.DoneOrder;
import com.qg.deprecated.logic.model.UserOrderInView;
import com.qg.smartprinter.util.FormatChecker;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 攀登 on 2016/8/1.
 * @deprecated
 */
public class DoneOrderActivity extends BaseActivity {

    private static final String TAG = "DoceOrderActivity";
    private EditText company, orderRemark, orderMealFee, orderDisFee, orderPreAmount, userName, userAddress, userTelephone;
    private Button done;
    private EditText expectTime;
    private String company1, expectTime1, orderRemark1, orderMealFee1, orderDisFee1, orderPreAmount1, userName1, userAddress1, userTelephone1;
    public static final String EXTRA_ORDER = "order";
    private DoneOrder mDoneOrder = new DoneOrder();
    private SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void start(Activity context, UserOrderInView order, int requestCode) {
        Intent intent = new Intent(context, DoneOrderActivity.class);
        intent.putExtra(EXTRA_ORDER, order);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_order);
        mDoneOrder.order = ((UserOrderInView) getIntent().getSerializableExtra(EXTRA_ORDER));
        initUI();
    }

    private void initUI() {
        company = (EditText) findViewById(R.id.company_name);
        expectTime = (EditText) findViewById(R.id.expectTime);
        orderRemark = (EditText) findViewById(R.id.orderRemark);
        orderMealFee = (EditText) findViewById(R.id.orderMealFee);
        orderDisFee = (EditText) findViewById(R.id.orderDisFee);
        orderPreAmount = (EditText) findViewById(R.id.orderPreAmount);
        userName = (EditText) findViewById(R.id.userName);
        userAddress = (EditText) findViewById(R.id.userAddress);
        userTelephone = (EditText) findViewById(R.id.userTelephone);
        done = (Button) findViewById(R.id.done_print);
        expectTime.setText(s.format(new Date()));
        expectTime.setOnClickListener(this);
        done.setOnClickListener(this);
    }

    private void becomePrint() {
        int hash = getEditTextToSting();
        if (hash == 0) {
            stringsToOrder();
            PrintPreviewActivity.start1(DoneOrderActivity.this, mDoneOrder, REQUEST_CODE);
        } else if (hash == 1) {
            Toast.makeText(DoneOrderActivity.this, R.string.input_error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(DoneOrderActivity.this, R.string.input_error_1, Toast.LENGTH_SHORT).show();
        }
    }

    private void stringsToOrder() {
        mDoneOrder.company = company1;
        mDoneOrder.expectTime = expectTime1;
        mDoneOrder.orderTime = s.format(new Date());
        mDoneOrder.orderRemark = orderRemark1;
        mDoneOrder.orderMealFee = Integer.valueOf(orderMealFee1);
        mDoneOrder.orderDisFee = Integer.valueOf(orderDisFee1);
        mDoneOrder.orderPreAmount = Integer.valueOf(orderPreAmount1);
        mDoneOrder.userName = userName1;
        mDoneOrder.userAddress = userAddress1;
        mDoneOrder.userTelephone = userTelephone1;
    }

    private int getEditTextToSting() {
        company1 = company.getText().toString();
        expectTime1 = expectTime.getText().toString();
        orderRemark1 = orderRemark.getText().toString();
        orderMealFee1 = orderMealFee.getText().toString();
        orderDisFee1 = orderDisFee.getText().toString();
        orderPreAmount1 = orderPreAmount.getText().toString();
        userName1 = userName.getText().toString();
        userAddress1 = userAddress.getText().toString();
        userTelephone1 = userTelephone.getText().toString();
        if (!company1.trim().equals("") && !expectTime1.trim().equals("") && !orderRemark1.trim().equals("") && !orderMealFee1.trim().equals("") &&
                !orderDisFee1.trim().equals("") && !orderPreAmount1.trim().equals("") && !userName1.trim().equals("") && !userAddress1.trim().equals("")) {
            if ((new FormatChecker()).isContact(userTelephone1)) {
                return 0;
            } else {
                return 1;
            }
        }
        return 2;
    }


    private void chooseTime() {
        TimePickDialogUtil dateTimePicKDialog = new TimePickDialogUtil(
                DoneOrderActivity.this, s.format(new Date()));
        dateTimePicKDialog.dateTimePicKDialog(expectTime);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.done_print:
                becomePrint();
                break;
            case R.id.expectTime:
                chooseTime();
                break;
        }
    }

    private final static int REQUEST_CODE = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }
}