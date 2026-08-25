package com.ychs.web.home.entity;

import lombok.Data;

@Data
public class TotalVo {
    //用户总数
    private long userCount;
    //律师总数
    private long lawyerCount;
    //进行案件（status=1 正常在途）
    private long ongoingCount;
    //结算案件（invoice_flag=1 已开票）
    private long settledCount;
}
