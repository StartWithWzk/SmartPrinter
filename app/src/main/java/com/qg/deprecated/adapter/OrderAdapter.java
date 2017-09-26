package com.qg.deprecated.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.qg.smartprinter.R;
import com.qg.deprecated.logic.model.Order1;

import java.util.List;

/**
 * Created by 攀登 on 2016/7/25.
 */
public class OrderAdapter extends ArrayAdapter<Order1> {

    private int resourceId;
    private Context mContext;

    public OrderAdapter(Context context, int resource, List<Order1> objects) {
        super(context, resource, objects);
        resourceId = resource;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Order1 order = getItem(position);
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.num = (TextView) convertView.findViewById(R.id.num);
            viewHolder.status = (TextView) convertView.findViewById(R.id.status);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.num.setText(String.valueOf(order.getId()));
        switch (order.getOrderStatus()) {
            case "0":
                viewHolder.status.setText("打印成功");
                break;
            case "1":
                viewHolder.status.setText("打印失败");
                break;
            case "2":
                viewHolder.status.setText("进入打印队列");
                break;
            case "3":
                viewHolder.status.setText("开始打印");
                break;
            case "4":
                viewHolder.status.setText("数据错误");
                break;
            case "5":
                viewHolder.status.setText("打印成功-之前的异常订单");
                break;
            case "6":
                viewHolder.status.setText("新来的订单还未发送");
                break;
            default:
                break;
        }
        return convertView;
    }

    class ViewHolder {
        TextView num;
        TextView status;
    }
}
