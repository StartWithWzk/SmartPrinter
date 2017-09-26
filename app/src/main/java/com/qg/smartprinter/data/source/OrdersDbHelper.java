package com.qg.smartprinter.data.source;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.qg.smartprinter.data.source.OrderContract.MessageColumns;
import com.qg.smartprinter.data.source.OrderContract.Messages;
import com.qg.smartprinter.data.source.OrderContract.OrderColumns;
import com.qg.smartprinter.data.source.OrderContract.Orders;

/**
 * OrdersDbHelper
 */
public class OrdersDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Orders.db";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    interface Tables {
        String ORDERS = "orders";
        String MESSAGES = "messages";
    }

    interface References {
        String ORDER_ID = "REFERENCES " + Tables.ORDERS + "(" + Orders.ORDER_ID + ")";
    }

    OrdersDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.ORDERS + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                OrderColumns.ORDER_ID + " TEXT NOT NULL," +
                OrderColumns.ORDER_TIME + " TEXT NOT NULL," +
                OrderColumns.ORDER_TYPE + " TEXT NOT NULL," +
                OrderColumns.ORDER_STATUS + " TEXT NOT NULL," +
                OrderColumns.ORDER_CONTENT + " TEXT NOT NULL," +
                OrderColumns.ORDER_MESSAGE + " BLOB)");

        db.execSQL("CREATE TABLE " + Tables.MESSAGES + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MessageColumns.MESSAGE_ID + " TEXT NOT NULL," +
                MessageColumns.MESSAGE_CONTENT + " TEXT NOT NULL," +
                MessageColumns.MESSAGE_TYPE + " TEXT NOT NULL," +
                MessageColumns.MESSAGE_TIME + " TEXT NOT NULL," +
                Messages.MESSAGE_ORDER_ID + " TEXT NOT NULL " + References.ORDER_ID + " ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.ORDERS);
        onCreate(db);
    }

    static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}


