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
import com.bogdan801.schedule.weekmanagement.WeekSchedule;



public class TimeScheduleFragment extends Fragment {
    //patent activity
    FragmentActivity listener;

    //parameter key
    private static final String WEEK_KEY = "week_key";

    //private fields
    private WeekSchedule weekSchedule;
    private View fragmentView;
    TextView textView;

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
        textView = (TextView)fragmentView.findViewById(R.id.textView2);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tp = new TimePickerDialog(
                        getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Time time = new Time(hourOfDay, minute);
                                textView.setText(time.toString());


                            }
                        },
                        12,
                        0,
                        true
                );

                tp.show();


            }
        });
    }
}