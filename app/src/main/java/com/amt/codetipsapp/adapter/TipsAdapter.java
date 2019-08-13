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

public class TipsAdapter extends ArrayAdapter<ErrorTips> {

    private int resourceId;

    public TipsAdapter(Context context, int tvResourceId, List<ErrorTips> codesList) {
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

            viewHolder.searchTopCode = view.findViewById(R.id.searchTopCode);
            viewHolder.leftCode = view.findViewById(R.id.leftCode);
            viewHolder.rightCode = view.findViewById(R.id.rightCode);
            viewHolder.leftUsedBy = view.findViewById(R.id.leftUsedBy);
            viewHolder.rightUsedBy = view.findViewById(R.id.rightUsedBy);
            viewHolder.leftDisplay = view.findViewById(R.id.leftDisplay);
            viewHolder.rightDisplay = view.findViewById(R.id.rightDisplay);
            viewHolder.leftCause = view.findViewById(R.id.leftCause);
            viewHolder.rightCause = view.findViewById(R.id.rightCause);
            viewHolder.leftRemedy = view.findViewById(R.id.leftRemedy);
            viewHolder.rightRemedy = view.findViewById(R.id.rightRemedy);

            // 将ViewHolder存储在View中
            view.setTag(viewHolder);
        }
        else {
            view = convertView;
            // 重新获取ViewHolder
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.searchTopCode.setText(errorTips.getCode());
        viewHolder.leftCode.setText("Code");
        viewHolder.rightCode.setText(errorTips.getCode());
        viewHolder.leftUsedBy.setText("UsedBy");
        viewHolder.rightUsedBy.setText(errorTips.getUsedBy());
        viewHolder.leftDisplay.setText("Display");
        viewHolder.rightDisplay.setText(errorTips.getDisplay());
        viewHolder.leftCause.setText("Cause");
        viewHolder.rightCause.setText(errorTips.getCause());
        viewHolder.leftRemedy.setText("Remedy");
        viewHolder.rightRemedy.setText(errorTips.getRemedy());

        return view;
    }

    class ViewHolder {
        // 右边布局使用的TextView
        private TextView searchTopCode;
        private TextView leftCode, rightCode;
        private TextView leftUsedBy, rightUsedBy;
        private TextView leftDisplay, rightDisplay;
        private TextView leftCause, rightCause;
        private TextView leftRemedy, rightRemedy;
    }

}
