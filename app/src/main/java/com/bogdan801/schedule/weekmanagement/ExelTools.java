package com.bogdan801.schedule.weekmanagement;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExelTools {
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

        CellRangeAddress cells = getMergedRegionForCell((org.apache.poi.ss.usermodel.Cell)Cell);
        if(cells != null){
            XSSFCell FirstRegCell = workbook.getSheetAt(sheet).getRow(cells.getFirstRow()).getCell(cell);
            return FirstRegCell.getStringCellValue();
        }
        else {
            return Cell.getStringCellValue();
        }



    }

    public static XSSFCell GetCell(XSSFWorkbook workbook, int sheet, int row, int colomn) {
        return workbook.getSheetAt(sheet).getRow(row).getCell(colomn);
    }

}
