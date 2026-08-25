<script setup lang="ts">
defineProps(["menuList"]);
</script>
<template>
  <template v-for="menu in menuList" :key="menu.path">
    <!-- 有下级菜单-->
    <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="menu.path">
      <template #title>
        <el-icon>
          <!-- 动态组件的方式生成图标 -->
          <component :is="menu.meta.icon"></component>
        </el-icon>
        <span>{{ menu.meta.title }}</span>
      </template>
      <!-- 生成下级： 递归调用，自己调用自己-->
      <menu-item :menuList="menu.children"></menu-item>
    </el-sub-menu>
    <!-- 没有下级菜单（文字色由 --el-menu-text-color 控制：白天深灰 / 黑夜浅色） -->
    <el-menu-item v-else :index="menu.path">
      <el-icon>
        <!-- 动态组件的方式生成图标 -->
        <component :is="menu.meta.icon"></component>
      </el-icon>
      <template #title>{{ menu.meta.title }}</template>
    </el-menu-item>
  </template>
</template>
<style scoped></style>
