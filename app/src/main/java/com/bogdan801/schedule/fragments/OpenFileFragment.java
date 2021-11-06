package com.bogdan801.schedule.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.FragmentActivity;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bogdan801.schedule.R;
import com.bogdan801.schedule.activities.MainActivity;
import com.bogdan801.schedule.weekmanagement.WeekSchedule;
import com.bogdan801.schedule.weekmanagement.WeekSelector;
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
    private WeekSelector weekSelector;

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


        majorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weekSelector.SelectMajor(position);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, weekSelector.GetAllYears());
                yearSpinner.setAdapter(adapter);
                //groupSpinner.setAdapter(null);
                weekSchedule = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weekSelector.SelectYear(position);
                String[] array = weekSelector.GetAllGroups();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, array);
                groupSpinner.setAdapter(adapter);
                weekSchedule = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weekSchedule = weekSelector.SelectGroupAsWeek(groupSpinner.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        chooseScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weekSchedule!=null){
                    ((MainActivity)getActivity()).updateWeekSchedule(weekSchedule);
                    dismiss();
                }
                else Toast.makeText(getActivity(), "Розклад не був вибраний!", Toast.LENGTH_SHORT).show();
            }
        });



    }


    private void readExel(Uri path){
        try {
            FileInputStream fis = (FileInputStream) getActivity().getContentResolver().openInputStream(path);
            workbook = new XSSFWorkbook(fis);
            weekSelector = new WeekSelector(workbook, 0);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, weekSelector.GetAllMajors());
            majorSpinner.setAdapter(adapter);
            //yearSpinner.setAdapter(null);
            //groupSpinner.setAdapter(null);

            fileNameTextView.setText(getFileName(path));
        } catch (IOException e) {
            Log.d("puk", "не прочитано бо: " + e.getLocalizedMessage());
            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}