/*
 * 英才汇硕信息科技有限公司 拥有本软件版权 2024-2028 并保留所有权利。
 * Copyright 2024-2028, YCHS Information&Science Technology Co.,Ltd,
 * All rights reserved.
 */
package com.ychs.web.lawyerArchievement.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ychs.utils.ResultUtils;
import com.ychs.utils.ResultVo;
import com.ychs.web.lawyerArchievement.entity.LawyerAchievement;
import com.ychs.web.lawyerArchievement.entity.LawyerAchievementParam;
import com.ychs.web.lawyerArchievement.service.LawyerAchievementService;
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
@RequestMapping("/api/lawyerAchievement")
public class LawyerAchievementController {
    @Autowired
    private LawyerAchievementService lawyerAchievementService;

    @PostMapping
    @PreAuthorize("hasAuthority('sys:lawyerAchievement:add')")
    public ResultVo add(@RequestBody LawyerAchievement achievement) {
        achievement.setCreateTime(new Date());
        achievement.setDelFlag((byte) 0);
        boolean flag = lawyerAchievementService.save(achievement);
        return flag ? ResultUtils.success("新增成功") : ResultUtils.error("新增失败");
    }

    @PutMapping
    @PreAuthorize("hasAuthority('sys:lawyerAchievement:edit')")
    public ResultVo update(@RequestBody LawyerAchievement achievement) {
        achievement.setUpdateTime(new Date());
        boolean flag = lawyerAchievementService.updateById(achievement);
        return flag ? ResultUtils.success("修改成功") : ResultUtils.error("修改失败");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:lawyerAchievement:delete')")
    public ResultVo remove(@PathVariable("id") Long id) {
        if (id == null) {
            return ResultUtils.error("没有传入id");
        }
        LawyerAchievement achievement = new LawyerAchievement();
        achievement.setId(id);
        achievement.setDelFlag((byte) 1);
        boolean flag = lawyerAchievementService.updateById(achievement);
        return flag ? ResultUtils.success("删除成功") : ResultUtils.error("删除失败");
    }

    @GetMapping("/getList")
    public ResultVo getList(LawyerAchievementParam param) {
        Page<LawyerAchievement> page = new Page<>(param.getCurrentPage(), param.getPageSize());
        LambdaQueryWrapper<LawyerAchievement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LawyerAchievement::getDelFlag, 0);
        if (param.getLawyerId() != null) {
            wrapper.eq(LawyerAchievement::getLawyerId, param.getLawyerId());
        }
        wrapper.orderByDesc(LawyerAchievement::getCreateTime);
        return ResultUtils.success("查询成功", lawyerAchievementService.page(page, wrapper));
    }
}
