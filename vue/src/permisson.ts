import router from "./router";
import { userStore } from "@/stores/user/index.ts";
import { menuStore } from "@/stores/menu/index.ts";
//定义白名单:不需要验证
const whiteList = ['/login']
router.beforeEach(async (to, from, next) => {
    const useStore = userStore()
    const mstore = menuStore()
    // console.log(to)
    console.log(from)
    // console.log(next)
    //获取token
    const token = useStore.getToken
    //判断token是否存在
    if (token) {
        //判断是否是登录或者首页来的：是 放行 不是：从服务器获取菜单数据
        if (to.path === '/login' || to.path === '/') {
            next({ path: '/' })
        } else {
            //判断权限数据是否存在
            const hasRoles = useStore.getCodeList.length > 0
            if (hasRoles) {//存在：放行
                next()
            } else {//不存在：从服务器获取
                try {
                    //获取用户信息
                    await useStore.getInfo()
                    //获取菜单数据
                    await mstore.getMenuList(router, useStore.getUserId)
                    //等待路由完全挂载
                    next({ ...to, replace: true })
                } catch (error) {
                    //清空数据
                    sessionStorage.clear()
                    //跳转登录
                    next({ path: '/login' })
                }
            }
        }
    } else {
        if (whiteList.indexOf(to.path) !== -1) { //说明在白名单，放行
            next()
        } else {//不在白名单，跳转登录
            next({ path: '/login' })
        }
    }
})
