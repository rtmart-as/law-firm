package com.ychs.web.admission.entity;

import lombok.Data;

@Data
public class AdmissionApplyParam {
    private Long currentPage;
    private Long pageSize;
    private String applicantName;   // 按姓名模糊查询
    private Byte status;            // 按状态过滤（可空）
    private Long lawyerId;          // 按律师ID精确查询（完善档案：审批通过后生成的 lawyer_id）
}