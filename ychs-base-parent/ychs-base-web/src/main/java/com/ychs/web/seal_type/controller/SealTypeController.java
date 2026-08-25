package com.ychs.web.seal_type.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ychs.utils.ResultUtils;
import com.ychs.utils.ResultVo;
import com.ychs.web.seal_type.entity.SealType;
import com.ychs.web.seal_type.entity.SealTypeParam;
import com.ychs.web.seal_type.service.SealTypeService;
import com.ychs.web.sys_user.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/sealType")
public class SealTypeController {

    @Autowired
    private SealTypeService sealTypeService;

    // 新增
    @PostMapping
    @PreAuthorize("hasAuthority('sys:sealType:add')")
    public ResultVo add(@RequestBody SealType sealType) {
        // 创建人：取当前登录账户（不能由前端指定，防止伪造）
        sealType.setCreateBy(getCurrentUser().getUsername());
        sealType.setCreateTime(new Date());
        sealType.setDelFlag((byte) 0);
        boolean flag = sealTypeService.save(sealType);
        return flag ? ResultUtils.success("新增成功") : ResultUtils.error("新增失败");
    }

    // 编辑（创建人/创建时间不允许修改，由后端维护）
    @PutMapping
    @PreAuthorize("hasAuthority('sys:sealType:edit')")
    public ResultVo update(@RequestBody SealType sealType) {
        sealType.setCreateBy(null);
        sealType.setCreateTime(null);
        sealType.setUpdateTime(new Date());
        boolean flag = sealTypeService.updateById(sealType);
        return flag ? ResultUtils.success("修改成功") : ResultUtils.error("修改失败");
    }

    // 删除（软删除）
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:sealType:delete')")
    public ResultVo remove(@PathVariable("id") Long id) {
        if (id == null) {
            return ResultUtils.error("没有传入id");
        }
        SealType sealType = new SealType();
        sealType.setId(id);
        sealType.setDelFlag((byte) 1);
        boolean flag = sealTypeService.updateById(sealType);
        return flag ? ResultUtils.success("删除成功") : ResultUtils.error("删除失败");
    }

    // 查询（分页 + 名称模糊）
    @GetMapping("/getList")
    public ResultVo getList(SealTypeParam param) {
        Page<SealType> page = new Page<>(param.getCurrentPage(), param.getPageSize());
        LambdaQueryWrapper<SealType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SealType::getDelFlag, 0);
        if (param.getName() != null && !param.getName().isEmpty()) {
            wrapper.like(SealType::getName, param.getName());
        }
        wrapper.orderByDesc(SealType::getCreateTime);
        return ResultUtils.success("查询成功", sealTypeService.page(page, wrapper));
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
