package com.qg.smartprinter.ui.cookbook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qg.smartprinter.R;
import com.qg.smartprinter.dummy.CookbookDummy;
import com.qg.smartprinter.localorder.OrderManager;
import com.qg.smartprinter.logic.model.Business;
import com.qg.smartprinter.logic.model.CookInView;
import com.qg.smartprinter.logic.model.LocalOrder;
import com.qg.smartprinter.logic.model.OrderContent;
import com.qg.smartprinter.ui.BaseActivity;
import com.qg.smartprinter.ui.LocalPrintPreviewActivity;
import com.qg.smartprinter.util.SharedPreferencesUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

public class CookbookActivity extends BaseActivity implements View.OnClickListener, MDialogFragment.MDialogFragmentListener {
    private RecyclerView.Adapter mAdapter;
    private ArrayList<CookInView> mCooks = new ArrayList<>();
    private LinkedList<CookInView> mOrderCooks = new LinkedList<>();
    private TextView mNumberTextView;

    public static void start(Context context) {
        Intent starter = new Intent(context, CookbookActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cookbook);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNumberTextView = (TextView) findViewById(R.id.num);
        mNumberTextView.setText(String.valueOf(getSumCount()));
        ImageView shoppingCar = (ImageView) findViewById(R.id.shopping_car);
        shoppingCar.setOnClickListener(this);

        // TODO: 2016/8/7 将测试数据变为真实可编辑的数据
        mCooks.addAll(CookbookDummy.newCooks());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter = new Adapter());

        View done = findViewById(R.id.fab);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmOrder();
            }
        });
    }

    private void confirmOrder() {
//            // TODO: 2016/10/21 加上桌号填写
//            order.setTableNumber(5);
        ArrayList<OrderContent> orderContents = new ArrayList<>();
        for (int i = 0; i < mOrderCooks.size(); i++) {
            CookInView cookInView = mOrderCooks.get(i);
            OrderContent oc = new OrderContent(
                    cookInView.name,
                    cookInView.count,
                    cookInView.count * cookInView.price
            );
            orderContents.add(oc);
        }

        SharedPreferencesUtils su = SharedPreferencesUtils.getInstance();
        Business business = new Business();
        business.setName(su.getString(SharedPreferencesUtils.BUSINESS_NAME, ""));
        business.setPhone(su.getString(SharedPreferencesUtils.BUSINESS_PHONE, ""));
        business.setAddress(su.getString(SharedPreferencesUtils.BUSINESS_ADDRESS, ""));
        business.setAdvertisement(su.getString(SharedPreferencesUtils.BUSINESS_AD, ""));

        LocalOrder order = new LocalOrder.Builder()
                .orderId(OrderManager.getInstance().getNextOrderNumber())
                .contents(orderContents)
                .business(business)
                .build();

        LocalPrintPreviewActivity.start(this, order);
    }

    private int getSumCount() {
        int sum = 0;
        for (CookInView c : mOrderCooks) {
            sum += c.count;
        }
        return sum;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shopping_car:
                showShoppingDialog();
                break;
        }
    }

    public void showShoppingDialog() {
        FragmentManager fm = getSupportFragmentManager();
        MDialogFragment dialog = MDialogFragment.newInstance(mOrderCooks);
        dialog.show(fm, "shopping_car");
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView mNameTextView;
        TextView mDescriptionTextView;
        ImageView mPictureImageView;
        TextView mPriceTextView;
        TextView mCountTextView;
        View mSubButton;
        View mAddButton;

        ViewHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.name);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.description);
            mPictureImageView = (ImageView) itemView.findViewById(R.id.picture);
            mPriceTextView = (TextView) itemView.findViewById(R.id.price);
            mCountTextView = (TextView) itemView.findViewById(R.id.count);
            mSubButton = itemView.findViewById(R.id.sub_btn);
            mAddButton = itemView.findViewById(R.id.add_btn);
            mSubButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    CookInView cook = mCooks.get(position);
                    cook.count--;
                    updateCount();
                    if (cook.count <= 0) {
                        mOrderCooks.remove(cook);
                    }
                }
            });
            mAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    CookInView cook = mCooks.get(position);
                    cook.count++;
                    if (!mOrderCooks.contains(cook)) {
                        mOrderCooks.add(cook);
                    }
                    updateCount();
                }
            });
        }

        void updateCount() {
            int count = mCooks.get(getAdapterPosition()).count;
            boolean hasCount = count > 0;
            mCountTextView.setText(String.valueOf(count));
            mNumberTextView.setText(String.valueOf(getSumCount()));
            mSubButton.setVisibility(hasCount ? View.VISIBLE : View.GONE);
            mCountTextView.setVisibility(hasCount ? View.VISIBLE : View.GONE);
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.item_cookbook, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CookInView cook = mCooks.get(position);
            holder.mNameTextView.setText(cook.name);
            holder.mDescriptionTextView.setText(cook.description);
            holder.mPriceTextView.setText(String.format(Locale.CHINA, "¥%d", cook.price));
            holder.updateCount();
            Picasso.with(CookbookActivity.this)
                    .load(cook.picture)
                    .placeholder(R.drawable.ic_photo_black_48dp)
                    .into(holder.mPictureImageView);
        }

        @Override
        public int getItemCount() {
            return mCooks.size();
        }

    }

    @Override
    public void onFinishEditDialog(LinkedList<CookInView> mCook, int requestCode) {
        mAdapter.notifyDataSetChanged();
        if (requestCode == 1) { // 打印
            confirmOrder();
        } else if (requestCode == 2) {
            mOrderCooks.clear();
        }
    }

    private final static int REQUEST_CODE = 1;

    private void clearCart() {
        for (CookInView c : mCooks) {
            c.count = 0;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case REQUEST_CODE:
                clearCart();
                break;
            default:
                break;
        }
    }
}
