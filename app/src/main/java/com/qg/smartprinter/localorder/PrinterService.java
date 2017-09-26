package com.qg.smartprinter.localorder;

import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.qg.common.logger.Log;
import com.qg.smartprinter.Injection;
import com.qg.smartprinter.data.source.Message;
import com.qg.smartprinter.data.source.Order;
import com.qg.smartprinter.data.source.OrdersDataSource;
import com.qg.smartprinter.localorder.device.WifiDevice;
import com.qg.smartprinter.util.DateUtils;
import com.qg.smartprinter.util.rxbus.RxBus;
import com.qg.smartprinter.util.scheduler.BaseSchedulerProvider;

import rx.functions.Action1;

public class PrinterService extends Service {
    private static final String TAG = "PrinterService";

    private OrdersDataSource mOrdersRepository;

    private static RxBus sBus = new RxBus();

    public static RxBus getBus() {
        return sBus;
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, PrinterService.class);
        context.startService(starter);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mOrdersRepository = Injection.provideOrdersRepository(getApplicationContext());

        BaseSchedulerProvider schedulerProvider = Injection.provideBaseSchedulerProvider();
        sBus.asObservable()
                .onBackpressureBuffer()
                .onBackpressureDrop(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        Log.d(TAG, "Lost event!!");
                    }
                })
                .observeOn(schedulerProvider.computation())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if (o instanceof InsertDBMessageEvent) {
                            InsertDBMessageEvent event = (InsertDBMessageEvent) o;
                            insertDBMessage(event.orderId, event.readBuf, event.type);
                        } else if (o instanceof InsertDBOrderEvent) {
                            InsertDBOrderEvent event = (InsertDBOrderEvent) o;
                            insertDBOrder(event.order);
                        } else if (o instanceof UpdateDBOrderStatusEvent) {
                            UpdateDBOrderStatusEvent event = ((UpdateDBOrderStatusEvent) o);
                            updateDBOrder(event.orderId, event.statusString);
                        } else if (o instanceof UpdateIPEvent) {
                            UpdateIPEvent event = (UpdateIPEvent) o;
                            WifiManager m = (WifiManager) getSystemService(WIFI_SERVICE);
                            int ipAddress = m.getConnectionInfo().getIpAddress();
                            event.mDevice.setIP(ipAddress);
                        }
                    }
                });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mOrdersRepository = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 在数据库中插入订单
     */
    private void insertDBOrder(Order order) {
        mOrdersRepository.sendOrder(order);
    }

    /**
     * 在数据库中更新订单状态
     */
    private void updateDBOrder(long orderId, String statusString) {
        mOrdersRepository.updateOrder(String.valueOf(orderId), statusString);
    }

    /**
     * 在数据库中插入报文
     */
    private void insertDBMessage(long orderId, byte[] readBuf, String type) {
        Message message = new Message(readBuf, type, DateUtils.getDateString(), String.valueOf(orderId));
        mOrdersRepository.receiveMessage(message);
    }

    public static class InsertDBOrderEvent {
        Order order;

        InsertDBOrderEvent(Order o) {
            this.order = o;
        }
    }

    public static class UpdateDBOrderStatusEvent {
        long orderId;
        String statusString;

        public UpdateDBOrderStatusEvent(long orderId, String statusString) {
            this.orderId = orderId;
            this.statusString = statusString;
        }
    }

    public static class UpdateDBOrderTypeEvent {
        long orderId;
        String type;

        public UpdateDBOrderTypeEvent(long orderId, String type) {
            this.orderId = orderId;
            this.type = type;
        }
    }

    public static class InsertDBMessageEvent {
        long orderId;
        byte[] readBuf;
        String type;

        public InsertDBMessageEvent(long orderId, byte[] readBuf, String type) {
            this.orderId = orderId;
            this.readBuf = readBuf;
            this.type = type;
        }
    }

    public static class UpdateIPEvent {
        WifiDevice mDevice;

        public UpdateIPEvent(WifiDevice device) {
            mDevice = device;
        }
    }
}
