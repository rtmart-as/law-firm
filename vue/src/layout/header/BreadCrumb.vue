<template>
  <el-breadcrumb class="bred" separator="/">
    <el-breadcrumb-item v-for="item in tabs">{{ item.meta.title }}</el-breadcrumb-item>
  </el-breadcrumb>
</template>
<script setup lang="ts">
import { ref, watch, type Ref } from "vue"
import { useRoute} from "vue-router"
import {type RouteLocationMatched} from 'vue-router'
const route = useRoute()
//定义面包屑导航数据
const tabs : Ref<RouteLocationMatched[]> = ref([])
//获取面包屑数据
const getBredcrumb = ()=>{
  let mached = route.matched.filter((item)=>item.meta && item.meta.title)
//获取第一个数据
  const first = mached[0]
//判断是否是首页，不是，自己构造
  if (!first || first.path !== "/dashboard") {
    mached = [{ path: "/dashboard", meta: { title: "首页" } } as any].concat(
        mached
    );
  }
  tabs.value = mached;
}
getBredcrumb()
//监听当前路由
watch(
    ()=>route.path,
    ()=>getBredcrumb()
)
</script>
<style scoped lang="scss">
.bred{
  margin-left: 20px;
}
//修改字体颜色（跟随顶栏文字色，兜底深灰）
:deep(.el-breadcrumb__inner) {
  color: var(--app-header-text, #303133) !important;
}
:deep(.el-breadcrumb__inner a) {
  color: var(--app-header-text, #303133) !important;
}
// 修改字体大小
// :deep(.el-breadcrumb__item) {
// font-size: 16px !important;
// }
</style>
