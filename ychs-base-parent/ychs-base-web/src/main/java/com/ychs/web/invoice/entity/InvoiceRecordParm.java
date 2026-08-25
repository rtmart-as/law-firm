package com.ychs.web.invoice.entity;

import lombok.Data;

@Data
public class InvoiceRecordParm {
    private Long currentPage;
    private Long pageSize;
    private Integer recordYear;   // 登记年份 精确
    private String lawyerName;    // 律师 模糊
    private String contractNo;    // 合同编号 模糊
    private String invoiceNo;     // 发票号码 模糊
}