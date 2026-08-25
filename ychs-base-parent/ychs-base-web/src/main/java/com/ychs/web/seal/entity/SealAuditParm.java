package com.ychs.web.seal.entity;

import lombok.Data;

/**
 * 用章登记审批参数
 */
@Data
public class SealAuditParm {
    private Long id;              // 登记ID
    private Byte status;          // 1通过 2驳回
    private String auditRemark;   // 审批意见
}
