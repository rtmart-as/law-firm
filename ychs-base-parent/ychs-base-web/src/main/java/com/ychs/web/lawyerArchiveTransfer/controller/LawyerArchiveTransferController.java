/*
 * 英才汇硕信息科技有限公司 拥有本软件版权 2024-2028 并保留所有权利。
 * Copyright 2024-2028, YCHS Information&Science Technology Co.,Ltd,
 * All rights reserved.
 */
package com.ychs.web.lawyerArchiveTransfer.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ychs.utils.ResultUtils;
import com.ychs.utils.ResultVo;
import com.ychs.web.lawyerArchiveTransfer.entity.LawyerArchiveTransfer;
import com.ychs.web.lawyerArchiveTransfer.entity.LawyerArchiveTransferParam;
import com.ychs.web.lawyerArchiveTransfer.service.LawyerArchiveTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 *
 * @author fanyuyang
 * @version 1.0
 */
@RestController
@RequestMapping("/api/lawyerArchiveTransfer")
public class LawyerArchiveTransferController {
    @Autowired
    private LawyerArchiveTransferService lawyerArchiveTransferService;

    @GetMapping("/getList")
    public ResultVo getList(LawyerArchiveTransferParam param) {
        Page<LawyerArchiveTransfer> page = new Page<>(param.getCurrentPage(), param.getPageSize());
        LambdaQueryWrapper<LawyerArchiveTransfer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LawyerArchiveTransfer::getDelFlag, 0);
        if (param.getLawyerId() != null) {
            wrapper.eq(LawyerArchiveTransfer::getLawyerId, param.getLawyerId());
        }
        wrapper.orderByDesc(LawyerArchiveTransfer::getCreateTime);
        return ResultUtils.success("查询成功", lawyerArchiveTransferService.page(page, wrapper));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sys:lawyerArchiveTransfer:add')")
    public ResultVo add(@RequestBody LawyerArchiveTransfer record) {
        record.setCreateTime(new Date());
        record.setDelFlag((byte) 0);
        boolean flag = lawyerArchiveTransferService.save(record);
        return flag ? ResultUtils.success("新增成功") : ResultUtils.error("新增失败");
    }

    @PutMapping
    @PreAuthorize("hasAuthority('sys:lawyerArchiveTransfer:edit')")
    public ResultVo update(@RequestBody LawyerArchiveTransfer record) {
        record.setUpdateTime(new Date());
        boolean flag = lawyerArchiveTransferService.updateById(record);
        return flag ? ResultUtils.success("修改成功") : ResultUtils.error("修改失败");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:lawyerArchiveTransfer:delete')")
    public ResultVo remove(@PathVariable("id") Long id) {
        if (id == null) {
            return ResultUtils.error("没有传入id");
        }
        LawyerArchiveTransfer record = new LawyerArchiveTransfer();
        record.setId(id);
        record.setDelFlag((byte) 1);
        boolean flag = lawyerArchiveTransferService.updateById(record);
        return flag ? ResultUtils.success("删除成功") : ResultUtils.error("删除失败");
    }
}
