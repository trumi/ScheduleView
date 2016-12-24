package com.hnust.lzm.scheduleview;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.view.View;
import android.widget.TextView;

/**
 * Created by lzm on 16-12-23.
 */

public class ScheduleItem implements View.OnClickListener, View.OnLongClickListener {

    private int bgColor;
    private int textColor;
    private String scheduleText;
    private Context context;
    private TextView rootView;
    private OnClickListener listener;
    private PositionInfo columnPositionInfo;
    private PositionInfo rowPositionInfo;

    public interface OnClickListener {
        void onClick(ScheduleItem item);

        void onLongClick(ScheduleItem item);
    }


    public ScheduleItem(Context context) {
        this.context = context;
        rootView = new TextView(context);
        rootView.setLongClickable(true);
        rootView.setOnClickListener(this);
    }

    public TextView getRootView() {
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (listener != null)
            listener.onClick(ScheduleItem.this);
    }

    @Override
    public boolean onLongClick(View v) {
        if (listener != null)
            listener.onLongClick(ScheduleItem.this);
        return true;
    }

    /**
     * Setters
     */
    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public void setBackgroundColorResources(@ColorRes int resId) {
        setBackgroundColor(context.getResources().getColor(resId));
    }

    public void setBackgroundColor(@ColorInt int colorInt) {
        this.bgColor = colorInt;
        rootView.setBackgroundColor(bgColor);
    }

    public void setTextColorResources(@ColorRes int resId) {
        ;
        setTextColor(context.getResources().getColor(resId));
    }

    public void setTextColor(@ColorInt int colorInt) {
        this.textColor = colorInt;
        rootView.setTextColor(textColor);
    }

    public void setText(String scheduleInfo) {
        this.scheduleText = scheduleInfo;
        rootView.setText(scheduleInfo);
    }

    public void setColumnSpec(int columnSpec) {
        setColumnSpec(columnSpec, 1);
    }

    public void setColumnSpec(int columnSpec, int columnSize) {
        columnPositionInfo = new PositionInfo(columnSpec, columnSize);
    }

    public void setRowSpec(int rowSpec) {
        setRowSpec(rowSpec, 1);
    }

    public void setRowSpec(int rowSpec, int rowSize) {
        rowPositionInfo = new PositionInfo(rowSpec, rowSize);
    }

    /**
     * Getters
     */
    public int getBackgroundColor() {
        return bgColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public String getText() {
        return scheduleText;
    }

    public PositionInfo getColumnSpec() {
        return columnPositionInfo;
    }

    public PositionInfo getRowSpec() {
        return rowPositionInfo;
    }
}
