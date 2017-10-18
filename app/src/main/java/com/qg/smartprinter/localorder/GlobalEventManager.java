package com.qg.smartprinter.localorder;

import com.google.gson.Gson;
import com.qg.common.logger.Log;
import com.qg.smartprinter.Injection;
import com.qg.smartprinter.data.source.Order;
import com.qg.smartprinter.localorder.device.BluetoothDeviceWrapper;
import com.qg.smartprinter.localorder.device.RemoteDevice;
import com.qg.smartprinter.localorder.device.WifiDevice;
import com.qg.smartprinter.localorder.event.Events;
import com.qg.smartprinter.localorder.event.Events.ConnectEvent;
import com.qg.smartprinter.localorder.event.Events.ConnectedEvent;
import com.qg.smartprinter.localorder.event.Events.ConnectionFailedEvent;
import com.qg.smartprinter.localorder.event.Events.ConnectionLostEvent;
import com.qg.smartprinter.localorder.event.Events.DisconnectEvent;
import com.qg.smartprinter.localorder.event.Events.ReadEvent;
import com.qg.smartprinter.localorder.event.Events.RemoteEvent;
import com.qg.smartprinter.localorder.messages.AbstractMessage;
import com.qg.smartprinter.localorder.messages.BOrder;
import com.qg.smartprinter.localorder.messages.BOrderStatus;
import com.qg.smartprinter.localorder.messages.BPrinterStatus;
import com.qg.smartprinter.localorder.messages.BResponse;
import com.qg.smartprinter.localorder.messages.OrderAcceptableResponse;
import com.qg.smartprinter.localorder.messages.WifiAddition;
import com.qg.smartprinter.localorder.util.DebugUtil;
import com.qg.smartprinter.util.rxbus.RxBus;

import rx.functions.Action1;

public class GlobalEventManager {
    private static final String TAG = "GlobalEventManager";

    private static final GlobalEventManager INSTANCE = new GlobalEventManager();

    public static void init() {
        INSTANCE.initial();
    }

    private GlobalEventManager() {

        RxBus.getDefault().asObservable()
                .onBackpressureBuffer(20)
                .observeOn(Injection.provideBaseSchedulerProvider().computation())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        processEvent(o);
                    }
                });
    }

    private void initial() {
    }

    private void processEvent(Object e) {
        Log.d(TAG, "processEvent: START:");
        RemoteDevice device = null;
        if (e instanceof RemoteEvent) {
            device = ((RemoteEvent) e).getDevice();
        }
        if (e instanceof DisconnectEvent) {
            device.stop();
            DevicesManager.getInstance().removeDevice(device);
            Log.d(TAG, "processEvent: Disconnect");
        } else if (e instanceof ConnectedEvent) {
            if (device instanceof WifiDevice) {
                PrinterService.getBus().post(new PrinterService.UpdateIPEvent((WifiDevice) device));
            }
            DevicesManager.getInstance().addPrinter(device);
            Log.d(TAG, "processEvent: Connected");
        } else if (e instanceof ConnectEvent) {
            DevicesManager.getInstance().addDevice(device);
            device.connect();
            Log.d(TAG, "processEvent: Connect");
        } else if (e instanceof ReadEvent) {
            processReadEvent((ReadEvent) e);
//            Log.d(TAG, "processEvent: ReadEvent" + DebugUtil.getBytesString(((ReadEvent) e).getBytes()));
        } else if (e instanceof ConnectionFailedEvent) {
            DevicesManager.getInstance().removeDevice(device);
            Log.d(TAG, "processEvent: Connect failed!");
        } else if (e instanceof ConnectionLostEvent) {
            DevicesManager.getInstance().removeDevice(device);
            Log.d(TAG, "processEvent: Connect lost!");
        } else {
            Log.d(TAG, "processEvent: Other event!!" + e.getClass().getSimpleName());
        }
    }

    /**
     * 处理收到的报文
     * 1.对于每一条状态报文，发送一次应答(蓝牙)
     * 2.对于收到的订单应答，取消本地的重发任务(蓝牙); 查看队列中是否有任务，若有，则发送
     * 3.更新数据库
     */
    private void processReadEvent(ReadEvent e) {
        byte[] readBytes = e.getBytes();
        RemoteDevice device = e.getDevice();
        AbstractMessage abstractMessage = AbstractMessage.bytesToAbstractStatus(readBytes);
        Log.d(TAG, "状态：" + abstractMessage.getStatusToken());
        switch (abstractMessage.getStatusToken()) {
            case BPrinterStatus.TYPE_TOKEN: {
                // 打印机状态
                BPrinterStatus printerStatus = BPrinterStatus.bytesToPrinterStatus(readBytes);
                // 收到的打印机主控板ID
                final long printerId = printerStatus.printerId;
                // 若是蓝牙， 发送应答
                if (device instanceof BluetoothDeviceWrapper) {
                    // 发送应答
                    device.write(BResponse.newCheckedSignal(
                            printerId,
                            BResponse.TOKEN_PRINTER_STATUS,
                            DevicesManager.getInstance().getNextDeviceStatusNumber(device)
                    ));
                }

                // 更新打印机状态（主控板ID及打印单元状态等)
                DevicesManager.getInstance().updateDeviceStatus(device, printerId, printerStatus);

                break;
            }
            case BOrderStatus.TYPE_TOKEN: {
                // 订单状态
                BOrderStatus orderStatus = BOrderStatus.bytesToOrderStatus(readBytes);
                Log.d(TAG, "processReadEvent: 收到订单状态" + new Gson().toJson(orderStatus));

                // 过滤Wifi非本机IP订单状态
                if (device instanceof WifiDevice) {
                    int ip = ((WifiDevice) device).getIP();
                    if (ip != WifiAddition.getIP(orderStatus)) {
                        break;
                    }
                }

                // 收到的打印机主控板ID
                long printerId = orderStatus.getPrinterId();
                // 更新ID
                DevicesManager.getInstance().updateDeviceStatus(device, printerId, null);

                // 若是蓝牙， 发送应答
                if (device instanceof BluetoothDeviceWrapper) {
                    device.write(BResponse.newCheckedSignal(
                            printerId,
                            BResponse.TOKEN_ORDER_STATUS,
                            DevicesManager.getInstance().getNextOrderStatusNumber(device)
                    ));
                }
                // 订单解析错误
                if (orderStatus.getStatus() == BOrderStatus.ORDER_DATA_ERROR
                        || orderStatus.getStatus() == BOrderStatus.ORDER_DATA_ERROR_PRE_EXCEPTION_ORDER) {
                    // 强制结束任务
                    BOrder order = device.forceFinish();
                    if (order != null) {
                        // 重新发送
                        OrderManager.getBus().post(new Events.OrderEvent(device, order, true));
                    }
                    break;
                }
                // 保存报文
                PrinterService.getBus().post(new PrinterService.InsertDBMessageEvent(
                        orderStatus.orderId,
                        readBytes,
                        orderStatus.getStatusString()
                ));
                // 更新订单状态
                PrinterService.getBus().post(new PrinterService.UpdateDBOrderStatusEvent(
                        orderStatus.orderId,
                        String.valueOf(orderStatus.getStatusString())
                ));
                break;
            }
            case BResponse.TYPE_TOKEN: {
                // 应答
                BResponse response = BResponse.bytesToResponse(readBytes);

                Log.i(TAG, "重要信息：" + response.getResponseTypeString());
                // 过滤Wifi非本机IP应答
                if (device instanceof WifiDevice) {
                    int ip = ((WifiDevice) device).getIP();
                    int localIp = WifiAddition.getIP(response);
                    if (ip != localIp) {
                        break;
                    }
                }
                // 完成订单
                finishOrder(response, readBytes, device);
                break;
            }
            case OrderAcceptableResponse.TYPE_TOKEN: {
                Log.d(TAG + "Mark", "send");
                // 可接受发送订单应答
                OrderAcceptableResponse response = OrderAcceptableResponse.bytesToResponse(readBytes);
                if (!(device instanceof WifiDevice)) {
                    return;
                }
                // 过滤Wifi非本机IP应答
                int ip = ((WifiDevice) device).getIP();
                if (ip != response.getIP()) {
                    break;
                }
                // 通知
                ((WifiDevice) device).notifySend();
                break;
            }

        }
    }

    private void finishOrder(BResponse response, byte[] readBytes, RemoteDevice device) {
        // 告知设备任务完成，让其处理
        long responseNum = response.getResponseNum();
        device.finish(responseNum);

        // 更新数据库
        PrinterService.getBus().post(new PrinterService.UpdateDBOrderStatusEvent(
                responseNum,
                Order.RECEIVE_SUCCESS
        ));
        PrinterService.getBus().post(new PrinterService.InsertDBMessageEvent(
                responseNum,
                readBytes,
                Order.RECEIVE_SUCCESS
        ));
    }

}
