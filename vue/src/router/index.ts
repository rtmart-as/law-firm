import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/Index.vue'
//动态生成
export const constantRoutes: Array<RouteRecordRaw> = [
  {
    path: "/login",
    component: () => import('@/views/login/login.vue'),
    name: "login"
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        component: () => import('@/views/dashboard/Index.vue'),
        name: 'dashboard',
        meta: {
          title: '首页',
          icon: 'House'
        }
      },
      // 完善档案：挂在 Layout 子路由下，渲染在 center 区域的 router-view 中
      // （带侧边栏/页头/标签页），而不是脱离布局的独立全屏页。
      // 用路径参数 /lawyer/detail/:id：每个律师独立 path，标签页按 path 去重，
      // 从而支持"多开"——可同时打开多个律师的完善档案标签。
      {
        path: '/lawyer/detail/:id',
        component: () => import('@/views/lawyer/detail.vue'),
        name: 'lawyerDetail',
        meta: { title: '完善档案' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes:constantRoutes
})

export default router
