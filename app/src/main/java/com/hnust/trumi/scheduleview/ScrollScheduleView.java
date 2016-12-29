package com.hnust.trumi.scheduleview;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.Display;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import static android.widget.GridLayout.VERTICAL;

/**
 * Created by lzm on 16-12-28.
 */

public class ScrollScheduleView extends ScheduleView{

    public ScrollScheduleView(Context context) {
        super(context);
    }

    public ScrollScheduleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollScheduleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void initScheduleView() {
        final ScrollView scrollView = new ScrollView(getContext());
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

    @Override
    protected void initItemSizeInfo() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int screenWidth = point.x;
        int screenHight = point.y;
        this.itemWidth = (int) (screenWidth * 1.05) / 8;
        this.itemHight = screenHight / 11;
    }
}
