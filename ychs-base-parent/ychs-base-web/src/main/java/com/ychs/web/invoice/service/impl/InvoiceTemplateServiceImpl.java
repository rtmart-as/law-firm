package com.ychs.web.invoice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychs.web.invoice.entity.InvoiceTemplate;
import com.ychs.web.invoice.mapper.InvoiceTemplateMapper;
import com.ychs.web.invoice.service.InvoiceTemplateService;
import org.springframework.stereotype.Service;

@Service
public class InvoiceTemplateServiceImpl extends ServiceImpl<InvoiceTemplateMapper, InvoiceTemplate> implements InvoiceTemplateService {
}
