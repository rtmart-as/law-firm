<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import { themeStore } from '@/stores/theme/index.ts'
// 应用持久化的主题（刷新/重新打开后保持）
themeStore().applyTheme()
</script>
<template>
  <RouterView />
</template>
<style lang="scss">
</style>
