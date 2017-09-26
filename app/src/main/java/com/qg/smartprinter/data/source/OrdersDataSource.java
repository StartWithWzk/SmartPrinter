package com.qg.smartprinter.data.source;

import android.support.annotation.NonNull;

import java.util.List;

import rx.Observable;

public interface OrdersDataSource {

    Observable<List<Order>> getOrders();

    Observable<List<Message>> getMessagesWithOrderId(@NonNull String orderId);

    void sendOrder(@NonNull Order order);

    void updateOrder(@NonNull String orderId, String status);

    void updateOrderType(@NonNull String orderId, String type);

    void receiveMessage(@NonNull Message message);
}
