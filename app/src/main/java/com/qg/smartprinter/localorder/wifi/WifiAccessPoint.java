package com.qg.smartprinter.localorder.wifi;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;

import static com.qg.smartprinter.localorder.wifi.AccessPoint.getSecurity;

/**
 * Wifi访问点.
 * Created by TZH on 2016/10/14.
 */

public class WifiAccessPoint {

    /**
     * Wifi是否需要密码
     */
    private boolean mNeedPassword;
    /**
     * 是否支持此类型的Wifi
     */
    private boolean mSupported;

   /**
     * Wi-fi的地址
     */
    private String BSSID;

    /**
     * Wi-fi的名称
     */
    private String SSID;

    /**
     * 安全类型
     */
    private @AccessPoint.Security
    int mSecurity;

    /**
     * 密码
     */
    private String mPassword;

    public void setSecurity(@AccessPoint.Security int security) {
        mSecurity = security;
    }

    /**
     * 对应的Configuration
     */
    private WifiConfiguration mConfiguration;

    private WifiAccessPoint() {
    }

    public static WifiAccessPoint from(ScanResult result) {
        WifiAccessPoint point = new WifiAccessPoint();
        point.BSSID = result.BSSID;
        point.SSID = result.SSID;
        point.mSecurity = getSecurity(result);
        point.mNeedPassword = WifiUtil.needPassword(point.mSecurity);
        point.mSupported = WifiUtil.isSupport(point.mSecurity);
        return point;
    }
}
