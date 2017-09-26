package com.qg.deprecated.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qg.smartprinter.ui.BaseActivity;
import com.qg.deprecated.util.NetworkHelper;
import com.qg.smartprinter.Application;
import com.qg.smartprinter.R;
import com.qg.smartprinter.logic.model.CookInView;
import com.qg.deprecated.logic.model.DoneOrder;
import com.qg.deprecated.logic.model.Item;
import com.qg.deprecated.logic.param.PlaceOrderParam;
import com.qg.deprecated.logic.result.PlaceOrderResult;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrintPreviewActivity extends BaseActivity {

    public static final String EXTRA_ORDER = "order";
    private RecyclerView mCooksView;
    private List<CookInView> mCooks = new ArrayList<>();
    private DoneOrder mDoneOrder;

    public static void start1(Activity context, DoneOrder order, int requestCode) {
        Intent starter = new Intent(context, PrintPreviewActivity.class);
        starter.putExtra(EXTRA_ORDER, order);
        context.startActivityForResult(starter, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_preview);
        mDoneOrder = (DoneOrder) getIntent().getSerializableExtra(EXTRA_ORDER);
        mCooks.addAll(mDoneOrder.order.cooks);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        mCooksView = ((RecyclerView) findViewById(R.id.list));
        mCooksView.setAdapter(new Adapter());

        TextView moneyView = (TextView) findViewById(R.id.money);
        float sum = 0;
        for (CookInView c : mCooks) {
            sum += (c.count * c.price);
        }
        sum = sum + mDoneOrder.orderMealFee + mDoneOrder.orderDisFee - mDoneOrder.orderPreAmount;
        moneyView.setText(String.format("餐盒费 \n配送费 \n折扣 \n合计 \n[%s]",
                mDoneOrder.orderMealFee, mDoneOrder.orderDisFee, mDoneOrder.orderPreAmount, String.valueOf(sum), "已付款"));
        TextView companyNameView = (TextView) findViewById(R.id.company_name);
        companyNameView.setText("某某公司");
        TextView clientNameView = (TextView) findViewById(R.id.client_name);
        clientNameView.setText("某某商家");
        ((TextView) findViewById(R.id.order_time)).setText("下单时间：" + mDoneOrder.orderTime);
        ((TextView) findViewById(R.id.other)).setText(String.format("备注：%s", mDoneOrder.orderRemark));
        ((TextView) findViewById(R.id.user_message)).setText(
                String.format("顾客姓名：%s\n送餐地址：%s\n电话：%s",
                        mDoneOrder.userName, mDoneOrder.userAddress, mDoneOrder.userTelephone));
        ((TextView) findViewById(R.id.other_client_message)).setText(
                String.format("商家地址：%s\n商家电话：%s",
                        "某某区", "10086"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingDialog();
                new AsyncTask<String, Void, String>() {

                    @Override
                    protected String doInBackground(String... params) {
                        try {
                            return requestPrint();
                        } catch (IOException e) {
                            return "出现异常！！！\n" + Arrays.toString(e.getStackTrace());
                        }
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        PlaceOrderResult result = new Gson().fromJson(s, PlaceOrderResult.class);
                        if (result.isOk()) {
                            if (result.status.equals("SUCCESS")) {
                                Toast.makeText(PrintPreviewActivity.this, R.string.place_order_success, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                Toast.makeText(PrintPreviewActivity.this, R.string.place_order_fail, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(PrintPreviewActivity.this, R.string.net_problem, Toast.LENGTH_LONG).show();
                        }
                        dismissMyDialog();
                    }
                }.execute();
            }
        });
    }

    private String requestPrint() throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ArrayList<Item> list = new ArrayList<>();
        for (CookInView c : mDoneOrder.order.cooks) {
            list.add(new Item(c.name, String.valueOf(c.price), String.valueOf(c.count)));
        }
        return NetworkHelper.getInstance().postToServer("/order/" + Application.getInstance().getUser().getId(), new Gson().toJson(new PlaceOrderParam(mDoneOrder.company,
                mDoneOrder.orderTime, mDoneOrder.expectTime, list, mDoneOrder.orderRemark, String.valueOf(mDoneOrder.orderMealFee), String.valueOf(mDoneOrder.orderDisFee),
                String.valueOf(mDoneOrder.orderPreAmount), "已付款", mDoneOrder.userName, mDoneOrder.userAddress, mDoneOrder.userTelephone)));
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mNameView;
        private final TextView mCountView;
        private final TextView mTotalView;

        public ViewHolder(View itemView) {
            super(itemView);
            mNameView = ((TextView) itemView.findViewById(R.id.name));
            mCountView = ((TextView) itemView.findViewById(R.id.count));
            mTotalView = ((TextView) itemView.findViewById(R.id.total));
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.item_cook_in_order, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CookInView cook = mCooks.get(position);
            final int total = cook.count * cook.price;
            holder.mNameView.setText(cook.name);
            holder.mCountView.setText(String.format("X%d", cook.count));
            holder.mTotalView.setText(String.format("%d", total));
        }

        @Override
        public int getItemCount() {
            return mCooks.size();
        }
    }
}
