
package com.qg.smartprinter.localorder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ViewAnimator;

import com.qg.common.logger.Linkable;
import com.qg.common.logger.LogFragment;
import com.qg.common.logger.LogView;
import com.qg.smartprinter.ui.BaseActivity;
import com.qg.smartprinter.R;

/**
 * 测试
 */
public abstract class PrinterTestActivity extends BaseActivity {
    public static final String TAG = "PrinterTestActivity";

    private LogFragment mLogFragment;
    private PrinterTestFragment mBluetoothPrinterFragment;
    private View mLogContainer;

    public static void start(Context context) {
        Intent starter = new Intent(context, PrinterTestActivity.class);
        context.startActivity(starter);
    }

    // Whether the Log Fragment is currently shown
    private boolean mLogShown = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.printer_test_activity);
        mLogContainer = findViewById(R.id.sample_output);
        supportInvalidateOptionsMenu();
        changeLog();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            mBluetoothPrinterFragment = getPrinterTestFragment();
            transaction.replace(R.id.sample_content_fragment, mBluetoothPrinterFragment);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_log, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_or_hide_log: {
                mLogShown = !mLogShown;
                changeLog();
                return true;
            }
        }
        return false;
    }

    private void changeLog() {
        if (mLogShown) {
            mLogContainer.setVisibility(View.VISIBLE);
        } else {
            mLogContainer.setVisibility(View.GONE);
        }

    }

    protected abstract PrinterTestFragment getPrinterTestFragment();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_log:
                mLogFragment.clearLog();
                if (mBluetoothPrinterFragment != null) {
                    mBluetoothPrinterFragment.clear();
                }

                break;
            case R.id.scroll_bottom:
                mLogFragment.fullScroll(View.FOCUS_DOWN);
                if (mBluetoothPrinterFragment != null) {
                    mBluetoothPrinterFragment.bottom();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeLogging();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeLogging();
    }

    @Override
    public Linkable initializeLogging() {
        Linkable node = super.initializeLogging();

        // On screen logging via a fragment with a TextView.
        mLogFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        LogView logView = mLogFragment.getLogView();

        node.setNext(logView);

        return logView;
    }
}
