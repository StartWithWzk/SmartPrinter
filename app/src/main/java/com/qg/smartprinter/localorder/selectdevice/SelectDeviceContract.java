package com.qg.smartprinter.localorder.selectdevice;

/**
 * @author TZH
 * @version 1.0
 */
public class SelectDeviceContract {

    // 乱写的View
    interface View {
        void showBTDevices();
        void showEnableBT();
        void updateWifiStatus();
        void updateBTStatus();
    }

    // 乱写的Presenter
    interface Presenter {
        void connectWifi();

        void disconnectWifi();

        void connectBT();

        void disconnectBT();
    }
}
