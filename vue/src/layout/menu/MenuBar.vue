<template>
  <menu-logo></menu-logo>
  <el-menu
      :default-active="defaultActive"
      class="el-menu-vertical-demo"
      :collapse="isCollapse"
      @open="handleOpen"
      @close="handleClose"
      unique-opened
      router
  >
    <menu-item :menuList="menuList"></menu-item>
  </el-menu>
</template>
<script setup lang="ts">
import { ref, reactive, computed} from "vue";
import {menuStore} from '@/stores/menu/index.ts'
import { useRoute } from 'vue-router';
import MenuItem from "@/layout/menu/MenuItem.vue";
import MenuLogo from "@/layout/menu/MenuLogo.vue";
//获取store
const store = menuStore()
//获取状态
const isCollapse = computed(()=>{
  return store.getCollapse
})
const route = useRoute()
//定义响应式数据
// const isCollapse = ref(false)
//当前激活的菜单:当前激活的菜单
const defaultActive = computed(()=>{
  const {path} = route
  return path
})
const menuList = computed(()=>{
  return store.getMenu
})
const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
</script>
<style scoped lang="scss">
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 230px;
  min-height: 400px;
}
.el-menu {
  border-right: none;
  /* 背景/文字/hover/激活色由全局主题变量控制（--el-menu-*，白天浅灰 / 黑夜深藏蓝） */
}
/* 当前打开菜单的子菜单展开区域底色（随主题变量） */
:deep(.is-opened .el-menu-item){
  background-color: var(--app-menu-sub-bg) !important;
}
</style>
