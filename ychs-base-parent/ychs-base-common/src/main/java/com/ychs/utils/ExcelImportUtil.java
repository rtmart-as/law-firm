package com.ychs.utils;

import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel 简易解析：只读第一个 sheet，第一行为表头，其余为数据行。
 * 值统一用 DataFormatter 转字符串（含公式单元格用 FormulaEvaluator 求值）。
 */
public class ExcelImportUtil {

    public static List<Map<String, Object>> readRows(InputStream in) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        Workbook wb = WorkbookFactory.create(in);
        try {
            Sheet sheet = wb.getSheetAt(0);
            if (sheet == null) {
                return result;
            }
            DataFormatter formatter = new DataFormatter();
            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return result;
            }
            int lastCell = headerRow.getLastCellNum();
            List<String> headers = new ArrayList<>();
            for (int i = 0; i < lastCell; i++) {
                Cell cell = headerRow.getCell(i);
                headers.add(cell == null ? "" : formatter.formatCellValue(cell).trim());
            }
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }
                Map<String, Object> map = new LinkedHashMap<>();
                boolean empty = true;
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.getCell(i);
                    String val = cell == null ? "" : formatter.formatCellValue(cell, evaluator).trim();
                    if (!val.isEmpty()) {
                        empty = false;
                    }
                    map.put(headers.get(i), val);
                }
                if (!empty) {
                    result.add(map);
                }
            }
            return result;
        } finally {
            wb.close();
        }
    }
}