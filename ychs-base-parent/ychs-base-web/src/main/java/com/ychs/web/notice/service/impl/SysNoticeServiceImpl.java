package com.ychs.web.notice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychs.web.notice.entity.SysNotice;
import com.ychs.web.notice.mapper.SysNoticeMapper;
import com.ychs.web.notice.service.SysNoticeService;
import org.springframework.stereotype.Service;

@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeService {
}
