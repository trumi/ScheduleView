package com.hnust.trumi.scheduleview;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.IdRes;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hnust.lzm.scheduleview.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.GridLayout.VERTICAL;

/**
 * Created by lzm on 16-12-23.
 */

public class ScheduleView extends RelativeLayout implements ScheduleItem.OnClickListener {
    @IdRes
    protected final int WEEK_BAR_WEEK_TEXT_ID = 0;
    @IdRes
    protected final int WEEK_BAR_DATE_TEXT_ID = 1;
    @IdRes
    protected final int TIME_BAR_ORDER_TEXT_ID = 2;
    @IdRes
    protected final int TIME_BAR_TIME_TEXT_ID = 3;
    @IdRes
    protected final int SCHEDULE_LAYOUT_ID = 4;
    @IdRes
    protected final int WEEK_BAR_LAYOUT_ID = 5;
    @IdRes
    protected final int TIME_BAR_LAYOUT_ID = 6;
    @IdRes
    protected final int MONTH_TEXT_ID = 7;
    protected final int defaultTextColor = Color.parseColor("#575757");
    protected final int defaultTextSize = 11;
    protected final int[] colors = {ItemColor.Amber, ItemColor.Blue, ItemColor.BlueGrey, ItemColor.Cyan, ItemColor.DeepOrange, ItemColor.DeepPurple,
            ItemColor.Green, ItemColor.LightBlue, ItemColor.Red, ItemColor.Teal, ItemColor.Orange, ItemColor.Pink, ItemColor.Purple};
    protected final String[] weekLabels = {"一", "二", "三", "四", "五", "六", "日"};
    protected int itemHight;
    protected int itemWidth;
    protected int rowNum;
    protected GridLayout scheduleLayout;
    protected LinearLayout weekBarLayout;
    protected LinearLayout timeBarLayout;
    private List<ScheduleItem> scheduleList;
    private List<TimeLabel> timeLabelList = new ArrayList<>();
    private List<WeekLabel> weekLabelList = new ArrayList<>();
    private List<Map<String, Integer>> colorToScheduleList = new ArrayList<>();
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
        if (scheduleList != null && scheduleLayout != null) {
            scheduleLayout.removeAllViews();
            for (ScheduleItem item : scheduleList) {
                addItem(item);
            }
            Log.i("AS", "notifyData: "+scheduleLayout.getRowCount());
        }
    }

    protected void initItemSizeInfo() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int screenWidth = point.x;
        int screenHight = point.y;
        this.itemWidth = (screenWidth - 14) * 2 / 15;
        this.itemHight = screenHight / 11;
    }

    protected void initMonthText() {
        TextView monthText = new TextView(getContext());
        monthText.setId(TIME_BAR_TIME_TEXT_ID);
        monthText.setTextColor(defaultTextColor);
        monthText.setText("12月");
        monthText.setId(MONTH_TEXT_ID);
        monthText.setTextSize(defaultTextSize);
        monthText.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, WEEK_BAR_LAYOUT_ID);
        params.width = itemWidth / 2;
        this.addView(monthText, params);
    }

    protected void initWeekBar() {
        weekBarLayout = new LinearLayout(getContext());
        weekBarLayout.setOrientation(LinearLayout.HORIZONTAL);
        weekBarLayout.setId(WEEK_BAR_LAYOUT_ID);
        RelativeLayout.LayoutParams weekBarParams = new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        weekBarParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        weekBarParams.addRule(RelativeLayout.RIGHT_OF, MONTH_TEXT_ID);

        for (int i = 0; i < 7; i++) {
            WeekLabel weekLabel = new WeekLabel();
            weekLabel.setWeek("周" + weekLabels[i]);
            weekLabelList.add(weekLabel);

            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            linearLayout.setLayoutParams(params);

            TextView dateText = new TextView(getContext());
            dateText.setId(WEEK_BAR_WEEK_TEXT_ID);
            dateText.setTextColor(defaultTextColor);
            dateText.setTextSize(defaultTextSize);
            dateText.setText(String.valueOf(i + 1) + "号");
            dateText.setGravity(Gravity.CENTER);
            linearLayout.addView(dateText);
/*            if (weekLabelList.get(i).getDate() == null) {
                dateText.setVisibility(GONE);
            } else {
                dateText.setVisibility(VISIBLE);
            }*/

            TextView weekText = new TextView(getContext());
            weekText.setId(WEEK_BAR_DATE_TEXT_ID);
            weekText.setTextColor(defaultTextColor);
            weekText.setTextSize(defaultTextSize + 2);
            weekText.setText(weekLabelList.get(i).getWeek());
            weekText.setGravity(Gravity.CENTER);
            linearLayout.addView(weekText);

            LinearLayout.LayoutParams gridLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            gridLayoutParams.width = itemWidth;
            gridLayoutParams.setMargins(1, 1, 1, 1);
            weekBarLayout.addView(linearLayout, gridLayoutParams);
        }
        this.addView(weekBarLayout, weekBarParams);
    }

    protected void initTimeBar() {
        timeBarLayout = new LinearLayout(getContext());
        timeBarLayout.setOrientation(LinearLayout.VERTICAL);
        timeBarLayout.setId(TIME_BAR_LAYOUT_ID);

        RelativeLayout.LayoutParams timeBarParams = new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        timeBarParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        timeBarParams.addRule(RelativeLayout.BELOW, MONTH_TEXT_ID);
        timeBarParams.addRule(RelativeLayout.ALIGN_RIGHT, MONTH_TEXT_ID);

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
            orderText.setTextColor(defaultTextColor);
            orderText.setText(timeLabelList.get(i).getOrder());
            orderText.setGravity(Gravity.CENTER);
            orderText.setTextSize(defaultTextSize + 3);
            orderText.setPadding(7, 0, 7, 0);
            linearLayout.addView(orderText);

            TextView timeText = new TextView(getContext());
            timeText.setId(TIME_BAR_TIME_TEXT_ID);
            timeText.setTextColor(defaultTextColor);
            timeText.setTextSize(defaultTextSize - 3);
            timeText.setText(timeLabelList.get(i).getTime());
            timeText.setGravity(Gravity.CENTER);
            linearLayout.addView(timeText);
            timeText.setText("24:00");
/*            if (timeLabelList.get(i).getTime() == null) {
                timeText.setVisibility(GONE);
            } else {
                timeText.setVisibility(VISIBLE);
            }*/

            LinearLayout.LayoutParams gridLayoutParams = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            gridLayoutParams.height = itemHight;
            gridLayoutParams.setMargins(0, 1, 0, 1);
            timeBarLayout.addView(linearLayout, gridLayoutParams);
        }
        this.addView(timeBarLayout, timeBarParams);
    }

    protected void initScheduleView() {
        final NestedScrollView scrollView = new NestedScrollView(getContext());
        final HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getContext());
        scrollView.setHorizontalScrollBarEnabled(false);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);

        scheduleLayout = new GridLayout(getContext());
        scheduleLayout.setId(SCHEDULE_LAYOUT_ID);
        scheduleLayout.setRowCount(rowNum);
        scheduleLayout.setColumnCount(7);
        scheduleLayout.setOrientation(VERTICAL);
        horizontalScrollView.addView(scheduleLayout);
        scrollView.addView(horizontalScrollView);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.RIGHT_OF, MONTH_TEXT_ID);
        params.addRule(RelativeLayout.BELOW, MONTH_TEXT_ID);
        this.addView(scrollView, params);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                timeBarLayout.scrollTo(0, scrollView.getScrollY());
            }
        });
        horizontalScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                weekBarLayout.scrollTo(horizontalScrollView.getScrollX(), 0);
            }
        });
    }

    private void addItem(ScheduleItem item) {

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.columnSpec = GridLayout.spec(item.getColumnSpec().getStart(), item.getColumnSpec().getSize(), 1f);
        params.rowSpec = GridLayout.spec(item.getRowSpec().getStart(), item.getRowSpec().getSize());
        params.setGravity(Gravity.FILL);
        params.setMargins(1, 1, 1, 1);
        params.setGravity(1);
        params.width = itemWidth;
        params.height = (itemHight + 1) * item.getRowSpec().getSize();
        if (item.getBackgroundColor() == 0) {
            int i;
            for (i = 0; i < colorToScheduleList.size(); i++) {
                Map<String, Integer> map = colorToScheduleList.get(i);
                if (item.getText() != null && map.containsKey(item.getText())) {
                    item.setBackgroundColor(map.get(item.getText()));
                    break;
                }
            }
            if (i == colorToScheduleList.size()) {
                Map<String, Integer> map = new HashMap<>();
                int backgroundColor = colors[colorToScheduleList.size() % colors.length];
                map.put(item.getText(), backgroundColor);
                colorToScheduleList.add(map);
                item.setBackgroundColor(backgroundColor);
            }
        }
        scheduleLayout.addView(item.getRootView(), params);
        item.setOnClickListener(this);
    }

    protected void init() {
        initItemSizeInfo();
        initMonthText();
        initWeekBar();
        initTimeBar();
        initScheduleView();
    }
}
