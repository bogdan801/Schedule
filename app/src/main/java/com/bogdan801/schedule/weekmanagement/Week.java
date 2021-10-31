package com.bogdan801.schedule.weekmanagement;

import org.apache.poi.xssf.usermodel.*;
import java.io.Serializable;


class WeekExeption extends RuntimeException{
    WeekExeption(String message){
        super(message);
    }
}

class WeekParsingExeption extends WeekExeption{
    WeekParsingExeption(String message){
        super(message);
    }
}


//Клас для організації розкладу тижня з Excel файлу розкладу, мета організувати обраний стовпець файлу, поділити інформацію по дням, урокам, чисельнику чи знаменнику;
//організувати доступ до даних
public class Week implements Serializable {
    private Day[] days = new Day[5];


    public Week(String[] week){
        for (int i = 0; i < days.length; i++) {
            days[i] = new Day();
        }
        Parse(week);
    }

    public Week(){
        for (int i = 0; i < days.length; i++) {
            days[i] = new Day();
        }
    }

    public Week(XSSFWorkbook workbook, int colomn){
        for (int i = 0; i < days.length; i++) {
            days[i] = new Day();
        }
        
        Parse(workbook, colomn);
        
    }

    public void Parse(XSSFWorkbook workbook, int colomn){
        int workbookLenth = workbook.getSheetAt(0).getPhysicalNumberOfRows();
        if (workbookLenth < 14*5) throw new WeekParsingExeption("Excel sheet does not contain enough rows");

        int i = 0;
        for (int j = 0; j < days.length; j++) {
            for (int k = 0; k < 14; k++) {
                days[j].AddLesson(k/2, (String)ExcelTools.GetCellValue(workbook, 0, i + 6, colomn), k%2==0);
                i++;
            }
        }

    }

    public void Parse(String[] week){
        if(week.length < 5*14) throw new WeekParsingExeption("Array 'week' is to short");

        int i = 0;
        for (int j = 0; j < days.length; j++) {
            for (int k = 0; k < 14; k++) {
                days[j].AddLesson(k/2, week[i], k%2==0);
                i++;
            }
        }
    }

    public Day GetDay(int index){
        return days[index-1];
    }

    public String GetLesson(int day, int lesson, boolean isNumerator){
        return isNumerator ? days[day-1].GetLesson(lesson).GetNumerator() : days[day-1].GetLesson(lesson).GetDenominator();
    }

    public String[] GetSchedule(int Day, boolean isNumerator) {
        String daySchedule[] = new String[7];
        for (Integer i = 1; i <= 7; i++) {
            String lesson = GetLesson(Day, i, isNumerator);
            daySchedule[i-1] = i.toString() + ". " + (lesson.isEmpty()? " - ":lesson);
        }

        return daySchedule;
    }
}
