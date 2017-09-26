package com.qg.smartprinter.localorder.messages;

/**
 * Wifi报文处理器
 */
public class WifiAddition {
    /**
     * Transfer order to wifi order. This method would change the parameter.
     */
    public static BOrder toWifiOrder(BOrder order, int ip) {
        order.stamp = ip;
        return order;
    }

    public static int getIP(BOrderStatus wifiOrderStatus) {
        return (int) wifiOrderStatus.seconds;
    }

    public static int getIP(BResponse wifiStatusResponse) {
        return (int) wifiStatusResponse.responseType;
    }
}

