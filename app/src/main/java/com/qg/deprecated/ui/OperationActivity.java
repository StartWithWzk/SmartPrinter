package com.qg.deprecated.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.qg.smartprinter.ui.BaseActivity;
import com.qg.smartprinter.R;
import com.qg.smartprinter.ui.ServerSettingActivity;

/**
 * Created by 攀登 on 2016/7/30.
 */
public class OperationActivity extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, OperationActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oper);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.server_setting:
                ServerSettingActivity.start(this);
                break;
            case R.id.order_status:
                OrderStatusActivity.start(this);
                break;
            case R.id.printers_status:
                PrinterStatusActivity.start(this);
                break;
        }
    }
}
