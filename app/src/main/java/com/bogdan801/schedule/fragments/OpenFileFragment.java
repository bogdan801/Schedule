package com.bogdan801.schedule.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bogdan801.schedule.R;
import com.bogdan801.schedule.activities.MainActivity;
import com.bogdan801.schedule.weekmanagement.WeekSchedule;
import com.bogdan801.schedule.weekmanagement.WeekSelector;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class OpenFileFragment extends BottomSheetDialogFragment {
    private XSSFWorkbook workbook;
    private WeekSchedule weekSchedule;
    private WeekSelector weekSelector;
    private int selectedColumn = -1;

    //layout elements
    private TextView fileNameTextView;
    private Button openButton;
    private Spinner majorSpinner;
    private Spinner yearSpinner;
    private Spinner groupSpinner;
    private Button chooseScheduleButton;

    ActivityResultLauncher<Intent> openFileActivityLauncher;
    Uri filePath;

    private int previouslySelectedMajor = 0;
    private int previouslySelectedYear = 0;
    private int previouslySelectedGroup = 0;

    public OpenFileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ContextThemeWrapper ctw = new  ContextThemeWrapper(getActivity().getApplicationContext(), R.style.Theme_Schedule);
        return inflater.cloneInContext(ctw).inflate(R.layout.fragment_open_file, container, false);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        weekSchedule = null;
    }

    //creating lookups and attaching view listeners
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //layout elements
        fileNameTextView = view.findViewById(R.id.fileNameTextView);
        openButton = view.findViewById(R.id.openButton);
        majorSpinner = view.findViewById(R.id.majorSpinner);
        yearSpinner = view.findViewById(R.id.yearSpinner);
        groupSpinner = view.findViewById(R.id.groupSpinner);
        chooseScheduleButton = view.findViewById(R.id.chooseScheduleButton);

        openFileActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
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
        );

        openButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            myIntent.addCategory(Intent.CATEGORY_OPENABLE);
            myIntent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            openFileActivityLauncher.launch(myIntent);
        });

        majorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weekSelector.SelectMajor(position);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, weekSelector.GetAllYearsAsList());
                yearSpinner.setAdapter(adapter);

                yearSpinner.setSelection(previouslySelectedYear, false);
                previouslySelectedYear = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weekSelector.SelectYear(position);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, weekSelector.GetAllGroupsAsList());
                groupSpinner.setAdapter(adapter);

                groupSpinner.setSelection(previouslySelectedGroup, false);
                previouslySelectedGroup = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedColumn = weekSelector.SelectGroupAsColumnNum(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        chooseScheduleButton.setOnClickListener(v -> {
            if(selectedColumn != -1)weekSchedule = new WeekSchedule(workbook, selectedColumn);
            if (weekSchedule!=null){
                ((MainActivity)getActivity()).updateWeekSchedule(weekSchedule);
                ((MainActivity)getActivity()).Serialize(weekSchedule, "week.bin");
                dismiss();
            }
            else Toast.makeText(getActivity(), "Розклад не був вибраний!", Toast.LENGTH_SHORT).show();
        });

        if(weekSelector!=null){
            previouslySelectedMajor = weekSelector.GetSelectedMajorIndex();
            previouslySelectedYear = weekSelector.GetSelectedYearIndex();
            previouslySelectedGroup = weekSelector.GetSelectedGroupIndex();

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, weekSelector.GetAllMajorsAsList());
            majorSpinner.setAdapter(adapter);

            majorSpinner.setSelection(previouslySelectedMajor, false);
            previouslySelectedMajor = 0;

            fileNameTextView.setText(getFileName(filePath));
        }
    }

    private void readExel(Uri path){
        try {
            FileInputStream fis = (FileInputStream) getActivity().getContentResolver().openInputStream(path);
            workbook = new XSSFWorkbook(fis);
            weekSelector = new WeekSelector(workbook, 0);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, weekSelector.GetAllMajorsAsList());
            majorSpinner.setAdapter(adapter);
            majorSpinner.setSelection(weekSelector.GetSelectedMajorIndex());

            fileNameTextView.setText(getFileName(path));
        } catch (IOException e) {
            Log.d("bob", "не прочитано бо: " + e.getLocalizedMessage());
            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
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