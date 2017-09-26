package com.qg.smartprinter.ui.cookbook;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v4.app.DialogFragment;

import com.qg.smartprinter.R;
import com.qg.smartprinter.logic.model.CookInView;

import java.util.LinkedList;

/**
 * Created by 攀登 on 2016/7/31.
 */
public class MDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "DatePickDialogFragment";
    public static final String ARGS_COOK = "cook";
    private LinkedList<CookInView> mCook;
    private LinkedList<CookInView> m = new LinkedList<>();
    private ShoppingCarAdapter adapter;
    private TextView sumPrice;

    public interface MDialogFragmentListener {
        void onFinishEditDialog(LinkedList<CookInView> mCook, int REQUEST);
    }

    public MDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME,
                android.R.style.Theme_Holo_Light);
        mCook = (LinkedList<CookInView>) getArguments().getSerializable(ARGS_COOK);
    }

    public static MDialogFragment newInstance(LinkedList<CookInView> cook) {

        Bundle args = new Bundle();
        args.putSerializable(ARGS_COOK, cook);

        MDialogFragment fragment = new MDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    RecyclerView mListView;

    private int getSumPrice() {
        int sum = 0;
        for(CookInView c : mCook) {
            sum += c.count * c.price;
        }
        return sum;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shopping_dialog, container, false);

        mListView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sumPrice = (TextView) view.findViewById(R.id.price);
        sumPrice.setText(String.valueOf(getSumPrice()));
        adapter = new ShoppingCarAdapter();
        mListView.setAdapter(adapter);

        view.findViewById(R.id.empty_car).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 删除所有的car
                for(CookInView c : mCook) {
                    c.count = 0;
                }
                MDialogFragmentListener mListener = (MDialogFragmentListener) getActivity();
                mListener.onFinishEditDialog(mCook, 2);
                getDialog().dismiss();
            }

        });
        view.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 确定打印
                MDialogFragmentListener mListener = (MDialogFragmentListener) getActivity();
                mListener.onFinishEditDialog(mCook, 1);
                getDialog().dismiss();
            }

        });
        view.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 取消
                MDialogFragmentListener mListener = (MDialogFragmentListener) getActivity();
                mListener.onFinishEditDialog(mCook, 0);
                getDialog().dismiss();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.BOTTOM;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
    }

    @Override
    public void onClick(View v) {

    }

    private class ShoppingCarAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder viewHolder = new ViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_shoppingcar, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CookInView cook = mCook.get(position);
            holder.name.setText(cook.name);
            holder.updateCount();
        }

        @Override
        public int getItemCount() {
            return mCook.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, count;
        ImageButton sub, add;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            price = (TextView) itemView.findViewById(R.id.price);
            sub = (ImageButton) itemView.findViewById(R.id.sub_btn);
            count = (TextView) itemView.findViewById(R.id.count);
            add = (ImageButton) itemView.findViewById(R.id.add_btn);

            sub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    CookInView cook = mCook.get(position);
                    cook.count--;
                    updateCount();
                    if (cook.count <= 0) {
                        mCook.remove(cook);
                        adapter.notifyDataSetChanged();

                    }
                }
            });
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    CookInView cook = mCook.get(position);
                    cook.count++;
                    updateCount();
                    if (!mCook.contains(cook)) {
                        mCook.add(cook);
                    }
                }
            });
        }

        void updateCount() {
            int c = mCook.get(getAdapterPosition()).count;
            boolean hasCount = c > 0;
            sumPrice.setText("¥" + String.valueOf(getSumPrice()));
            price.setText("¥" + String.valueOf(c * mCook.get(getAdapterPosition()).price));
            count.setText(String.valueOf(c));
        }
    }
}
