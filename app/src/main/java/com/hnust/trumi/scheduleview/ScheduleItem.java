package com.hnust.trumi.scheduleview;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by lzm on 16-12-23.
 */

public class ScheduleItem implements View.OnClickListener, View.OnLongClickListener {
    private int bgColor;
    private int textColor;
    private int padding;
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
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.setLayoutParams(params);
        rootView.setLongClickable(true);
        rootView.setOnClickListener(this);
        rootView.setAlpha(0.8f);
        rootView.setTextSize(12);
        rootView.setPadding(5, 5, 5, 5);
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
        rootView.setBackgroundColor(colorInt);
    }

    public void setTextColorResources(@ColorRes int resId) {
        setTextColor(context.getResources().getColor(resId));
    }

    public void setPadding(int padding) {
        this.padding = padding;
        rootView.setPadding(padding, padding, padding, padding);
    }

    public void setTextColor(@ColorInt int colorInt) {
        this.textColor = colorInt;
        rootView.setTextColor(textColor);
    }

    public void setText(String scheduleInfo) {
        this.scheduleText = scheduleInfo;
        rootView.setText(scheduleInfo);
        rootView.setTextColor(Color.WHITE);
    }

    public void setColumnSpec(int columnSpec) {
        setColumnSpec(columnSpec, 1);
    }

    public void setColumnSpec(int columnSpec, int columnSize) {
        columnPositionInfo = new PositionInfo(columnSpec, columnSize);
    }

    public void setRowSpec(int rowSpec) {
        setRowSpec(rowSpec, 2);
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

    public int getPadding() {
        return padding;
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
