<template>
  <div class="close">
    <el-dropdown>
<span class="el-dropdown-link">
<el-icon class="el-icon--right">
<Close />
</el-icon>
关闭
</span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="closeCurrent">关闭当前</el-dropdown-item>
          <el-dropdown-item @click="closeLeft">关闭左侧</el-dropdown-item>
          <el-dropdown-item @click="closeRight">关闭右侧</el-dropdown-item>
          <el-dropdown-item @click="closeAll">关闭所有</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>
<script setup lang="ts">
import { useRoute,useRouter } from 'vue-router';
import { tabStore,type Tab } from '@/stores/tabs/index.ts';
import { Close } from '@element-plus/icons-vue'
const store = tabStore()
const route = useRoute()
const router = useRouter()
//关闭当前
const closeCurrent = () => {
  const targetName = route.path;
//首页不能关闭
  if(targetName === '/dashboard') return;
//选项卡数据
  const tabs = store.getTab;
//当前激活的选项卡数据
  let activeName = route.path;
  if (activeName === targetName) {
    tabs.forEach((tab: Tab, index: number) => {
      if (tab.path === targetName) {
        const nextTab = tabs[index + 1] || tabs[index - 1];
        if (nextTab) {
          activeName = nextTab.path;
        }
      }
    });
  }
//重新设置选项卡数据
  store.tabList = tabs.filter((tab) => tab.path !== targetName);
//跳转路由
  router.push({ path: activeName });
};
//关闭右侧
const closeRight = ()=>{
  const path = route.path;
//找到当前路由的索引
  let index = store.tabList.findIndex(item=>item.path === path);
  store.tabList.splice(index+1)
}
//关闭左侧
const closeLeft = ()=>{
  const path = route.path;
//找到当前路由的索引
  let index = store.tabList.findIndex(item=>item.path === path);
  store.tabList.splice(1,index-1)
}
//关闭所有
const closeAll = ()=>{
  store.tabList.splice(0,store.tabList.length)
  const tab:Tab = {
    path:'/dashboard',
    title:'首页'
  }
  store.addTab(tab)
//跳转首页
  router.push({path:'/dashboard'})
}
</script>
<style scoped lang="scss">
.close {
  width: 60px;
  height: 40px;
  position: fixed;
  top: 100;
  right: 0;
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
  border-left: 1px solid #e4e7ed;
  padding-right: 10px;
}
.el-dropdown-link {
  cursor: pointer;
  // color: var(--el-color-primary);
  display: flex;
  align-items: center;
}
.el-dropdown-link:focus {
  outline: none;

}
.el-dropdown-link:focus {
  outline: none;
}
</style>
