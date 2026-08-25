/*
 * 英才汇硕信息科技有限公司 拥有本软件版权 2024-2028 并保留所有权利。
 * Copyright 2024-2028, YCHS Information&Science Technology Co.,Ltd,
 * All rights reserved.
 */
package com.ychs.web.lawyerCert.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ychs.utils.ResultUtils;
import com.ychs.utils.ResultVo;
import com.ychs.web.lawyerCert.entity.LawyerCert;
import com.ychs.web.lawyerCert.entity.LawyerCertParam;
import com.ychs.web.lawyerCert.service.LawyerCertService;
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
@RequestMapping("/api/lawyerCert")
public class LawyerCertController {

    @Autowired
    private LawyerCertService lawyerCertService;

    @GetMapping("/getList")
    public ResultVo getList(LawyerCertParam param) {
        Page<LawyerCert> page = new Page<>(param.getCurrentPage(), param.getPageSize());
        LambdaQueryWrapper<LawyerCert> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LawyerCert::getDelFlag, 0);
        if (param.getLawyerId() != null) {
            wrapper.eq(LawyerCert::getLawyerId, param.getLawyerId());
        }
        wrapper.orderByDesc(LawyerCert::getCreateTime);
        return ResultUtils.success("查询成功", lawyerCertService.page(page, wrapper));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sys:lawyerCert:add')")
    public ResultVo add(@RequestBody LawyerCert cert) {
        cert.setCreateTime(new Date());
        cert.setDelFlag((byte) 0);
        boolean flag = lawyerCertService.save(cert);
        return flag ? ResultUtils.success("新增成功") : ResultUtils.error("新增失败");
    }

    @PutMapping
    @PreAuthorize("hasAuthority('sys:lawyerCert:edit')")
    public ResultVo update(@RequestBody LawyerCert cert) {
        cert.setUpdateTime(new Date());
        boolean flag = lawyerCertService.updateById(cert);
        return flag ? ResultUtils.success("修改成功") : ResultUtils.error("修改失败");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:lawyerCert:delete')")
    public ResultVo remove(@PathVariable("id") Long id) {
        if (id == null) {
            return ResultUtils.error("没有传入id");
        }
        LawyerCert cert = new LawyerCert();
        cert.setId(id);
        cert.setDelFlag((byte) 1);
        boolean flag = lawyerCertService.updateById(cert);
        return flag ? ResultUtils.success("删除成功") : ResultUtils.error("删除失败");
    }
}
