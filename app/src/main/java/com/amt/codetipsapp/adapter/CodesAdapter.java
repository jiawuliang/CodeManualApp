package com.amt.codetipsapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.amt.codetipsapp.R;
import com.amt.codetipsapp.bean.ErrorTips;

import java.util.List;

/**
 * Created by liangjw on 2019.07.26.
 */

public class CodesAdapter extends ArrayAdapter<ErrorTips> {

    private int resourceId;

    public CodesAdapter(Context context, int tvResourceId, List<ErrorTips> codesList) {
        super(context, tvResourceId, codesList);
        resourceId = tvResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取当前项的ErrorCode实例
        ErrorTips errorTips = getItem(position);

        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            // 为子项加载传入的布局
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvCode = view.findViewById(R.id.tv_codes);

            // 将ViewHolder存储在View中
            view.setTag(viewHolder);
        }
        else {
            view = convertView;
            // 重新获取ViewHolder
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tvCode.setText(errorTips.getCode());

        return view;
    }

    class ViewHolder {
        private TextView tvCode;
    }

}
