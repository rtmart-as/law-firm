<template>
  <el-dialog style="
  padding-top: 0px;
  padding-left: 0px;
  padding-right: 0px;"
    :model-value="props.visible"
    :title="props.title"
    :width="props.width + 'px'"
    :before-close="onClose"
    append-to-body
    :close-on-click-modal="false"
  >
    <!-- 展示内容 -->
    <div :style="{ height: props.height + 'px' }">
      <slot name="content"></slot>
    </div>
    <template #footer>
    <span class="dialog-footer">
      <el-button type="danger" @click="onClose">取消</el-button>
      <el-button type="primary" @click="onConfirm">确定</el-button>
    </span>
    </template>
  </el-dialog>
</template>
<script setup lang="ts">
import {ElMessageBox} from 'element-plus'

//定义参数类型
interface DialogProps {
  title?: string;
  visible: boolean;
  width?: number;
  height?: number;
}

/**
 * 接收父组件传递的数据
 * withDefaults:设置默认值
 * defineProps：接收父组件传递的参数
 */
const props = withDefaults(defineProps<DialogProps>(), {
  title: "标题",
  visible: false,
  width: 630,
  height: 280,
});
//注册事件
const emit = defineEmits(["onClose", "onConfirm"]);
//关闭弹框
const onClose = () => {
  emit("onClose");
};
//确定弹框
const onConfirm = () => {
  emit("onConfirm");
};
</script>
<style lang="scss" scope>
.container {
  overflow-x: initial;
  overflow-y: auto;
}

.el-dialog {
  border-top-left-radius: 7px !important;
  border-top-right-radius: 7px !important;

  .el-dialog__header {
    margin-right: 0px;
    border-top-left-radius: 7px !important;
    border-top-right-radius: 7px !important;
    /* 弹窗头部：灰白风（白天浅灰 / 黑夜深灰），跟随主题 */
    background-color: var(--el-fill-color-light) !important;

    .el-dialog__title {
      padding-left: 10px;
      color: var(--el-text-color-primary);
      font-size: 16px;
      font-weight: 600;
      line-height: 35px;
    }
  }

  .el-dialog__headerbtn {
    .el-dialog__close {
      color: var(--el-text-color-secondary);
    }
  }

  .el-dialog__body {
    padding-top: 20px;
    padding-left: 10px;
    padding-right: 10px;
  }

  .el-dialog__footer {
    border-top: 1px solid #e8eaec !important;
    padding: 10px;
  }
}
</style>
