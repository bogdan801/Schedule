package com.bogdan801.schedule.timemanagement;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.LocalTime;


class TimeException extends RuntimeException{
    TimeException(String message){
        super(message);
    }
}

public class Time implements Serializable {
    private int hours = 0;
    private int minutes = 0;

    public Time(){}
    public Time(int hours, int minutes){
        setHours(hours);
        setMinutes(minutes);
    }

    public Time(String timeString){
        parseTimeFromString(timeString);
    }


    public static Time getCurrent(){
        LocalTime now = LocalTime.now();
        return new Time(now.getHour(), now.getMinute());
    }

    public void setHours(int hours) {
        if(hours>=0 && hours<=23) this.hours = hours;
        else throw new TimeException("Value of hours \"" + hours + "\" is out of bounds(0-23)");
    }

    public void setMinutes(int minutes) {
        if(minutes>=0 && minutes<=59) this.minutes = minutes;
        else throw new TimeException("Value of minutes \"" + minutes + "\" is out of bounds(0-59)");
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public boolean isEqual(Time t){
        return t.hours == hours && t.minutes == minutes;
    }

    public boolean isGreater(Time t){
        return hours > t.hours || (hours == t.hours && minutes > t.minutes);
    }

    public boolean isLesser(Time t){
        return hours < t.hours || (hours == t.hours && minutes < t.minutes);
    }

    public  boolean isBetween(Time start, Time end){
        return this.isGreater(start) && this.isLesser(end);
    }

    public void parseTimeFromString(String timeString){
        String[] splitted = timeString.split(":");
        if(splitted.length!=2) throw new TimeException("The string provided has wrong format. The correct one is hh:ss");
        try {
            setHours(Integer.parseInt(splitted[0]));
            setMinutes(Integer.parseInt(splitted[1]));
        }catch (Exception e){
            throw new TimeException("The string provided has wrong format. The correct one is hh:ss");
        }

    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format("%02d:%02d", hours, minutes);
    }

}
