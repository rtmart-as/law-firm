/*
 * 英才汇硕信息科技有限公司 拥有本软件版权 2024-2028 并保留所有权利。
 * Copyright 2024-2028, YCHS Information&Science Technology Co.,Ltd,
 * All rights reserved.
 */
package com.ychs.web.lawyer.entity;

import lombok.Data;

/**
 *
 * @author fanyuyang
 * @version 1.0
 */
@Data
public class LawyerParam {
    private Long currentPage;
    private Long pageSize;
    private String name;
}
