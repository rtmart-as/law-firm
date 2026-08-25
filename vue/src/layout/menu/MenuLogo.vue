<template>
  <div class="logo">
    <img :src="MenuLogo" />
    <span v-if="show" class="logo-title">{{ title }}</span>
  </div>
</template>
<script setup lang="ts">
import {ref, watch} from 'vue'
import MenuLogo from '@/assets/logo/MenuLogo.jpg'
import {menuStore} from '@/stores/menu/index.ts'
//获取store
const store = menuStore()
const title = ref('东奥律所管理系统')
//解决伸缩僵硬问题：延时
const show = ref(true)
watch(
  ()=>store.getCollapse,
  (collapsed:boolean)=>{
    if(!collapsed){
      setTimeout(() => {
        show.value = !collapsed;
      }, 300);
    }else{
      show.value = !collapsed;
    }
  }
)
</script>
<style scoped lang="scss">
.logo {
  display: flex;
  width: 100%;
  height: 60px;
  background-color: var(--app-logo-bg);
  text-align: center;
  cursor: pointer;
  align-items: center;
  img{
    width: 50px;
    height: 50px;
    margin-right: 5px;
    margin-left: 5px;
  }
  .logo-title{
    color: var(--app-logo-text);
    font-weight: 800;
    line-height: 60px;
    font-size: 22px;
    font-family: FangSong;
  }
}
</style>
