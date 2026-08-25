package com.ychs.web.notice.entity;

import lombok.Data;

@Data
public class SysNoticeParam {
    private Long currentPage;
    private Long pageSize;
    private String title;   // 公告标题（模糊搜索）
}
