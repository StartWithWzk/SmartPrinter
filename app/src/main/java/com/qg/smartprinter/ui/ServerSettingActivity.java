package com.qg.smartprinter.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.qg.smartprinter.R;
import com.qg.smartprinter.util.SharedPreferencesUtils;

/**
 * 服务器设置
 */
public class ServerSettingActivity extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, ServerSettingActivity.class);
        context.startActivity(starter);
    }

    private EditText mIPEditText;
    private EditText mPortEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_setting);
        mIPEditText = ((EditText) findViewById(R.id.ip));
        mPortEditText = ((EditText) findViewById(R.id.port));
        mIPEditText.setText(SharedPreferencesUtils.getInstance().getServerIP());
        mPortEditText.setText(String.valueOf(SharedPreferencesUtils.getInstance().getServerPort()));
        View okView = findViewById(R.id.ok);
        if (okView != null) {
            okView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferencesUtils.getInstance()
                            .setServer(
                                    mIPEditText.getText().toString(),
                                    Integer.valueOf(mPortEditText.getText().toString()));
                    finish();
                }
            });
        }
    }
}
