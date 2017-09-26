package com.qg.deprecated.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.google.gson.Gson;
import com.qg.smartprinter.ui.BaseActivity;
import com.qg.deprecated.util.NetworkHelper;
import com.qg.smartprinter.Application;
import com.qg.smartprinter.R;
import com.qg.deprecated.adapter.OrderAdapter;
import com.qg.deprecated.logic.model.Order1;
import com.qg.deprecated.logic.result.OrderStatusResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 攀登 on 2016/7/25.
 */
public class OrderStatusActivity extends BaseActivity implements View.OnClickListener {

    public static void start(Context context) {
        Intent intent = new Intent(context, OrderStatusActivity.class);
        context.startActivity(intent);
    }

    private final static String TAG = "OrderStatusActivity";
    private ListView mListView;
    private View view;
    private RelativeLayout printnow;
    private RelativeLayout printfinish;
    private List<Order1> mList1 = new ArrayList<>();
    private List<Order1> mList2 = new ArrayList<>();
    private OrderAdapter adapter;
    private Timer mTimer;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        initUI();
        startTimer();
    }

    private void getOrderTyped() {
        showLoadingDialog();
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                return NetworkHelper.getInstance().doGetFromServer("/order/typed/" + Application.getInstance().getUser().getId());
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // 处理结果
                OrderStatusResult result = new Gson().fromJson(s, OrderStatusResult.class);
                if (result.isOk()) {
                    if (result.data != null) {
                        if (result.data.size() > 0) {
                            mList2.clear();
                            mList2.addAll(result.data);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(OrderStatusActivity.this, R.string.noneorder, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(OrderStatusActivity.this, R.string.get_fail, Toast.LENGTH_LONG).show();
                }
                dismissMyDialog();
            }
        }.execute();
    }

    private void getOrderTyping() {
        showLoadingDialog();
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                return NetworkHelper.getInstance().doGetFromServer("/order/typing/" + Application.getInstance().getUser().getId());
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // 处理结果
                OrderStatusResult result = new Gson().fromJson(s, OrderStatusResult.class);
                if (result.isOk()) {
                    if (result.data != null) {
                        if (result.data.size() > 0) {
                            mList1.clear();
                            mList1.addAll(result.data);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(OrderStatusActivity.this, R.string.noneorder, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(OrderStatusActivity.this, R.string.get_fail, Toast.LENGTH_LONG).show();
                }
                dismissMyDialog();
            }
        }.execute();
    }

    private void initUI() {
        mListView = (ListView) findViewById(R.id.listview);
        printnow = (RelativeLayout) findViewById(R.id.printnow);
        printfinish = (RelativeLayout) findViewById(R.id.printfinish);

        view = LayoutInflater.from(OrderStatusActivity.this).inflate(R.layout.order_listview_head, null);
        mListView.addHeaderView(view);
        printnow.setOnClickListener(this);
        printfinish.setOnClickListener(this);

        adapter = new OrderAdapter(OrderStatusActivity.this, R.layout.order_item, mList1);
        mListView.setAdapter(adapter);
    }

    private void printNow() {
        adapter = new OrderAdapter(OrderStatusActivity.this, R.layout.order_item, mList1);
        mListView.setAdapter(adapter);
    }

    private void printFinish() {
        adapter = new OrderAdapter(OrderStatusActivity.this, R.layout.order_item, mList2);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.printnow:
                printnow.setBackgroundColor(getResources().getColor(R.color.colorSelect));
                printfinish.setBackgroundColor(getResources().getColor(R.color.colorNotSelect));
                printNow();
                break;
            case R.id.printfinish:
                printnow.setBackgroundColor(getResources().getColor(R.color.colorNotSelect));
                printfinish.setBackgroundColor(getResources().getColor(R.color.colorSelect));
                printFinish();
                break;
        }
    }

    class RefreshTask extends TimerTask {

        @Override
        public void run() {
            //用handler更新数据操作
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // 更新数据方法
                    getOrderTyped();
                    getOrderTyping();
                }
            });

        }
    }

    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new RefreshTask(), 0, 15 * 1000);
        }
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}
