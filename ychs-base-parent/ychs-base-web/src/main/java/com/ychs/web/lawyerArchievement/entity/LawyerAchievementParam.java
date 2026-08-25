package com.ychs.web.lawyerArchievement.entity;

import lombok.Data;

@Data
public class LawyerAchievementParam {
    private Long currentPage;
    private Long pageSize;
    private Long lawyerId;
}