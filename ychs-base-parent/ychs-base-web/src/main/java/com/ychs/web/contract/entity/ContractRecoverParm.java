package com.ychs.web.contract.entity;

import lombok.Data;

@Data
public class ContractRecoverParm {
    private Long contractId;
    private String recoverDate;    // 收回日期 YYYY-MM-DD
    private String fileUrl;        // 合同PDF地址
}