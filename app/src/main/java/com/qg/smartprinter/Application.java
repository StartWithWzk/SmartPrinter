package com.qg.smartprinter;

import com.qg.smartprinter.localorder.GlobalEventManager;
import com.qg.smartprinter.localorder.OrderManager;
import com.qg.smartprinter.localorder.PrinterService;
import com.qg.smartprinter.logic.model.User;
import com.qg.smartprinter.util.SharedPreferencesUtils;

public class Application extends android.app.Application {

    private static Application sInstance = null;

    private User mUser;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        PrinterService.start(this);

        SharedPreferencesUtils spu = SharedPreferencesUtils.getInstance();

        spu.init(getApplicationContext(), getPackageName());
        OrderManager.getInstance().init();
        OrderManager.getInstance().setOrderNumber(spu.getOrderNumber());
        GlobalEventManager.init();
    }

    public static Application getInstance() {
        return sInstance;
    }

    public synchronized void setUser(User user) {
        this.mUser = user;
    }

    public synchronized User getUser() {
        return mUser;
    }
}
