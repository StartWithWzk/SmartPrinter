package com.qg.deprecated.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.qg.smartprinter.R;
import com.qg.deprecated.logic.model.PrinterStatus;

import java.util.List;

/**
 * Created by 攀登 on 2016/7/31.
 */
public class PrinterAdapter extends ArrayAdapter<PrinterStatus> {

    private int resourceId;
    private Context mContext;

    public PrinterAdapter(Context context, int resource, List<PrinterStatus> objects) {
        super(context, resource, objects);
        resourceId = resource;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PrinterStatus status = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.id = (TextView) convertView.findViewById(R.id.num);
            viewHolder.status = (TextView) convertView.findViewById(R.id.status);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.id.setText(String.valueOf(status.getId()));
        switch (status.getPrinterStatus()) {
            case 1:
                viewHolder.status.setText("切刀错误");
                break;
            case 2:
                viewHolder.status.setText("机盒打开");
                break;
            case 3:
                viewHolder.status.setText("纸将用尽");
                break;
            case 4:
                viewHolder.status.setText("正在进纸");
                break;
            case 5:
                viewHolder.status.setText("机芯高温");
                break;
            case 6:
                viewHolder.status.setText("正常状态");
                break;
            case 7:
                viewHolder.status.setText("待定");
                break;
            case 8:
                viewHolder.status.setText("待定");
                break;
            case 9:
                viewHolder.status.setText("待定");
                break;
            case 10:
                viewHolder.status.setText("待定");
                break;
            case 11:
                viewHolder.status.setText("待定");
                break;
            case 12:
                viewHolder.status.setText("普通缓冲区满");
                break;
            case 13:
                viewHolder.status.setText("紧急缓冲区满");
                break;
            case 14:
                viewHolder.status.setText("健康状态");
                break;
            case 15:
                viewHolder.status.setText("亚健康状态");
                break;
            default:
                break;
        }
        return convertView;
    }

    class ViewHolder {
        TextView id;
        TextView status;
    }
}
