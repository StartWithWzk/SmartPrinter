package com.qg.deprecated.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.qg.smartprinter.R;

import java.util.List;

/**
 * Created by 攀登 on 2016/7/29.
 */
public class PrintIdAdapter extends ArrayAdapter<String> {

    private int resourceId;
    private Context mContext;

    public PrintIdAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        mContext = context;
        resourceId = resource;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        String id = getItem(position);
        ViewHolder viewHolder;
        if (converView == null) {
            converView = LayoutInflater.from(mContext).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.id = (TextView) converView.findViewById(R.id.printId);
            converView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) converView.getTag();
        }
        viewHolder.id.setText(id);
        return converView;
    }

    class ViewHolder {
        TextView id;
    }
}
