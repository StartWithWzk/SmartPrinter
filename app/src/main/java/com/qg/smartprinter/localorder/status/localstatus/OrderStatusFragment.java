package com.qg.smartprinter.localorder.status.localstatus;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qg.common.logger.Log;
import com.qg.common.widget.CommonViewHolder;
import com.qg.common.widget.RecyclerArrayAdapter;
import com.qg.smartprinter.Injection;
import com.qg.smartprinter.R;
import com.qg.smartprinter.data.source.Order;
import com.qg.smartprinter.localorder.status.message.MessageActivity;
import com.qg.smartprinter.ui.BaseFragment;

import java.util.List;

import static com.qg.common.Preconditions.checkNotNull;

/**
 * Display a list of {@link Order}s.
 */
public class OrderStatusFragment extends BaseFragment
        implements LocalStatusContract.OrderStatusView {

    public RecyclerView mOrderListView;

    public Adapter mOrderArrayAdapter;

    private LocalStatusContract.OrderStatusPresenter mPresenter;

    public static OrderStatusFragment newInstanceWithPresenter(Context context) {
        OrderStatusFragment fragment = new OrderStatusFragment();
        new OrderStatusPresenter(
                Injection.provideOrdersRepository(context),
                fragment,
                Injection.provideBaseSchedulerProvider()
        );
        return fragment;
    }

    public static OrderStatusFragment newInstance() {
        return new OrderStatusFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_local_order_status, container, false);
        mOrderListView = (RecyclerView) v.findViewById(R.id.lv_finished_order);

        mOrderArrayAdapter = new Adapter(getContext());
        mOrderArrayAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                final Order item = checkNotNull(mOrderArrayAdapter.getItem(position));
                mPresenter.openOrderDetails(item);
            }
        });

        mOrderListView.setAdapter(mOrderArrayAdapter);
        setRetainInstance(true);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void setLoadingIndicator(boolean active) {
    }

    @Override
    public void showOrderDetailsUi(String orderId) {
        MessageActivity.start(getContext(), orderId);
    }

    @Override
    public void showOrders(List<Order> orders) {
        mOrderArrayAdapter.setList(orders);
    }

    @Override
    public void showNoOrders() {
        mOrderArrayAdapter.clear();
    }

    @Override
    public void showLoadingOrdersError() {
        Toast.makeText(getContext(), "加载出错", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showError(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(@NonNull LocalStatusContract.OrderStatusPresenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    private class Adapter extends RecyclerArrayAdapter<Order> {

        public Adapter(Context context) {
            super(context, R.layout.item_layout_local_order);
        }

        private static final String TAG = "Adapter";
        @Override
        protected void onBind(CommonViewHolder holder, final Order item) {
            holder.getTextView(R.id.text).setText(item.toString());
            final View resendButton = holder.get(R.id.corrected);
            String status = item.getStatus();
            resendButton.setVisibility(Order.UNFINISHED.equals(status) ? View.VISIBLE : View.GONE);
            resendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPresenter.resend(item)) {
                        resendButton.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

}
