package com.ychs.web.invoice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ychs.utils.ExcelImportUtil;
import com.ychs.utils.ResultUtils;
import com.ychs.utils.ResultVo;
import com.ychs.web.invoice.entity.*;
import com.ychs.web.invoice.service.InvoiceFileService;
import com.ychs.web.invoice.service.InvoiceImportBatchService;
import com.ychs.web.invoice.service.InvoiceRecordService;
import com.ychs.web.invoice.service.InvoiceTemplateService;
import com.ychs.web.invoice.util.ExcelExportUtil;
import com.ychs.web.sys_user.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceRecordService invoiceRecordService;
    @Autowired
    private InvoiceTemplateService invoiceTemplateService;
    @Autowired
    private InvoiceImportBatchService invoiceImportBatchService;
    @Autowired
    private InvoiceFileService invoiceFileService;

    // ========== 表头模板 ==========

    @GetMapping("/template")
    public ResultVo getTemplate(@RequestParam("recordYear") Integer recordYear) {
        List<InvoiceTemplate> list = invoiceTemplateService.list(new LambdaQueryWrapper<InvoiceTemplate>()
                .eq(InvoiceTemplate::getRecordYear, recordYear)
                .eq(InvoiceTemplate::getDelFlag, 0)
                .orderByAsc(InvoiceTemplate::getColOrder));
        return ResultUtils.success("查询成功", list);
    }

    @PostMapping("/template")
    @PreAuthorize("hasAuthority('sys:finance:templateSave')")
    public ResultVo saveTemplate(@RequestBody List<InvoiceTemplate> templates) {
        if (templates == null || templates.isEmpty()) {
            return ResultUtils.error("模板不能为空");
        }
        Integer year = templates.get(0).getRecordYear();
        if (year == null) {
            return ResultUtils.error("缺少登记年份");
        }
        // 先删除该年度旧模板，再整体插入
        invoiceTemplateService.remove(new LambdaQueryWrapper<InvoiceTemplate>()
                .eq(InvoiceTemplate::getRecordYear, year));
        int order = 1;
        for (InvoiceTemplate t : templates) {
            t.setId(null);
            t.setColOrder(order++);
            t.setCreateTime(new Date());
            t.setDelFlag((byte) 0);
        }
        boolean flag = invoiceTemplateService.saveBatch(templates);
        return flag ? ResultUtils.success("模板保存成功") : ResultUtils.error("保存失败");
    }

    /** 根据模板生成可填写的 Excel 模板文件（表头与导入模板一致） */
    @GetMapping("/template/export")
    public void exportTemplate(HttpServletResponse response, @RequestParam("recordYear") Integer recordYear) {
        List<InvoiceTemplate> templates = invoiceTemplateService.list(new LambdaQueryWrapper<InvoiceTemplate>()
                .eq(InvoiceTemplate::getRecordYear, recordYear)
                .eq(InvoiceTemplate::getDelFlag, 0)
                .orderByAsc(InvoiceTemplate::getColOrder));
        if (templates.isEmpty()) {
            writeError(response, "该年度尚未配置表头，请先到“表头配置”维护模板");
            return;
        }
        List<String> headers = templates.stream()
                .map(InvoiceTemplate::getColLabel)
                .collect(Collectors.toList());
        // 先生成完整字节再写响应，失败时可返回正常错误而不会污染已设置的下载响应头
        byte[] data;
        try {
            data = ExcelExportUtil.buildHeaderTemplate(headers);
        } catch (Exception e) {
            e.printStackTrace();
            writeError(response, "生成 Excel 失败：" + e.getMessage());
            return;
        }
        try {
            String fileName = URLEncoder.encode(recordYear + "年发票登记模板.xlsx", StandardCharsets.UTF_8.toString());
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setContentLength(data.length);
            response.getOutputStream().write(data);
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 下载场景下以 JSON 形式返回错误信息 */
    private void writeError(HttpServletResponse response, String msg) {
        try {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":500,\"msg\":\"" + msg.replace("\"", "'") + "\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ========== Excel 导入 ==========

    @PostMapping("/import")
    @PreAuthorize("hasAuthority('sys:finance:import')")
    public ResultVo importExcel(@RequestParam("file") MultipartFile file,
                                @RequestParam("recordYear") Integer recordYear) {
        if (file.isEmpty()) {
            return ResultUtils.error("上传文件为空");
        }
        if (recordYear == null) {
            return ResultUtils.error("未选择登记年份");
        }
        List<InvoiceTemplate> templates = invoiceTemplateService.list(new LambdaQueryWrapper<InvoiceTemplate>()
                .eq(InvoiceTemplate::getRecordYear, recordYear)
                .eq(InvoiceTemplate::getDelFlag, 0)
                .orderByAsc(InvoiceTemplate::getColOrder));
        if (templates.isEmpty()) {
            return ResultUtils.error("该年度尚未配置表头，请先到“表头配置”维护模板");
        }
        List<Map<String, Object>> rows;
        try {
            rows = ExcelImportUtil.readRows(file.getInputStream());
        } catch (Exception e) {
            return ResultUtils.error("Excel 解析失败：" + e.getMessage());
        }
        if (rows.isEmpty()) {
            return ResultUtils.error("Excel 无有效数据行");
        }
        String batchNo = recordYear + "-" + System.currentTimeMillis();
        List<Map<String, Object>> failDetail = new ArrayList<>();
        List<InvoiceRecord> okRecords = new ArrayList<>();
        int rowIndex = 1; // 表头占第一行
        for (Map<String, Object> row : rows) {
            rowIndex++;
            List<String> errors = new ArrayList<>();
            InvoiceRecord rec = buildRecord(row, templates, recordYear, batchNo, errors);
            if (!errors.isEmpty()) {
                failDetail.add(failMap(rowIndex, String.join("；", errors), row));
                continue;
            }
            long dup = invoiceRecordService.count(new LambdaQueryWrapper<InvoiceRecord>()
                    .eq(InvoiceRecord::getInvoiceNo, rec.getInvoiceNo())
                    .eq(InvoiceRecord::getDelFlag, 0));
            if (dup > 0) {
                failDetail.add(failMap(rowIndex, "发票号码已存在：" + rec.getInvoiceNo(), row));
                continue;
            }
            rec.setCreateTime(new Date());
            rec.setDelFlag((byte) 0);
            okRecords.add(rec);
        }
        int successCount = 0;
        if (!okRecords.isEmpty()) {
            invoiceRecordService.saveBatch(okRecords);
            successCount = okRecords.size();
        }
        InvoiceImportBatch batch = new InvoiceImportBatch();
        batch.setRecordYear(recordYear);
        batch.setBatchNo(batchNo);
        batch.setFileName(file.getOriginalFilename());
        batch.setSuccessCount(successCount);
        batch.setFailCount(failDetail.size());
        batch.setFailDetail(failDetail);
        batch.setImportUser(getCurrentUser().getUserId());
        batch.setImportTime(new Date());
        batch.setCreateTime(new Date());
        batch.setDelFlag((byte) 0);
        invoiceImportBatchService.save(batch);

        Map<String, Object> data = new HashMap<>();
        data.put("batchNo", batchNo);
        data.put("successCount", successCount);
        data.put("failCount", failDetail.size());
        data.put("failDetail", failDetail);
        return ResultUtils.success("导入完成", data);
    }

    /** 按模板把一行数据映射成 InvoiceRecord：核心列进真实列，扩展列进 ext_json */
    private InvoiceRecord buildRecord(Map<String, Object> row, List<InvoiceTemplate> templates,
                                      Integer recordYear, String batchNo, List<String> errors) {
        InvoiceRecord rec = new InvoiceRecord();
        rec.setRecordYear(recordYear);
        rec.setBatchNo(batchNo);
        Map<String, Object> ext = new LinkedHashMap<>();
        for (InvoiceTemplate t : templates) {
            Object val = row.get(t.getColLabel());
            String value = val == null ? "" : val.toString().trim();
            if (t.getIsCore() != null && t.getIsCore() == 1) {
                mapCore(rec, t.getColKey(), value, errors);
            } else {
                ext.put(t.getColKey(), value);
            }
        }
        rec.setExtJson(ext);
        return rec;
    }

    private void mapCore(InvoiceRecord rec, String colKey, String value, List<String> errors) {
        if (value == null || value.isEmpty()) {
            errors.add("[" + colKey + "]不能为空");
            return;
        }
        switch (colKey) {
            case "invoice_no":
                rec.setInvoiceNo(value);
                break;
            case "invoice_date":
                try {
                    rec.setInvoiceDate(parseDate(value));
                } catch (Exception e) {
                    errors.add("开票日期格式错误：" + value);
                }
                break;
            case "contract_no":
                rec.setContractNo(value);
                break;
            case "lawyer_name":
                rec.setLawyerName(value);
                break;
            case "invoice_amount":
                rec.setInvoiceAmount(parseAmount(value, "开票金额", errors));
                break;
            case "tax_amount":
                rec.setTaxAmount(parseAmount(value, "税额", errors));
                break;
            case "invoice_total":
                rec.setInvoiceTotal(parseAmount(value, "价税合计", errors));
                break;
            default:
                break;
        }
    }

    private BigDecimal parseAmount(String value, String label, List<String> errors) {
        try {
            return new BigDecimal(value.replace(",", "").trim());
        } catch (Exception e) {
            errors.add(label + "格式错误：" + value);
            return null;
        }
    }

    private Date parseDate(String value) throws Exception {
        String v = value.trim();
        if (v.matches("\\d{8}")) {                       // 20250115
            return new SimpleDateFormat("yyyyMMdd").parse(v);
        }
        if (v.contains("/")) {                            // 2025/1/15
            return new SimpleDateFormat("yyyy/M/d").parse(v);
        }
        return new SimpleDateFormat("yyyy-MM-dd").parse(v);
    }

    private Map<String, Object> failMap(int row, String error, Map<String, Object> data) {
        Map<String, Object> fail = new HashMap<>();
        fail.put("row", row);
        fail.put("error", error);
        fail.put("data", data);
        return fail;
    }

    // ========== 发票记录 CRUD + 改动留痕 ==========

    @GetMapping("/record/getList")
    public ResultVo getRecordList(InvoiceRecordParm parm) {
        Page<InvoiceRecord> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        LambdaQueryWrapper<InvoiceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvoiceRecord::getDelFlag, 0);
        if (parm.getRecordYear() != null) {
            wrapper.eq(InvoiceRecord::getRecordYear, parm.getRecordYear());
        }
        if (parm.getLawyerName() != null && !parm.getLawyerName().isEmpty()) {
            wrapper.like(InvoiceRecord::getLawyerName, parm.getLawyerName());
        }
        if (parm.getContractNo() != null && !parm.getContractNo().isEmpty()) {
            wrapper.like(InvoiceRecord::getContractNo, parm.getContractNo());
        }
        if (parm.getInvoiceNo() != null && !parm.getInvoiceNo().isEmpty()) {
            wrapper.like(InvoiceRecord::getInvoiceNo, parm.getInvoiceNo());
        }
        wrapper.orderByDesc(InvoiceRecord::getInvoiceDate);
        return ResultUtils.success("查询成功", invoiceRecordService.page(page, wrapper));
    }

    @PutMapping("/record")
    @PreAuthorize("hasAuthority('sys:finance:edit')")
    public ResultVo updateRecord(@RequestBody InvoiceRecord record) {
        if (record.getId() == null) {
            return ResultUtils.error("没有传入id");
        }
        InvoiceRecord exist = invoiceRecordService.getById(record.getId());
        if (exist == null) {
            return ResultUtils.error("记录不存在");
        }
        // 改动留痕：记录到所属批次 change_log
        if (exist.getBatchNo() != null && !exist.getBatchNo().isEmpty()) {
            List<Map<String, String>> changes = diff(exist, record);
            if (!changes.isEmpty()) {
                Map<String, Object> change = new HashMap<>();
                change.put("recordId", exist.getId());
                change.put("invoiceNo", exist.getInvoiceNo());
                change.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                change.put("user", getCurrentUser().getUsername());
                change.put("changes", changes);
                appendChangeLog(exist.getBatchNo(), change);
            }
        }
        record.setUpdateTime(new Date());
        boolean flag = invoiceRecordService.updateById(record);
        return flag ? ResultUtils.success("修改成功") : ResultUtils.error("修改失败");
    }

    @DeleteMapping("/record/{id}")
    @PreAuthorize("hasAuthority('sys:finance:edit')")
    public ResultVo removeRecord(@PathVariable("id") Long id) {
        if (id == null) {
            return ResultUtils.error("没有传入id");
        }
        InvoiceRecord record = new InvoiceRecord();
        record.setId(id);
        record.setDelFlag((byte) 1);
        boolean flag = invoiceRecordService.updateById(record);
        return flag ? ResultUtils.success("删除成功") : ResultUtils.error("删除失败");
    }

    /** 回看某批次的改动记录 */
    @GetMapping("/record/diff")
    @PreAuthorize("hasAuthority('sys:finance:edit')")
    public ResultVo diffByBatch(@RequestParam("batchNo") String batchNo) {
        InvoiceImportBatch batch = invoiceImportBatchService.getOne(new LambdaQueryWrapper<InvoiceImportBatch>()
                .eq(InvoiceImportBatch::getBatchNo, batchNo)
                .eq(InvoiceImportBatch::getDelFlag, 0)
                .last("LIMIT 1"));
        if (batch == null) {
            return ResultUtils.error("批次不存在");
        }
        return ResultUtils.success("查询成功",
                batch.getChangeLog() == null ? new ArrayList<>() : batch.getChangeLog());
    }

    // ========== 发票关联合同原件 ==========

    @PostMapping("/file")
    @PreAuthorize("hasAuthority('sys:finance:file')")
    public ResultVo saveFile(@RequestBody InvoiceFile file) {
        if (file.getFileUrl() == null || file.getFileUrl().isEmpty()) {
            return ResultUtils.error("请先上传合同原件");
        }
        file.setUploadUser(getCurrentUser().getUserId());
        file.setUploadTime(new Date());
        file.setCreateTime(new Date());
        file.setDelFlag((byte) 0);
        boolean flag = invoiceFileService.save(file);
        return flag ? ResultUtils.success("关联成功") : ResultUtils.error("关联失败");
    }

    @GetMapping("/file/list")
    public ResultVo fileList(@RequestParam(required = false) Integer recordYear,
                             @RequestParam(required = false) Long invoiceId) {
        LambdaQueryWrapper<InvoiceFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvoiceFile::getDelFlag, 0);
        if (recordYear != null) {
            wrapper.eq(InvoiceFile::getRecordYear, recordYear);
        }
        if (invoiceId != null) {
            wrapper.eq(InvoiceFile::getInvoiceId, invoiceId);
        }
        wrapper.orderByDesc(InvoiceFile::getUploadTime);
        return ResultUtils.success("查询成功", invoiceFileService.list(wrapper));
    }

    // ========== 工具方法 ==========

    private List<Map<String, String>> diff(InvoiceRecord old, InvoiceRecord neu) {
        List<Map<String, String>> list = new ArrayList<>();
        addDiff(list, "发票号码", old.getInvoiceNo(), neu.getInvoiceNo());
        addDiff(list, "开票日期", fmtDate(old.getInvoiceDate()), fmtDate(neu.getInvoiceDate()));
        addDiff(list, "合同编号", old.getContractNo(), neu.getContractNo());
        addDiff(list, "律师", old.getLawyerName(), neu.getLawyerName());
        addDiff(list, "开票金额", str(old.getInvoiceAmount()), str(neu.getInvoiceAmount()));
        addDiff(list, "税额", str(old.getTaxAmount()), str(neu.getTaxAmount()));
        addDiff(list, "价税合计", str(old.getInvoiceTotal()), str(neu.getInvoiceTotal()));
        return list;
    }

    private void addDiff(List<Map<String, String>> list, String field, String oldVal, String newVal) {
        String o = oldVal == null ? "" : oldVal;
        String n = newVal == null ? "" : newVal;
        if (!o.equals(n)) {
            Map<String, String> m = new HashMap<>();
            m.put("field", field);
            m.put("old", o);
            m.put("new", n);
            list.add(m);
        }
    }

    private void appendChangeLog(String batchNo, Map<String, Object> change) {
        InvoiceImportBatch batch = invoiceImportBatchService.getOne(new LambdaQueryWrapper<InvoiceImportBatch>()
                .eq(InvoiceImportBatch::getBatchNo, batchNo)
                .eq(InvoiceImportBatch::getDelFlag, 0)
                .last("LIMIT 1"));
        if (batch == null) {
            return;
        }
        List<Map<String, Object>> log = batch.getChangeLog();
        if (log == null) {
            log = new ArrayList<>();
        }
        log.add(change);
        batch.setChangeLog(log);
        batch.setUpdateTime(new Date());
        invoiceImportBatchService.updateById(batch);
    }

    private String fmtDate(Date d) {
        return d == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(d);
    }

    private String str(Object o) {
        return o == null ? "" : o.toString();
    }

    private SysUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication == null ? null : authentication.getPrincipal();
        if (!(principal instanceof SysUser)) {
            throw new RuntimeException("未获取到当前登录账户，请重新登录");
        }
        return (SysUser) principal;
    }
}