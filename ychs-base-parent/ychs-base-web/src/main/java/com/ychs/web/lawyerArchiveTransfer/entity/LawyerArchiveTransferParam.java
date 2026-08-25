package com.ychs.web.lawyerArchiveTransfer.entity;

import lombok.Data;

@Data
public class LawyerArchiveTransferParam {
    private Long currentPage;
    private Long pageSize;
    private Long lawyerId;
}