package com.qg.smartprinter.localorder.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.qg.smartprinter.localorder.wifi.AccessPoint.Security;

import static com.qg.smartprinter.localorder.wifi.AccessPoint.SECURITY_EAP;
import static com.qg.smartprinter.localorder.wifi.AccessPoint.SECURITY_NONE;
import static com.qg.smartprinter.localorder.wifi.AccessPoint.SECURITY_PSK;
import static com.qg.smartprinter.localorder.wifi.AccessPoint.SECURITY_WEP;
import static com.qg.smartprinter.localorder.wifi.AccessPoint.convertToQuotedString;
import static com.qg.smartprinter.localorder.wifi.AccessPoint.getSecurity;

/**
 * 用于处理Wi-fi扫描结果，连接Wi-fi
 */
public class WifiUtil {
    public static boolean needPassword(@NonNull ScanResult r) {
        return getSecurity(r) == SECURITY_NONE;
    }

    public static boolean needPassword(@Security int s) {
        return s == SECURITY_NONE;
    }

    public static boolean isSupport(@NonNull ScanResult r) {
        return getSecurity(r) != SECURITY_EAP;
    }

    public static boolean isSupport(@Security int s) {
        return s != SECURITY_EAP;
    }

    public static void connect(@NonNull Context context, @NonNull ScanResult r) {
        connect(context, r, null);
    }

    public static void connect(@NonNull Context context, @NonNull ScanResult r, @Nullable String password) {
        WifiConfiguration config = getConfig(r, password);
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // Remember id
        int netId = manager.addNetwork(config);
        if (netId != -1) {
            // success
            manager.disconnect();
            manager.enableNetwork(netId, true);
            manager.reconnect();
        }
    }

    /**
     * 通过ScanResult和密码（可选）生成 WifiConfiguration
     *
     * @param r        扫描结果
     * @param password 密码
     * @return 对应的WifiConfiguration
     */
    public static WifiConfiguration getConfig(@NonNull ScanResult r, @Nullable String password) {
        WifiConfiguration config = new WifiConfiguration();

//        if (!AccessPoint.isSaved(r)) {
        config.SSID = convertToQuotedString(r.SSID);
//        } else {
//            config.networkId = AccessPoint.getNetworkId(r);
//        }

        switch (getSecurity(r)) {
            case SECURITY_NONE:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                break;

            case SECURITY_WEP:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
                // 检查密码
                if (!TextUtils.isEmpty(password)) {
                    int length = password.length();
                    // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                    if ((length == 10 || length == 26 || length == 58) &&
                            password.matches("[0-9A-Fa-f]*")) {
                        config.wepKeys[0] = password;
                    } else {
                        config.wepKeys[0] = convertToQuotedString(password);
                    }
                }
                break;

            case SECURITY_PSK:
                config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
                // 检查密码
                if (!TextUtils.isEmpty(password)) {
                    if (password.matches("[0-9A-Fa-f]{64}")) {
                        config.preSharedKey = password;
                    } else {
                        config.preSharedKey = convertToQuotedString(password);
                    }
                }
                break;

            default:
                return null;
        }
        return config;
    }
}
