package com.ychs.web.lawyerConsult.entity;

import lombok.Data;

@Data
public class LawyerConsultParam {
    private Long currentPage;
    private Long pageSize;
    private Long lawyerId;
}