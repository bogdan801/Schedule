package com.bogdan801.schedule.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bogdan801.schedule.R;
import com.bogdan801.schedule.activities.MainActivity;
import com.bogdan801.schedule.weekmanagement.WeekSchedule;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class OpenFileFragment extends BottomSheetDialogFragment {
    //patent activity
    private FragmentActivity listener;
    private View fragmentView;

    private XSSFWorkbook workbook;
    private WeekSchedule weekSchedule;

    //layout elements
    private TextView fileNameTextView;
    private Button openButton;
    private Spinner majorSpinner;
    private Spinner yearSpinner;
    private Spinner groupSpinner;
    private Button chooseScheduleButton;


    ActivityResultLauncher<Intent> openFileActivityLauncher;
    Uri filePath;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ContextThemeWrapper ctw = new  ContextThemeWrapper(listener.getApplicationContext(), R.style.Theme_Schedule);
        return inflater.cloneInContext(ctw).inflate(R.layout.fragment_open_file, container, false);
    }

    //creating lookups and attaching view listeners
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        fragmentView = view;

        fileNameTextView = (TextView)view.findViewById(R.id.fileNameTextView);
        openButton = (Button)view.findViewById(R.id.openButton);
        majorSpinner = (Spinner)view.findViewById(R.id.majorSpinner);
        yearSpinner = (Spinner)view.findViewById(R.id.yearSpinner);
        groupSpinner = (Spinner)view.findViewById(R.id.groupSpinner);
        chooseScheduleButton = (Button)view.findViewById(R.id.chooseScheduleButton);

        openFileActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            filePath = data.getData();

                            final int takeFlags = data.getFlags()
                                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            getActivity().getContentResolver().takePersistableUriPermission(filePath, takeFlags);

                            readExel(filePath);
                        }
                    }
                }
        );

        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                myIntent.addCategory(Intent.CATEGORY_OPENABLE);
                myIntent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                openFileActivityLauncher.launch(myIntent);
            }
        });

        chooseScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).updateWeekSchedule(weekSchedule);
                dismiss();
            }
        });


    }


    private void readExel(Uri path){
        try {
            FileInputStream fis = (FileInputStream) getActivity().getContentResolver().openInputStream(path);
            workbook = new XSSFWorkbook(fis);
            weekSchedule = new WeekSchedule(workbook, 18);

            fileNameTextView.setText(weekSchedule.GetLesson(1,3,true));



        } catch (IOException e) {
            Log.d("puk", "не прочитано бо: " + e.getLocalizedMessage());
            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }
}