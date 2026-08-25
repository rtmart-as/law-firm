package com.ychs.web.invoice.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Excel 简易导出：根据发票模板列头生成可填写的 xlsx 模板。
 * 第一行为表头（与导入 ExcelImportUtil 读取的表头一致），其余行由用户填写后用于导入。
 * 先整体生成字节再返回，便于接口在成功前不污染响应头。
 */
public class ExcelExportUtil {

    /** 按表头列表生成空模板，返回 xlsx 字节数组 */
    public static byte[] buildHeaderTemplate(List<String> headers) throws Exception {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("发票登记表");

            // 表头样式：加粗 + 浅灰底 + 细边框 + 居中
            CellStyle headStyle = wb.createCellStyle();
            Font headFont = wb.createFont();
            headFont.setBold(true);
            headStyle.setFont(headFont);
            headStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headStyle.setBorderBottom(BorderStyle.THIN);
            headStyle.setBorderTop(BorderStyle.THIN);
            headStyle.setBorderLeft(BorderStyle.THIN);
            headStyle.setBorderRight(BorderStyle.THIN);
            headStyle.setAlignment(HorizontalAlignment.CENTER);

            Row headRow = sheet.createRow(0);
            if (headers != null) {
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = headRow.createCell(i);
                    cell.setCellValue(headers.get(i));
                    cell.setCellStyle(headStyle);
                    sheet.setColumnWidth(i, 22 * 256); // 约 22 个字符宽
                }
            }
            sheet.createFreezePane(0, 1); // 冻结表头行

            wb.write(out);
            return out.toByteArray();
        }
    }
}
