package com.qg.smartprinter.data.source;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.qg.common.logger.Log;
import com.qg.smartprinter.data.source.OrderContract.Messages;
import com.qg.smartprinter.data.source.OrderContract.Orders;
import com.qg.smartprinter.data.source.OrdersDbHelper.Tables;
import com.qg.smartprinter.util.scheduler.BaseSchedulerProvider;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Concrete implementation to load orders from the data sources into the memory.
 *
 * @author TZH
 * @version 1.0
 */
public class OrdersRepository implements OrdersDataSource {
    private static final String TAG = "OrdersRepository";

    private static OrdersRepository INSTANCE;

    private BriteDatabase mDbHelper;

    // Prevent direct instantiation.
    private OrdersRepository(@NonNull Context context,
                             @NonNull BaseSchedulerProvider schedulerProvider) {
        checkNotNull(context);
        init(context, schedulerProvider);
    }

    public void init(Context context, BaseSchedulerProvider schedulerProvider) {
        OrdersDbHelper dbHelper = new OrdersDbHelper(context);
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        mDbHelper = sqlBrite.wrapDatabaseHelper(dbHelper, schedulerProvider.io());
    }

    public void deleteDatabase(Context context, BaseSchedulerProvider schedulerProvider) {
        mDbHelper.close();
        OrdersDbHelper.deleteDatabase(context);
        init(context, schedulerProvider);
    }

    public static OrdersRepository getInstance(@NonNull Context context,
                                               @NonNull BaseSchedulerProvider schedulerProvider) {
        if (INSTANCE == null) {
            INSTANCE = new OrdersRepository(context, schedulerProvider);
        }
        return INSTANCE;
    }

    @Override
    public Observable<List<Order>> getOrders() {
        String[] projection = {
                Orders.ORDER_ID,
                Orders.ORDER_CONTENT,
                Orders.ORDER_TIME,
                Orders.ORDER_TYPE,
                Orders.ORDER_STATUS,
                Orders.ORDER_MESSAGE
        };

        String sql = String.format("SELECT %s FROM %s", TextUtils.join(",", projection), Tables.ORDERS);
        return mDbHelper.createQuery(Tables.ORDERS, sql)
                .mapToList(new Func1<Cursor, Order>() {
                    @Override
                    public Order call(Cursor c) {
                        String itemId = c.getString(c.getColumnIndexOrThrow(Orders.ORDER_ID));
                        String time = c.getString(c.getColumnIndexOrThrow(Orders.ORDER_TIME));
                        String type = c.getString(c.getColumnIndexOrThrow(Orders.ORDER_TYPE));
                        String status = c.getString(c.getColumnIndexOrThrow(Orders.ORDER_STATUS));
                        String content = c.getString(c.getColumnIndexOrThrow(Orders.ORDER_CONTENT));
                        byte[] message = c.getBlob(c.getColumnIndexOrThrow(Orders.ORDER_MESSAGE));

                        return new Order(itemId, content, time, type, message, status);
                    }
                });
    }

    @Override
    public Observable<List<Message>> getMessagesWithOrderId(@NonNull String orderId) {
        String[] projection = {
                Messages.MESSAGE_ID,
                Messages.MESSAGE_CONTENT,
                Messages.MESSAGE_TYPE,
                Messages.MESSAGE_TIME,
                Messages.MESSAGE_ORDER_ID
        };

        String sql = String.format("SELECT %s FROM %s WHERE %s LIKE ?",
                TextUtils.join(",", projection), Tables.MESSAGES, Messages.MESSAGE_ORDER_ID);
        return mDbHelper.createQuery(Tables.MESSAGES, sql, orderId)
                .mapToList(new Func1<Cursor, Message>() {
                    @Override
                    public Message call(Cursor c) {
                        String itemId = c.getString(c.getColumnIndexOrThrow(Messages.MESSAGE_ID));
                        String type = c.getString(c.getColumnIndexOrThrow(Messages.MESSAGE_TYPE));
                        String time = c.getString(c.getColumnIndexOrThrow(Messages.MESSAGE_TIME));
                        byte[] content = c.getBlob(c.getColumnIndexOrThrow(Messages.MESSAGE_CONTENT));
                        String itemOrderId = c.getString(c.getColumnIndexOrThrow(Messages.MESSAGE_ORDER_ID));

                        return new Message(itemId, content, type, time, itemOrderId);
                    }
                });
    }

    @Override
    public void sendOrder(@NonNull Order order) {
        checkNotNull(order);
        ContentValues values = new ContentValues();
        values.put(Orders.ORDER_ID, order.getId());
        values.put(Orders.ORDER_TIME, order.getTime());
        values.put(Orders.ORDER_TYPE, order.getType());
        values.put(Orders.ORDER_STATUS, order.getStatus());
        values.put(Orders.ORDER_CONTENT, order.getContent());
        values.put(Orders.ORDER_MESSAGE, order.getMessage());
        mDbHelper.insert(Tables.ORDERS, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public void updateOrder(@NonNull String orderId, String status) {
        ContentValues values = new ContentValues();
        values.put(Orders.ORDER_STATUS, status);

        String selection = Orders.ORDER_ID + " LIKE ?";
        String[] selectionArgs = {orderId};

        mDbHelper.update(Tables.ORDERS, values, selection, selectionArgs);
    }

    @Override
    public void updateOrderType(@NonNull String orderId, String type) {
        ContentValues values = new ContentValues();
        values.put(Orders.ORDER_TYPE, type);

        String selection = Orders.ORDER_ID + " LIKE ?";
        String[] selectionArgs = {orderId};

        mDbHelper.update(Tables.ORDERS, values, selection, selectionArgs);
    }

    @Override
    public void receiveMessage(@NonNull Message message) {
        ContentValues values = new ContentValues();
        values.put(Messages.MESSAGE_ID, message.getId());
        values.put(Messages.MESSAGE_CONTENT, message.getContent());
        values.put(Messages.MESSAGE_TIME, message.getTime());
        values.put(Messages.MESSAGE_TYPE, message.getType());
        values.put(Messages.MESSAGE_ORDER_ID, message.getOrderId());
        mDbHelper.insert(Tables.MESSAGES, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
}
