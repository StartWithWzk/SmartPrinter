package com.qg.smartprinter.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.qg.smartprinter.localorder.wifi.Config;
import com.qg.smartprinter.logic.model.Business;

/**
 * Created by TZH on 2016/7/25.
 */
public class SharedPreferencesUtils {
    public static final String SERVER_IP = "serverIP";
    public static final String SERVER_PORT = "serverPort";
    public static final String BUSINESS_NAME = "businessName";
    public static final String BUSINESS_ADDRESS = "businessAddress";
    public static final String BUSINESS_PHONE = "businessPhone";
    public static final String BUSINESS_AD = "businessAdvertisement";
    public static final String BUSINESS_QR_CODE = "businessQRCode";
    public static final String BUSINESS_LOGO_URI = "businessLogoURI";
    private static final String ORDER_NUMBER = "orderNumber";

    private static SharedPreferencesUtils sInstance = new SharedPreferencesUtils();

    public static SharedPreferencesUtils getInstance() {
        return sInstance;
    }

    private SharedPreferences mSharedPreferences;

    private SharedPreferencesUtils() {
    }

    public void init(Context context, String name) {
        mSharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public <T> T getObj(String key, Class<T> cls) {
        String jsonString = getString(key, null);
        if (jsonString != null) {
            return JsonUtil.fromJson(jsonString, cls);
        } else {
            return null;
        }
    }

    public void saveObj(String key, Object obj) {
        setString(key, JsonUtil.toJson(obj));
    }

    public void setServer(String ip, int port) {
        mSharedPreferences.edit().putString(SERVER_IP, ip).putInt(SERVER_PORT, port).apply();
    }

    public String getServerIP() {
        return mSharedPreferences.getString(SERVER_IP, Config.SERVER_IP);
    }

    public int getServerPort() {
        return mSharedPreferences.getInt(SERVER_PORT, Config.SERVER_PORT);
    }

    public void setString(String key, String value, String... kvs) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(key, value);
        for (int i = 0; i + 1 < kvs.length; i += 2) {
            edit.putString(kvs[i], kvs[i + 1]);
        }
        edit.apply();
    }

    public String getString(String key, String def) {
        return mSharedPreferences.getString(key, def);
    }

    public void setOrderNumber(int orderNumber) {
        mSharedPreferences.edit()
                .putInt(ORDER_NUMBER, orderNumber)
                .apply();
    }

    public int getOrderNumber() {
        return mSharedPreferences.getInt(ORDER_NUMBER, 0);
    }
}
