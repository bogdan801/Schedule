package com.bogdan801.schedule.fragments;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bogdan801.schedule.R;
import com.bogdan801.schedule.timemanagement.Time;
import com.bogdan801.schedule.timemanagement.TimeSchedule;
import com.bogdan801.schedule.weekmanagement.WeekSchedule;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;


public class TimeScheduleFragment extends Fragment {
    //patent activity
    FragmentActivity listener;

    //parameter key
    private static final String WEEK_KEY = "week_key";

    //private fields
    private WeekSchedule weekSchedule;
    private TimeSchedule timeSchedule;
    private View fragmentView;
    private TextView text;

    //empty constructor
    public TimeScheduleFragment() {}

    //method to create new instance of a fragment with parameter
    public static TimeScheduleFragment newInstance(WeekSchedule param1) {
        TimeScheduleFragment fragment = new TimeScheduleFragment();
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
            weekSchedule = (WeekSchedule)getArguments().getSerializable(WEEK_KEY);
        }
    }

    //inflating fragment view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_schedule, container, false);
    }




    //creating lookups and attaching view listeners
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        fragmentView = view;
        text = (TextView)view.findViewById(R.id.textView5);

        text.setOnClickListener(timeTextViewOnClickListener);
    }

    View.OnClickListener timeTextViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView view = (TextView)v;
            MaterialTimePicker mtp = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText("Оберіть час")
                    .build();

            mtp.addOnPositiveButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Time time = new Time(mtp.getHour(), mtp.getMinute());
                    view.setText(time.toString());
                }
            });

            mtp.show(getParentFragmentManager(), getTag());
        }
    };

}