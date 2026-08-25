package com.ychs.web.seal.entity;

import lombok.Data;

@Data
public class SealRegisterParam {
    private Long currentPage;
    private Long pageSize;
    private String useDateStart;   // 用章日期区间-开始
    private String useDateEnd;     // 用章日期区间-结束
    private String handlerName;    // 经办人
    private Long status;           // 审批状态 0待审批 1已通过 2已驳回
}