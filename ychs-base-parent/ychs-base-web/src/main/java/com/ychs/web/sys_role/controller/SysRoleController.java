package com.ychs.web.sys_role.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ychs.utils.ResultUtils;
import com.ychs.utils.ResultVo;
import com.ychs.web.sys_role.entity.RoleParm;
import com.ychs.web.sys_role.entity.SaveMenuParm;
import com.ychs.web.sys_role.entity.SelectItem;
import com.ychs.web.sys_role.entity.SysRole;
import com.ychs.web.sys_role.service.SysRoleService;
import com.ychs.web.sys_role_menu.service.RoleMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag(name="角色管理")
@RequestMapping("/api/role")
@RestController
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private RoleMenuService roleMenuService;
    @Operation(summary = "新增角色")
    //新增
    @PostMapping
    public ResultVo add(@RequestBody SysRole sysRole){
        if(sysRoleService.save(sysRole)){
            return ResultUtils.success("新增成功!");
        }
        return ResultUtils.error("新增失败!");
    }
    @Operation(summary = "编辑角色")
    //编辑
    @PutMapping
    public ResultVo edit(@RequestBody SysRole sysRole){
        if(sysRoleService.updateById(sysRole)){
            return ResultUtils.success("编辑成功!");
        }
        return ResultUtils.error("编辑失败!");
    }

    @Operation(summary = "新增角色")
    //删除
    @DeleteMapping("/{roleId}")
    public ResultVo delete(@PathVariable("roleId") Long roleId){
        if(sysRoleService.removeById(roleId)){
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }

    @Operation(summary = "查询角色列表")
    //列表
    @GetMapping("/getList")
    public ResultVo getList(RoleParm parm){
        System.out.println("currentPage:"+parm.getCurrentPage());
        System.out.println("pageSize:"+parm.getPageSize());
        //构造分页对象
        IPage<SysRole> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        //构造查询条件
        QueryWrapper<SysRole> query = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(parm.getRoleName())){
            query.lambda().like(SysRole::getRoleName,parm.getRoleName());
        }
        //按照创建时间进行降序排序
        query.lambda().orderByDesc(SysRole::getCreateTime);

        IPage<SysRole> list = sysRoleService.page(page, query);
        return ResultUtils.success("查询成功",list);
    }

    @Operation(summary = "角色下拉")
    //角色下拉数据
    @GetMapping("/selectList")
    public ResultVo selectList(){
        List<SysRole> list = sysRoleService.list();
        //返回的值
        List<SelectItem> selectItems = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(item ->{
                    SelectItem vo = new SelectItem();
                    vo.setCheck(false);
                    vo.setLabel(item.getRoleName());
                    vo.setValue(item.getRoleId());
                    selectItems.add(vo);
                });
        return  ResultUtils.success("查询成功",selectItems);
    }
    //保存角色菜单
    @PostMapping("/saveRoleMenu")
    public ResultVo saveRoleMenu(@RequestBody SaveMenuParm parm){
        roleMenuService.saveRoleMenu(parm);
        return  ResultUtils.success("分配成功");
    }
}
