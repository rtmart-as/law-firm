package com.ychs.web.lawyerCert.entity;

import lombok.Data;

@Data
public class LawyerCertParam {
    private Long currentPage;
    private Long pageSize;
    private Long lawyerId;
}