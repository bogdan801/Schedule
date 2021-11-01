package com.bogdan801.schedule.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bogdan801.schedule.R;
import com.bogdan801.schedule.weekmanagement.Week;

public class LessonsScheduleFragment extends Fragment {
    //patent activity
    FragmentActivity listener;

    //parameter key
    private static final String WEEK_KEY = "week_key";



    //layout elements
    ConstraintLayout dowPanel;
    TextView[] daysOfWeekText = new TextView[5];
    FrameLayout dayIndicator;


    //private fields
    private Week week;
    private View fragmentView;
    private int dayOfWeek = 1;


    //empty constructor
    public LessonsScheduleFragment() {}

    //method to create new instance of a fragment with parameter
    public static LessonsScheduleFragment newInstance(Week param1) {
        LessonsScheduleFragment fragment = new LessonsScheduleFragment();
        Bundle args = new Bundle();
        args.putSerializable(WEEK_KEY, param1);
        fragment.setArguments(args);
        return fragment;
    }


    //getting parent activity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (FragmentActivity) context;
        }
    }

    //event occurs when fragment is being created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            week = (Week)getArguments().getSerializable(WEEK_KEY);
        }
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
        dowPanel = (ConstraintLayout)fragmentView.findViewById(R.id.dayOfWeekPanel);
        daysOfWeekText[0] = (TextView)fragmentView.findViewById(R.id.monT);
        daysOfWeekText[1] = (TextView)fragmentView.findViewById(R.id.tueT);
        daysOfWeekText[2] = (TextView)fragmentView.findViewById(R.id.wenT);
        daysOfWeekText[3] = (TextView)fragmentView.findViewById(R.id.thuT);
        daysOfWeekText[4] = (TextView)fragmentView.findViewById(R.id.friT);
        dayIndicator = (FrameLayout)fragmentView.findViewById(R.id.dayIndicator);


        View.OnClickListener dayClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int startTextInd = dayOfWeek-1;

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

                int endTextInd = dayOfWeek-1;

                if (startBias != endBias){
                    ConstraintSet set = new ConstraintSet();
                    set.clone(dowPanel);

                    final ValueAnimator anim = ValueAnimator.ofFloat(startBias, endBias);
                    anim.setDuration(200);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            set.setHorizontalBias(dayIndicator.getId(), (float)anim.getAnimatedValue());
                            set.applyTo(dowPanel);
                        }
                    });

                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            daysOfWeekText[endTextInd].setTextColor(ContextCompat.getColor(getActivity(), R.color.green_100));
                        }
                    });

                    anim.start();

                    daysOfWeekText[startTextInd].setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                }
            }
        };

        for (int i = 0; i < daysOfWeekText.length; i++) {
            daysOfWeekText[i].setOnClickListener(dayClickListener);
        }
    }


}