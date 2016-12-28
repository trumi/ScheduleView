package com.hnust.lzm.scheduleview;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.widget.GridLayout.VERTICAL;

/**
 * Created by lzm on 16-12-23.
 */

public class ScheduleView extends RelativeLayout implements ScheduleItem.OnClickListener {
    @IdRes
    private final int WEEK_BAR_WEEK_TEXT_ID = 0;
    @IdRes
    private final int WEEK_BAR_DATE_TEXT_ID = 1;
    @IdRes
    private final int TIME_BAR_ORDER_TEXT_ID = 2;
    @IdRes
    private final int TIME_BAR_TIME_TEXT_ID = 3;
    @IdRes
    private final int SCHEDULE_LAYOUT_ID = 4;

    private final int textColor = Color.parseColor("#000000");
    private int screenHight;
    private int screenWidth;
    private int itemHight;
    private int itemWidth;
    private int columnNum;
    private int rowNum;
    private final String[] weekLabels = {"一", "二", "三", "四", "五", "六", "日"};
    private GridLayout scheduleLayout;
    private LinearLayout weekBarLayout;
    private LinearLayout timeBarLayout;
    private List<ScheduleItem> scheduleList;
    private List<TimeLabel> timeLabelList = new ArrayList<>();
    private List<WeekLabel> weekLabelList = new ArrayList<>();
    private OnScheduleItemClickListener mOnScheduleItemClick;
    private TypedArray typedArray;

    public interface OnScheduleItemClickListener {
        void onClick(ScheduleItem item);

        void onLongClick(ScheduleItem item);
    }

    public ScheduleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScheduleView, defStyle, 0);
        rowNum = typedArray.getInt(R.styleable.ScheduleView_day, 10);
        typedArray.recycle();
        init();
    }

    public ScheduleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScheduleView);
        rowNum = typedArray.getInt(R.styleable.ScheduleView_day, 10);
        typedArray.recycle();
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
        return scheduleList;
    }

    public void setSourceList(List<ScheduleItem> list) {
        this.scheduleList = list;
    }

    public void notifyData() {
        if (scheduleList != null) {
            for (ScheduleItem item : scheduleList) {
                addItem(item);
            }
        }
    }

    private void initWeekBar() {
        for (int i = 0; i < 7; i++) {
            WeekLabel weekLabel = new WeekLabel();
            weekLabel.setWeek("周" + weekLabels[i]);
            weekLabelList.add(weekLabel);

            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            linearLayout.setLayoutParams(params);

            TextView dateText = new TextView(getContext());
            dateText.setId(WEEK_BAR_WEEK_TEXT_ID);
            dateText.setTextColor(textColor);
            dateText.setText(String.valueOf(i + 1) + "号");
            dateText.setGravity(Gravity.CENTER);
            linearLayout.addView(dateText);
            if (weekLabelList.get(i).getDate() == null) {
                dateText.setVisibility(GONE);
            } else {
                dateText.setVisibility(VISIBLE);
            }

            TextView weekText = new TextView(getContext());
            weekText.setId(WEEK_BAR_DATE_TEXT_ID);
            weekText.setTextColor(textColor);
            weekText.setText(weekLabelList.get(i).getWeek());
            weekText.setGravity(Gravity.CENTER);
            linearLayout.addView(weekText);

            LinearLayout.LayoutParams gridLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            gridLayoutParams.columnSpec = GridLayout.spec(i + 1, 1, 1f);
            gridLayoutParams.rowSpec = GridLayout.spec(0, 1);
            gridLayoutParams.setGravity(Gravity.FILL);
            gridLayoutParams.setMargins(1, 1, 1, 1);
            weekBarLayout.addView(linearLayout, gridLayoutParams);
        }
    }

    public void initTimeBar() {
        for (int i = 0; i < rowNum; i++) {
            TimeLabel timeLabel = new TimeLabel();
            timeLabel.setOrder(String.valueOf(i + 1));
            timeLabelList.add(timeLabel);

            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout.setLayoutParams(params);

            TextView orderText = new TextView(getContext());
            orderText.setId(TIME_BAR_ORDER_TEXT_ID);
            orderText.setTextColor(textColor);
            orderText.setText(timeLabelList.get(i).getOrder());
            orderText.setGravity(Gravity.CENTER);
            linearLayout.addView(orderText);

            TextView timeText = new TextView(getContext());
            timeText.setId(TIME_BAR_TIME_TEXT_ID);
            timeText.setTextColor(textColor);
            timeText.setText(timeLabelList.get(i).getTime());
            timeText.setGravity(Gravity.CENTER);
            linearLayout.addView(timeText);
            if (timeLabelList.get(i).getTime() == null) {
                timeText.setVisibility(GONE);
            } else {
                timeText.setVisibility(VISIBLE);
            }

            GridLayout.LayoutParams gridLayoutParams = new GridLayout.LayoutParams();
            gridLayoutParams.columnSpec = GridLayout.spec(0, 1, 1f);
            gridLayoutParams.rowSpec = GridLayout.spec(i + 1, 1);
            gridLayoutParams.height = itemHight;
            gridLayoutParams.setGravity(Gravity.FILL);
            gridLayoutParams.setMargins(1, 1, 1, 1);
            scheduleLayout.addView(linearLayout, gridLayoutParams);
        }
    }

    public void initMonthText() {
        TextView monthText = new TextView(getContext());
        monthText.setId(TIME_BAR_TIME_TEXT_ID);
        monthText.setTextColor(textColor);
        monthText.setText("12月");
        monthText.setTextSize(13);
        monthText.setGravity(Gravity.CENTER);
        GridLayout.LayoutParams gridLayoutParams = new GridLayout.LayoutParams();
        gridLayoutParams.columnSpec = GridLayout.spec(0, 1, 1f);
        gridLayoutParams.rowSpec = GridLayout.spec(0, 1);
        gridLayoutParams.setGravity(Gravity.FILL);
        gridLayoutParams.setMargins(1, 1, 1, 1);
        scheduleLayout.addView(monthText, gridLayoutParams);
    }

    private void addItem(ScheduleItem item) {

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.columnSpec = GridLayout.spec(item.getColumnSpec().getStart(), item.getColumnSpec().getSize(), 1f);
        params.rowSpec = GridLayout.spec(item.getRowSpec().getStart(), item.getRowSpec().getSize());
        params.setGravity(Gravity.FILL);
        params.setMargins(1, 1, 1, 1);
        params.width = itemWidth - 5;
        params.height = itemHight * item.getRowSpec().getSize();
        scheduleLayout.addView(item.getRootView(), params);
        item.setOnClickListener(this);
    }

    private void init() {
        ScrollView scrollView=new ScrollView(getContext());
        HorizontalScrollView horizontalScrollView=new HorizontalScrollView(getContext());
        scheduleLayout=new GridLayout(getContext());
        scheduleLayout.setId(SCHEDULE_LAYOUT_ID);
        scheduleLayout.setRowCount(rowNum);
        scheduleLayout.setColumnCount(8);
        scheduleLayout.setOrientation(VERTICAL);
        horizontalScrollView.addView(scheduleLayout);
        scrollView.addView(horizontalScrollView);
        this.addView(scrollView);
        weekBarLayout=new LinearLayout(getContext());
        weekBarLayout.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout.LayoutParams weekBarParams = new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        weekBarParams.addRule(RelativeLayout.ABOVE,SCHEDULE_LAYOUT_ID);
        weekBarParams.setMargins(10,0,0,0);
        this.addView(weekBarLayout,weekBarParams);
        /*
        android.support.v7.widget.GridLayout.LayoutParams params =
                new android.support.v7.widget.GridLayout.LayoutParams();
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        setLayoutParams(params);*/
        /*ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                screenWidth = getWidth();
                screenHight = getHeight();
                itemWidth = screenWidth / 8;
                itemHight = screenHight / 11;
                initWeekBar();
                initTimeBar();
                initMonthText();
            }
        });*/
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        this.screenWidth = display.getWidth();
        this.screenHight = display.getHeight();
        this.itemWidth = this.screenWidth / 8;
        this.itemHight = this.screenHight / 11;
        initWeekBar();
        initTimeBar();
        initMonthText();
    }

}
