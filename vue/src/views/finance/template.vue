<template>
  <el-main>
    <el-card>
      <div style="margin-bottom:12px">
        <el-form inline>
          <el-form-item label="登记年度">
            <el-select v-model="year" placeholder="请选择年度" style="width:140px" @change="loadTemplate">
              <el-option v-for="y in yearOptions" :key="y" :label="y + ' 年'" :value="y"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button @click="genDefault">生成默认模板</el-button>
            <el-button type="primary" v-if="global.$hasPerm(['sys:finance:templateSave'])" @click="save">保存模板</el-button>
            <el-button type="success" plain @click="downloadExcel">生成Excel模板</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="cols" border size="small">
        <el-table-column label="Excel 表头文字" min-width="200">
          <template #default="scope">
            <el-input v-model="scope.row.colLabel" placeholder="与 Excel 第一行表头一致"></el-input>
          </template>
        </el-table-column>
        <el-table-column label="列键" min-width="180">
          <template #default="scope">
            <el-select v-if="scope.row.isCore == 1" v-model="scope.row.colKey" style="width:100%">
              <el-option v-for="c in CORE_KEYS" :key="c.key" :label="c.key + '（' + c.label + '）'" :value="c.key"></el-option>
            </el-select>
            <el-input v-else v-model="scope.row.colKey" placeholder="扩展列键，如 bank、payer"></el-input>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="140">
          <template #default="scope">
            <el-select v-model="scope.row.isCore" style="width:100%" @change="onTypeChange(scope.row)">
              <el-option label="核心列" :value="1"></el-option>
              <el-option label="扩展列" :value="0"></el-option>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="90">
          <template #default="scope">
            <el-button type="danger" size="small" text @click="cols.splice(scope.$index, 1)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-button style="margin-top:10px" icon="Plus" @click="addCol">添加列</el-button>
    </el-card>
  </el-main>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import useInstance from "@/hooks/useInstance";
import * as invoiceApi from "@/api/invoice/index.ts";

const { global } = useInstance();
const yearOptions = () => {
  const now = new Date().getFullYear();
  return [now - 1, now, now + 1];
};
const year = ref<number>(nowYear());
function nowYear() { return new Date().getFullYear(); }
const cols = ref<any[]>([]);

const CORE_KEYS = [
  { key: 'invoice_no', label: '发票号码' },
  { key: 'invoice_date', label: '开票日期' },
  { key: 'contract_no', label: '关联合同编号' },
  { key: 'lawyer_name', label: '律师姓名' },
  { key: 'invoice_amount', label: '开票金额' },
  { key: 'tax_amount', label: '税额' },
  { key: 'invoice_total', label: '价税合计' }
];

const loadTemplate = async () => {
  if (!year.value) return;
  const res = await invoiceApi.getTemplateApi(year.value);
  if (res && res.code == 200) {
    cols.value = (res.data || []).map((t: any) => ({ id: t.id, colLabel: t.colLabel, colKey: t.colKey, isCore: t.isCore }));
  }
};

const genDefault = () => {
  cols.value = CORE_KEYS.map(c => ({ colLabel: c.label, colKey: c.key, isCore: 1 }));
  ElMessage.success('已生成默认 7 个核心列，可再添加扩展列');
};

const addCol = () => {
  cols.value.push({ colLabel: '', colKey: '', isCore: 0 });
};

const onTypeChange = (row: any) => {
  if (row.isCore == 1) row.colKey = '';   // 切到核心列后列键重新选
};

const save = async () => {
  if (!year.value) { ElMessage.warning('请先选择年度'); return; }
  for (const c of cols.value) {
    if (!c.colLabel || !c.colLabel.trim()) { ElMessage.warning('表头文字不能为空'); return; }
    if (c.isCore == 1 && !c.colKey) { ElMessage.warning('核心列必须选择列键'); return; }
  }
  const parm = cols.value.map((c, i) => ({
    recordYear: year.value, colKey: c.colKey || c.colLabel, colLabel: c.colLabel.trim(), colOrder: i + 1, isCore: c.isCore
  }));
  const res = await invoiceApi.saveTemplateApi(parm);
  if (res && res.code == 200) { ElMessage.success(res.msg); loadTemplate(); }
};

/** 按已保存的模板生成可填写的 Excel 文件 */
const downloadExcel = async () => {
  if (!year.value) { ElMessage.warning('请先选择年度'); return; }
  if (cols.value.length === 0) { ElMessage.warning('该年度尚未配置表头，请先“生成默认模板”并“保存模板”'); return; }
  try {
    await invoiceApi.exportTemplateApi(year.value);
    ElMessage.success('Excel 模板已开始下载');
  } catch (e) {
    ElMessage.error('模板下载失败，请稍后重试');
  }
};

onMounted(() => { loadTemplate(); });
</script>

<style scoped></style>
