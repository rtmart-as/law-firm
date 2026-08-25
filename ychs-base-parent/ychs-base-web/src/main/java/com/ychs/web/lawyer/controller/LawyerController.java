/*
 * 英才汇硕信息科技有限公司 拥有本软件版权 2024-2028 并保留所有权利。
 * Copyright 2024-2028, YCHS Information&Science Technology Co.,Ltd,
 * All rights reserved.
 */
package com.ychs.web.lawyer.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ychs.utils.ResultUtils;
import com.ychs.utils.ResultVo;
import com.ychs.web.lawyer.entity.Lawyer;
import com.ychs.web.lawyer.entity.LawyerParam;
import com.ychs.web.lawyer.service.LawyerService;
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
@RequestMapping("/api/lawyer")
public class LawyerController {
    @Autowired
    private LawyerService lawyerService;

    // 新增
    @PostMapping
    @PreAuthorize("hasAuthority('sys:lawyer:add')")
    public ResultVo add(@RequestBody Lawyer lawyer) {
        lawyer.setCreateTime(new Date());
        lawyer.setDelFlag((byte) 0);
        boolean flag = lawyerService.save(lawyer);
        if(flag){
            return  ResultUtils.success("新增成功");
        }
        return ResultUtils.error("新增失败");
    }

    // 编辑
    @PutMapping
    @PreAuthorize("hasAuthority('sys:lawyer:edit')")
    public ResultVo update(@RequestBody Lawyer lawyer){
        lawyer.setUpdateTime(new Date());
        boolean flag = lawyerService.updateById(lawyer);
        if(flag){
            return  ResultUtils.success("修改成功");
        }
        return ResultUtils.error("修改失败");
    }

    // 删除
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:lawyer:delete')")
    public ResultVo remove(@PathVariable("id") Long id) {
        if(id == null){
            return ResultUtils.error("没有传入id");
        }
        Lawyer lawyer = new Lawyer();
        lawyer.setId(id);
        lawyer.setDelFlag((byte) 1);
        boolean flag = lawyerService.updateById(lawyer);
        if(flag){
            return ResultUtils.success("删除成功");
        }else{
            return ResultUtils.error("删除失败");
        }
    }

    // 查询
    @GetMapping("/getList")
    public ResultVo getList( LawyerParam lawyerParam){
        Page<Lawyer> page = new Page<>(lawyerParam.getCurrentPage(), lawyerParam.getPageSize());
        LambdaQueryWrapper<Lawyer> wrapper = new LambdaQueryWrapper<Lawyer>();
        wrapper.eq(Lawyer::getDelFlag,0);
        if(lawyerParam.getName() != null && !lawyerParam.getName().isEmpty()){
            wrapper.like(Lawyer::getName,lawyerParam.getName());
        }
        Page<Lawyer> data = lawyerService.page(page, wrapper);
        return ResultUtils.success("查询成功",data);
    }

    @GetMapping("/{id}")
    public ResultVo getById(@PathVariable("id") Long id) {
        return ResultUtils.success("查询成功", lawyerService.getById(id));
    }
}
