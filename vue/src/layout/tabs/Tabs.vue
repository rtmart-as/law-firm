<template>
  <el-tabs
      v-model="activeTab"
      type="card"
      class="demo-tabs"
      closable
      @tab-remove="removeTab"
      @tab-click="clickBtn"
  >
    <el-tab-pane
        v-for="item in tabsList"
        :key="item.path"
        :label="item.title"
        :name="item.path"
    >
    </el-tab-pane>
  </el-tabs>
</template>
<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import {type Tab, tabStore} from '@/stores/tabs/index.ts'
import { useRoute } from 'vue-router';
import { useRouter } from 'vue-router';
import type {TabsPaneContext} from 'element-plus';
const router = useRouter()
const route = useRoute()
//获取store
const store = tabStore()
//选中的选项卡数据
const activeTab = ref('')
//选项卡数据
const tabsList = computed(()=>{
  return store.getTab
})
//选项卡点击事件
const clickBtn = (pane: TabsPaneContext)=>{
  console.log(pane)
  const {props} = pane
//跳转路由
  router.push({path:props.name as string})
}
//删除
const removeTab = (targetName: string) => {
  const tabs = tabsList.value
  let activeName = activeTab.value
  if (activeName === targetName) {
    tabs.forEach((tab:Tab, index:number) => {
      if (tab.path === targetName) {
        const nextTab = tabs[index + 1] || tabs[index - 1]
        if (nextTab) {
          activeName = nextTab.path
        }
      }
    })
  }
//重新设置激活的选项卡
  activeTab.value = activeName
//重新设置选项卡数据
  store.tabList = tabs.filter((tab) => tab.path !== targetName)
//跳转路由
  router.push({path:activeName})
}
const addTab = ()=>{
  const {path,meta} = route;
  console.log(path)
  console.log(meta)
  const tab:Tab = {
    path:path,
    title:meta.title as string
  }
  store.addTab(tab)
}
//添加选项卡：监听当前路由
watch(
    ()=>route.path,
    ()=>{
      setActiveTab()
//添加选项卡数据
      addTab()
    }
)
//设置激活的选项卡
const setActiveTab = ()=>{
  activeTab.value = route.path
}
onMounted(()=>{
  setActiveTab()
  addTab()
})
</script>
<style scoped lang="scss">
:deep(.el-tabs__header) {
  margin: 0px;
}
:deep(.el-tabs__item) {
  height: 26px !important;
  line-height: 26px !important;
  text-align: center !important;
  border: 1px solid var(--el-border-color-light) !important;
  margin: 0px 3px !important;
  color: var(--el-text-color-regular);
  font-size: 12px !important;
  padding: 0xp 10px !important;
}
:deep(.el-tabs__nav) {
  border: none !important;
}
:deep(.is-active) {
  border-bottom: 1px solid transparent !important;
  border: 1px solid var(--el-color-primary) !important;
  background-color: var(--el-color-primary) !important;
  color: #fff !important;
}
:deep(.el-tabs__item:hover) {
  color: var(--el-text-color-regular) !important;
}
:deep(.is-active:hover) {
  color: #fff !important;
}
:deep(.el-tabs__nav-next){
  line-height: 26px !important;
}
:deep(.el-tabs__nav-prev){
  line-height: 26px !important;
}
</style>
