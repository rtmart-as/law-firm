package com.ychs.web.lawyerTransfer.entity;

import lombok.Data;

@Data
public class LawyerTransferParam {
    private Long currentPage;
    private Long pageSize;
    private Long lawyerId;   // 按律师过滤
}