import http from "@/http/index.ts";
import type {MenuType} from "@/api/menu/MenuModel.ts";
//获取上级菜单
export const getParentApi = ()=>{
    return http.get("/api/sysMenu/getParent")
}
//新增角色
export const addApi = (parm:MenuType)=>{
    return http.post("/api/sysMenu",parm)
}
//列表
export const getListApi = ()=>{
    return http.get("/api/sysMenu/list")
}
//编辑
export const editApi = (parm:MenuType)=>{
    return http.put("/api/sysMenu",parm)
}
//删除
export const deleteApi = (menuId:string)=>{
    return http.delete(`/api/sysMenu/${menuId}`)
}
//查询用户菜单
export const getMenuListApi = (userId:string)=>{
    return http.get("/api/sysMenu/getMenuList",{userId:userId})
}
