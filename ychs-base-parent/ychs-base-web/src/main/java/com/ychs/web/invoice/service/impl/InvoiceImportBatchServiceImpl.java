package com.ychs.web.invoice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychs.web.invoice.entity.InvoiceImportBatch;
import com.ychs.web.invoice.mapper.InvoiceImportBatchMapper;
import com.ychs.web.invoice.service.InvoiceImportBatchService;
import org.springframework.stereotype.Service;

@Service
public class InvoiceImportBatchServiceImpl extends ServiceImpl<InvoiceImportBatchMapper, InvoiceImportBatch> implements InvoiceImportBatchService {
}
