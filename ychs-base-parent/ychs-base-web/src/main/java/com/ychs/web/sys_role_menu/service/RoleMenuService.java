package com.ychs.web.sys_role_menu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ychs.web.sys_role.entity.SaveMenuParm;
import com.ychs.web.sys_role_menu.entity.RoleMenu;

public interface RoleMenuService extends IService<RoleMenu> {
    void saveRoleMenu(SaveMenuParm parm);
}