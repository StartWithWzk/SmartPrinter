package com.qg.common.caughtexception;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author TZH
 * @version 1.0
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "ExceptionHandler";

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;

    public ExceptionHandler(Thread.UncaughtExceptionHandler defaultHandler, Context context) {
        mDefaultHandler = defaultHandler;
        mContext = context;
        Log.d(TAG, "ExceptionHandler: Create");
    }

    @Override
    public final void uncaughtException(Thread thread, Throwable throwable) {
        onCaughtException(thread, throwable);
        mDefaultHandler.uncaughtException(thread, throwable);
    }

    private void onCaughtException(Thread thread, Throwable e) {
        test0(thread, e);
    }

    private void test0(Thread thread, Throwable e) {
        Intent intent = new Intent();
        intent.setAction("com.qg.common.CAUGHT_EXCEPTION");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK); // required when starting from Application
        intent.putExtra(CaughtExceptionActivity.EXTRA_THROWABLE, e);

        mContext.startActivity(intent);
        Log.d(TAG, "handleUncaughtException: StartActivity");
    }

}
