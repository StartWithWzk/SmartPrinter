package com.qg.smartprinter.localorder;

import com.qg.common.logger.Log;
import com.qg.smartprinter.Injection;
import com.qg.smartprinter.data.source.Order;
import com.qg.smartprinter.localorder.device.BluetoothDeviceWrapper;
import com.qg.smartprinter.localorder.device.RemoteDevice;
import com.qg.smartprinter.localorder.device.WifiDevice;
import com.qg.smartprinter.localorder.event.Events;
import com.qg.smartprinter.localorder.messages.BOrder;
import com.qg.smartprinter.localorder.messages.WifiAddition;
import com.qg.smartprinter.util.SharedPreferencesUtils;
import com.qg.smartprinter.util.rxbus.RxBus;

import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * 订单管理器
 */
public class OrderManager {
    private static final String TAG = "OrderManager";

    private int mOrderNumber = 0;

    private RxBus mBus;

    private CompositeSubscription mSubscriptions;

    private static OrderManager sInstance = new OrderManager();

    public static OrderManager getInstance() {
        return sInstance;
    }

    private OrderManager() {
        mBus = new RxBus();
        mSubscriptions = new CompositeSubscription();
    }

    public OrderManager init() {
        mSubscriptions.clear();
        Subscription subscription = mBus.asObservable()
                .onBackpressureBuffer()
                .onBackpressureDrop(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        Log.e(TAG, "call: Order Lost!!" + o.getClass().getSimpleName());
                        if (o instanceof Events.OrderEvent) {
                            RxBus.getDefault().post(new Events.OrderLostEvent(((Events.OrderEvent) o)));
                        }
                    }
                })
                .observeOn(Injection.provideBaseSchedulerProvider().computation())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object e) {
                        if (e instanceof Events.OrderEvent) {
                            // 下单事件
                            Events.OrderEvent event = (Events.OrderEvent) e;
                            BOrder o = event.getOrder();
                            RemoteDevice device = event.getDevice();
                            // Check
                            if (device == null) {
                                return;
                            }
                            if (device instanceof WifiDevice) {
                                int ip = ((WifiDevice) device).getIP();
                                WifiAddition.toWifiOrder(o, ip);
                                if (event.isCorrected()) {
                                    // 修改订单类型
                                    PrinterService.getBus().post(
                                            new PrinterService.UpdateDBOrderTypeEvent(
                                                    event.getOrder().getOrderNumber(),
                                                    BOrder.WIFI_CORR_ORDER_STR
                                            ));
                                    o.setRetainField(BOrder.WIFI_CORR_ORDER);
                                } else {
                                    o.setRetainField(BOrder.WIFI_ORDER);
                                }
                            } else if (device instanceof BluetoothDeviceWrapper) {
                                if (event.isCorrected()) {
                                    // 修改订单类型
                                    PrinterService.getBus().post(
                                            new PrinterService.UpdateDBOrderTypeEvent(
                                                    event.getOrder().getOrderNumber(),
                                                    BOrder.BT_CORR_ORDER_STR
                                            ));
                                    o.setRetainField(BOrder.BT_CORR_ORDER);
                                } else {
                                    o.setRetainField(BOrder.BT_ORDER);
                                }
                            } else {
                                Log.e(TAG, "Unknown device type!");
                            }
                            // 如果不是修正订单
                            if (!event.isCorrected()) {
                                Order order = Order.fromBOrder(o);
                                // 添加到数据库
                                PrinterService.getBus().post(
                                        new PrinterService.InsertDBOrderEvent(order));
                            }

                            // 添加到队列
                            DevicesManager.getInstance().sendOrder(device, o);
                        }
                    }
                });
        mSubscriptions.add(subscription);
        return this;
    }

    public int getNextOrderNumber() {
        int nextOrderNumber = ++mOrderNumber;
        // Save
        SharedPreferencesUtils.getInstance().setOrderNumber(nextOrderNumber);
        return nextOrderNumber;
    }

    public void resetOrderNumber() {
        mOrderNumber = 0;
    }

    public static RxBus getBus() {
        return getInstance().mBus;
    }

    public void setOrderNumber(int orderNumber) {
        mOrderNumber = orderNumber;
    }
}
