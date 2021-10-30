package com.bogdan801.schedule.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bogdan801.schedule.R;
import com.bogdan801.schedule.weekmanagement.Week;


public class TimeScheduleFragment extends Fragment {
    //patent activity
    FragmentActivity listener;

    //parameter key
    private static final String WEEK_KEY = "week_key";

    //private fields
    private Week week;
    private View fragmentView;

    //empty constructor
    public TimeScheduleFragment() {}

    //method to create new instance of a fragment with parameter
    public static TimeScheduleFragment newInstance(Week param1) {
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
            week = (Week)getArguments().getSerializable(WEEK_KEY);
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
    }
}