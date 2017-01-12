package com.hnust.trumi.scheduleview;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.IdRes;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.GridLayout.VERTICAL;

/**
 * Created by trumi on 16-12-23.
 */

public class ScheduleView extends RelativeLayout implements ScheduleItem.OnClickListener {
    private final int[] colors = {ItemColor.Amber, ItemColor.Blue, ItemColor.BlueGrey, ItemColor.Cyan, ItemColor.DeepOrange, ItemColor.DeepPurple,
            ItemColor.Green, ItemColor.LightBlue, ItemColor.Red, ItemColor.Teal, ItemColor.Orange, ItemColor.Pink, ItemColor.Purple};
    private final String[] weekLabels = {"一", "二", "三", "四", "五", "六", "日"};
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
    @IdRes
    private final int WEEK_BAR_LAYOUT_ID = 5;
    @IdRes
    private final int TIME_BAR_LAYOUT_ID = 6;
    @IdRes
    private final int MONTH_TEXT_ID = 7;
    private final int MODE_TILE = 0, MODE_BIG = 1;
    private final int defaultTextColor = Color.parseColor("#575757");
    private int signColor = Color.parseColor("#9CCC65");
    private final int defaultTextSize = 11;
    private final int columeNum = 7;
    private int itemHight;
    private int itemWidth;
    private int rowNum;
    private int layoutMode;
    //设置被标志的星期,0为不标志
    private int weekOfToday = 0;
    private GridLayout scheduleLayout;
    private LinearLayout weekBarLayout;
    private LinearLayout timeBarLayout;
    private NestedScrollView scheduleVerticalScrollView;
    private HorizontalScrollView scheduleHorizontalScrollView;
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
        layoutMode = typedArray.getInt(R.styleable.ScheduleView_layout_mode, 0);
        typedArray.recycle();
        init();
    }

    public ScheduleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScheduleView);
        rowNum = typedArray.getInt(R.styleable.ScheduleView_day, 10);
        layoutMode = typedArray.getInt(R.styleable.ScheduleView_layout_mode, 0);
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

    public int getWeekOfToday() {
        return weekOfToday;
    }

    public void setWeekOfToday(int weekOfToday) {
        this.weekOfToday = weekOfToday;
    }

    public int getSignColor() {
        return signColor;
    }

    public void setSignColor(int signColor) {
        this.signColor = signColor;
    }

    public void notifyData() {
        updateWeekBar();
        if (scheduleList != null && scheduleLayout != null) {
            scheduleLayout.removeAllViews();
            addBaseBlock();
            for (ScheduleItem item : scheduleList) {
                addItem(item);
            }
        }
        //解决在big模式下周六、日课表超出屏幕的问题
        if (layoutMode == MODE_BIG && weekOfToday >= 6) {
            scheduleHorizontalScrollView.scrollTo(500, 0);
        }
    }

    private void initItemSizeInfo() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int screenWidth = point.x;
        int screenHight = point.y;
        this.itemHight = screenHight / 11;
        if (layoutMode == MODE_TILE) {
            this.itemWidth = (screenWidth - 2 * columeNum) * 2 / (2 * columeNum + 1);
        } else if (layoutMode == MODE_BIG) {
            this.itemWidth = (screenWidth - 2 * (columeNum - 1)) * 2 / (2 * (columeNum - 1) + 1);
        }
    }

    private void initMonthText() {
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

    private void initWeekBar() {
        weekBarLayout = new LinearLayout(getContext());
        weekBarLayout.setOrientation(LinearLayout.HORIZONTAL);
        weekBarLayout.setId(WEEK_BAR_LAYOUT_ID);
        weekBarLayout.setPadding(0, 5, 0, 5);
        RelativeLayout.LayoutParams weekBarParams = new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        weekBarParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        weekBarParams.addRule(RelativeLayout.RIGHT_OF, MONTH_TEXT_ID);

        for (int i = 0; i < columeNum; i++) {
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
            if (weekLabelList.get(i).getDate() == null) {
                dateText.setVisibility(GONE);
            } else {
                dateText.setVisibility(VISIBLE);
            }

            TextView weekText = new TextView(getContext());
            weekText.setId(WEEK_BAR_DATE_TEXT_ID);
            weekText.setTextColor(defaultTextColor);
            weekText.setTextSize(defaultTextSize + 2);
            weekText.setText(weekLabelList.get(i).getWeek());
            weekText.setGravity(Gravity.CENTER);
            linearLayout.addView(weekText);

            View sign = new View(getContext());
            sign.setBackgroundColor(signColor);
            LinearLayout.LayoutParams signParams = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            signParams.height = 5;
            signParams.width = itemWidth / 2;
            linearLayout.addView(sign, signParams);
            sign.setVisibility(GONE);
            if (layoutMode == MODE_TILE && i == weekOfToday - 1) {
                sign.setVisibility(VISIBLE);
            }

            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if (layoutMode == MODE_BIG && i == weekOfToday - 1) {
                itemParams.width = itemWidth * 2;
            } else if (layoutMode == MODE_BIG && weekOfToday <= 0) {
                itemParams.width = itemWidth * 6 / 7;
            } else {
                itemParams.width = itemWidth;
            }
            itemParams.setMargins(1, 1, 1, 1);
            weekBarLayout.addView(linearLayout, itemParams);
        }
        this.addView(weekBarLayout, weekBarParams);
    }

    private void updateWeekBar() {
        for (int i = 0; i < columeNum; i++) {
            LinearLayout itemLayout = (LinearLayout) weekBarLayout.getChildAt(i);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(1, 1, 1, 1);

            TextView dateText = (TextView) itemLayout.getChildAt(0);
            TextView weekText = (TextView) itemLayout.getChildAt(1);
            View sign = itemLayout.getChildAt(2);

            if (layoutMode == MODE_TILE && i == weekOfToday - 1) {
                sign.setVisibility(VISIBLE);
            } else {
                sign.setVisibility(GONE);
            }

            if (layoutMode == MODE_BIG && i == weekOfToday - 1) {
                layoutParams.width = itemWidth * 2;
            } else if (layoutMode == MODE_BIG && weekOfToday <= 0) {
                layoutParams.width = itemWidth * 6 / 7;
            } else {
                layoutParams.width = itemWidth;
            }
            weekBarLayout.updateViewLayout(itemLayout, layoutParams);
        }
    }

    private void initTimeBar() {
        if (timeBarLayout != null) {
            this.removeView(timeBarLayout);
        }
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
            if (timeLabelList.get(i).getTime() == null) {
                timeText.setVisibility(GONE);
            } else {
                timeText.setVisibility(VISIBLE);
            }

            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            itemParams.height = itemHight;
            itemParams.setMargins(0, 1, 0, 1);
            timeBarLayout.addView(linearLayout, itemParams);
        }
        this.addView(timeBarLayout, timeBarParams);
    }

    private void initScheduleView() {
        scheduleVerticalScrollView = new NestedScrollView(getContext());
        scheduleHorizontalScrollView = new CustomHorizontalScrollView(getContext());
        scheduleVerticalScrollView.setHorizontalScrollBarEnabled(false);
        scheduleHorizontalScrollView.setHorizontalScrollBarEnabled(false);

        scheduleLayout = new GridLayout(getContext());
        scheduleLayout.setId(SCHEDULE_LAYOUT_ID);
        scheduleLayout.setRowCount(rowNum);
        scheduleLayout.setColumnCount(columeNum);
        scheduleLayout.setOrientation(VERTICAL);
        scheduleHorizontalScrollView.addView(scheduleLayout);
        scheduleVerticalScrollView.addView(scheduleHorizontalScrollView);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.RIGHT_OF, MONTH_TEXT_ID);
        params.addRule(RelativeLayout.BELOW, MONTH_TEXT_ID);
        this.addView(scheduleVerticalScrollView, params);

        scheduleVerticalScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                timeBarLayout.scrollTo(0, scheduleVerticalScrollView.getScrollY());
            }
        });
        scheduleHorizontalScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                weekBarLayout.scrollTo(scheduleHorizontalScrollView.getScrollX(), 0);
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
        if (layoutMode == MODE_BIG && item.getColumnSpec().getStart() == weekOfToday - 1) {
            params.width = itemWidth * 2;
        } else if (layoutMode == MODE_BIG && weekOfToday <= 0) {
            params.width = itemWidth * 6 / 7;
        } else {
            params.width = itemWidth;
        }
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

    /**
     * 添加一个空的填满gridlayout的View，避免item元素不够无法滑动到指定行的情况
     */

    private void addBaseBlock() {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.columnSpec = GridLayout.spec(0, columeNum, 1f);
        params.rowSpec = GridLayout.spec(0, rowNum);
        params.setGravity(Gravity.FILL);
        params.setMargins(1, 1, 1, 1);
        params.setGravity(1);
        if (layoutMode == MODE_BIG) {
            if (weekOfToday > 0) {
                params.width = itemWidth * (columeNum + 1);
            } else {
                params.width = itemWidth * 6 / 7 * columeNum;
            }
        } else {
            params.width = itemWidth * columeNum;
        }
        params.height = itemHight * rowNum;
        scheduleLayout.addView(new View(getContext()), params);
    }

    private void init() {
        initItemSizeInfo();
        initMonthText();
        initWeekBar();
        initTimeBar();
        initScheduleView();
    }

    /**
     * <p>
     * 用于解决嵌套在viewpager中时viewpager无法滑动的bug
     */

    private class CustomHorizontalScrollView extends HorizontalScrollView {
        public CustomHorizontalScrollView(Context context) {
            super(context);
        }

        public CustomHorizontalScrollView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CustomHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            if (canScrollHorizontally(-1) && canScrollHorizontally(1)) {
                getParent().requestDisallowInterceptTouchEvent(true);
            } else {
                getParent().requestDisallowInterceptTouchEvent(false);
            }
            return super.onTouchEvent(ev);
        }
    }
}
