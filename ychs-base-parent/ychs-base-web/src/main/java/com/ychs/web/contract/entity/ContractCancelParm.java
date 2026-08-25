package com.ychs.web.contract.entity;

import lombok.Data;

@Data
public class ContractCancelParm {
    private Long contractId;
    private String cancelReason;   // 解除原因
    private String fileUrl;        // 解除合同PDF地址
    private String cancelDate;     // 解除日期 YYYY-MM-DD
}