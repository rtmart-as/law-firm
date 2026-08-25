<template>
  <el-icon @click="setCollapse" style="cursor: pointer;" color="var(--app-header-text, #303133)" size="30">
    <component :is="status ? Fold : Expand"></component>
  </el-icon>
</template>
<script setup lang="ts">
import {computed} from 'vue'
import { Fold, Expand } from "@element-plus/icons-vue";
import {menuStore} from '@/stores/menu/index.ts'
//获取store
const store = menuStore()
//获取状态
const status = computed(()=>{
  return !store.getCollapse
})
//切换图标点击事件
const setCollapse = ()=>{
  store.setCollapse(!store.getCollapse)
}
</script>
<style scoped>
</style>
