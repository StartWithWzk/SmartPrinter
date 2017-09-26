package com.qg.deprecated.adapter;

import java.util.List;

import com.qg.smartprinter.R;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class PhotoAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<String> list;
    private ViewHolder viewHolder;
    private Context context;

    public PhotoAdapter(Context context, List<String> list) {
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
     }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.photo_item, null);
            viewHolder.image = (ImageView) convertView
                    .findViewById(R.id.image);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Picasso.with(context)
                .load(list.get(position))
                .into(viewHolder.image);

        return convertView;
    }

    public class ViewHolder {
        public ImageView image;
    }
}
