package com.ychs.web.invoice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychs.web.invoice.entity.InvoiceRecord;
import com.ychs.web.invoice.mapper.InvoiceRecordMapper;
import com.ychs.web.invoice.service.InvoiceRecordService;
import org.springframework.stereotype.Service;

@Service
public class InvoiceRecordServiceImpl extends ServiceImpl<InvoiceRecordMapper, InvoiceRecord> implements InvoiceRecordService {
}
