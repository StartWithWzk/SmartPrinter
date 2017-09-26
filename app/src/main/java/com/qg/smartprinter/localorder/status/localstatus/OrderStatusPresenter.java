package com.qg.smartprinter.localorder.status.localstatus;

import android.support.annotation.NonNull;

import com.qg.common.logger.Log;
import com.qg.smartprinter.data.source.Order;
import com.qg.smartprinter.data.source.OrdersDataSource;
import com.qg.smartprinter.localorder.DevicesManager;
import com.qg.smartprinter.localorder.OrderManager;
import com.qg.smartprinter.localorder.device.RemoteDevice;
import com.qg.smartprinter.localorder.event.Events;
import com.qg.smartprinter.localorder.messages.BOrder;
import com.qg.smartprinter.util.scheduler.BaseSchedulerProvider;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.qg.common.Preconditions.checkNotNull;
import static com.qg.smartprinter.data.source.Order.TYPE_BT;
import static com.qg.smartprinter.data.source.Order.TYPE_WIFI;

/**
 * Listens to user actions from the UI ({@link OrderStatusFragment}), retrieves the data and
 * updates the UI as required.
 */
public class OrderStatusPresenter implements LocalStatusContract.OrderStatusPresenter {

    private OrdersDataSource mOrdersRepository;

    private LocalStatusContract.OrderStatusView mOrderStatusView;

    private BaseSchedulerProvider mSchedulerProvider;

    private CompositeSubscription mSubscriptions;

    public OrderStatusPresenter(@NonNull OrdersDataSource ordersRepository,
                                @NonNull LocalStatusContract.OrderStatusView orderStatusView,
                                @NonNull BaseSchedulerProvider schedulerProvider) {
        mOrdersRepository = checkNotNull(ordersRepository);
        mOrderStatusView = checkNotNull(orderStatusView);
        mSchedulerProvider = checkNotNull(schedulerProvider);

        mSubscriptions = new CompositeSubscription();
        mOrderStatusView.setPresenter(this);
    }

    @Override
    public void loadOrders(boolean forceUpdate) {
        mOrderStatusView.setLoadingIndicator(true);

        mSubscriptions.clear();
        Subscription subscription = mOrdersRepository
                .getOrders()
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<List<Order>>() {
                    @Override
                    public void onCompleted() {
                        mOrderStatusView.setLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mOrderStatusView.showLoadingOrdersError();
                    }

                    @Override
                    public void onNext(List<Order> orders) {
                        Log.d(TAG, "onNext: SIZE:" + orders.size());
                        processOrders(orders);
                    }
                });
        mSubscriptions.add(subscription);
    }

    private static final String TAG = "OrderStatusPresenter";

    private void processOrders(@NonNull List<Order> orders) {
        if (orders.isEmpty()) {
            mOrderStatusView.showNoOrders();
        } else {
            mOrderStatusView.showOrders(orders);
        }
    }

    @Override
    public void openOrderDetails(@NonNull Order order) {
        mOrderStatusView.showOrderDetailsUi(order.getId());
    }

    @Override
    public boolean resend(Order item) {
        // 直接重传原类型设备
        if (Order.getTargetDeviceType(item.getType()) == TYPE_BT) {
            RemoteDevice btDevice = DevicesManager.getInstance().getBTDevice();
            if (btDevice != null) {
                OrderManager.getBus().post(new Events.OrderEvent(btDevice, BOrder.fromOrder(item, 0), true));
                return true;
            } else {
                mOrderStatusView.showError("还没有连接到蓝牙设备");
            }
        } else if (Order.getTargetDeviceType(item.getType()) == TYPE_WIFI) {
            RemoteDevice tcpDevice = DevicesManager.getInstance().getTCPDevice();
            if (tcpDevice != null) {
                OrderManager.getBus().post(new Events.OrderEvent(tcpDevice, BOrder.fromOrder(item, 0), true));
                return true;
            } else {
                mOrderStatusView.showError("还没有连接到Wifi设备");
            }
        }
        return false;
    }

    @Override
    public void subscribe() {
        loadOrders(false);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

}
