package com.bogdan801.schedule.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bogdan801.schedule.R;
import com.bogdan801.schedule.timemanagement.Time;
import com.bogdan801.schedule.timemanagement.TimeSchedule;
import com.bogdan801.schedule.weekmanagement.WeekSchedule;

import org.apache.poi.ss.formula.functions.T;

import java.time.LocalDate;
import java.util.Timer;
import java.util.TimerTask;

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
    Switch isNumeratorSwitch;

    //private fields
    private WeekSchedule weekSchedule;
    private TimeSchedule timeSchedule;
    private View fragmentView;
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
        fragmentView = view;
        //string array of names of days of week
        daysOfWeekNames = view.getResources().getStringArray(R.array.days_of_week);

        //LAYOUT ELEMENTS

        //general panel
        basePanel = (ConstraintLayout)fragmentView.findViewById(R.id.basePanel);

        //schedule panel

        //top labels
        dayOfWeekLabel = (TextView)fragmentView.findViewById(R.id.dayOfWeekLabel);
        chosenDayDateLabel = (TextView)fragmentView.findViewById(R.id.dateLabel);


        //schedule cells
        //backgrounds
        cellBackgrounds[0] = (ConstraintLayout) fragmentView.findViewById(R.id.cell1);
        cellBackgrounds[1] = (ConstraintLayout) fragmentView.findViewById(R.id.cell2);
        cellBackgrounds[2] = (ConstraintLayout) fragmentView.findViewById(R.id.cell3);
        cellBackgrounds[3] = (ConstraintLayout) fragmentView.findViewById(R.id.cell4);
        cellBackgrounds[4] = (ConstraintLayout) fragmentView.findViewById(R.id.cell5);
        cellBackgrounds[5] = (ConstraintLayout) fragmentView.findViewById(R.id.cell6);
        cellBackgrounds[6] = (ConstraintLayout) fragmentView.findViewById(R.id.cell7);
        //text
        cells[0] = (TextView)fragmentView.findViewById(R.id.cell1Text);
        cells[1] = (TextView)fragmentView.findViewById(R.id.cell2Text);
        cells[2] = (TextView)fragmentView.findViewById(R.id.cell3Text);
        cells[3] = (TextView)fragmentView.findViewById(R.id.cell4Text);
        cells[4] = (TextView)fragmentView.findViewById(R.id.cell5Text);
        cells[5] = (TextView)fragmentView.findViewById(R.id.cell6Text);
        cells[6] = (TextView)fragmentView.findViewById(R.id.cell7Text);

        //switch
        isNumeratorSwitch = (Switch)fragmentView.findViewById(R.id.isNumeratorSwitch);
        isNumeratorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isNumeratorSwitch.setText((isChecked)?"Чисельник  ":"Знаменник ");
                showDay();
            }
        });

        //day of week selection panel
        dowPanel = (ConstraintLayout)fragmentView.findViewById(R.id.dayOfWeekPanel);
        dayIndicator = (FrameLayout)fragmentView.findViewById(R.id.dayIndicator);

        daysOfWeekText[0] = (TextView)fragmentView.findViewById(R.id.monT);
        daysOfWeekText[1] = (TextView)fragmentView.findViewById(R.id.tueT);
        daysOfWeekText[2] = (TextView)fragmentView.findViewById(R.id.wenT);
        daysOfWeekText[3] = (TextView)fragmentView.findViewById(R.id.thuT);
        daysOfWeekText[4] = (TextView)fragmentView.findViewById(R.id.friT);

        //day of week navigation bar onClickListener
        View.OnClickListener dayClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            set.setHorizontalBias(dayIndicator.getId(), (float)anim.getAnimatedValue());
                            set.applyTo(dowPanel);
                        }
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
            }
        };

        for (int i = 0; i < daysOfWeekText.length; i++) {
            daysOfWeekText[i].setOnClickListener(dayClickListener);
        }


        //determining should panels be shown
        setVisibility();
        setDayOfWeek(LocalDate.now().getDayOfWeek().getValue());

    }

    public void setDayOfWeek(int day){
        if(day>=1 && day <= 5) daysOfWeekText[day-1].performClick();
        else daysOfWeekText[0].performClick();
    }

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

    //method that loads the information from schedules
    public void showDay(){
        LocalDate currentDate = LocalDate.now();
        int currentDayOfWeek = currentDate.getDayOfWeek().getValue();
        Time currentTime = Time.getCurrent();

        //selected date
        LocalDate selectedDate = LocalDate.ofEpochDay(currentDate.toEpochDay() + (dayOfWeek - currentDayOfWeek));
        String currentWeekDate = String.format("%02d.%02d.%02d", (int)selectedDate.getDayOfMonth(), (int)selectedDate.getMonth().getValue(), (int)selectedDate.getYear());
        chosenDayDateLabel.setText(currentWeekDate);

        //highlighting current lesson
        if(dayOfWeek == currentDayOfWeek){
            int currentLesson = 1;
            for (int i = 1; i <= 7; i++) {
                if(currentTime.isBetween(timeSchedule.getPreviousEnd(i), timeSchedule.getEnd(i))){
                    currentLesson = i;
                    break;
                }
            }

            for (int i = 1; i <= 7; i++) {
                if(i<currentLesson) cellBackgrounds[i-1].setBackgroundColor(getResources().getColor(R.color.green_40));
                else if(i == currentLesson) cellBackgrounds[i-1].setBackgroundColor(getResources().getColor(R.color.green_60));
                else cellBackgrounds[i-1].setBackgroundColor(getResources().getColor(R.color.green_20));
            }

        }
        else {
            for (int i = 1; i <= 7; i++) {
                cellBackgrounds[i-1].setBackgroundColor(getResources().getColor(R.color.green_20));
            }
        }

        dayOfWeekLabel.setText(daysOfWeekNames[dayOfWeek-1]);
        String[] lessons = weekSchedule.GetSchedule(dayOfWeek, isNumeratorSwitch.isChecked());
        for (int i = 0; i < cells.length; i++) {
            cells[i].setText(lessons[i]);
        }
    }
}