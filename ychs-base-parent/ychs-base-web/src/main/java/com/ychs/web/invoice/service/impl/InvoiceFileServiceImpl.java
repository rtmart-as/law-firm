package com.ychs.web.invoice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychs.web.invoice.entity.InvoiceFile;
import com.ychs.web.invoice.mapper.InvoiceFileMapper;
import com.ychs.web.invoice.service.InvoiceFileService;
import org.springframework.stereotype.Service;

@Service
public class InvoiceFileServiceImpl extends ServiceImpl<InvoiceFileMapper, InvoiceFile> implements InvoiceFileService {
}
