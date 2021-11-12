package com.bogdan801.schedule.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import com.bogdan801.schedule.adapters.MainViewPageAdapter;
import com.bogdan801.schedule.fragments.LessonsScheduleFragment;
import com.bogdan801.schedule.R;
import com.bogdan801.schedule.fragments.OpenFileFragment;
import com.bogdan801.schedule.fragments.TimeScheduleFragment;
import com.bogdan801.schedule.timemanagement.TimeSchedule;
import com.bogdan801.schedule.weekmanagement.WeekSchedule;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.*;

public class MainActivity extends AppCompatActivity {
    LessonsScheduleFragment lsFrag;
    TimeScheduleFragment tsFrag;
    OpenFileFragment ofFrag = new OpenFileFragment();
    MainViewPageAdapter adapter = new MainViewPageAdapter(this);

    WeekSchedule weekSchedule = new WeekSchedule();
    TimeSchedule timeSchedule = new TimeSchedule();

    ViewPager2 fragmentPager;
    ImageButton lsButton, tsButton;
    FloatingActionButton ofButton;
    int activeFragment = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentPager = findViewById(R.id.fragmentPager);
        lsButton = findViewById(R.id.lsButton);
        tsButton = findViewById(R.id.tsButton);
        ofButton = findViewById(R.id.openFileButton);
        tsButton.getBackground().setAlpha(0);

        fragmentPager.setAdapter(adapter);
        lsFrag = (LessonsScheduleFragment) adapter.getFragment(0);
        tsFrag = (TimeScheduleFragment) adapter.getFragment(1);

        fragmentPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                double realPosition = position + positionOffset;
                activeFragment = (realPosition < 0.5)? 0 : 1;

                int alpha = (int)(realPosition*255);
                lsButton.getBackground().setAlpha(255 - alpha);
                tsButton.getBackground().setAlpha(alpha);
            }
        });

        lsButton.setOnClickListener(v -> {
            if (activeFragment!=0){
                activeFragment = 0;
                fragmentPager.setCurrentItem(0, true);
            }
        });

        tsButton.setOnClickListener(v -> {
            if (activeFragment!=1){
                activeFragment = 1;
                fragmentPager.setCurrentItem(1, true);
            }
        });

        ofButton.setOnClickListener(v -> ofFrag.show(getSupportFragmentManager(), ofFrag.getTag()));


        try {
            weekSchedule = (WeekSchedule) Deserialize("week.bin");
            timeSchedule = (TimeSchedule) Deserialize("time.bin");
        } catch (IOException e) {
            e.printStackTrace();
        }

        lsFrag.setUpSchedules(weekSchedule, timeSchedule);
        tsFrag.setUpTimeSchedule(timeSchedule);
    }

    public void updateWeekSchedule(WeekSchedule weekSchedule){
        this.weekSchedule = weekSchedule;
        lsFrag.setUpSchedules(weekSchedule, timeSchedule);
        lsFrag.setVisibility();
        lsFrag.showDay();
    }

    public void updateTimeSchedule(TimeSchedule timeSchedule){
        lsFrag.setUpSchedules(weekSchedule, timeSchedule);
        lsFrag.setVisibility();
        lsFrag.showDay();
    }

    public void Serialize(Object o, String fileName){
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(o);
            os.close();
            fos.close();
        }catch (IOException e){
            Log.d("bob", e.getLocalizedMessage());
        }
    }

    public Object Deserialize(String fileName) throws IOException{
        Object o = null;
        try {
            FileInputStream fis = getApplicationContext().openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            o = is.readObject();
            is.close();
            fis.close();
        }catch (IOException e){
            Log.d("bob", e.getLocalizedMessage());
            throw e;
        }catch (ClassNotFoundException ignored){}
        return o;
    }
}