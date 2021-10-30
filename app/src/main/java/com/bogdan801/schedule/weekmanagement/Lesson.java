package com.bogdan801.schedule.weekmanagement;

import java.io.Serializable;

public class Lesson implements Serializable {
    private String Numerator = "";
    private String Denominator = "";

    public Lesson(){}

    public Lesson(String Numerator, String Denominator){
        this.Numerator = Numerator;
        this.Denominator = Denominator;
    }

    public void SetNumerator(String lesson) {
        Numerator = lesson;
    }

    public void SetDenominator(String lesson) {
        Denominator = lesson;
    }

    public String GetNumerator(){
        return Numerator;
    }

    public String GetDenominator(){
        return Denominator;
    }    
}
