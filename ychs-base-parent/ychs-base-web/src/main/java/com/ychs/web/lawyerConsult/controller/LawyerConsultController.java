package com.ychs.web.lawyerConsult.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ychs.utils.ResultUtils;
import com.ychs.utils.ResultVo;
import com.ychs.web.lawyerConsult.entity.LawyerConsult;
import com.ychs.web.lawyerConsult.entity.LawyerConsultParam;
import com.ychs.web.lawyerConsult.service.LawyerConsultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/lawyerConsult")
public class LawyerConsultController {

    @Autowired
    private LawyerConsultService lawyerConsultService;

    @GetMapping("/getList")
    public ResultVo getList(LawyerConsultParam param) {
        Page<LawyerConsult> page = new Page<>(param.getCurrentPage(), param.getPageSize());
        LambdaQueryWrapper<LawyerConsult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LawyerConsult::getDelFlag, 0);
        if (param.getLawyerId() != null) {
            wrapper.eq(LawyerConsult::getLawyerId, param.getLawyerId());
        }
        wrapper.orderByDesc(LawyerConsult::getCreateTime);
        return ResultUtils.success("查询成功", lawyerConsultService.page(page, wrapper));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sys:lawyerConsult:add')")
    public ResultVo add(@RequestBody LawyerConsult consult) {
        consult.setCreateTime(new Date());
        consult.setDelFlag((byte) 0);
        boolean flag = lawyerConsultService.save(consult);
        return flag ? ResultUtils.success("新增成功") : ResultUtils.error("新增失败");
    }

    @PutMapping
    @PreAuthorize("hasAuthority('sys:lawyerConsult:edit')")
    public ResultVo update(@RequestBody LawyerConsult consult) {
        consult.setUpdateTime(new Date());
        boolean flag = lawyerConsultService.updateById(consult);
        return flag ? ResultUtils.success("修改成功") : ResultUtils.error("修改失败");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:lawyerConsult:delete')")
    public ResultVo remove(@PathVariable("id") Long id) {
        if (id == null) {
            return ResultUtils.error("没有传入id");
        }
        LawyerConsult consult = new LawyerConsult();
        consult.setId(id);
        consult.setDelFlag((byte) 1);
        boolean flag = lawyerConsultService.updateById(consult);
        return flag ? ResultUtils.success("删除成功") : ResultUtils.error("删除失败");
    }
}