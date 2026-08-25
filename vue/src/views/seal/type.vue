<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
  <el-main>
    <el-card>
      <div class="search">
        <el-form inline>
          <el-form-item label="公章名称">
            <el-input v-model="parm.name" placeholder="请输入" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="searchBtn">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div style="margin-bottom:10px">
        <el-button v-if="global.$hasPerm(['sys:sealType:add'])" type="primary" icon="Plus" @click="addBtn">新增公章</el-button>
      </div>

      <el-table :data="tableList" border stripe>
        <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
        <el-table-column prop="name" label="名称"></el-table-column>
        <el-table-column prop="createBy" label="创建人" align="center"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" align="center"></el-table-column>
        <el-table-column prop="remark" label="备注"></el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button v-if="global.$hasPerm(['sys:sealType:edit'])" type="primary" size="small" @click="editBtn(scope.row)">编辑</el-button>
            <el-button v-if="global.$hasPerm(['sys:sealType:delete'])" type="danger" size="small" plain @click="deleteBtn(scope.row.id)">删除</el-button>
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

    <SysDialog :title="dialog.title" :width="500" :height="300"
               :visible="dialog.visible" @on-close="onClose" @on-confirm="commit">
      <template v-slot:content>
        <el-form ref="form" :model="model" label-width="100px" size="default">
          <el-form-item label="名称">
            <el-input v-model="model.name" placeholder="请输入公章名称"></el-input>
          </el-form-item>
          <el-form-item label="创建人">
            <el-input :value="model.createBy" disabled placeholder="新增时自动记录"></el-input>
          </el-form-item>
          <el-form-item label="创建时间">
            <el-input :value="model.createTime" disabled placeholder="新增时自动记录"></el-input>
          </el-form-item>
          <el-form-item label="备注"><el-input v-model="model.remark"></el-input></el-form-item>
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
import * as sealTypeApi from "@/api/seal/type.ts";

const { global } = useInstance();
const { dialog, onClose } = useDialog();

const parm = reactive({ currentPage: 1, pageSize: 10, total: 0, name: '' });
const tableList = ref([]);
const model = ref<any>({});
const tags = ref('0');

const getList = async () => {
  const res = await sealTypeApi.getListApi(parm);
  if (res && res.code == 200) {
    tableList.value = res.data.records;
    parm.total = Number(res.data.total);
  }
};
const searchBtn = () => { parm.currentPage = 1; getList(); };
const pageChange = (val: number) => { parm.currentPage = val; getList(); };

const addBtn = () => {
  tags.value = '0';
  model.value = { id: '', name: '', createBy: '', createTime: '', remark: '' };
  dialog.title = '新增公章';
  dialog.visible = true;
};
const editBtn = (row: any) => {
  tags.value = '1';
  model.value = { ...row };
  dialog.title = '编辑公章';
  dialog.visible = true;
};
const commit = async () => {
  let res;
  if (tags.value == '0') res = await sealTypeApi.addApi(model.value);
  else res = await sealTypeApi.editApi(model.value);
  if (res && res.code == 200) {
    ElMessage.success(res.msg);
    dialog.visible = false;
    getList();
  }
};
const deleteBtn = async (id: number) => {
  const confirm = await global.$myconfirm('确定删除该公章吗?');
  if (confirm) {
    const res = await sealTypeApi.deleteApi(id);
    if (res && res.code == 200) { ElMessage.success(res.msg); getList(); }
  }
};

onMounted(() => { getList(); });
</script>

<style scoped></style>
