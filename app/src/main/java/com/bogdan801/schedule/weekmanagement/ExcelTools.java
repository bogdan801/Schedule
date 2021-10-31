package com.bogdan801.schedule.weekmanagement;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//Клас для спрощення доступу до Excel файлу, організації роботи з бібліотекою Apache Poi
public class ExcelTools {
    public static CellRangeAddress getMergedRegionForCell(Cell c) {
        Sheet s = c.getRow().getSheet();
        CellRangeAddress[] addresses = new CellRangeAddress[s.getNumMergedRegions()];
        for (int i = 0; i < addresses.length; i++) {
            addresses[i] = s.getMergedRegion(i);
        }


        for (CellRangeAddress mergedRegion : addresses) {
            if (mergedRegion.isInRange(c.getRowIndex(), c.getColumnIndex())) {
                // This region contains the cell in question
                return mergedRegion;
            }
        }
        // Not in any
        return null;
    }

    public static Object GetCellValue(XSSFWorkbook workbook, int sheet, int row, int cell) {
        XSSFCell Cell = workbook.getSheetAt(sheet).getRow(row).getCell(cell);
        if (Cell == null) return "";

        CellRangeAddress cells = getMergedRegionForCell((org.apache.poi.ss.usermodel.Cell)Cell);//
        if(cells != null){
            XSSFCell FirstRegCell = workbook.getSheetAt(sheet).getRow(cells.getFirstRow()).getCell(cells.getFirstColumn());
            
            switch (FirstRegCell.getCellType()) {
                case STRING:
                    return FirstRegCell.getStringCellValue();
                case NUMERIC:
                    return String.valueOf(FirstRegCell.getNumericCellValue());
                case BOOLEAN:
                    return String.valueOf(FirstRegCell.getBooleanCellValue());
                default:
                    return "";
            }
            
            
            
        }
        else {
            switch (Cell.getCellType()) {
                case STRING:
                    return Cell.getStringCellValue();
                case NUMERIC:
                    return String.valueOf(Cell.getNumericCellValue());
                case BOOLEAN:
                    return String.valueOf(Cell.getBooleanCellValue());
                default:
                    return "";
            }
        }
    }

    public static XSSFCell GetCell(XSSFWorkbook workbook, int sheet, int row, int colomn) {
        return workbook.getSheetAt(sheet).getRow(row).getCell(colomn);
    }

    public static int GetFirstRowNum(XSSFWorkbook workbook, int sheet){
        return workbook.getSheetAt(sheet).getFirstRowNum();
    }

    public static int GetLastRowNum(XSSFWorkbook workbook, int sheet){
        return workbook.getSheetAt(sheet).getLastRowNum();
    }

    public static int GetFirstColumnNum(XSSFWorkbook workbook, int sheet){
        return workbook.getSheetAt(sheet).getRow(0).getFirstCellNum();
    }
    
    public static int GetLastColumnNum(XSSFWorkbook workbook, int sheet){
        return workbook.getSheetAt(sheet).getRow(0).getLastCellNum();
    }
}
