package com.hnust.lzm.scheduleview;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;

import java.util.List;

/**
 * Created by lzm on 16-12-23.
 */

public class ScheduleView extends GridLayout implements ScheduleItem.OnClickListener {

    private int screenHight;
    private int screenWidth;
    private int itemHight;
    private int itemWidth;
    private List<ScheduleItem> list;
    private OnScheduleItemClickListener mOnScheduleItemClick;
    private GridLayout.LayoutParams params;
    private TypedArray typedArray;

    public interface OnScheduleItemClickListener {
        void onClick(ScheduleItem item);

        void onLongClick(ScheduleItem item);
    }

    public ScheduleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        typedArray=context.obtainStyledAttributes(attrs,R.styleable.ScheduleView,defStyle,0);
        typedArray.getInt(R.styleable.ScheduleView_week,7);
        typedArray.getInt(R.styleable.ScheduleView_day,10);
        typedArray.recycle();
    }

    public ScheduleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScheduleView(Context context) {
        super(context);
        init();
    }

    public void setOnScheduleItemClickListener(OnScheduleItemClickListener listener) {
        this.mOnScheduleItemClick = listener;
    }

    @Override
    public void onClick(ScheduleItem item) {
        if (mOnScheduleItemClick != null) {
            mOnScheduleItemClick.onClick(item);
        }
    }

    @Override
    public void onLongClick(ScheduleItem item) {

        if (mOnScheduleItemClick != null) {
            mOnScheduleItemClick.onLongClick(item);
        }
    }

    public List<ScheduleItem> getList() {
        return list;
    }

    public void setSourceList(List<ScheduleItem> list) {
        this.list = list;
    }

    public void notifyData(){
        if (list!=null) {
            for (ScheduleItem item : list) {
                addItem(item);
            }
        }
    }

    public void addItem(ScheduleItem item) {
        params.columnSpec = GridLayout.spec(item.getColumnSpec().getStart(), item.getColumnSpec().getSize());
        params.rowSpec = GridLayout.spec(item.getRowSpec().getStart(), item.getRowSpec().getSize());
        params.setGravity(Gravity.FILL);
        params.setMargins(1, 1, 1, 1);
        params.width = itemWidth - 5;
        params.height = itemHight;
        this.addView(item.getRootView(), params);
        item.setOnClickListener(this);
    }

    private void init() {
        params = new GridLayout.LayoutParams();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        this.screenWidth = display.getWidth();
        this.screenHight = display.getHeight();
        this.itemWidth = this.screenWidth / 8;
        this.itemHight = this.screenHight / 6;
    }
}
