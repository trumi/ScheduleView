package main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hnust.lzm.scheduleview.R;
import com.hnust.lzm.scheduleview.ScheduleItem;
import com.hnust.lzm.scheduleview.ScheduleView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ScheduleView scheduleView;
    private List<ScheduleItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scheduleView = (ScheduleView) findViewById(R.id.scheduleview);
        for (int i = 0; i < 30; i++) {
            ScheduleItem item = new ScheduleItem(this);
            item.setText("asdaff@qwqee");
            item.setBackgroundColor(0xffff00);
            item.setColumnSpec(i % 7 );
            item.setRowSpec(2 * (i / 7) );
            list.add(item);
        }
        scheduleView.setSourceList(list);
        scheduleView.setOnScheduleItemClickListener(new ScheduleView.OnScheduleItemClickListener() {
            @Override
            public void onClick(ScheduleItem item) {

            }

            @Override
            public void onLongClick(ScheduleItem item) {

            }
        });
        scheduleView.notifyData();
    }
}
