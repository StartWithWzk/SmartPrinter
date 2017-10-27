package com.qg.smartprinter.localorder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.qg.smartprinter.Injection;
import com.qg.smartprinter.R;
import com.qg.smartprinter.dummy.OrderDummy;
import com.qg.smartprinter.localorder.AutoOrdersContainer.AutoOrder;
import com.qg.smartprinter.localorder.device.RemoteDevice;
import com.qg.smartprinter.localorder.event.Events;
import com.qg.smartprinter.localorder.messages.AbstractMessage;
import com.qg.smartprinter.localorder.messages.BOrder;
import com.qg.smartprinter.localorder.messages.BOrderStatus;
import com.qg.smartprinter.localorder.messages.BPrinterStatus;
import com.qg.smartprinter.localorder.messages.BResponse;
import com.qg.smartprinter.localorder.status.localstatus.LocalStatusActivity;
import com.qg.smartprinter.ui.BaseFragment;
import com.qg.smartprinter.util.rxbus.RxBus;
import com.qg.smartprinter.util.scheduler.BaseSchedulerProvider;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;

/**
 * 测试界面
 */
public abstract class PrinterTestFragment extends BaseFragment {

    private ListView mConversationView;

    private EditText mBytesEditText;

    private EditText mNumEditText;

    private CheckBox mQRCodeCheckBox;

    private CheckBox mPhotoCheckBox;

    private CheckBox mShuffleCheckBox;

    private String mConnectedDeviceName = null;

    private ArrayAdapter<String> mConversationArrayAdapter;

    private TextView mStatusView;

    private TextView mAutoMsgView;

    private TextView mAutoNumbersView;

    private AutoOrdersContainer mAutoOrdersContainer = new AutoOrdersContainer();

    private CompositeSubscription mSubscriptions;

    private BaseSchedulerProvider mSchedulerProvider;

    private int mTotalNum;
    private int mReceiveNum;

    public void clear() {
        mConversationArrayAdapter.clear();
    }

    public void bottom() {
        mConversationView.smoothScrollToPosition(mConversationArrayAdapter.getCount() - 1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSubscriptions = new CompositeSubscription();
        addDefaultAutoOrders();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_printer_test, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mConversationView = (ListView) view.findViewById(R.id.in);
        mStatusView = (TextView) view.findViewById(R.id.status);
        mAutoMsgView = (TextView) view.findViewById(R.id.auto_order_message);
        mAutoNumbersView = (TextView) view.findViewById(R.id.auto_order_numbers);
        mBytesEditText = (EditText) view.findViewById(R.id.edit_text_bytes);
        mNumEditText = (EditText) view.findViewById(R.id.edit_text_num);
        mPhotoCheckBox = (CheckBox) view.findViewById(R.id.checkbox_photo);
        mShuffleCheckBox = (CheckBox) view.findViewById(R.id.checkbox_shuffle);
        mQRCodeCheckBox = (CheckBox) view.findViewById(R.id.checkbox_url_qr_code);

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.message);

        mConversationView.setAdapter(mConversationArrayAdapter);

        updateAutoOrderStatus(0, 0);
        RxView.clicks(view.findViewById(R.id.button_send))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        autoOrder();
                    }
                });
        RxView.clicks(view.findViewById(R.id.auto_order_minus))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        mAutoOrdersContainer.remove();
                        updateAutoOrderNumbers();
                    }
                });

        RxView.clicks(view.findViewById(R.id.auto_order_add))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        try {
                            mAutoOrdersContainer.addAutoOther(
                                    parseInt(mBytesEditText.getText().toString(), "字节数有误！"),
                                    parseInt(mNumEditText.getText().toString(), "份数有误！")
                            );
                            updateAutoOrderNumbers();
                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        mAutoNumbersView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mAutoOrdersContainer.reset();
                updateAutoOrderNumbers();
                return true;
            }
        });
        updateStatus();
        updateAutoOrderNumbers();
    }

    private int parseInt(String s, String message) throws NumberFormatException {
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(message);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSchedulerProvider = Injection.provideBaseSchedulerProvider();
    }

    private static final AutoOrder[] DEFAULT_AUTO_ORDERS = {
            new AutoOrder(300, 20),
//            new AutoOrder(2000, 4),
//            new AutoOrder(4000, 4),
//            new AutoOrder(10000, 2),
    };

    private void addDefaultAutoOrders() {
        if (mAutoOrdersContainer.getAutoOrderList().isEmpty()) {
            for (AutoOrder autoOrder : DEFAULT_AUTO_ORDERS) {
                mAutoOrdersContainer.addAutoOther(autoOrder);
            }
        }
    }

    private void autoOrder() {
        if (!hasDevice()) {
            Toast.makeText(getContext(), R.string.title_not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        final List<Integer> orders = mAutoOrdersContainer.getOrders(mShuffleCheckBox.isChecked());

        mReceiveNum = 0;
        mTotalNum = orders.size();
        updateAutoOrderStatus(mTotalNum, mReceiveNum);

        Observable.just(orders)
                .flatMapIterable(new Func1<List<Integer>, Iterable<Integer>>() {
                    @Override
                    public Iterable<Integer> call(List<Integer> integers) {
                        return integers;
                    }
                })
                .zipWith(Observable.interval(500, TimeUnit.MILLISECONDS), new Func2<Integer, Long, Integer>() {
                    @Override
                    public Integer call(Integer o, Long aLong) {
                        return o;
                    }
                }) // Emmit one order per 0.5s
                .observeOn(Injection.provideBaseSchedulerProvider().computation())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer order) {
                        BOrder bOrder = OrderDummy.generateOrder(
                                mPhotoCheckBox.isChecked(),
                                order,
                                mQRCodeCheckBox.isChecked());
                        OrderManager.getBus().post(new Events.OrderEvent(getDevice(), bOrder));
                    }
                });
    }

    void updateAutoOrderNumbers() {
        mAutoNumbersView.setText(null);
        for (AutoOrder autoOrder : mAutoOrdersContainer.getAutoOrderList()) {
            mAutoNumbersView.append(autoOrder.toString());
            mAutoNumbersView.append(";");
        }
    }

    private void updateAutoOrderStatus(int total, int current) {
        mAutoMsgView.setText(getString(R.string.auto_order_status_format, current, total));
    }

    protected void setStatus(int resId) {
        mStatusView.setText(resId);
    }

    protected void updateStatus() {
        int state = getState();
        switch (state) {
            case RemoteDevice.STATE_CONNECTED:
                mConnectedDeviceName = getDevice().getName();
                mStatusView.setText(getString(R.string.title_connected_to, mConnectedDeviceName));
                break;
            case RemoteDevice.STATE_CONNECTING:
                setStatus(R.string.title_connecting);
                break;
            case RemoteDevice.STATE_NONE:
                setStatus(R.string.title_not_connected);
                break;
        }
    }

    public int getState() {
        if (hasDevice()) {
            return getDevice().getState();
        }
        return RemoteDevice.STATE_NONE;
    }

    private void successSendOrder(BResponse response) {
        updateAutoOrderStatus(mTotalNum, ++mReceiveNum);
        mConversationArrayAdapter.add("收到应答！\n" +
                mConnectedDeviceName + ": " + response
        );
    }

    private void updatePrinterStatus(BPrinterStatus bPrinterStatus) {
        mConversationArrayAdapter.add("收到打印机状态：\n" +
                bPrinterStatus
        );
    }

    private void updateOrderStatus(BOrderStatus bOrderStatus) {
        mConversationArrayAdapter.add("收到订单状态：\n" +
                bOrderStatus
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        Subscription orderSubscription = RxBus.getDefault().asObservable()
                .onBackpressureDrop() // Guard against uncontrollable frequency of upstream emissions.
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object e) {
                        if (e instanceof Events.StateChangeEvent) {
                            updateStatus();
                        } else if (e instanceof Events.ConnectionFailedEvent) {
                            Toast.makeText(getContext(), getContext().getString(R.string.connect_fail),
                                    Toast.LENGTH_SHORT).show();
                        } else if (e instanceof Events.ConnectionLostEvent) {
                            Toast.makeText(getContext(), getContext().getString(R.string.connect_lost),
                                    Toast.LENGTH_SHORT).show();
                        } else if (e instanceof Events.ConnectedEvent) {
                            mConnectedDeviceName = ((Events.ConnectedEvent) e).getDevice().getName();
                            Toast.makeText(getContext(), getString(R.string.title_connected_to, mConnectedDeviceName),
                                    Toast.LENGTH_SHORT).show();
                        } else if (e instanceof Events.ReadEvent) {
                            byte[] bytes = ((Events.ReadEvent) e).getBytes();
                            processReadBytes(bytes);
                        } else if (e instanceof Events.OrderLostEvent) {
                            Toast.makeText(getContext(), R.string.order_lost, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        mSubscriptions.add(orderSubscription);
    }

    // 仅限于更新界面，显示出已收到信息
    private void processReadBytes(byte[] readBuf) {
        AbstractMessage abstractMessage = AbstractMessage.bytesToAbstractStatus(readBuf);
        switch (abstractMessage.getStatusToken()) {
            case BPrinterStatus.TYPE_TOKEN:
                BPrinterStatus bPrinterStatus = BPrinterStatus.bytesToPrinterStatus(readBuf);
                updatePrinterStatus(bPrinterStatus);
                break;
            case BOrderStatus.TYPE_TOKEN:
                BOrderStatus bOrderStatus = BOrderStatus.bytesToOrderStatus(readBuf);
                updateOrderStatus(bOrderStatus);
                break;
            case BResponse.TYPE_TOKEN:
                BResponse response = BResponse.bytesToResponse(readBuf);
                successSendOrder(response);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mSubscriptions.clear();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_printer_test, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connect_scan: {
                connect();
                return true;
            }
            case R.id.disconnect_printer: {
                disconnect();
                return true;
            }
            case R.id.status: {
                LocalStatusActivity.start(getContext());
                return true;
            }
        }
        return false;
    }

    protected abstract RemoteDevice getDevice();

    protected abstract void connect();

    protected void disconnect() {
        if (hasDevice()) {
            RxBus.getDefault().post(new Events.DisconnectEvent(getDevice()));
        }
    }

    protected boolean hasDevice() {
        return getDevice() != null;
    }

}
