package com.ychs.web.contract.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContractChangeParm {
    private Long contractId;
    private Integer changeType;      // 1追加律师费 2退律师费
    private BigDecimal changeAmount; // 变更金额
    private String changeDate;       // 变更日期 YYYY-MM-DD
    private String reasonFileUrl;    // 情况说明PDF地址
}