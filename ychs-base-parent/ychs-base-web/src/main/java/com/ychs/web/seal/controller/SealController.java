package com.ychs.web.seal.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ychs.utils.ResultUtils;
import com.ychs.utils.ResultVo;
import com.ychs.web.seal.entity.SealAuditParm;
import com.ychs.web.seal.entity.SealRegister;
import com.ychs.web.seal.entity.SealRegisterParam;
import com.ychs.web.seal.service.SealRegisterService;
import com.ychs.web.sys_user.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/seal")
public class SealController {

    @Autowired
    private SealRegisterService sealRegisterService;

    @GetMapping("/getList")
    public ResultVo getList(SealRegisterParam param) {
        Page<SealRegister> page = new Page<>(param.getCurrentPage(), param.getPageSize());
        LambdaQueryWrapper<SealRegister> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SealRegister::getDelFlag, 0);
        if (param.getUseDateStart() != null && !param.getUseDateStart().isEmpty()) {
            wrapper.ge(SealRegister::getUseDate, param.getUseDateStart());
        }
        if (param.getUseDateEnd() != null && !param.getUseDateEnd().isEmpty()) {
            wrapper.le(SealRegister::getUseDate, param.getUseDateEnd());
        }
        if (param.getHandlerName() != null && !param.getHandlerName().isEmpty()) {
            wrapper.like(SealRegister::getHandlerName, param.getHandlerName());
        }
        if (param.getStatus() != null) {
            wrapper.eq(SealRegister::getStatus, param.getStatus());
        }
        wrapper.orderByDesc(SealRegister::getUseDate);
        return ResultUtils.success("查询成功", sealRegisterService.page(page, wrapper));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sys:seal:add')")
    public ResultVo add(@RequestBody SealRegister seal) {
        // 登记账户：取当前登录账户（由后端从登录态读取，不能由前端指定，防止伪造登记人）
        seal.setStatus((byte) 0);
        seal.setRegisterAccount(getCurrentUser().getUsername());
        seal.setCreateTime(new Date());
        seal.setDelFlag((byte) 0);
        boolean flag = sealRegisterService.save(seal);
        return flag ? ResultUtils.success("提交成功，等待审批") : ResultUtils.error("新增失败");
    }

    @PutMapping
    @PreAuthorize("hasAuthority('sys:seal:edit')")
    public ResultVo update(@RequestBody SealRegister seal) {
        SealRegister exist = sealRegisterService.getById(seal.getId());
        if (exist == null) {
            return ResultUtils.error("登记不存在");
        }
        if (exist.getStatus() != null && exist.getStatus() == 1) {
            return ResultUtils.error("已通过审批，不能修改");
        }
        // 驳回后修改视为重新提交，回到待审批
        if (exist.getStatus() != null && exist.getStatus() == 2) {
            seal.setStatus((byte) 0);
        }
        seal.setUpdateTime(new Date());
        boolean flag = sealRegisterService.updateById(seal);
        return flag ? ResultUtils.success("修改成功") : ResultUtils.error("修改失败");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:seal:delete')")
    public ResultVo remove(@PathVariable("id") Long id) {
        if (id == null) {
            return ResultUtils.error("没有传入id");
        }
        SealRegister exist = sealRegisterService.getById(id);
        if (exist == null) {
            return ResultUtils.error("登记不存在");
        }
        SealRegister seal = new SealRegister();
        seal.setId(id);
        seal.setDelFlag((byte) 1);
        boolean flag = sealRegisterService.updateById(seal);
        return flag ? ResultUtils.success("删除成功") : ResultUtils.error("删除失败");
    }

    // ========== 审批（通过后才能用章） ==========
    @PostMapping("/audit")
    @PreAuthorize("hasAuthority('sys:seal:audit')")
    public ResultVo audit(@RequestBody SealAuditParm parm) {
        if (parm.getId() == null) {
            return ResultUtils.error("没有传入id");
        }
        if (parm.getStatus() == null || (parm.getStatus() != 1 && parm.getStatus() != 2)) {
            return ResultUtils.error("审批状态不正确");
        }
        SealRegister exist = sealRegisterService.getById(parm.getId());
        if (exist == null) {
            return ResultUtils.error("登记不存在");
        }
        if (exist.getStatus() != null && exist.getStatus() != 0) {
            return ResultUtils.error("该登记已审批，不能重复审批");
        }
        // 审批账户：取当前登录账户（审批人为当前账户，不信任前端传值）
        SysUser currentUser = getCurrentUser();
        exist.setStatus(parm.getStatus());
        exist.setAuditBy(currentUser.getUserId());
        exist.setAuditAccount(currentUser.getUsername());
        exist.setAuditTime(new Date());
        exist.setAuditRemark(parm.getAuditRemark());
        exist.setUpdateTime(new Date());
        boolean flag = sealRegisterService.updateById(exist);
        return flag ? ResultUtils.success(parm.getStatus() == 1 ? "审批通过" : "已驳回") : ResultUtils.error("审批失败");
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