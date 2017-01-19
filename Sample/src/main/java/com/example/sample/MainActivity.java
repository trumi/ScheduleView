package com.example.sample;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.hnust.trumi.scheduleview.ScheduleItem;
import com.hnust.trumi.scheduleview.ScheduleView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ScheduleView scheduleView;
    //private List<ScheduleItem> list = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private ViewPager viewPager;
    private List<View> viewList = new ArrayList<>();// 将要分页显示的View装入数组中

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.sw);
        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0; i < 10; i++) {
            scheduleView = (ScheduleView) inflater.inflate(R.layout.temp, null);
            List<ScheduleItem> list = new ArrayList<>();
            for (int h = 0; h < 30; h++) {
                ScheduleItem item = new ScheduleItem(this);
                String s = "毛泽东思想和中国特色社会主义理论体系概论@南校区第五教学楼4654室";
                for (int j = 0; j < h; j++) {
                    s += "s";
                }
                item.setText(s);
                //item.setBackgroundColor(Color.parseColor("#ff00ff"));
                item.setColumnSpec(h % 7);
                item.setRowSpec(2 * (h / 7));
                list.add(item);
            }
            scheduleView.setSourceList(list);
            scheduleView.setWeekOfToday(3);
            //scheduleView.setWeekOfToday(3);
            scheduleView.setOnScheduleItemClickListener(new ScheduleView.OnScheduleItemClickListener() {
                @Override
                public void onClick(ScheduleItem item) {
                    Log.i("asd", "onClick: " + item.getText());
                }

                @Override
                public void onLongClick(ScheduleItem item) {

                }
            });
            scheduleView.notifyData();
            viewList.add(scheduleView);
        }
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        swipeRefreshLayout.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        swipeRefreshLayout.setEnabled(true);
                        break;
                }
                return false;
            }
        });


        PagerAdapter pagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                // TODO Auto-generated method stub
                container.removeView(viewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                container.addView(viewList.get(position));


                return viewList.get(position);
            }
        };
        viewPager.setAdapter(pagerAdapter);
    }
/*        scheduleView = (ScheduleView) findViewById(R.id.scheduleview);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.sw);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                for (int i = 0; i < 35; i++) {
                    ScheduleItem item = new ScheduleItem(MainActivity.this);
                    String s="毛泽东思想和中国特色社会主义理论体系概论@南校区第五教学楼4654室";
                    for (int j=0;j<i;j++){
                        s+="s";
                    }
                    item.setText(s);
                    //item.setBackgroundColor(Color.parseColor("#ff00ff"));
                    item.setColumnSpec(i % 7 );
                    item.setRowSpec(2 * (i / 7) );
                    list.add(item);
                }
                scheduleView.setWeekOfToday(3);
                scheduleView.notifyData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        for (int i = 0; i < 30; i++) {
            ScheduleItem item = new ScheduleItem(this);
            String s="毛泽东思想和中国特色社会主义理论体系概论@南校区第五教学楼4654室";
            for (int j=0;j<i;j++){
                s+="s";
            }
            item.setText(s);
            //item.setBackgroundColor(Color.parseColor("#ff00ff"));
            item.setColumnSpec(i % 7 );
            item.setRowSpec(2 * (i / 7) );
            list.add(item);
        }
        scheduleView.setSourceList(list);
        //scheduleView.setWeekOfToday(3);
        scheduleView.setOnScheduleItemClickListener(new ScheduleView.OnScheduleItemClickListener() {
            @Override
            public void onClick(ScheduleItem item) {
                Log.i("asd", "onClick: "+item.getText());
            }

            @Override
            public void onLongClick(ScheduleItem item) {

            }
        });
        scheduleView.notifyData();
    }*/
}
