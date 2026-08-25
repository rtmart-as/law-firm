<template>
  <!-- 下拉多选框（给用户分配角色用） -->
  <!--
    ★ 设计要点（为什么这样写）：
    之前这里在 <el-option> 里又嵌套了 <el-checkbox>，并用 checkbox 的 change 事件手动往 selectedOptions 里 push/remove。
    但 el-select(multiple) 本身点击 option 就会自动往 v-model 里增删选中值，
    两套逻辑同时生效 = "点一下被加进去又被删掉"，导致选中后标签不显示、roleId 不同步、表单校验不过（无法确认）。
    所以改成：完全交给 el-select 的 v-model 统一管理选中值，再通过 watch 把变化上报给父组件。
    选中是否显示标签、增删，全部由 el-select 原生处理，不存在"双套逻辑打架"。
  -->
  <el-select
      v-model="selectedOptions"
      multiple
      clearable
      placeholder="请选择"
      style="width: 100%"
  >
    <!-- 普通选项：value 是角色 id，label 是角色名，交给 el-select 自己管选中 -->
    <el-option
        v-for="item in options"
        :key="item.value"
        :label="item.label"
        :value="item.value"
    />
    <!-- 全选 / 反选：注意用 @mousedown.prevent 阻止默认行为，避免点到下拉框之外导致面板关闭 -->
    <div class="is-all">
      <div @mousedown.prevent @click="selectAll(true)">全选</div>
      <div @mousedown.prevent @click="selectAll(false)">反选</div>
    </div>
  </el-select>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
//定义下拉的数据类型
type SelectIem = {
  value: string | number;
  label: string;
  check?: boolean;
};
//接收父组件的参数
let props = defineProps({
  options: {
    type: Array<SelectIem>,
    required: true,
  },
  width: {
    type: Number,
    default() {
      return 220;
    },
  },
  bindValue: {
    type: Array<string | number>,
    default() {
      return [];
    },
  },
});
//注册事件：选中值变化时通知父组件（父组件用它拼 roleId 字符串）
const emit = defineEmits(["selected"]);
//当前已选中的值（el-select 的 v-model，唯一的数据源）
let selectedOptions = ref<Array<string | number>>([]);

//选中值一变化 → 上报给父组件。这样新增/编辑时 roleId 永远和界面勾选同步
watch(selectedOptions, (val) => {
  emit("selected", val);
});

//全选 / 反选
const selectAll = (isAll: boolean) => {
  if (isAll) {
    //全选：把所有选项的 value 都选上
    selectedOptions.value = props.options.map((item) => item.value);
  } else {
    //反选：清空（简单实现，与原模板一致）
    selectedOptions.value = [];
  }
  emit("selected", selectedOptions.value);
};
//清空下拉的数据（新增弹框打开时调用，避免上次的选择残留）
const clear = () => {
  selectedOptions.value = [];
};
//暴露出去，给外部组件使用
defineExpose({
  clear,
});
//监听父组件传入的回显值（编辑用户时回显该用户已分配的角色）
watch(
    () => props.bindValue,
    (val) => {
      selectedOptions.value = val;
    },
    { immediate: true }
);
</script>

<style lang="scss">
.is-all {
  display: flex;
  padding-left: 10px;
  div {
    cursor: pointer;
    margin: 6px 10px;
    transition: 0.2s;
    &:hover {
      opacity: 0.7;
    }
  }
}
</style>
