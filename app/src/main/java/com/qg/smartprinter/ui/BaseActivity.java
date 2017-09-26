package com.qg.smartprinter.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.qg.common.caughtexception.ExceptionHandler;
import com.qg.common.logger.FileLogNode;
import com.qg.common.logger.Linkable;
import com.qg.common.logger.Log;
import com.qg.common.logger.LogWrapper;
import com.qg.smartprinter.R;
import com.qg.smartprinter.ui.dialog.MyLoadingDialog;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "BaseActivity";

    private static final String DIALOG = "dialog";
    private static final boolean WRITE_TO_FILE = true;
    protected FragmentManager mFragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(defaultHandler, getApplicationContext()));
        Log.d(TAG, "onCreate: setDefaultUn");
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeLogging();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initializeLogging();
    }

    /**
     * Set up targets to receive log data
     *
     * @return The last node of the log list.
     */
    public Linkable initializeLogging() {
        Linkable tail;
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        // Wraps Android's native log framework
        LogWrapper logWrapper = new LogWrapper();
        tail = (Linkable) Log.setLogNode(logWrapper);

        if (WRITE_TO_FILE) {
            FileLogNode fileLogNode = new FileLogNode();
            tail = (Linkable) tail.setNext(fileLogNode);
        }
        Log.i(TAG, "Ready");
        return tail;
    }

    @Override
    public void onClick(View v) {
    }

    public void showLoadingDialog() {
        showLoadingDialog(getString(R.string.loading));
    }

    public void showLoadingDialog(String msg) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();

        dismissMyDialog();

        DialogFragment dialog = MyLoadingDialog.newInstance(msg);
        dialog.show(ft, DIALOG);
    }

    public void dismissMyDialog() {
        Fragment prev = mFragmentManager.findFragmentByTag(DIALOG);
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
    }
}
