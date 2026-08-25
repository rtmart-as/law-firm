package com.ychs.web.admission.entity;

import lombok.Data;

@Data
public class AuditParm {
    private Long id;              // 申请ID
    private Byte status;          // 1通过 2驳回
    private String auditRemark;   // 审批意见
}