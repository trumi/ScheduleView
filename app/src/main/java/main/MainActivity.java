package main;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hnust.lzm.scheduleview.R;
import com.hnust.trumi.scheduleview.ScheduleItem;
import com.hnust.trumi.scheduleview.ScheduleView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ScheduleView scheduleView;
    private List<ScheduleItem> list = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scheduleView = (ScheduleView) findViewById(R.id.scheduleview);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.sw);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                for (int i = 0; i < 1; i++) {
                    ScheduleItem item = new ScheduleItem(MainActivity.this);
                    String s="毛泽东思想和中国特色社会主义理论体系概论@南校区第五教学楼4654室";
                    item.setText(s);
                    //item.setBackgroundColor(Color.parseColor("#ff00ff"));
                    item.setColumnSpec(i % 7 );
                    item.setRowSpec(2 * (i / 7) );
                    list.add(item);
                }
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
    }
}