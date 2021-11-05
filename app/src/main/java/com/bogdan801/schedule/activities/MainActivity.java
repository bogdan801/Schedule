package com.bogdan801.schedule.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bogdan801.schedule.adapters.MainViewPageAdapter;
import com.bogdan801.schedule.fragments.LessonsScheduleFragment;
import com.bogdan801.schedule.R;
import com.bogdan801.schedule.fragments.OpenFileFragment;
import com.bogdan801.schedule.fragments.TimeScheduleFragment;
import com.bogdan801.schedule.timemanagement.TimeSchedule;
import com.bogdan801.schedule.weekmanagement.WeekSchedule;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class MainActivity extends AppCompatActivity {
    LessonsScheduleFragment lsFrag;
    TimeScheduleFragment tsFrag;
    OpenFileFragment ofFrag = new OpenFileFragment();
    MainViewPageAdapter adapter = new MainViewPageAdapter(this);

    WeekSchedule weekSchedule = new WeekSchedule();
    TimeSchedule timeSchedule = new TimeSchedule();
    XSSFWorkbook workbook;

    ViewPager2 fragmentPager;
    ImageButton lsButton, tsButton;
    FloatingActionButton ofButton;
    int activeFragment = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentPager = (ViewPager2)findViewById(R.id.fragmentPager);
        lsButton = (ImageButton)findViewById(R.id.lsButton);
        tsButton = (ImageButton)findViewById(R.id.tsButton);
        ofButton = (FloatingActionButton)findViewById(R.id.openFileButton);
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

        lsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeFragment!=0){
                    activeFragment = 0;
                    fragmentPager.setCurrentItem(0, true);
                }
            }
        });

        tsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeFragment!=1){
                    activeFragment = 1;
                    fragmentPager.setCurrentItem(1, true);
                }
            }
        });

        ofButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ofFrag.show(getSupportFragmentManager(), ofFrag.getTag());
            }
        });

        //ofFrag.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        lsFrag.setUpSchedules(weekSchedule, timeSchedule);
        tsFrag.setUpSchedules(weekSchedule, timeSchedule);
    }

    public  void replaceFragment(Fragment newFrag, int containerId){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(containerId, newFrag);
        ft.commit();
    }

    public void Serialize(Object o, String fileName){
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(o);
            os.close();
            fos.close();
        }catch (IOException e){
            Log.d("puk", e.getLocalizedMessage());
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
            Log.d("puk", e.getLocalizedMessage());
            throw e;
        }catch (ClassNotFoundException ignored){}
        return o;
    }
}