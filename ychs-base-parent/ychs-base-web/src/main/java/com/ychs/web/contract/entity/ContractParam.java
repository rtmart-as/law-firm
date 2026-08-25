package com.ychs.web.contract.entity;

import lombok.Data;

@Data
public class ContractParam {
    private Long currentPage;
    private Long pageSize;
    private String contractNo;   // 合同编号 模糊
    private String caseType;     // 案件类型 精确
    private String lawyerName;   // 承办律师 模糊
    private Integer status;      // 状态 1正常 2解除 3变更 4收回
    private String startDate;    // 领用日期区间-开始
    private String endDate;      // 领用日期区间-结束
}