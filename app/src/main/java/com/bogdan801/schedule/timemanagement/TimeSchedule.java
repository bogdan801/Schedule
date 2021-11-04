package com.bogdan801.schedule.timemanagement;

import java.io.Serializable;

class LessonDuration implements Serializable{
    private Time start;
    private Time end;

    LessonDuration(){}
    LessonDuration(Time start, Time end){
        this.start = start;
        this.end = end;
    }

    public void setStart(Time start) {
        this.start = start;
    }

    public void setEnd(Time end) {
        this.end = end;
    }

    public Time getStart() {
        return start;
    }

    public Time getEnd() {
        return end;
    }
}

public class TimeSchedule implements Serializable {
    LessonDuration[] lessonsTime = new LessonDuration[7];
    public TimeSchedule(){
        lessonsTime[0] = new LessonDuration(new Time(8,30), new Time(9,50));
        lessonsTime[1] = new LessonDuration(new Time(10,10), new Time(11,30));
        lessonsTime[2] = new LessonDuration(new Time(11,50), new Time(13,10));
        lessonsTime[3] = new LessonDuration(new Time(14,0), new Time(15,20));
        lessonsTime[4] = new LessonDuration(new Time(15,35), new Time(16,55));
        lessonsTime[5] = new LessonDuration(new Time(17,10), new Time(18,30));
        lessonsTime[6] = new LessonDuration(new Time(18,45), new Time(20,5));
    }

    public TimeSchedule(LessonDuration[] schedule){
        lessonsTime = schedule;
    }

    public void setStart(int lesson, Time time){
        lessonsTime[lesson-1].setStart(time);
    }

    public void setEnd(int lesson, Time time){
        lessonsTime[lesson-1].setEnd(time);
    }
    
    public Time getStart(int lesson){
        return lessonsTime[lesson-1].getStart();
    }

    public Time getEnd(int lesson){
        return lessonsTime[lesson-1].getEnd();
    }
}
