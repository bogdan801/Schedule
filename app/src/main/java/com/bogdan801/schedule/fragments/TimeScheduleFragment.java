package com.bogdan801.schedule.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bogdan801.schedule.R;
import com.bogdan801.schedule.activities.MainActivity;
import com.bogdan801.schedule.timemanagement.Time;
import com.bogdan801.schedule.timemanagement.TimeSchedule;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

public class TimeScheduleFragment extends Fragment {
    //private fields
    private TimeSchedule timeSchedule;
    private final TextView[][] timeCells = new TextView[2][7];

    //empty constructor
    public TimeScheduleFragment() {}

    //setting up schedule
    public void setUpTimeSchedule(TimeSchedule time){
        timeSchedule = time;
    }

    //inflating fragment view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_schedule, container, false);
    }

    //creating lookups and attaching view listeners
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        timeCells[0][0] = view.findViewById(R.id.startTimeCell1);
        timeCells[0][1] = view.findViewById(R.id.startTimeCell2);
        timeCells[0][2] = view.findViewById(R.id.startTimeCell3);
        timeCells[0][3] = view.findViewById(R.id.startTimeCell4);
        timeCells[0][4] = view.findViewById(R.id.startTimeCell5);
        timeCells[0][5] = view.findViewById(R.id.startTimeCell6);
        timeCells[0][6] = view.findViewById(R.id.startTimeCell7);

        timeCells[1][0] = view.findViewById(R.id.endTimeCell1);
        timeCells[1][1] = view.findViewById(R.id.endTimeCell2);
        timeCells[1][2] = view.findViewById(R.id.endTimeCell3);
        timeCells[1][3] = view.findViewById(R.id.endTimeCell4);
        timeCells[1][4] = view.findViewById(R.id.endTimeCell5);
        timeCells[1][5] = view.findViewById(R.id.endTimeCell6);
        timeCells[1][6] = view.findViewById(R.id.endTimeCell7);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 7; j++) {
                timeCells[i][j].setOnClickListener(timeTextViewOnClickListener);
            }
        }

        updateTimeCellsFromSchedule(timeSchedule);
    }

    //filling up all of the time cells from time schedule
    public void updateTimeCellsFromSchedule(TimeSchedule schedule){
        for (int i = 0; i < 7; i++) {
            timeCells[0][i].setText(schedule.getStart(i+1).toString());
        }
        for (int i = 0; i < 7; i++) {
            timeCells[1][i].setText(schedule.getEnd(i+1).toString());
        }
        ((MainActivity)getActivity()).Serialize(schedule, "time.bin");
        ((MainActivity)getActivity()).updateTimeSchedule(schedule);
    }

    //time cell onClickListener
    View.OnClickListener timeTextViewOnClickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            TextView view = (TextView)v;
            Time pickerDefaultTime = new Time(view.getText().toString());

            //creating timePicker object
            MaterialTimePicker mtp = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(pickerDefaultTime.getHours())
                    .setMinute(pickerDefaultTime.getMinutes())
                    .setTitleText(getResources().getString(R.string.tsTime))
                    .build();

            //adding event which occurs when user have picked the time
            mtp.addOnPositiveButtonClickListener(v1 -> {
                //the time picked by user
                Time time = new Time(mtp.getHour(), mtp.getMinute());

                //creating and setting up the index of the current time cell (it's position on the grid)
                int[] index;
                switch (view.getId()){
                    case R.id.startTimeCell1:
                        index = new int[]{0, 1};
                        break;
                    case R.id.startTimeCell2:
                        index = new int[]{0, 2};
                        break;
                    case R.id.startTimeCell3:
                        index = new int[]{0, 3};
                        break;
                    case R.id.startTimeCell4:
                        index = new int[]{0, 4};
                        break;
                    case R.id.startTimeCell5:
                        index = new int[]{0, 5};
                        break;
                    case R.id.startTimeCell6:
                        index = new int[]{0, 6};
                        break;
                    case R.id.startTimeCell7:
                        index = new int[]{0, 7};
                        break;

                    case R.id.endTimeCell1:
                        index = new int[]{1, 1};
                        break;
                    case R.id.endTimeCell2:
                        index = new int[]{1, 2};
                        break;
                    case R.id.endTimeCell3:
                        index = new int[]{1, 3};
                        break;
                    case R.id.endTimeCell4:
                        index = new int[]{1, 4};
                        break;
                    case R.id.endTimeCell5:
                        index = new int[]{1, 5};
                        break;
                    case R.id.endTimeCell6:
                        index = new int[]{1, 6};
                        break;
                    case R.id.endTimeCell7:
                        index = new int[]{1, 7};
                        break;
                    default:
                        index = new int[]{-1, -1};
                }

                //setting up permissible bounds of selected time
                Time start = new Time(0,0);
                Time end = new Time(0,0);

                if(index[0] == 0 && index[1] == 1){         //for first item in start column
                    start = new Time(0,0);
                    end = timeSchedule.getEnd(1);
                }
                else if (index[0] == 1 && index[1] == 7){   //for last item in end column
                    start = timeSchedule.getStart(7);
                    end = new Time(23,59);
                }
                else if(index[0] == 0){                     //for any other item in start column
                    start = timeSchedule.getEnd(index[1]-1);
                    end = timeSchedule.getEnd(index[1]);
                }
                else if(index[0] == 1){                     //for any other item in end column
                    start = timeSchedule.getStart(index[1]);
                    end = timeSchedule.getStart(index[1]+1);
                }

                //figuring out if selected time is in the permissible bounds;
                //if yes - updating timeSchedule with selected time;
                //if no - showing the corresponding message
                if(time.isBetween(start, end, true) && index[0]==0) timeSchedule.setStart(index[1], time);
                else if(time.isBetween(start, end, true) && index[0]==1) timeSchedule.setEnd(index[1], time);
                else {
                    Toast.makeText(
                            getActivity(),
                            "Не можна встановити такий час в дане поле! Допустимі рамки: " + start + "-" + end,
                            Toast.LENGTH_LONG
                    ).show();
                }

                //updating all time cells from timeSchedule
                updateTimeCellsFromSchedule(timeSchedule);
            });

            //showing timePicker object
            mtp.show(getParentFragmentManager(), getTag());
        }
    };
}