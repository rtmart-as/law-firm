<template>
  <div class="header-container">
    <div class="left">
      <Collapse></Collapse>
      <BreadCrumb></BreadCrumb>
    </div>
    <div class="right">
      <!-- 主题切换：白天/黑夜 -->
      <span class="theme-btn" title="切换主题" @click="toggleTheme">
        <el-icon :size="18">
          <component :is="isDark ? 'Sunny' : 'Moon'" />
        </el-icon>
      </span>
      <span style="padding-right: 15px;color: var(--app-header-text, #303133);">欢迎您,{{ name }}</span>
      <LoginOut></LoginOut>
    </div>
  </div>
</template>

<script setup lang="ts">
import Collapse from "./Collapse.vue";
import BreadCrumb from "./BreadCrumb.vue";
import LoginOut from "./LoginOut.vue";
import { userStore } from "@/stores/user";
import { themeStore } from "@/stores/theme/index.ts";
import { computed,ref,nextTick } from "vue";
const store = userStore()
const name = computed(()=>{
  return store.getNickName
})
//主题切换
const tStore = themeStore()
const isDark = computed(() => tStore.getIsDark)
const toggleTheme = () => {
  tStore.toggle()
}
//头部背景色
let headerbg = ref("");
nextTick(() => {
  // 侧边栏菜单元素(id="mymenu")可能不存在（如菜单渲染失败），
  // 直接 getComputedStyle(null) 会抛 TypeError，必须判空。
  let box = document.getElementById("mymenu") as HTMLElement | null;
  if (box) {
    headerbg.value = getComputedStyle(box).getPropertyValue("--el-color-mymenu");
  }
  // document.documentElement.style.setProperty('--el-color-mymenu')
});
</script>

<style scoped lang="scss">
.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  .left{
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .right{
    display: flex;
    align-items: center;
  }
  .theme-btn{
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    margin-right: 15px;
    border-radius: 50%;
    color: var(--app-header-text, #303133);
    cursor: pointer;
    transition: background-color .2s;
    &:hover{
      background-color: var(--app-header-hover);
    }
  }
}
// :deep(.el-dropdown-link:focus){
//     outline: none;
// }
</style>
