/*
 * 英才汇硕信息科技有限公司 拥有本软件版权 2024-2028 并保留所有权利。
 * Copyright 2024-2028, YCHS Information&Science Technology Co.,Ltd,
 * All rights reserved.
 */
package com.ychs.web.lawyerTransfer.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ychs.utils.ResultUtils;
import com.ychs.utils.ResultVo;
import com.ychs.web.lawyerTransfer.entity.ConfirmParm;
import com.ychs.web.lawyerTransfer.entity.LawyerTransfer;
import com.ychs.web.lawyerTransfer.entity.LawyerTransferParam;
import com.ychs.web.lawyerTransfer.service.LawyerTransferService;
import com.ychs.web.sys_user.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 *
 * @author fanyuyang
 * @version 1.0
 */
@RestController
@RequestMapping("/api/lawyerTransfer")
public class LawyerTransferController {
    @Autowired
    private LawyerTransferService lawyerTransferService;

    // 新增
    @PostMapping
    @PreAuthorize("hasAuthority('sys:lawyerTransfer:add')")
    public ResultVo add(@RequestBody LawyerTransfer transfer) {
        transfer.setStatus((byte) 0);
        transfer.setCreateTime(new Date());
        transfer.setDelFlag((byte) 0);
        boolean flag = lawyerTransferService.save(transfer);
        return flag ? ResultUtils.success("新增成功") : ResultUtils.error("新增失败");
    }

    // 删除
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:lawyerTransfer:delete')")
    public ResultVo remove(@PathVariable("id") Long id) {
        if (id == null) {
            return ResultUtils.error("没有传入id");
        }
        LawyerTransfer transfer = new LawyerTransfer();
        transfer.setId(id);
        transfer.setDelFlag((byte) 1);
        boolean flag = lawyerTransferService.updateById(transfer);
        return flag ? ResultUtils.success("删除成功") : ResultUtils.error("删除失败");
    }

    // 编辑
    @PutMapping
    @PreAuthorize("hasAuthority('sys:lawyerTransfer:edit')")
    public ResultVo update(@RequestBody LawyerTransfer transfer) {
        transfer.setUpdateTime(new Date());
        boolean flag = lawyerTransferService.updateById(transfer);
        return flag ? ResultUtils.success("修改成功") : ResultUtils.error("修改失败");
    }

    // 查表
    @GetMapping("/getList")
    public ResultVo getList(LawyerTransferParam param) {
        Page<LawyerTransfer> page = new Page<>(param.getCurrentPage(), param.getPageSize());
        LambdaQueryWrapper<LawyerTransfer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LawyerTransfer::getDelFlag, 0);
        if (param.getLawyerId() != null) {
            wrapper.eq(LawyerTransfer::getLawyerId, param.getLawyerId());
        }
        wrapper.orderByDesc(LawyerTransfer::getCreateTime);
        return ResultUtils.success("查询成功", lawyerTransferService.page(page, wrapper));
    }

    @PostMapping("/confirm")
    @PreAuthorize("hasAuthority('sys:lawyerTransfer:confirm')")
    public ResultVo confirm(@RequestBody ConfirmParm parm) {
        LawyerTransfer transfer = lawyerTransferService.getById(parm.getId());
        if (transfer == null) {
            return ResultUtils.error("记录不存在");
        }
        transfer.setStatus(parm.getStatus());
        // 审批人：取当前登录账户
        SysUser currentUser = getCurrentUser();
        transfer.setConfirmBy(currentUser.getUserId());
        transfer.setConfirmAccount(currentUser.getUsername());
        transfer.setConfirmTime(new Date());
        transfer.setAuditRemark(parm.getAuditRemark());
        transfer.setUpdateTime(new Date());
        boolean flag = lawyerTransferService.updateById(transfer);
        if (flag) {
            return ResultUtils.success(parm.getStatus() == 1 ? "确认成功" : "已驳回");
        }
        return ResultUtils.error("操作失败");
    }

    /**
     * 获取当前登录账户（从 Spring Security 上下文读取）
     */
    private SysUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication == null ? null : authentication.getPrincipal();
        if (!(principal instanceof SysUser)) {
            throw new RuntimeException("未获取到当前登录账户，请重新登录");
        }
        return (SysUser) principal;
    }
}
