<template>
  <el-main>
    <el-card>
      <div class="search">
        <el-form inline>
          <el-form-item label="公告标题">
            <el-input v-model="parm.title" placeholder="请输入" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="searchBtn">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div style="margin-bottom:10px">
        <el-button v-if="global.$hasPerm(['sys:notice:add'])" type="primary" icon="Plus" @click="addBtn">新增公告</el-button>
      </div>

      <el-table :data="tableList" border stripe>
        <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
        <el-table-column prop="title" label="公告标题" min-width="180"></el-table-column>
        <el-table-column prop="content" label="公告内容" min-width="300" show-overflow-tooltip></el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="170" align="center">
          <template #default="scope">
            <span>{{ formatTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <el-button v-if="global.$hasPerm(['sys:notice:edit'])" type="primary" size="small" @click="editBtn(scope.row)">编辑</el-button>
            <el-button v-if="global.$hasPerm(['sys:notice:delete'])" type="danger" size="small" plain @click="deleteBtn(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
          style="margin-top:10px"
          background
          layout="total,prev,pager,next,jumper"
          :total="parm.total"
          :page-size="parm.pageSize"
          :current-page="parm.currentPage"
          @current-change="pageChange">
      </el-pagination>
    </el-card>

    <SysDialog :title="dialog.title" :width="650" :height="320"
               :visible="dialog.visible" @on-close="onClose" @on-confirm="commit">
      <template v-slot:content>
        <el-form ref="form" :model="model" label-width="80px" size="default">
          <el-form-item label="公告标题">
            <el-input v-model="model.title" placeholder="请输入公告标题" maxlength="128"></el-input>
          </el-form-item>
          <el-form-item label="公告内容">
            <el-input v-model="model.content" type="textarea" :rows="6" placeholder="请输入公告内容"></el-input>
          </el-form-item>
        </el-form>
      </template>
    </SysDialog>
  </el-main>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import SysDialog from "@/components/SysDialog.vue";
import useDialog from "@/hooks/useDialog";
import useInstance from "@/hooks/useInstance";
import * as noticeApi from "@/api/notice/index.ts";

const { global } = useInstance();
const { dialog, onClose, onConfirm } = useDialog();

const parm = reactive({ currentPage: 1, pageSize: 10, total: 0, title: '' });
const tableList = ref([]);
const model = ref<any>({});
const tags = ref('0');

const getList = async () => {
  const res = await noticeApi.getListApi(parm);
  if (res && res.code == 200) {
    tableList.value = res.data.records;
    parm.total = Number(res.data.total);
  }
};
const searchBtn = () => { parm.currentPage = 1; getList(); };
const pageChange = (val: number) => { parm.currentPage = val; getList(); };

const addBtn = () => {
  tags.value = '0';
  model.value = { id: '', title: '', content: '' };
  dialog.title = '新增公告';
  dialog.visible = true;
};
const editBtn = (row: any) => {
  tags.value = '1';
  model.value = { ...row };
  dialog.title = '编辑公告';
  dialog.visible = true;
};
const commit = async () => {
  if (!model.value.title || !model.value.title.trim()) {
    ElMessage.warning('请输入公告标题');
    return;
  }
  if (!model.value.content || !model.value.content.trim()) {
    ElMessage.warning('请输入公告内容');
    return;
  }
  let res;
  if (tags.value == '0') res = await noticeApi.addApi(model.value);
  else res = await noticeApi.editApi(model.value);
  if (res && res.code == 200) {
    ElMessage.success(res.msg);
    dialog.visible = false;
    getList();
  }
};
const deleteBtn = async (id: number) => {
  const confirm = await global.$myconfirm('确定删除该公告吗?');
  if (confirm) {
    const res = await noticeApi.deleteApi(id);
    if (res && res.code == 200) { ElMessage.success(res.msg); getList(); }
  }
};
const formatTime = (time: string) => {
  if (!time) return "";
  return String(time).replace("T", " ").substring(0, 19);
};

onMounted(() => { getList(); });
</script>

<style scoped></style>
