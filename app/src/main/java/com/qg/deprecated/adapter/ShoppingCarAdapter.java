package com.qg.deprecated.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qg.smartprinter.R;
import com.qg.deprecated.logic.model.Item;
import java.util.List;

/**
 * Created by 攀登 on 2016/7/31.
 */
public class ShoppingCarAdapter extends ArrayAdapter<Item> implements View.OnClickListener {

    private int resourceId;
    private Context mContext;

    public ShoppingCarAdapter(Context context, int resource, List<Item> objects) {
       super(context, resource, objects);
        resourceId = resource;
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = getItem(position);
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.price = (TextView) convertView.findViewById(R.id.price);
            viewHolder.sub = (ImageButton) convertView.findViewById(R.id.sub_btn);
            viewHolder.count = (TextView) convertView.findViewById(R.id.count);
            viewHolder.add = (ImageButton) convertView.findViewById(R.id.add_btn);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(item.getName());
        viewHolder.price.setText("¥" + item.getPrice());
        viewHolder.sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        viewHolder.count.setText(item.getCount());
        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return convertView;
    }

    @Override
    public void onClick(View v) {

    }

    class ViewHolder{
        TextView name, price, count;
        ImageButton sub, add;
    }
}
