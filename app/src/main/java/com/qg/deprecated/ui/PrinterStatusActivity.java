package com.qg.deprecated.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qg.smartprinter.ui.BaseActivity;
import com.qg.deprecated.adapter.PrinterAdapter;
import com.qg.deprecated.logic.result.PrinterStatusResult;
import com.qg.deprecated.util.NetworkHelper;
import com.qg.smartprinter.Application;
import com.qg.smartprinter.R;
import com.qg.deprecated.logic.model.PrinterStatus;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 攀登 on 2016/7/31.
 */
public class PrinterStatusActivity extends BaseActivity implements View.OnClickListener {

    private ListView mListView;
    private View view;
    private ArrayList<PrinterStatus> mList = new ArrayList<>();
    private Timer mTimer;
    private Handler mHandler = new Handler();
    private PrinterAdapter adaper;

    public static void start(Context context) {
        Intent intent = new Intent(context, PrinterStatusActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printers_status);
        initUI();
        intiData();
    }

    private void initUI() {
        mListView = (ListView) findViewById(R.id.listview);
        view = LayoutInflater.from(PrinterStatusActivity.this).inflate(R.layout.order_listview_head, null);
        ((TextView) view.findViewById(R.id.left)).setText(R.string.printer_id);
        ((TextView) view.findViewById(R.id.right)).setText(R.string.printer_status);
        mListView.addHeaderView(view);
        adaper = new PrinterAdapter(PrinterStatusActivity.this, R.layout.order_item, mList);
        mListView.setAdapter(adaper);
    }

    private void intiData() {
        startTimer();
//        new RefreshTask();
    }

    private String getPrinterStatus() {
        return NetworkHelper.getInstance().doGetFromServer("/printer/" + Application.getInstance().getUser().getId());
    }

    @Override
    public void onClick(View v) {

    }

    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new RefreshTask(), 0, 10 * 1000);
        }
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    class RefreshTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    showLoadingDialog();
                    new AsyncTask<String, Void, String>() {

                        @Override
                        protected String doInBackground(String... params) {
                            return getPrinterStatus();
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            // 处理结果
                            PrinterStatusResult result = new Gson().fromJson(s, PrinterStatusResult.class);
                            if (result.isOk()) {
                                if (result.data != null) {
                                    if (result.data.size() > 0) {
                                        mList.clear();
                                        mList.addAll(result.data);
                                        adaper.notifyDataSetChanged();
                                    }
                                } else {
                                    Toast.makeText(PrinterStatusActivity.this, R.string.noneorder, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(PrinterStatusActivity.this, R.string.get_fail, Toast.LENGTH_LONG).show();
                            }
                            dismissMyDialog();
                        }
                    }.execute();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}
