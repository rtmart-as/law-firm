package com.ychs.web.lawyerTransfer.entity;

import lombok.Data;

@Data
public class ConfirmParm {
    private Long id;          // 转所记录ID
    private Byte status;      // 1已确认 2驳回
    private String auditRemark; // 审批意见（驳回时填写的驳回理由）
}