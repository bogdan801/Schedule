package com.bogdan801.schedule.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bogdan801.schedule.R;
import com.bogdan801.schedule.weekmanagement.WeekSchedule;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.apache.poi.ss.usermodel.Workbook;

public class OpenFileFragment extends BottomSheetDialogFragment {
    //patent activity
    private FragmentActivity listener;
    private View fragmentView;

    private Workbook workbook;
    private WeekSchedule weekSchedule;


    public OpenFileFragment() {}

    //getting parent activity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (FragmentActivity) context;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(STYLE_NORMAL, R.style.Theme_Schedule);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ContextThemeWrapper ctw = new  ContextThemeWrapper(listener.getApplicationContext(), R.style.Theme_Schedule);
        //var contextThemeWrapper = new ContextThemeWrapper(activity, R.style.AppTheme);
        return inflater.cloneInContext(ctw).inflate(R.layout.fragment_open_file, container, false);
    }

    //creating lookups and attaching view listeners
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        fragmentView = view;

    }
}