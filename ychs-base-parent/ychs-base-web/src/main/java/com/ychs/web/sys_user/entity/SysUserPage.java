package com.ychs.web.sys_user.entity;

import lombok.Data;

@Data
public class SysUserPage {
     private String phone;
     private String nickName;
      //当前第几页
    private Long currentPage;
    // 每页查询的条数
    private Long pageSize;
}
