package com.ychs.web.contract.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ychs.utils.ResultUtils;
import com.ychs.utils.ResultVo;
import com.ychs.web.contract.entity.*;
import com.ychs.web.contract.service.ContractCancelService;
import com.ychs.web.contract.service.ContractChangeService;
import com.ychs.web.contract.service.ContractRecoverService;
import com.ychs.web.contract.service.ContractService;
import com.ychs.web.invoice.entity.InvoiceRecord;
import com.ychs.web.invoice.service.InvoiceRecordService;
import com.ychs.web.sys_user.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/contract")
public class ContractController {

    @Autowired
    private ContractService contractService;
    @Autowired
    private ContractChangeService contractChangeService;
    @Autowired
    private ContractCancelService contractCancelService;
    @Autowired
    private ContractRecoverService contractRecoverService;
    @Autowired
    private InvoiceRecordService invoiceRecordService;

    // ========== 合同 CRUD ==========

    @GetMapping("/getList")
    public ResultVo getList(ContractParam param) {
        Page<Contract> page = new Page<>(param.getCurrentPage(), param.getPageSize());
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contract::getDelFlag, 0);
        if (param.getContractNo() != null && !param.getContractNo().isEmpty()) {
            wrapper.like(Contract::getContractNo, param.getContractNo());
        }
        if (param.getCaseType() != null && !param.getCaseType().isEmpty()) {
            wrapper.eq(Contract::getCaseType, param.getCaseType());
        }
        if (param.getLawyerName() != null && !param.getLawyerName().isEmpty()) {
            wrapper.like(Contract::getLawyerName, param.getLawyerName());
        }
        if (param.getStatus() != null) {
            wrapper.eq(Contract::getStatus, param.getStatus());
        }
        if (param.getStartDate() != null && !param.getStartDate().isEmpty()) {
            wrapper.ge(Contract::getReceiveDate, param.getStartDate());
        }
        if (param.getEndDate() != null && !param.getEndDate().isEmpty()) {
            wrapper.le(Contract::getReceiveDate, param.getEndDate());
        }
        wrapper.orderByDesc(Contract::getReceiveDate);
        return ResultUtils.success("查询成功", contractService.page(page, wrapper));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sys:contract:add')")
    public ResultVo add(@RequestBody Contract contract) {
        long count = contractService.count(new LambdaQueryWrapper<Contract>()
                .eq(Contract::getContractNo, contract.getContractNo()).eq(Contract::getDelFlag, 0));
        if (count > 0) {
            return ResultUtils.error("合同编号已存在");
        }
        contract.setStatus((byte) 1);   // 默认正常
        if (contract.getInvoiceFlag() == null) {
            contract.setInvoiceFlag((byte) 0);
        }
        if (contract.getReceiptFlag() == null) {
            contract.setReceiptFlag((byte) 0);
        }
        if (contract.getIsReturned() == null) {
            contract.setIsReturned((byte) 0);
        }
        contract.setCreateTime(new Date());
        contract.setDelFlag((byte) 0);
        boolean flag = contractService.save(contract);
        return flag ? ResultUtils.success("新增成功") : ResultUtils.error("新增失败");
    }

    @PutMapping
    @PreAuthorize("hasAuthority('sys:contract:edit')")
    public ResultVo update(@RequestBody Contract contract) {
        Contract exist = contractService.getById(contract.getId());
        if (exist == null) {
            return ResultUtils.error("合同不存在");
        }
        if (exist.getStatus() != null && exist.getStatus() == 2) {
            return ResultUtils.error("合同已解除，不能修改");
        }
        contract.setStatus(exist.getStatus());   // 状态由 变更/解除/收回 操作维护，编辑不改变
        contract.setUpdateTime(new Date());
        boolean flag = contractService.updateById(contract);
        return flag ? ResultUtils.success("修改成功") : ResultUtils.error("修改失败");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:contract:delete')")
    public ResultVo remove(@PathVariable("id") Long id) {
        if (id == null) {
            return ResultUtils.error("没有传入id");
        }
        Contract contract = new Contract();
        contract.setId(id);
        contract.setDelFlag((byte) 1);
        boolean flag = contractService.updateById(contract);
        return flag ? ResultUtils.success("删除成功") : ResultUtils.error("删除失败");
    }

    // ========== 变更 / 解除 / 收回 ==========

    @PostMapping("/change")
    @PreAuthorize("hasAuthority('sys:contract:change')")
    public ResultVo change(@RequestBody ContractChangeParm parm) {
        if (parm.getContractId() == null) {
            return ResultUtils.error("没有传入合同id");
        }
        if (parm.getChangeAmount() == null || parm.getChangeAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResultUtils.error("变更金额必须大于0");
        }
        Contract exist = contractService.getById(parm.getContractId());
        if (exist == null) {
            return ResultUtils.error("合同不存在");
        }
        if (exist.getStatus() != null && exist.getStatus() == 2) {
            return ResultUtils.error("合同已解除，不能变更");
        }
        ContractChange change = new ContractChange();
        change.setContractId(exist.getId());
        change.setChangeType(parm.getChangeType() == null ? (byte) 1 : parm.getChangeType().byteValue());
        change.setChangeAmount(parm.getChangeAmount());
        change.setChangeDate(parseDate(parm.getChangeDate()));
        change.setReasonFileUrl(parm.getReasonFileUrl());
        change.setOperator(getCurrentUser().getUserId());
        change.setCreateTime(new Date());
        change.setDelFlag((byte) 0);
        boolean flag = contractChangeService.save(change);
        if (flag) {
            // 追加律师费 => 合同金额增加；退律师费 => 合同金额减少
            BigDecimal base = exist.getContractAmount() == null ? BigDecimal.ZERO : exist.getContractAmount();
            BigDecimal after = parm.getChangeType() != null && parm.getChangeType() == 2
                    ? base.subtract(parm.getChangeAmount()) : base.add(parm.getChangeAmount());
            if (after.compareTo(BigDecimal.ZERO) < 0) {
                after = BigDecimal.ZERO;
            }
            exist.setContractAmount(after);
            exist.setStatus((byte) 3);   // 变更标记
            exist.setUpdateTime(new Date());
            contractService.updateById(exist);
            return ResultUtils.success("变更成功");
        }
        return ResultUtils.error("变更失败");
    }

    @PostMapping("/cancel")
    @PreAuthorize("hasAuthority('sys:contract:cancel')")
    public ResultVo cancel(@RequestBody ContractCancelParm parm) {
        if (parm.getContractId() == null) {
            return ResultUtils.error("没有传入合同id");
        }
        Contract exist = contractService.getById(parm.getContractId());
        if (exist == null) {
            return ResultUtils.error("合同不存在");
        }
        if (exist.getStatus() != null && exist.getStatus() == 2) {
            return ResultUtils.error("合同已解除，不能重复解除");
        }
        ContractCancel cancel = new ContractCancel();
        cancel.setContractId(exist.getId());
        cancel.setCancelReason(parm.getCancelReason());
        cancel.setFileUrl(parm.getFileUrl());
        cancel.setAmountAfter(BigDecimal.ZERO);   // 完全解除
        cancel.setCancelDate(parseDate(parm.getCancelDate()));
        cancel.setOperator(getCurrentUser().getUserId());
        cancel.setCreateTime(new Date());
        cancel.setDelFlag((byte) 0);
        boolean flag = contractCancelService.save(cancel);
        if (flag) {
            // 完全解除：所有金额置 0（not_empty 策略下必须显式传非 null 的 ZERO）
            exist.setContractAmount(BigDecimal.ZERO);
            exist.setReceiptAmount(BigDecimal.ZERO);
            exist.setInvoiceAmount(BigDecimal.ZERO);
            exist.setManageFee(BigDecimal.ZERO);
            exist.setAcceptAmount(BigDecimal.ZERO);
            exist.setStatus((byte) 2);
            exist.setUpdateTime(new Date());
            contractService.updateById(exist);
            return ResultUtils.success("解除成功");
        }
        return ResultUtils.error("解除失败");
    }

    @PostMapping("/recover")
    @PreAuthorize("hasAuthority('sys:contract:recover')")
    public ResultVo recover(@RequestBody ContractRecoverParm parm) {
        if (parm.getContractId() == null) {
            return ResultUtils.error("没有传入合同id");
        }
        Contract exist = contractService.getById(parm.getContractId());
        if (exist == null) {
            return ResultUtils.error("合同不存在");
        }
        if (exist.getStatus() != null && (exist.getStatus() == 2 || exist.getStatus() == 4)) {
            return ResultUtils.error("该合同已解除/已收回，不能再收回");
        }
        ContractRecover recover = new ContractRecover();
        recover.setContractId(exist.getId());
        recover.setRecoverDate(parseDate(parm.getRecoverDate()));
        recover.setFileUrl(parm.getFileUrl());
        recover.setOperator(getCurrentUser().getUserId());
        recover.setCreateTime(new Date());
        recover.setDelFlag((byte) 0);
        boolean flag = contractRecoverService.save(recover);
        if (flag) {
            exist.setIsReturned((byte) 1);
            exist.setStatus((byte) 4);
            exist.setUpdateTime(new Date());
            contractService.updateById(exist);
            return ResultUtils.success("收回成功");
        }
        return ResultUtils.error("收回失败");
    }

    // ========== 7 类统计 ==========
    // 时间参数 startDate/endDate 形如 2025-01-01，可为空（空则不限时间）

    /** ① 管理费统计（按案件类型 × 月） */
    @GetMapping("/statistics/manageFee")
    @PreAuthorize("hasAuthority('sys:contract:statistics')")
    public ResultVo statisticsManageFee(@RequestParam(required = false) String startDate,
                                        @RequestParam(required = false) String endDate) {
        QueryWrapper<Contract> qw = new QueryWrapper<>();
        qw.select("case_type, DATE_FORMAT(receive_date,'%Y-%m') AS month, "
                + "SUM(manage_fee) AS total_manage_fee, COUNT(*) AS case_count");
        qw.eq("del_flag", 0);
        between(qw, "receive_date", startDate, endDate);
        qw.groupBy("case_type", "DATE_FORMAT(receive_date,'%Y-%m')");
        qw.orderByAsc("case_type").orderByAsc("month");
        return ResultUtils.success("查询成功", contractService.listMaps(qw));
    }

    /** ② 管理费个人统计（每位律师每月） */
    @GetMapping("/statistics/manageFeeByLawyer")
    @PreAuthorize("hasAuthority('sys:contract:statistics')")
    public ResultVo statisticsManageFeeByLawyer(@RequestParam(required = false) String startDate,
                                                @RequestParam(required = false) String endDate) {
        QueryWrapper<Contract> qw = new QueryWrapper<>();
        qw.select("lawyer_id, lawyer_name, DATE_FORMAT(receive_date,'%Y-%m') AS month, "
                + "SUM(manage_fee) AS total_manage_fee");
        qw.eq("del_flag", 0);
        between(qw, "receive_date", startDate, endDate);
        qw.groupBy("lawyer_id", "lawyer_name", "DATE_FORMAT(receive_date,'%Y-%m')");
        qw.orderByAsc("lawyer_id").orderByAsc("month");
        return ResultUtils.success("查询成功", contractService.listMaps(qw));
    }

    /** ③ 应收案件统计（未开票 invoice_flag=0） */
    @GetMapping("/statistics/receivable")
    @PreAuthorize("hasAuthority('sys:contract:statistics')")
    public ResultVo statisticsReceivable(@RequestParam(required = false) String startDate,
                                         @RequestParam(required = false) String endDate) {
        QueryWrapper<Contract> qw = new QueryWrapper<>();
        qw.select("case_type, DATE_FORMAT(receive_date,'%Y-%m') AS month, "
                + "SUM(receipt_amount) AS total_receipt_amount, COUNT(*) AS case_count");
        qw.eq("del_flag", 0).eq("invoice_flag", 0);
        between(qw, "receive_date", startDate, endDate);
        qw.groupBy("case_type", "DATE_FORMAT(receive_date,'%Y-%m')");
        qw.orderByAsc("case_type").orderByAsc("month");
        return ResultUtils.success("查询成功", contractService.listMaps(qw));
    }

    /** ④ 案件汇总统计（收据+发票，分类别按月） */
    @GetMapping("/statistics/summary")
    @PreAuthorize("hasAuthority('sys:contract:statistics')")
    public ResultVo statisticsSummary(@RequestParam(required = false) String startDate,
                                      @RequestParam(required = false) String endDate) {
        QueryWrapper<Contract> qw = new QueryWrapper<>();
        qw.select("case_type, DATE_FORMAT(receive_date,'%Y-%m') AS month, "
                + "SUM(contract_amount) AS total_amount, COUNT(*) AS case_count");
        qw.eq("del_flag", 0);
        between(qw, "receive_date", startDate, endDate);
        qw.groupBy("case_type", "DATE_FORMAT(receive_date,'%Y-%m')");
        qw.orderByAsc("case_type").orderByAsc("month");
        return ResultUtils.success("查询成功", contractService.listMaps(qw));
    }

    /** ⑤ 不开票案件统计（invoice_flag=0） */
    @GetMapping("/statistics/noInvoice")
    @PreAuthorize("hasAuthority('sys:contract:statistics')")
    public ResultVo statisticsNoInvoice(@RequestParam(required = false) String startDate,
                                        @RequestParam(required = false) String endDate) {
        QueryWrapper<Contract> qw = new QueryWrapper<>();
        qw.select("case_type, DATE_FORMAT(receive_date,'%Y-%m') AS month, "
                + "SUM(contract_amount) AS total_amount, COUNT(*) AS case_count");
        qw.eq("del_flag", 0).eq("invoice_flag", 0);
        between(qw, "receive_date", startDate, endDate);
        qw.groupBy("case_type", "DATE_FORMAT(receive_date,'%Y-%m')");
        qw.orderByAsc("case_type").orderByAsc("month");
        return ResultUtils.success("查询成功", contractService.listMaps(qw));
    }

    /** ⑥ 发票个人明细（按月按律师 + 全年每位律师 + 每月全所） */
    @GetMapping("/statistics/invoiceDetail")
    @PreAuthorize("hasAuthority('sys:contract:statistics')")
    public ResultVo statisticsInvoiceDetail(@RequestParam(required = false) String startDate,
                                            @RequestParam(required = false) String endDate) {
        // 按月按律师
        QueryWrapper<InvoiceRecord> qw1 = new QueryWrapper<>();
        qw1.select("lawyer_id, lawyer_name, DATE_FORMAT(invoice_date,'%Y-%m') AS month, "
                + "SUM(invoice_total) AS month_total");
        qw1.eq("del_flag", 0);
        between(qw1, "invoice_date", startDate, endDate);
        qw1.groupBy("lawyer_id", "lawyer_name", "DATE_FORMAT(invoice_date,'%Y-%m')");
        qw1.orderByAsc("lawyer_id").orderByAsc("month");
        // 全年每位律师合计
        QueryWrapper<InvoiceRecord> qw2 = new QueryWrapper<>();
        qw2.select("lawyer_id, lawyer_name, YEAR(invoice_date) AS year, "
                + "SUM(invoice_total) AS year_total");
        qw2.eq("del_flag", 0);
        qw2.groupBy("lawyer_id", "lawyer_name", "YEAR(invoice_date)");
        qw2.orderByAsc("lawyer_id").orderByAsc("year");
        // 每月全所总额
        QueryWrapper<InvoiceRecord> qw3 = new QueryWrapper<>();
        qw3.select("DATE_FORMAT(invoice_date,'%Y-%m') AS month, SUM(invoice_total) AS month_total");
        qw3.eq("del_flag", 0);
        qw3.groupBy("DATE_FORMAT(invoice_date,'%Y-%m')");
        qw3.orderByAsc("month");
        Map<String, Object> data = new HashMap<>();
        data.put("monthly", invoiceRecordService.listMaps(qw1));
        data.put("yearTotal", invoiceRecordService.listMaps(qw2));
        data.put("officeMonthly", invoiceRecordService.listMaps(qw3));
        return ResultUtils.success("查询成功", data);
    }

    /** ⑦ 年度收案统计（每位律师每月金额，年度金额=当年各月之和） */
    @GetMapping("/statistics/yearSummary")
    @PreAuthorize("hasAuthority('sys:contract:statistics')")
    public ResultVo statisticsYearSummary() {
        QueryWrapper<Contract> qw = new QueryWrapper<>();
        qw.select("lawyer_id, lawyer_name, YEAR(receive_date) AS year, "
                + "DATE_FORMAT(receive_date,'%m') AS month, SUM(contract_amount) AS total_amount");
        qw.eq("del_flag", 0);
        qw.groupBy("lawyer_id", "lawyer_name", "YEAR(receive_date)", "DATE_FORMAT(receive_date,'%m')");
        qw.orderByAsc("year").orderByAsc("lawyer_id").orderByAsc("month");
        return ResultUtils.success("查询成功", contractService.listMaps(qw));
    }

    // ========== 工具方法 ==========

    private void between(QueryWrapper<?> qw, String column, String startDate, String endDate) {
        if (startDate != null && !startDate.isEmpty()) {
            qw.ge(column, startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            qw.le(column, endDate);
        }
    }

    private Date parseDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (Exception e) {
            return null;
        }
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