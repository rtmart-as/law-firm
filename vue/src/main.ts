import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPersist from 'pinia-plugin-persistedstate'
//echarts
import * as echarts from 'echarts'

import myconfirm from './utils/myconfirm'
import { userStore } from './stores/user'
import App from './App.vue'
import router from './router'
// 引入elementplus
import ElementPlus from 'element-plus'
//国际化
import zhCn from 'element-plus/es/locale/lang/zh-cn'
// 引入样式
import 'element-plus/dist/index.css'
// 引入element-plus暗色模式样式（配合 html.dark 生效）
import 'element-plus/theme-chalk/dark/css-vars.css'
// 引入自定义暗色变量覆盖（柔和深蓝灰，非纯黑；顶栏配色）
import './assets/dark.css'
// 引入element-plus图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
//权限验证
import './permisson'


const app = createApp(App)
const pinia = createPinia()
pinia.use(piniaPersist)

// 修复：之前 app.use(createPinia()) 新创建一个未注册持久化插件的实例，
// 而 pinia.use(piniaPersist) 注册在另一个从未安装的实例上，导致持久化从不生效，
// 刷新页面即丢失登录态（userStore.token 无法从 sessionStorage 恢复）。
app.use(pinia)
app.use(router)
// 使用ElementPlus,使用国际化
app.use(ElementPlus,{ locale: zhCn})
// 全局挂载
app.config.globalProperties.$myconfirm = myconfirm;
// 权限判断：当前用户是否拥有权限码列表中任一权限
app.config.globalProperties.$hasPerm = (perms: string[]) => {
  const store = userStore()
  const codeList = store.getCodeList
  return perms.some(p => codeList.includes(p))
}

app.mount('#app')
// 遍历图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
// 全局挂载
app.config.globalProperties.$myconfirm = myconfirm;
app.config.globalProperties.$echarts = echarts;
