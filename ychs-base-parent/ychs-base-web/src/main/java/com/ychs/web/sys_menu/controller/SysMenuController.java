package com.ychs.web.sys_menu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ychs.utils.ResultUtils;
import com.ychs.utils.ResultVo;
import com.ychs.web.sys_menu.entity.MakeMenuTree;
import com.ychs.web.sys_menu.entity.RouterVO;
import com.ychs.web.sys_menu.entity.SysMenu;
import com.ychs.web.sys_menu.service.SysMenuService;
import com.ychs.web.sys_user.entity.SysUser;
import com.ychs.web.sys_user.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/api/sysMenu")
@RestController
@Tag(name="菜单管理")
public class SysMenuController {
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysUserService sysUserService;
    //新增
    @Operation(summary = "新增菜单")
    @PostMapping
    public ResultVo add(@RequestBody SysMenu sysMenu){
        sysMenu.setCreateTime(new Date());
        if(sysMenuService.save(sysMenu)){
            return ResultUtils.success("新增成功!");
        }
        return ResultUtils.error("新增失败!");
    }

    //编辑
    @Operation(summary = "编辑菜单")
    @PutMapping
    public ResultVo edit(@RequestBody SysMenu sysMenu){
        sysMenu.setUpdateTime(new Date());
        if(sysMenuService.updateById(sysMenu)){
            return ResultUtils.success("编辑成功!");
        }
        return ResultUtils.error("编辑失败!");
    }

    //删除
    @Operation(summary = "删除菜单")
    @DeleteMapping("/{menuId}")
    public ResultVo delete(@PathVariable("menuId") Long menuId){
        if(sysMenuService.removeById(menuId)){
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }

    //列表
    @Operation(summary = "查询菜单列表")
    @GetMapping("/list")
    public ResultVo getList(){
        //排序
        QueryWrapper<SysMenu> query = new QueryWrapper<>();
        query.lambda().orderByAsc(SysMenu::getOrderNum);
        //查询出所有的菜单
        List<SysMenu> list = sysMenuService.list(query);
        //组装树数据
        List<SysMenu> menuList = MakeMenuTree.makeTree(list, 0L);
        return ResultUtils.success("查询成功",menuList);
    }

    //上级菜单
    @Operation(summary = "查询上一级菜单")
    @GetMapping("/getParent")
    public ResultVo getParent(){
        List<SysMenu> list = sysMenuService.getParent();
        return ResultUtils.success("查询成功",list);
    }
    //获取菜单
    @Operation(summary = "获取菜单")
    @GetMapping("/getMenuList")
    public ResultVo getMenuList(Long userId){
        //获取用户的信息
        SysUser user = sysUserService.getById(userId);
        //菜单数据
        List<SysMenu> menuList = null;
        //判断是否是超级管理员
        if(StringUtils.isNotEmpty(user.getIsAdmin()) && "1".equals(user.getIsAdmin())){
            QueryWrapper<SysMenu> query = new QueryWrapper<>();
            query.lambda().orderByAsc(SysMenu::getOrderNum);
            menuList = sysMenuService.list(query);
        }else{
            menuList = sysMenuService.getMenuByUserId(userId);
        }
        //过滤菜单数据,去掉按钮数据
        List<SysMenu> collect = Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null &&  StringUtils.isNotEmpty(item.getType()) && !item.getType().equals("2")).collect(Collectors.toList());
        //组装路由数据
        List<RouterVO> rourer = MakeMenuTree.makeRourer(collect, 0L);
        return ResultUtils.success("查询成功",rourer);

    }

}
