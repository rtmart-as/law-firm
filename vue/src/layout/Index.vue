<script setup lang="ts">
import Header from "@/layout/header/Header.vue";
import MenuBar from "@/layout/menu/MenuBar.vue";
import Tabs from "./tabs/Tabs.vue";
import ColseTabs from "./tabs/CloseTabs.vue";
</script>
<template>
  <el-container class="mycontainer">
    <el-aside width="auto" class="leftmenu">
      <menu-bar></menu-bar>
    </el-aside>
    <el-container>
      <el-header class="header">
        <Header></Header>
      </el-header>
      <el-main class="main">
        <div class="tabs">
          <Tabs class="tabs-content"></Tabs>
          <ColseTabs></ColseTabs>
        </div>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>
<style lang="scss">
.mycontainer {
  height: 100%;
  .leftmenu {
    /* 侧边栏背景跟随主题：白天浅灰 / 黑夜深藏蓝 */
    background-color: var(--el-menu-bg-color);
  }
  .header {
    display: flex;
    align-items: center;
    /* 顶栏背景/分隔线跟随主题：白天白底，黑夜深蓝灰 */
    background-color: var(--app-header-bg);
    border-bottom: 1px solid var(--el-border-color-lighter);
  }
  .main {
    padding: 0px;
    /* 内容区：白天浅灰 #f2f3f5，黑夜深蓝灰，现代灰白风层次 */
    background-color: var(--el-bg-color-page);
  }
  .tabs{
    display: flex;
    justify-content: space-between;
    flex-grow: 1;
    border-bottom: 1px solid var(--el-border-color-light);
    padding-right: 75px;
    .el-tabs{
      width: 100%;
      height: 33px;
    }
    .tabs-content{
      padding-top: 8px;
      padding-left: 10px;
      padding-right: 10px;
    }
  }
}
:deep(.el-tabs--card>.el-tabs__header){
  border-bottom: none !important;
}

</style>

