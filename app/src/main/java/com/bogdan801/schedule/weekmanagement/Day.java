package com.bogdan801.schedule.weekmanagement;

import java.io.Serializable;

public class Day implements Serializable {
    private Lesson[] lessons = new Lesson[7];
    public Day(){
        for (int i = 0; i < lessons.length; i++) {
            lessons[i] = new Lesson();
        }
    }

    public void AddLesson(int index, String lesson, boolean isNumerator){
        if (isNumerator) {
            lessons[index].SetNumerator(lesson);
        }
        else lessons[index].SetDenominator(lesson);
    }

    Lesson GetLesson(int index){
        return lessons[index-1];
    }
}
