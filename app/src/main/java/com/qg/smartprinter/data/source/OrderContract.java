package com.qg.smartprinter.data.source;

import android.provider.BaseColumns;

/**
 * The contract used for the db to save the orders locally.
 */
public final class OrderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private OrderContract() {
    }

    interface OrderColumns {
        String ORDER_ID = "order_id";
        String ORDER_CONTENT = "order_content";
        String ORDER_TIME = "order_time";
        String ORDER_STATUS = "order_status";
        String ORDER_TYPE = "order_type";
        String ORDER_MESSAGE = "order_message";
    }

    interface MessageColumns {
        String MESSAGE_ID = "message_id";
        String MESSAGE_CONTENT = "message_content";
        String MESSAGE_TYPE = "message_type";
        String MESSAGE_TIME = "message_time";
        String MESSAGE_ORDER_ID = "message_order_id";
    }

    public static class Orders implements OrderColumns, BaseColumns {

    }

    public static class Messages implements MessageColumns, BaseColumns {

    }

}
