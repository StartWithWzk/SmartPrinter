package com.qg.smartprinter.dummy;

import com.qg.common.logger.Log;
import com.qg.smartprinter.localorder.OrderManager;
import com.qg.smartprinter.localorder.messages.BOrder;
import com.qg.smartprinter.localorder.messages.BOrderData;
import com.qg.smartprinter.localorder.messages.BPhoto;
import com.qg.smartprinter.localorder.messages.BQRCode;
import com.qg.smartprinter.localorder.messages.BText;
import com.qg.smartprinter.localorder.util.Charsets;
import com.qg.smartprinter.logic.model.Business;
import com.qg.smartprinter.logic.model.LocalOrder;
import com.qg.smartprinter.logic.model.OrderContent;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * 订单数据模拟
 */
public class OrderDummy {
    private static final String TAG = "OrderDummy";

    private static String TEST_URL = "https://www.baidu.com/";

    private static byte[] TEST_PHOTO = {
            (byte) 0x78, (byte) 0x0F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xAB, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x66, (byte) 0x81, (byte) 0x5B, (byte) 0x82, (byte) 0x17, (byte) 0x84, (byte) 0x5B, (byte) 0x86, (byte) 0x10, (byte) 0x87, (byte) 0x57, (byte) 0x8C, (byte) 0x0B, (byte) 0x8A, (byte) 0x02, (byte) 0x82, (byte) 0x52, (byte) 0x8F, (byte) 0x08, (byte) 0x8F, (byte) 0x51, (byte) 0x91, (byte) 0x05, (byte) 0x91, (byte) 0x51, (byte) 0x92, (byte) 0x03, (byte) 0x93, (byte) 0x4F, (byte) 0x89, (byte) 0x03, (byte) 0x92, (byte) 0x03, (byte) 0x88, (byte) 0x4F, (byte) 0x86, (byte) 0x08, (byte) 0x8E, (byte) 0x07, (byte) 0x87, (byte) 0x4D, (byte) 0x87, (byte) 0x08, (byte) 0x8D, (byte) 0x08, (byte) 0x88, (byte) 0x4C, (byte) 0x87, (byte) 0x08, (byte) 0x8E, (byte) 0x08, (byte) 0x87, (byte) 0x4C, (byte) 0x87, (byte) 0x08, (byte) 0x8E, (byte) 0x07, (byte) 0x88, (byte) 0x4C, (byte) 0x87, (byte) 0x08, (byte) 0x8E, (byte) 0x07, (byte) 0x89, (byte) 0x4A, (byte) 0x88, (byte) 0x08, (byte) 0x8D, (byte) 0x08, (byte) 0x89, (byte) 0x4A, (byte) 0x88, (byte) 0x08, (byte) 0x8D, (byte) 0x08, (byte) 0x89, (byte) 0x4A, (byte) 0x88, (byte) 0x08, (byte) 0x8E, (byte) 0x07, (byte) 0x89, (byte) 0x4A, (byte) 0x89, (byte) 0x06, (byte) 0x90, (byte) 0x05, (byte) 0x8B, (byte) 0x48, (byte) 0xA1, (byte) 0x01, (byte) 0x8E, (byte) 0x46, (byte) 0xB4, (byte) 0x41, (byte) 0xB9, (byte) 0x3D, (byte) 0xBD, (byte) 0x3B, (byte) 0xBF, (byte) 0x38, (byte) 0x88, (byte) 0x03, (byte) 0x94, (byte) 0x02, (byte) 0x9F, (byte) 0x36, (byte) 0x88, (byte) 0x07, (byte) 0x90, (byte) 0x05, (byte) 0x91, (byte) 0x06, (byte) 0x87, (byte) 0x36, (byte) 0x87, (byte) 0x0A, (byte) 0x8D, (byte) 0x08, (byte) 0x8D, (byte) 0x09, (byte) 0x86, (byte) 0x36, (byte) 0x86, (byte) 0x0C, (byte) 0x8B, (byte) 0x0A, (byte) 0x8B, (byte) 0x0B, (byte) 0x88, (byte) 0x33, (byte) 0x87, (byte) 0x0B, (byte) 0x8A, (byte) 0x0C, (byte) 0x89, (byte) 0x0C, (byte) 0x88, (byte) 0x33, (byte) 0x86, (byte) 0x0C, (byte) 0x8A, (byte) 0x0C, (byte) 0x89, (byte) 0x0C, (byte) 0x87, (byte) 0x34, (byte) 0x87, (byte) 0x0B, (byte) 0x8A, (byte) 0x0C, (byte) 0x89, (byte) 0x0C, (byte) 0x87, (byte) 0x34, (byte) 0x87, (byte) 0x0A, (byte) 0x8A, (byte) 0x0E, (byte) 0x89, (byte) 0x0A, (byte) 0x88, (byte) 0x34, (byte) 0x88, (byte) 0x06, (byte) 0x8C, (byte) 0x10, (byte) 0x89, (byte) 0x08, (byte) 0x89, (byte) 0x34, (byte) 0x99, (byte) 0x12, (byte) 0x99, (byte) 0x34, (byte) 0x98, (byte) 0x13, (byte) 0x99, (byte) 0x35, (byte) 0x96, (byte) 0x08, (byte) 0x81, (byte) 0x0C, (byte) 0x98, (byte) 0x35, (byte) 0x95, (byte) 0x08, (byte) 0x82, (byte) 0x0D, (byte) 0x96, (byte) 0x36, (byte) 0x95, (byte) 0x09, (byte) 0x81, (byte) 0x0D, (byte) 0x96, (byte) 0x37, (byte) 0x93, (byte) 0x19, (byte) 0x95, (byte) 0x37, (byte) 0x92, (byte) 0x1B, (byte) 0x94, (byte) 0x37, (byte) 0x91, (byte) 0x1D, (byte) 0x92, (byte) 0x39, (byte) 0x8F, (byte) 0x20, (byte) 0x8F, (byte) 0x3B, (byte) 0x8C, (byte) 0x23, (byte) 0x8E, (byte) 0x3B, (byte) 0x8B, (byte) 0x25, (byte) 0x8C, (byte) 0x3D, (byte) 0x8A, (byte) 0x26, (byte) 0x8A, (byte) 0x3F, (byte) 0x88, (byte) 0x27, (byte) 0x89, (byte) 0x40, (byte) 0x88, (byte) 0x15, (byte) 0x81, (byte) 0x12, (byte) 0x87, (byte) 0x42, (byte) 0x87, (byte) 0x08, (byte) 0x81, (byte) 0x07, (byte) 0x81, (byte) 0x03, (byte) 0x81, (byte) 0x01, (byte) 0x81, (byte) 0x11, (byte) 0x87, (byte) 0x42, (byte) 0x87, (byte) 0x08, (byte) 0x8A, (byte) 0x03, (byte) 0x88, (byte) 0x0A, (byte) 0x88, (byte) 0x41, (byte) 0x89, (byte) 0x08, (byte) 0x89, (byte) 0x03, (byte) 0x86, (byte) 0x01, (byte) 0x81, (byte) 0x0A, (byte) 0x88, (byte) 0x42, (byte) 0x88, (byte) 0x09, (byte) 0x87, (byte) 0x06, (byte) 0x86, (byte) 0x0A, (byte) 0x88, (byte) 0x41, (byte) 0x8A, (byte) 0x24, (byte) 0x89, (byte) 0x41, (byte) 0x8A, (byte) 0x12, (byte) 0x81, (byte) 0x11, (byte) 0x89, (byte) 0x41, (byte) 0x8B, (byte) 0x0E, (byte) 0x85, (byte) 0x0F, (byte) 0x8B, (byte) 0x40, (byte) 0x8C, (byte) 0x0C, (byte) 0x87, (byte) 0x0D, (byte) 0x8C, (byte) 0x40, (byte) 0x8E, (byte) 0x08, (byte) 0x8B, (byte) 0x0B, (byte) 0x8C, (byte) 0x40, (byte) 0x90, (byte) 0x04, (byte) 0x8E, (byte) 0x08, (byte) 0x8E, (byte) 0x40, (byte) 0xA6, (byte) 0x01, (byte) 0x91, (byte) 0x40, (byte) 0xB8, (byte) 0x40, (byte) 0xB7, (byte) 0x42, (byte) 0xB6, (byte) 0x42, (byte) 0xB6, (byte) 0x43, (byte) 0xB4, (byte) 0x44, (byte) 0xB4, (byte) 0x45, (byte) 0xB2, (byte) 0x47, (byte) 0xB0, (byte) 0x48, (byte) 0xAF, (byte) 0x4A, (byte) 0xAE, (byte) 0x4C, (byte) 0xAB, (byte) 0x4E, (byte) 0x92, (byte) 0x03, (byte) 0x93, (byte) 0x53, (byte) 0x88, (byte) 0x11, (byte) 0x8A, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x7F, (byte) 0x4F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xAB,
    };

    private static String[] clientNames = {
            "X亭烤肉",
            "路边大排档",
            "X工三饭"
    };

//    private static String[] companyNames = {
//            "丑团外卖",
//            "饱了么",
//            "千分外卖"
//    };
//    private static final String[] userNames = {
//            "丁一",
//            "陈二",
//            "张三",
//            "李四",
//            "王五",
//            "赵六",
//            "田七",
//            "钱八",
//            "孙九"
//    };

    //    private static final String[] userAddresses = {
//            "宿舍3楼",
//            "宿舍4楼",
//            "宿舍5楼",
//            "宿舍6楼",
//            "宿舍7楼",
//            "工一",
//            "教六",
//    };
//
    private static final String[] addresses = {
            "X工路边",
            "X亭美食街",
            "X亭小作坊"
    };

    private static final String[] phones = {
            "10100000000",
            "10111111111",
            "10122222222",
            "10133333333",
    };

    private static <T> T random(T[] objs) {
        Random random = new Random();
        return objs[random.nextInt(objs.length)];
    }

    private static LocalOrder newOrder(int orderId) {
        String clientName = random(clientNames);
        String clientAddress = random(addresses);
        String clientPhone = random(phones);

        Business u = new Business();
        u.setName(clientName);
        u.setAddress(clientAddress);
        u.setPhone(clientPhone);
        u.setAdvertisement("");

        Date inDate = new Date();

        final int contentSize = 3;
        ArrayList<OrderContent> contents = new ArrayList<>();
        for (int i = 0; i < contentSize; i++) {
            contents.add(new OrderContent(
                    random(CookbookDummy.NAMES),
                    1,
                    random(CookbookDummy.PRICES)
            ));
        }

        return new LocalOrder.Builder()
                .orderId(orderId)
                .orderTime(inDate)
                .tableNumber(new Random().nextInt(9) + 1)
                .paymentMethod("支付宝")
                .contents(contents)
                .business(u)
                .build();
    }


    /**
     * 获取足够大的订单文本
     *
     * @param orderId   订单ID
     * @param maxLength 最大长度
     * @return 文本,字节数在[maxLength - 1000, maxLength]这个区间内
     */
    private static String newLargeNumText(int orderId, int maxLength) {
//        int minLength = maxLength - 1000;
//        minLength = minLength < 0 ? 0 : minLength;
        int minLength = maxLength;
        return newLargeNumText(orderId, minLength, maxLength);
    }

    private static String newLargeNumText(int orderId, int min, int max) {
//        int needLength = new Random().nextInt(max - min) + min;
        int needLength = max;
        StringBuilder sb = new StringBuilder();
        int length = 0;
        int orderCount = 0;
        while (length < needLength) {
            String printString = "(第" + (orderCount + 1) + "份文本):\n" + newOrder(orderId).getPrintString() + "\n";
            int appendLength = printString.getBytes(Charsets.PRINTER_CHARSET).length;

            if (length + appendLength >= needLength) {
                break;
            }
            sb.append(printString);
            length += appendLength;
            orderCount++;
        }
        return sb.toString();
    }

    public static BOrderData newOrderData(int orderId) {
        BOrderData orderData = new BOrderData();

        orderData.add(new BPhoto(TEST_PHOTO));
        orderData.add(new BText(newOrder(orderId).getPrintString()));
        orderData.add(new BQRCode(TEST_URL));

        return orderData;
    }


    public static BOrder generateOrder(boolean needPhoto, int needLength, boolean needQRCode) {
        return OrderDummy.newBOrder(
                OrderManager.getInstance().getNextOrderNumber(),
                needPhoto,
                needLength,
                needQRCode
        );
    }
    /**
     * 获取足够大的订单
     *
     * @param hasPhoto  是否有图
     * @param orderId   订单ID
     * @param maxLength 最大长度
     * @param hasQRCode 是否有二维码
     * @return 一份订单, 长度(字节数)在[maxLength - 1000, maxLength]这个区间内
     */
    public static BOrder newBOrder(int orderId, boolean hasPhoto, int maxLength, boolean hasQRCode) {
        BOrderData bOrderData = new BOrderData();
        BPhoto bPhoto = new BPhoto(OrderDummy.TEST_PHOTO);
        BQRCode bqrCode = new BQRCode(TEST_URL);

        int needTextLength = maxLength;
        needTextLength -= (hasPhoto ? bPhoto.toBytes().length : 0);
        needTextLength -= (hasQRCode ? bqrCode.toBytes().length : 0);
        Log.d(TAG, "newBOrder: " + needTextLength);
//        int needTextLength = maxLength;

        if (hasPhoto) {
            bOrderData.add(bPhoto);
        }
        bOrderData.add(new BText(newLargeNumText(orderId, needTextLength)));
        if (hasQRCode) {
            bOrderData.add(bqrCode);
        }

        Log.d(TAG, "生成订单数据字节数: " + bOrderData.toBytes().length);
        BOrder bOrder = BOrder.fromOrderData(orderId, bOrderData, 0);
        Log.d(TAG, "生成订单字节数: " + bOrder.toRealBytes().length);
        return bOrder;
    }

}
