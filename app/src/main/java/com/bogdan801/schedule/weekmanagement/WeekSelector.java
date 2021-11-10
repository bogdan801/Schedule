package com.bogdan801.schedule.weekmanagement;

import java.util.ArrayList;
import org.apache.poi.xssf.usermodel.*;

class WeekSelectorExeption extends RuntimeException{
    WeekSelectorExeption(String message){
        super(message);
    }
}

class WrongSelectionOrderExeption extends WeekSelectorExeption{
    WrongSelectionOrderExeption(String message){
        super(message);
    }
}

//Клас для вибору групи з файлу розкладу, мета отримати номер стовпця або об'єкт Week обраної групи
//Після створення WeekSelector в список (ArrayList<String> majors) записуються всі спеціальності з листа файлу
//Для отримання номеру стовпця або об'єкт Week обраної групи потрібно виконати такі методи в цій послідовності:
//1. SelectMajor - обираємо спеціальність, передаємо назву або індекс вибраної спеціальності як параметр; метод повертає список курсів на цій спеціальності, зберігаючи його в WeekSelector
//2. SelectYear - обираємо курс, передаємо назву або індекс вибраного курсу як параметр; метод повертає список груп на вибраному курсі і спеціальності, зберігаючи його в WeekSelector
//3. SelectGroupAsWeek - обираємо групу, передаємо назву або індекс вибраної групи як параметр; метод повертає об'єкт Week, в який вже зчитаний розклад обраної групи
// АБО
//3. SelectGroupAsColumnNum - обираємо групу, передаємо назву або індекс вибраної групи як параметр; метод повертає номер стовпця обраної групи
public class WeekSelector {
    XSSFWorkbook workbook;
    int sheet;
    private ArrayList<String> allMajors = new ArrayList<String>();
    private ArrayList<String> allYears = new ArrayList<String>();
    private ArrayList<String> allGroups = new ArrayList<String>();

    private ArrayList<String> majors = new ArrayList<String>();
    private ArrayList<String> years = new ArrayList<String>();
    private ArrayList<String> groups = new ArrayList<String>();
    private String selectedMajor = null;
    private String selectedYear = null;
    private String selectedGroup = null;
    private WeekSchedule weekSchedule = null;
    private int colomn = -1;

    //В список (ArrayList<String> majors) записуються всі спеціальності з листа файлу
    public WeekSelector(XSSFWorkbook workbook, int sheet){
        this.workbook = workbook;
        this.sheet = sheet;

        for (int i = 2; i < ExcelTools.GetLastColumnNum(workbook, sheet); i++) {
            String major = (String)ExcelTools.GetCellValue(workbook, sheet, 3, i);
            allMajors.add(major);
            allYears.add((String)ExcelTools.GetCellValue(workbook, sheet, 2, i));
            allGroups.add((String)ExcelTools.GetCellValue(workbook, sheet, 4, i));

            if (!majors.contains(major)) {
                majors.add(major);
            }
        }
    }

    //Метод повертає масив з назвами всіх спеціальностей
    public String[] GetAllMajors(){
        return majors.toArray(new String[majors.size()]);
    }

    //Метод повертає список з назвами всіх спеціальностей
    public ArrayList<String> GetAllMajorsAsList(){
        return majors;
    }

    //Метод вибору спеціальності за її назвою; повертає список курсів на цій спеціальності, зберігаючи його в WeekSelector
    public String[] SelectMajor(String selectedMajor){
        if (!majors.contains(selectedMajor)) throw new WeekSelectorExeption("No such major in major list");

        this.selectedMajor = selectedMajor;
        years.clear();
        groups.clear();
        colomn = -1;

        for (int i = 0; i < allMajors.size(); i++) {
            if(allMajors.get(i).equals(selectedMajor)){
                String year = allYears.get(i);
                if (!years.contains(year)) {
                    years.add(year);
                }
            }
        }

        return years.toArray(new String[years.size()]);
    }

    //Метод вибору спеціальності за її індексом; повертає список курсів на цій спеціальності, зберігаючи його в WeekSelector
    public String[] SelectMajor(int selectedMajorIndex){
        if (selectedMajorIndex < 0 || selectedMajorIndex >= majors.size()) throw new WeekSelectorExeption("Index out of bounds");
        return SelectMajor(majors.get(selectedMajorIndex));
    }

    //Метод повертає масив з назвами всіх курсів
    public String[] GetAllYears(){
        return years.toArray(new String[years.size()]);
    }

    //Метод повертає список з назвами всіх курсів
    public ArrayList<String> GetAllYearsAsList(){
        return years;
    }

    //Метод вибору курсу за його індексом; повертає список груп на вибраному курсі і спеціальності, зберігаючи його в WeekSelector
    public String[] SelectYear(String selectedYear){
        if (selectedMajor == null) throw new WrongSelectionOrderExeption("Major was not selected");
        if (!years.contains(selectedYear)) throw new WeekSelectorExeption("No such year in years list");

        this.selectedYear = selectedYear;
        groups.clear();
        colomn = -1;

        for (int i = 0; i < allYears.size(); i++) {
            if(allMajors.get(i).equals(selectedMajor) && allYears.get(i).equals(selectedYear)){
                String group = allGroups.get(i);
                if (!groups.contains(group)) {
                    groups.add(group);
                }
            }
        }

        return groups.toArray(new String[groups.size()]);
    }

    //Метод вибору курсу за його індексом; повертає список груп на вибраному курсі і спеціальності, зберігаючи його в WeekSelector
    public String[] SelectYear(int selectedYearIndex){
        if (selectedMajor == null) throw new WrongSelectionOrderExeption("Major was not selected");
        if (selectedYearIndex < 0 || selectedYearIndex >= years.size()) throw new WeekSelectorExeption("Index out of bounds");
        return SelectYear(years.get(selectedYearIndex));
    }

    //Метод повертає масив з назвами всіх груп обраної спеціальності і курсу
    public String[] GetAllGroups(){
        return groups.toArray(new String[years.size()]);
    }

    //Метод повертає список з назвами всіх груп обраної спеціальності і курсу
    public ArrayList<String> GetAllGroupsAsList(){
        return groups;
    }

    //Метод вибору групи за її назвою; повертає об'єкт Week, в який вже зчитаний розклад обраної групи
    public WeekSchedule SelectGroupAsWeek(String selectedGroup){
        if (selectedMajor == null) throw new WrongSelectionOrderExeption("Major was not selected");
        if (selectedYear == null) throw new WrongSelectionOrderExeption("Year was not selected");
        if (!groups.contains(selectedGroup)) throw new WeekSelectorExeption("No such year in years list");

        this.selectedGroup = selectedGroup;

        for (int i = 0; i < allGroups.size(); i++) {
            if(allMajors.get(i).equals(selectedMajor) && allYears.get(i).equals(selectedYear) && allGroups.get(i).equals(selectedGroup)){
                colomn = i + 2;
                weekSchedule = new WeekSchedule(workbook, colomn);
                return weekSchedule;
            }
        }

        return null;
    }

    //Метод вибору групи за її індексом; повертає об'єкт Week, в який вже зчитаний розклад обраної групи
    public WeekSchedule SelectGroupAsWeek(int selectedGroupIndex){
        if (selectedMajor == null) throw new WrongSelectionOrderExeption("Major was not selected");
        if (selectedYear == null) throw new WrongSelectionOrderExeption("Year was not selected");
        if (selectedGroupIndex < 0 || selectedGroupIndex >= groups.size()) throw new WeekSelectorExeption("Index out of bounds");
        return SelectGroupAsWeek(groups.get(selectedGroupIndex));
    }

    //Метод вибору групи за її назвою; повертає номер стовпця обраної групи
    public int SelectGroupAsColumnNum(String selectedGroup){
        if (selectedMajor == null) throw new WrongSelectionOrderExeption("Major was not selected");
        if (selectedYear == null) throw new WrongSelectionOrderExeption("Year was not selected");
        if (!groups.contains(selectedGroup)) throw new WeekSelectorExeption("No such year in years list");

        this.selectedGroup = selectedGroup;
        for (int i = 0; i < allGroups.size(); i++) {
            if(allMajors.get(i).equals(selectedMajor) && allYears.get(i).equals(selectedYear) && allGroups.get(i).equals(selectedGroup)){
                colomn = i+2;
                return colomn;
            }
        }

        return -1;
    }

    //Метод вибору групи за її індексом; повертає номер стовпця обраної групи
    public int SelectGroupAsColumnNum(int selectedGroupIndex){
        if (selectedMajor == null) throw new WrongSelectionOrderExeption("Major was not selected");
        if (selectedYear == null) throw new WrongSelectionOrderExeption("Year was not selected");
        if (selectedGroupIndex < 0 || selectedGroupIndex >= groups.size()) throw new WeekSelectorExeption("Index "+selectedGroupIndex+" out of bounds");
        return SelectGroupAsColumnNum(groups.get(selectedGroupIndex));
    }

    //Метод повертає об'єкт Week, заповнений даними з обраного стовпця
    public WeekSchedule GetSelectedWeek(){
        return weekSchedule;
    }

    //Метод повертає номер обраного стовпця
    public int GetSelectedColomnNum(){
        return colomn;
    }

    public int GetSelectedMajorIndex(){
        if(selectedMajor != null){
            return majors.indexOf(selectedMajor);
        }
        else return 0;
    }

    public int GetSelectedYearIndex(){
        if(selectedYear != null){
            return years.indexOf(selectedYear);
        }
        else return 0;
    }

    public int GetSelectedGroupIndex(){
        if(selectedGroup != null){
            return groups.indexOf(selectedGroup);
        }
        else return 0;
    }

}