package com.qg.common.widget;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.qg.common.logger.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link RecyclerView.Adapter} with FooterView.
 */
public abstract class RecyclerArrayAdapter<T> extends RecyclerBaseAdapter {

    private List<T> mItems;

    private OnItemClickListener mOnItemClickListener;

    public RecyclerArrayAdapter(Context context, @LayoutRes int layoutResource) {
        this(context, new ArrayList<T>(), layoutResource);
    }

    public RecyclerArrayAdapter(Context context, ArrayList<T> items, @LayoutRes int layoutResource) {
        super(context, layoutResource);
        mItems = items;
    }

    protected abstract void onBind(CommonViewHolder holder, T item);

    @Override
    public void onBindViewHolder(final CommonViewHolder holder, int position) {
        final T item = getItem(position);
        onBind(holder, item);
        holder.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(holder.getAdapterPosition());
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setList(List<T> list) {
        mItems = list;
        notifyDataSetChanged();
    }

    public void addAll(List<T> list) {
        int size = mItems.size();
        mItems.addAll(list);
        notifyItemRangeInserted(size, list.size());
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    public T getItem(int position) {
        return mItems.get(position);
    }

    private static final String TAG = "RecyclerArrayAdapter";
    public int getItemCount() {
        int size = mItems.size();
        Log.d(TAG, "getItemCount: " + size);
        return size;
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
