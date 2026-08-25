<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
  <el-main>
    <el-card>
      <div class="search">
        <el-form inline>
          <el-form-item label="用章日期">
            <el-date-picker
                v-model="dateRange"
                type="daterange"
                value-format="YYYY-MM-DD"
                range-separator="至"
                start-placeholder="开始" end-placeholder="结束">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="经办人">
            <el-input v-model="parm.handlerName" placeholder="请输入" clearable></el-input>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="parm.status" placeholder="全部" clearable style="width:140px">
              <el-option label="待审批" :value="0"></el-option>
              <el-option label="已通过" :value="1"></el-option>
              <el-option label="已驳回" :value="2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="searchBtn">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div style="margin-bottom:10px">
        <el-button v-if="global.$hasPerm(['sys:seal:add'])" type="primary" icon="Plus" @click="addBtn">新增登记</el-button>
      </div>

      <el-table :data="tableList" border stripe>
        <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
        <el-table-column prop="useDate" label="用章日期"></el-table-column>
        <el-table-column prop="useReason" label="使用原因"></el-table-column>
        <el-table-column prop="copyCount" label="份数" align="center"></el-table-column>
        <el-table-column prop="sealType" label="公章种类"></el-table-column>
        <el-table-column prop="handlerName" label="经办人"></el-table-column>
        <el-table-column prop="remark" label="备注"></el-table-column>
        <el-table-column prop="status" label="审批状态" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.status == 0" type="warning">待审批</el-tag>
            <el-tag v-else-if="scope.row.status == 1" type="success">已通过</el-tag>
            <el-tag v-else type="danger">已驳回</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="registerAccount" label="登记账户" align="center"></el-table-column>
        <el-table-column prop="auditAccount" label="审批账户" align="center">
          <template #default="scope">
            <span>{{ scope.row.auditAccount || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="auditRemark" label="审批意见" min-width="120">
          <template #default="scope">
            <span v-if="scope.row.status != 0">{{ scope.row.auditRemark || '—' }}</span>
            <span v-else style="color:#999">—</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" align="center">
          <template #default="scope">
            <el-button v-if="scope.row.status == 0 && global.$hasPerm(['sys:seal:edit'])" type="primary" size="small" @click="editBtn(scope.row)">编辑</el-button>
            <el-button v-if="scope.row.status == 0 && global.$hasPerm(['sys:seal:audit'])" type="success" size="small" @click="auditBtn(scope.row, 1)">通过</el-button>
            <el-button v-if="scope.row.status == 0 && global.$hasPerm(['sys:seal:audit'])" type="danger" size="small" @click="auditBtn(scope.row, 2)">驳回</el-button>
            <el-button v-if="global.$hasPerm(['sys:seal:delete'])" type="danger" size="small" plain @click="deleteBtn(scope.row.id)">删除</el-button>
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

    <SysDialog :title="dialog.title" :width="700" :height="400"
               :visible="dialog.visible" @on-close="onClose" @on-confirm="commit">
      <template v-slot:content>
        <el-form ref="form" :model="model" label-width="100px" size="default">
          <el-form-item label="用章日期">
            <el-date-picker v-model="model.useDate" type="date" value-format="YYYY-MM-DD"></el-date-picker>
          </el-form-item>
          <el-form-item label="登记账户">
            <el-input :value="model.registerAccount" disabled></el-input>
          </el-form-item>
          <el-form-item label="使用原因"><el-input v-model="model.useReason" type="textarea"></el-input></el-form-item>
          <el-form-item label="份数"><el-input-number v-model="model.copyCount" :min="1"></el-input-number></el-form-item>
          <el-form-item label="公章种类">
            <el-select v-model="model.sealType" placeholder="请选择公章" clearable style="width:100%">
              <el-option v-for="s in sealTypeOptions" :key="s.id" :label="s.name" :value="s.name"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="经办人">
            <el-select v-model="model.handlerLawyerId" placeholder="请选择律师" @change="selectLawyer">
              <el-option v-for="l in lawyerOptions" :key="l.id" :label="l.name" :value="l.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="备注"><el-input v-model="model.remark"></el-input></el-form-item>
        </el-form>
      </template>
    </SysDialog>
  </el-main>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import SysDialog from "@/components/SysDialog.vue";
import useDialog from "@/hooks/useDialog";
import useInstance from "@/hooks/useInstance";
import * as sealApi from "@/api/seal/index.ts";
import * as sealTypeApi from "@/api/seal/type.ts";
import * as lawyerApi from "@/api/lawyer/index.ts";
import { type Lawyer } from "@/api/lawyer/Lawyer.ts";
import { userStore } from "@/stores/user/index.ts";

const { global } = useInstance();
const uStore = userStore();
const { dialog, onClose, onConfirm } = useDialog();

const parm = reactive({ currentPage: 1, pageSize: 10, total: 0, handlerName: '', useDateStart: '', useDateEnd: '', status: null as any });
const dateRange = ref<string[]>([]);
const tableList = ref([]);
const lawyerOptions = ref<Lawyer[]>([]);
// 公章管理下拉选项（公章种类）
const sealTypeOptions = ref<any[]>([]);
const model = ref<any>({});
const tags = ref('0');

const getList = async () => {
  parm.useDateStart = dateRange.value?.[0] || '';
  parm.useDateEnd = dateRange.value?.[1] || '';
  const res = await sealApi.getListApi(parm);
  if (res && res.code == 200) {
    tableList.value = res.data.records;
    parm.total = Number(res.data.total);
  }
};
const searchBtn = () => { parm.currentPage = 1; getList(); };
const pageChange = (val: number) => { parm.currentPage = val; getList(); };

const getLawyerOptions = async () => {
  const res = await lawyerApi.getListApi({ currentPage: 1, pageSize: 100 });
  if (res && res.code == 200) lawyerOptions.value = res.data.records;
};
// 加载公章管理列表，作为"公章种类"下拉选项
const getSealTypeOptions = async () => {
  const res = await sealTypeApi.getListApi({ currentPage: 1, pageSize: 100 });
  if (res && res.code == 200) sealTypeOptions.value = res.data.records;
};

const addBtn = () => {
  tags.value = '0';
  model.value = { id: '', useDate: '', useReason: '', copyCount: 1, sealType: '', handlerLawyerId: '', handlerName: '', remark: '', status: 0, registerAccount: uStore.getUserName };
  dialog.title = '新增印章登记';
  dialog.visible = true;
};
const editBtn = (row: any) => {
  tags.value = '1';
  model.value = { ...row };
  dialog.title = '编辑印章登记';
  dialog.visible = true;
};
const auditBtn = async (row: any, status: number) => {
  let remark = '审批通过';
  if (status == 2) {
    // 驳回时录入驳回意见
    const { value } = await ElMessageBox.prompt('请输入驳回意见', '驳回', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputPlaceholder: '请填写驳回原因',
      inputValidator: (val: string) => (val && val.trim() ? true : '驳回意见不能为空')
    }).catch(() => ({ value: null }));
    if (value == null) return;
    remark = value;
  }
  const res = await sealApi.auditApi({ id: row.id, status, auditRemark: remark || '' });
  if (res && res.code == 200) {
    ElMessage.success(res.msg);
    getList();
  }
};
const selectLawyer = (id: any) => {
  const lawyer = lawyerOptions.value.find((l: any) => l.id == id);
  if (lawyer) model.value.handlerName = lawyer.name;
};
const commit = async () => {
  let res;
  if (tags.value == '0') res = await sealApi.addApi(model.value);
  else res = await sealApi.editApi(model.value);
  if (res && res.code == 200) {
    ElMessage.success(res.msg);
    dialog.visible = false;
    getList();
  }
};
const deleteBtn = async (id: number) => {
  const confirm = await global.$myconfirm('确定删除该登记吗?');
  if (confirm) {
    const res = await sealApi.deleteApi(id);
    if (res && res.code == 200) { ElMessage.success(res.msg); getList(); }
  }
};

onMounted(() => { getList(); getLawyerOptions(); getSealTypeOptions(); });
</script>

<style scoped></style>
