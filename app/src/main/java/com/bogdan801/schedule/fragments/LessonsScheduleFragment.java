package com.bogdan801.schedule.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bogdan801.schedule.R;
import com.bogdan801.schedule.activities.MainActivity;
import com.bogdan801.schedule.timemanagement.Time;
import com.bogdan801.schedule.timemanagement.TimeSchedule;
import com.bogdan801.schedule.weekmanagement.WeekSchedule;

import java.io.IOException;
import java.time.LocalDate;

public class LessonsScheduleFragment extends Fragment {
    //layout elements
    ConstraintLayout basePanel;
    TextView dayOfWeekLabel;
    TextView chosenDayDateLabel;
    ConstraintLayout[] cellBackgrounds = new ConstraintLayout[7];
    TextView[] cells = new TextView[7];
    ConstraintLayout dowPanel;
    TextView[] daysOfWeekText = new TextView[5];
    FrameLayout dayIndicator;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch isNumeratorSwitch;

    //private fields
    private WeekSchedule weekSchedule;
    private TimeSchedule timeSchedule;
    private int dayOfWeek = 1;
    private String[] daysOfWeekNames;

    //empty constructor
    public LessonsScheduleFragment() {}

    public void setUpSchedules(WeekSchedule week, TimeSchedule time){
        weekSchedule = week;
        timeSchedule = time;
    }

    //inflating fragment view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lessons_schedule, container, false);
    }

    //creating lookups and attaching view listeners
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //string array of names of days of week
        daysOfWeekNames = view.getResources().getStringArray(R.array.days_of_week);

        //LAYOUT ELEMENTS

        //general panel
        basePanel = view.findViewById(R.id.basePanel);

        //schedule panel
        //top labels
        dayOfWeekLabel = view.findViewById(R.id.dayOfWeekLabel);
        chosenDayDateLabel = view.findViewById(R.id.dateLabel);

        //schedule cells
        //backgrounds
        cellBackgrounds[0] = view.findViewById(R.id.cell1);
        cellBackgrounds[1] = view.findViewById(R.id.cell2);
        cellBackgrounds[2] = view.findViewById(R.id.cell3);
        cellBackgrounds[3] = view.findViewById(R.id.cell4);
        cellBackgrounds[4] = view.findViewById(R.id.cell5);
        cellBackgrounds[5] = view.findViewById(R.id.cell6);
        cellBackgrounds[6] = view.findViewById(R.id.cell7);
        //text
        cells[0] = view.findViewById(R.id.cell1Text);
        cells[1] = view.findViewById(R.id.cell2Text);
        cells[2] = view.findViewById(R.id.cell3Text);
        cells[3] = view.findViewById(R.id.cell4Text);
        cells[4] = view.findViewById(R.id.cell5Text);
        cells[5] = view.findViewById(R.id.cell6Text);
        cells[6] = view.findViewById(R.id.cell7Text);

        //switch
        isNumeratorSwitch = view.findViewById(R.id.isNumeratorSwitch);
        isNumeratorSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isNumeratorSwitch.setText((isChecked)?getResources().getString(R.string.num):getResources().getString(R.string.denom));
            ((MainActivity)getActivity()).Serialize(isChecked, "switch.bin");
            showDay();
        });

        //day of week selection panel
        dowPanel = view.findViewById(R.id.dayOfWeekPanel);
        dayIndicator = view.findViewById(R.id.dayIndicator);

        daysOfWeekText[0] = view.findViewById(R.id.monT);
        daysOfWeekText[1] = view.findViewById(R.id.tueT);
        daysOfWeekText[2] = view.findViewById(R.id.wenT);
        daysOfWeekText[3] = view.findViewById(R.id.thuT);
        daysOfWeekText[4] = view.findViewById(R.id.friT);

        //day of week navigation bar onClickListener
        @SuppressLint("NonConstantResourceId")
        View.OnClickListener dayClickListener = v -> {
            //index of TextView of previously selected day
            int startTextInd = dayOfWeek-1;

            //starting bias of indicator
            float startBias = 0;
            switch (dayOfWeek){
                case 1:
                    startBias = 0;
                    break;
                case 2:
                    startBias = 0.25f;
                    break;
                case 3:
                    startBias = 0.5f;
                    break;
                case 4:
                    startBias = 0.75f;
                    break;
                case 5:
                    startBias = 1;
                    break;
            }

            //ending bias of indicator, updating dayOfWeek
            float endBias = 0;
            switch (v.getId()){
                case R.id.monT:
                    dayOfWeek = 1;
                    endBias = 0;
                    break;
                case R.id.tueT:
                    dayOfWeek = 2;
                    endBias = 0.25f;
                    break;
                case R.id.wenT:
                    dayOfWeek = 3;
                    endBias = 0.5f;
                    break;
                case R.id.thuT:
                    dayOfWeek = 4;
                    endBias = 0.75f;
                    break;
                case R.id.friT:
                    dayOfWeek = 5;
                    endBias = 1;
                    break;
            }

            //new index of TextView of the selected day
            int endTextInd = dayOfWeek-1;

            //if selected day is other then current, animate transition
            if (startTextInd != endTextInd){
                ConstraintSet set = new ConstraintSet();
                set.clone(dowPanel);

                //animating value of horizontal bias of the indicator
                final ValueAnimator anim = ValueAnimator.ofFloat(startBias, endBias);
                anim.setDuration(150);

                //onAnimationUpdate
                anim.addUpdateListener(animation -> {
                    set.setHorizontalBias(dayIndicator.getId(), (float)anim.getAnimatedValue());
                    set.applyTo(dowPanel);
                });

                //onAnimationEnd
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        daysOfWeekText[endTextInd].setTextColor(ContextCompat.getColor(getActivity(), R.color.green_100));
                    }
                });

                anim.start();

                daysOfWeekText[startTextInd].setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

                showDay();
            }
        };

        for (TextView textView : daysOfWeekText) {
            textView.setOnClickListener(dayClickListener);
        }


        //determining should panels be shown
        setVisibility();
        //default day of week
        setDayOfWeek(LocalDate.now().getDayOfWeek().getValue());
        //deserializing switch
        try {
            setSwitch((Boolean)((MainActivity)getActivity()).Deserialize("switch.bin"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        showDay();

        //thread to update the schedule each 10 seconds
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!this.isInterrupted()) {
                        Thread.sleep(10000);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDay();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        thread.start();
    }

    //method to select day of week
    public void setDayOfWeek(int day){
        if(day>=1 && day <= 5) daysOfWeekText[day-1].performClick();
        else daysOfWeekText[0].performClick();
    }

    //setting up visibility of panel (hiding it when there's no file open)
    public void setVisibility(){
        if (weekSchedule!=null){
            if (weekSchedule.isEmpty()){
                basePanel.setVisibility(View.GONE);
            }
            else {
                basePanel.setVisibility(View.VISIBLE);
            }
        }
        else basePanel.setVisibility(View.GONE);
    }

    //public method to set a switch
    public void setSwitch(boolean value){
        isNumeratorSwitch.setChecked(value);
    }

    //method that loads the information from schedules
    public void showDay(){
        LocalDate currentDate = LocalDate.now();
        int currentDayOfWeek = currentDate.getDayOfWeek().getValue();
        Time currentTime = Time.getCurrent();

        //selected date
        int offsetDays = ((currentDayOfWeek<=5)?(dayOfWeek - currentDayOfWeek):(7-currentDayOfWeek+dayOfWeek));
        LocalDate selectedDate = LocalDate.ofEpochDay(currentDate.toEpochDay() + offsetDays);
        @SuppressLint("DefaultLocale") String currentWeekDate = String.format("%02d.%02d.%02d", selectedDate.getDayOfMonth(), selectedDate.getMonth().getValue(), selectedDate.getYear());
        chosenDayDateLabel.setText(currentWeekDate);

        //highlighting current lesson
        if(dayOfWeek == currentDayOfWeek){
            int currentLesson = 1;
            for (int i = 1; i <= 7; i++) {
                if(currentTime.isBetween(timeSchedule.getPreviousEnd(i), timeSchedule.getEnd(i), true)){
                    currentLesson = i;
                    break;
                }
                currentLesson = 8;
            }

            for (int i = 1; i <= 7; i++) {
                if(i<currentLesson) cellBackgrounds[i-1].setBackgroundColor(getResources().getColor(R.color.green_40));
                else if(i == currentLesson) {
                    if (currentTime.isGreaterOrEquals(timeSchedule.getStart(currentLesson))){
                        cellBackgrounds[i-1].setBackgroundColor(getResources().getColor(R.color.green_60));
                    }
                    else cellBackgrounds[i-1].setBackgroundColor(getResources().getColor(R.color.green_20));
                }
                else cellBackgrounds[i-1].setBackgroundColor(getResources().getColor(R.color.green_20));
            }

        }
        else {
            for (int i = 1; i <= 7; i++) {
                cellBackgrounds[i-1].setBackgroundColor(getResources().getColor(R.color.green_20));
            }
        }

        dayOfWeekLabel.setText(daysOfWeekNames[dayOfWeek-1]);
        if(weekSchedule!=null){
            String[] lessons = weekSchedule.GetSchedule(dayOfWeek, isNumeratorSwitch.isChecked());
            for (int i = 0; i < cells.length; i++) {
                cells[i].setText(lessons[i]);
            }
        }

    }
}