package com.qg.common.caughtexception;

import android.app.AlarmManager;
import android.app.LauncherActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ricco
 * @version 1.1
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "ExceptionHandler";

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;

    private static ExceptionHandler INSTANCE = null;

    // 单例实现
    public static ExceptionHandler getINSTANCE(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ExceptionHandler(context);
        }
        return INSTANCE;
    }

    // 保证只有一个实例
    private ExceptionHandler(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        Log.d(TAG, "ExceptionHandler: Create");
    }

    @Override
    public final void uncaughtException(Thread thread, Throwable throwable) {
        if (!handleException(thread, throwable) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, throwable);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private boolean handleException(Thread thread, Throwable throwable) {
        Intent intent = new Intent();
        intent.setAction("com.qg.common.CAUGHT_EXCEPTION");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK); // required when starting from Application
        intent.putExtra(CaughtExceptionActivity.EXTRA_THROWABLE, throwable);

        mContext.startActivity(intent);
        Log.d(TAG, "handleUncaughtException: StartActivity");
        return true;
    }
}
