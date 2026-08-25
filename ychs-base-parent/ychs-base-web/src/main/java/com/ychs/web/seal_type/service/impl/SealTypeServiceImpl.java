package com.ychs.web.seal_type.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychs.web.seal_type.entity.SealType;
import com.ychs.web.seal_type.mapper.SealTypeMapper;
import com.ychs.web.seal_type.service.SealTypeService;
import org.springframework.stereotype.Service;

@Service
public class SealTypeServiceImpl extends ServiceImpl<SealTypeMapper, SealType> implements SealTypeService {
}
