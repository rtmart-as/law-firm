package com.ychs.web.seal_type.entity;

import lombok.Data;

@Data
public class SealTypeParam {
    private Long currentPage;
    private Long pageSize;
    private String name;
}
