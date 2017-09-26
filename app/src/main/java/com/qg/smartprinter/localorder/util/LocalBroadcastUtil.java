package com.qg.smartprinter.localorder.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;

import com.qg.smartprinter.localorder.Constants;

import java.io.Serializable;

/**
 * 简易辅助类
 */
public class LocalBroadcastUtil {
    public static IntentFilter newIntentFilter(String... actions) {
        IntentFilter intentFilter = new IntentFilter();
        for (String action : actions) {
            intentFilter.addAction(action);
        }
        return intentFilter;
    }

    public static void sendBroadcast(Context context, String action) {
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(new Intent(action));
    }

    public static void sendBroadcast(Context context, String action, int obj) {
        Intent intent = new Intent(action);
        intent.putExtra(Constants.OBJECT, obj);
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(intent);
    }

    public static <T extends Serializable> void sendBroadcast(Context context, String action, T obj) {
        Intent intent = new Intent(action);
        intent.putExtra(Constants.OBJECT, obj);
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(intent);
    }

    public static <T extends Parcelable> void sendBroadcast(Context context, String action, T obj) {
        Intent intent = new Intent(action);
        intent.putExtra(Constants.OBJECT, obj);
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(intent);
    }
}
