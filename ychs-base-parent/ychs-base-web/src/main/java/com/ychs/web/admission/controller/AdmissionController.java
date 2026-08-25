/*
 * 英才汇硕信息科技有限公司 拥有本软件版权 2024-2028 并保留所有权利。
 * Copyright 2024-2028, YCHS Information&Science Technology Co.,Ltd,
 * All rights reserved.
 */
package com.ychs.web.admission.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ychs.utils.ResultUtils;
import com.ychs.utils.ResultVo;
import com.ychs.web.admission.entity.AdmissionApply;
import com.ychs.web.admission.entity.AdmissionApplyParam;
import com.ychs.web.admission.entity.AdmissionAttachment;
import com.ychs.web.admission.entity.AuditParm;
import com.ychs.web.admission.service.AdmissionApplyService;
import com.ychs.web.admission.service.AdmissionAttachmentService;
import com.ychs.web.lawyer.entity.Lawyer;
import com.ychs.web.lawyer.service.LawyerService;
import com.ychs.web.sys_user.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 *
 * @author fanyuyang
 * @version 1.0
 */
@RestController
@RequestMapping("/api/admission")
public class AdmissionController {
    @Autowired
    private AdmissionApplyService admissionApplyService;
    @Autowired
    private AdmissionAttachmentService admissionAttachmentService;
    @Autowired
    private LawyerService lawyerService;

    @Value("${upload.path:upload}")
    private String uploadPath;

    // ========== 1. 模板下载（放在 上传目录/template/律师转入申请表.docx） ==========
    @GetMapping("/template/download")
    public void downloadTemplate(HttpServletResponse response) {
        try {
            String dir = System.getProperty("user.dir") + File.separator + uploadPath + File.separator + "template";
            File file = new File(dir, "律师转入申请表.docx");
            if (!file.exists()) {
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write("模板文件不存在，请联系管理员上传");
                return;
            }
            response.setContentType("application/octet-stream");
            String fileName = URLEncoder.encode("入所审批表模板.docx", StandardCharsets.UTF_8.toString());
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            ServletOutputStream out = response.getOutputStream();
            try (InputStream in = new FileInputStream(file)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========== 2. 提交申请 ==========
    @PostMapping("/apply")
    @PreAuthorize("hasAuthority('sys:admission:apply')")
    public ResultVo add(@RequestBody AdmissionApply apply) {
        apply.setStatus((byte) 0);
        apply.setCreateTime(new Date());
        apply.setDelFlag((byte) 0);
        // 申请账户：取当前登录账户（由后端从登录态读取，不能由前端指定，防止伪造申请人）
        apply.setApplyAccount(getCurrentUser().getUsername());
        boolean flag = admissionApplyService.save(apply);
        return flag ? ResultUtils.success("提交成功，等待主任确认", apply) : ResultUtils.error("提交失败");
    }

    // ========== 3. 申请列表（主任查看） ==========
    @GetMapping("/apply/getList")
    public ResultVo getList(AdmissionApplyParam param) {
        Page<AdmissionApply> page = new Page<>(param.getCurrentPage(), param.getPageSize());
        LambdaQueryWrapper<AdmissionApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdmissionApply::getDelFlag, 0);
        if (param.getApplicantName() != null && !param.getApplicantName().isEmpty()) {
            wrapper.like(AdmissionApply::getApplicantName, param.getApplicantName());
        }
        if (param.getStatus() != null) {
            wrapper.eq(AdmissionApply::getStatus, param.getStatus());
        }
        if (param.getLawyerId() != null) {
            wrapper.eq(AdmissionApply::getLawyerId, param.getLawyerId());
        }
        wrapper.orderByAsc(AdmissionApply::getStatus).orderByDesc(AdmissionApply::getCreateTime);
        return ResultUtils.success("查询成功", admissionApplyService.page(page, wrapper));
    }

    // ========== 4. 主任审批（通过则自动生成律师档案） ==========
    @PostMapping("/apply/audit")
    @PreAuthorize("hasAuthority('sys:admission:audit')")
    public ResultVo audit(@RequestBody AuditParm parm) {
        AdmissionApply apply = admissionApplyService.getById(parm.getId());
        if (apply == null) {
            return ResultUtils.error("申请不存在");
        }
        if (parm.getStatus() == 1) {
            // 通过：自动创建律师档案
            Lawyer lawyer = new Lawyer();
            lawyer.setName(apply.getApplicantName());
            lawyer.setGender(apply.getGender());
            lawyer.setIdCard(apply.getIdCard());
            lawyer.setPhone(apply.getPhone());
            lawyer.setEmail(apply.getEmail());
            lawyer.setWorkStatus((byte) 1);   // 在职
            lawyer.setCreateTime(new Date());
            lawyer.setDelFlag((byte) 0);
            lawyerService.save(lawyer);
            apply.setLawyerId(lawyer.getId());
        }
        apply.setStatus(parm.getStatus());
        // 审批人：取当前登录账户（审批人为当前账户，不再信任前端传的 auditBy）
        SysUser currentUser = getCurrentUser();
        apply.setAuditBy(currentUser.getUserId());
        apply.setAuditAccount(currentUser.getUsername());
        apply.setAuditRemark(parm.getAuditRemark());
        apply.setAuditTime(new Date());
        apply.setUpdateTime(new Date());
        boolean flag = admissionApplyService.updateById(apply);
        if (flag) {
            return ResultUtils.success(parm.getStatus() == 1 ? "审批通过，已生成律师档案" : "已驳回");
        }
        return ResultUtils.error("操作失败");
    }

    // ========== 5. 删除申请 ==========
    @DeleteMapping("/apply/{id}")
    @PreAuthorize("hasAuthority('sys:admission:delete')")
    public ResultVo remove(@PathVariable("id") Long id) {
        if (id == null) {
            return ResultUtils.error("没有传入id");
        }
        AdmissionApply apply = new AdmissionApply();
        apply.setId(id);
        apply.setDelFlag((byte) 1);
        boolean flag = admissionApplyService.updateById(apply);
        return flag ? ResultUtils.success("删除成功") : ResultUtils.error("删除失败");
    }

    // ========== 6. 附件：新增（url 由 UploadController 返回） ==========
    @PostMapping("/attachment")
    public ResultVo addAttachment(@RequestBody AdmissionAttachment attachment) {
        attachment.setCreateTime(new Date());
        attachment.setDelFlag((byte) 0);
        boolean flag = admissionAttachmentService.save(attachment);
        return flag ? ResultUtils.success("保存成功") : ResultUtils.error("保存失败");
    }

    // ========== 7. 附件：按申请查询 ==========
    @GetMapping("/attachment/list")
    public ResultVo listAttachment(Long applyId) {
        LambdaQueryWrapper<AdmissionAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdmissionAttachment::getDelFlag, 0);
        if (applyId != null) {
            wrapper.eq(AdmissionAttachment::getApplyId, applyId);
        }
        return ResultUtils.success("查询成功", admissionAttachmentService.list(wrapper));
    }

    // ========== 8. 附件：删除 ==========
    @DeleteMapping("/attachment/{id}")
    public ResultVo removeAttachment(@PathVariable("id") Long id) {
        AdmissionAttachment att = new AdmissionAttachment();
        att.setId(id);
        att.setDelFlag((byte) 1);
        boolean flag = admissionAttachmentService.updateById(att);
        return flag ? ResultUtils.success("删除成功") : ResultUtils.error("删除失败");
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
