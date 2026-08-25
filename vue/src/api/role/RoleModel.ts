//定义角色数据类型
export type SysRole = {
    roleId:string;
    roleName:string;
    remark:string;
}
//列表查询的参数
export type RoleListParm = {
    currentPage:number;
    pageSize:number;
    roleName:string;
    total:number;
}
//分配菜单数据类型
export type SaveMenuParm = {
    roleId:string;
    list:Array<string>;
}
