package com.ychs.web.notice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ychs.utils.ResultUtils;
import com.ychs.utils.ResultVo;
import com.ychs.web.notice.entity.SysNotice;
import com.ychs.web.notice.entity.SysNoticeParam;
import com.ychs.web.notice.service.SysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Autowired
    private SysNoticeService sysNoticeService;

    //公告分页列表（首页公告展示走 /api/home/getNoticeList）
    @GetMapping("/getList")
    public ResultVo getList(SysNoticeParam param) {
        Page<SysNotice> page = new Page<>(param.getCurrentPage(), param.getPageSize());
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysNotice::getDelFlag, 0);
        if (param.getTitle() != null && !param.getTitle().isEmpty()) {
            wrapper.like(SysNotice::getTitle, param.getTitle());
        }
        wrapper.orderByDesc(SysNotice::getCreateTime);
        return ResultUtils.success("查询成功", sysNoticeService.page(page, wrapper));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sys:notice:add')")
    public ResultVo add(@RequestBody SysNotice notice) {
        if (notice.getTitle() == null || notice.getTitle().isEmpty()) {
            return ResultUtils.error("公告标题不能为空");
        }
        if (notice.getContent() == null || notice.getContent().isEmpty()) {
            return ResultUtils.error("公告内容不能为空");
        }
        notice.setCreateTime(new Date());
        notice.setDelFlag((byte) 0);
        boolean flag = sysNoticeService.save(notice);
        return flag ? ResultUtils.success("新增成功") : ResultUtils.error("新增失败");
    }

    @PutMapping
    @PreAuthorize("hasAuthority('sys:notice:edit')")
    public ResultVo update(@RequestBody SysNotice notice) {
        if (notice.getId() == null) {
            return ResultUtils.error("没有传入id");
        }
        if (notice.getTitle() == null || notice.getTitle().isEmpty()) {
            return ResultUtils.error("公告标题不能为空");
        }
        if (notice.getContent() == null || notice.getContent().isEmpty()) {
            return ResultUtils.error("公告内容不能为空");
        }
        notice.setUpdateTime(new Date());
        boolean flag = sysNoticeService.updateById(notice);
        return flag ? ResultUtils.success("修改成功") : ResultUtils.error("修改失败");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:notice:delete')")
    public ResultVo remove(@PathVariable("id") Long id) {
        if (id == null) {
            return ResultUtils.error("没有传入id");
        }
        SysNotice notice = new SysNotice();
        notice.setId(id);
        notice.setDelFlag((byte) 1);
        boolean flag = sysNoticeService.updateById(notice);
        return flag ? ResultUtils.success("删除成功") : ResultUtils.error("删除失败");
    }
}
