package com.ychs.web.sys_user.entity;

import lombok.Data;

@Data
public class LoginVo {
    private Long userId;
    private String username;
    private String nickName;
    private String token;
}