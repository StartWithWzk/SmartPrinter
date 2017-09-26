package com.qg.smartprinter.localorder.status.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.qg.smartprinter.Injection;
import com.qg.smartprinter.R;
import com.qg.smartprinter.ui.BaseActivity;
import com.qg.smartprinter.util.ActivityUtils;

public class MessageActivity extends BaseActivity {

    private static final String ARG_ORDER_ID = "ORDER_ID";

    public static void start(Context context, String orderId) {
        Intent starter = new Intent(context, MessageActivity.class);
        starter.putExtra(ARG_ORDER_ID, orderId);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        MessageFragment fragment =
                (MessageFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        String orderId = getIntent().getStringExtra(ARG_ORDER_ID);
        if (fragment == null) {
            fragment = MessageFragment.newInstance(orderId);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    fragment, R.id.contentFrame);
        }

        new MessagePresenter(
                orderId,
                Injection.provideOrdersRepository(this),
                fragment,
                Injection.provideBaseSchedulerProvider()
        );
    }

}
