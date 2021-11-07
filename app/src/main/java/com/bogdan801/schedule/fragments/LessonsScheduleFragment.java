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
import com.bogdan801.schedule.timemanagement.TimeSchedule;
import com.bogdan801.schedule.weekmanagement.WeekSchedule;

public class LessonsScheduleFragment extends Fragment {
    //layout elements
    ConstraintLayout basePanel;
    TextView dayOfWeekLabel;
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

        basePanel = (ConstraintLayout)fragmentView.findViewById(R.id.basePanel);
        daysOfWeekNames = view.getResources().getStringArray(R.array.days_of_week);
        dayOfWeekLabel = (TextView)fragmentView.findViewById(R.id.dayOfWeekLabel);

        dowPanel = (ConstraintLayout)fragmentView.findViewById(R.id.dayOfWeekPanel);
        dayIndicator = (FrameLayout)fragmentView.findViewById(R.id.dayIndicator);

        daysOfWeekText[0] = (TextView)fragmentView.findViewById(R.id.monT);
        daysOfWeekText[1] = (TextView)fragmentView.findViewById(R.id.tueT);
        daysOfWeekText[2] = (TextView)fragmentView.findViewById(R.id.wenT);
        daysOfWeekText[3] = (TextView)fragmentView.findViewById(R.id.thuT);
        daysOfWeekText[4] = (TextView)fragmentView.findViewById(R.id.friT);

        cells[0] = (TextView)fragmentView.findViewById(R.id.cell1Text);
        cells[1] = (TextView)fragmentView.findViewById(R.id.cell2Text);
        cells[2] = (TextView)fragmentView.findViewById(R.id.cell3Text);
        cells[3] = (TextView)fragmentView.findViewById(R.id.cell4Text);
        cells[4] = (TextView)fragmentView.findViewById(R.id.cell5Text);
        cells[5] = (TextView)fragmentView.findViewById(R.id.cell6Text);
        cells[6] = (TextView)fragmentView.findViewById(R.id.cell7Text);

        isNumeratorSwitch = (Switch)fragmentView.findViewById(R.id.isNumeratorSwitch);
        isNumeratorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isNumeratorSwitch.setText((isChecked)?"Чисельник  ":"Знаменник ");
                showDay();
            }
        });


        setVisibility();

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
    }

    public void setDayOfWeek(int day){
        daysOfWeekText[day-1].performClick();
    }

    public void setVisibility(){
        if (weekSchedule.isEmpty()){
            basePanel.setVisibility(View.GONE);
        }
        else {
            basePanel.setVisibility(View.VISIBLE);
        }
    }

    public void showDay(){
        dayOfWeekLabel.setText(daysOfWeekNames[dayOfWeek-1]);
        String[] lessons = weekSchedule.GetSchedule(dayOfWeek, isNumeratorSwitch.isChecked());
        for (int i = 0; i < cells.length; i++) {
            cells[i].setText(lessons[i]);
        }
    }
}