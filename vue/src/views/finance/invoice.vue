<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
  <el-main>
    <el-card>
      <div style="margin-bottom:12px">
        <el-form inline>
          <el-form-item label="登记年度">
            <el-select v-model="parm.recordYear" placeholder="请选择年度" style="width:140px" @change="searchBtn">
              <el-option v-for="y in yearOptions" :key="y" :label="y + ' 年'" :value="y"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="发票号码">
            <el-input v-model="parm.invoiceNo" placeholder="模糊查询" clearable></el-input>
          </el-form-item>
          <el-form-item label="律师">
            <el-input v-model="parm.lawyerName" placeholder="模糊查询" clearable></el-input>
          </el-form-item>
          <el-form-item label="合同编号">
            <el-input v-model="parm.contractNo" placeholder="模糊查询" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="searchBtn">查询</el-button>
          </el-form-item>
          <el-form-item v-if="global.$hasPerm(['sys:finance:import'])">
            <input type="file" accept=".xlsx,.xls" ref="fileRef" style="display:none" @change="onImport" />
            <el-button type="success" icon="Upload" :loading="importing" @click="fileRef?.click()">导入 Excel</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableList" border stripe size="small">
        <el-table-column type="index" label="#" width="50" align="center"></el-table-column>
        <el-table-column v-for="col in tableCols" :key="col.prop" :label="col.label" :prop="col.prop" :align="col.align" :width="col.width"></el-table-column>
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="scope">
            <el-button v-if="global.$hasPerm(['sys:finance:edit'])" type="primary" size="small" @click="editBtn(scope.row)">编辑</el-button>
            <el-button v-if="global.$hasPerm(['sys:finance:file'])" type="success" size="small" @click="fileBtn(scope.row)">原件</el-button>
            <el-button v-if="global.$hasPerm(['sys:finance:edit']) && scope.row.batchNo" type="info" size="small" @click="diffBtn(scope.row)">改动</el-button>
            <el-button v-if="global.$hasPerm(['sys:finance:edit'])" type="danger" size="small" plain @click="deleteBtn(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:10px" background layout="total,prev,pager,next,jumper"
                     :total="parm.total" :page-size="parm.pageSize" :current-page="parm.currentPage"
                     @current-change="pageChange"></el-pagination>
    </el-card>

    <!-- 导入结果（成功/失败明细） -->
    <el-dialog v-model="importDialog.visible" title="导入结果" width="640" :close-on-click-modal="false">
      <el-alert :title="'成功 ' + importResult.successCount + ' 条，失败 ' + importResult.failCount + ' 条'" :type="importResult.failCount > 0 ? 'warning' : 'success'" :closable="false"></el-alert>
      <el-table v-if="importResult.failCount > 0" :data="importResult.failDetail" border size="small" style="margin-top:10px">
        <el-table-column prop="row" label="行号" width="70" align="center"></el-table-column>
        <el-table-column prop="error" label="失败原因" min-width="200"></el-table-column>
      </el-table>
      <template #footer>
        <el-button type="primary" @click="importDialog.visible = false">知道了</el-button>
      </template>
    </el-dialog>

    <!-- 编辑记录（动态字段） -->
    <SysDialog title="编辑发票记录" :width="760" :height="500"
               :visible="editDialog.visible" @on-close="editDialog.visible = false" @on-confirm="commitEdit">
      <template v-slot:content>
        <el-form :model="editModel" label-width="120px">
          <el-row :gutter="10">
            <template v-for="col in tableCols" :key="col.prop">
              <el-col :span="12">
                <el-form-item :label="col.label">
                  <el-date-picker v-if="col.type == 'date'" v-model="editModel[col.prop]" type="date" value-format="YYYY-MM-DD" style="width:100%"></el-date-picker>
                  <el-input-number v-else-if="col.type == 'money'" v-model="editModel[col.prop]" :min="0" :precision="2" :controls="false" style="width:100%"></el-input-number>
                  <el-input v-else v-model="editModel[col.prop]"></el-input>
                </el-form-item>
              </el-col>
            </template>
          </el-row>
        </el-form>
      </template>
    </SysDialog>

    <!-- 上传合同原件 -->
    <el-dialog v-model="fileDialog.visible" title="关联合同原件" width="520" :close-on-click-modal="false">
      <el-form label-width="110px">
        <el-form-item label="发票号码">
          <el-input :value="fileDialog.row?.invoiceNo" disabled></el-input>
        </el-form-item>
        <el-form-item label="合同编号">
          <el-input v-model="fileModel.contractNo" placeholder="自动带出可修改"></el-input>
        </el-form-item>
        <el-form-item label="合同原件PDF">
          <input type="file" accept="application/pdf" ref="pdfRef" style="display:none" @change="onPdf" />
          <el-button size="small" @click="pdfRef?.click()">选择文件</el-button>
          <span style="margin-left:8px;color:#409EFF">{{ fileModel.fileName || '' }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="fileDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="commitFile">保存关联</el-button>
      </template>
    </el-dialog>

    <!-- 批次改动留痕 -->
    <el-dialog v-model="diffDialog.visible" title="批次改动记录" width="760" :close-on-click-modal="false">
      <el-table :data="diffList" border size="small">
        <el-table-column prop="time" label="时间" width="150"></el-table-column>
        <el-table-column prop="user" label="操作人" width="100"></el-table-column>
        <el-table-column prop="invoiceNo" label="发票号码" width="120"></el-table-column>
        <el-table-column label="改动明细" min-width="260">
          <template #default="scope">
            <div v-for="c in scope.row.changes" :key="c.field" style="line-height:1.8">
              {{ c.field }}：<span style="color:#999;text-decoration:line-through">{{ c.old || '—' }}</span>
              → <span style="color:#67C23A">{{ c.new || '—' }}</span>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </el-main>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import SysDialog from "@/components/SysDialog.vue";
import useInstance from "@/hooks/useInstance";
import * as invoiceApi from "@/api/invoice/index.ts";

const { global } = useInstance();
const now = new Date().getFullYear();
const yearOptions = computed(() => [now - 1, now, now + 1]);

const CORE_PROP: Record<string, any> = {
  invoice_no: { prop: 'invoiceNo', type: 'text' },
  invoice_date: { prop: 'invoiceDate', type: 'date' },
  contract_no: { prop: 'contractNo', type: 'text' },
  lawyer_name: { prop: 'lawyerName', type: 'text' },
  invoice_amount: { prop: 'invoiceAmount', type: 'money' },
  tax_amount: { prop: 'taxAmount', type: 'money' },
  invoice_total: { prop: 'invoiceTotal', type: 'money' }
};

const parm = reactive({ currentPage: 1, pageSize: 10, total: 0, recordYear: now, invoiceNo: '', lawyerName: '', contractNo: '' });
const tableList = ref<any[]>([]);
const cols = ref<any[]>([]);
const tableCols = computed(() => cols.value.map((t: any) => {
  if (t.isCore == 1) {
    const core = CORE_PROP[t.colKey] || { prop: t.colKey, type: 'text' };
    return { label: t.colLabel, prop: core.prop, type: core.type, align: core.type == 'money' ? 'right' : 'left', width: 100 };
  }
  return { label: t.colLabel, prop: '_ext_' + t.colKey, type: 'text', align: 'left', width: 100 };
}));

const fileRef = ref<any>(null);
const importing = ref(false);

const getList = async () => {
  const res = await invoiceApi.getRecordListApi(parm);
  if (res && res.code == 200) {
    tableList.value = (res.data.records || []).map(normalizeRow);
    parm.total = Number(res.data.total);
  }
};
const loadCols = async () => {
  const res = await invoiceApi.getTemplateApi(parm.recordYear);
  if (res && res.code == 200) cols.value = res.data || [];
};
const searchBtn = () => { parm.currentPage = 1; getList(); loadCols(); };
const pageChange = (val: number) => { parm.currentPage = val; getList(); };

// 把 extJson 拍平成 row._ext_xxx，表格/表单直接绑定
const normalizeRow = (row: any) => {
  const r: any = { ...row };
  Object.keys(row.extJson || {}).forEach((k: string) => { r['_ext_' + k] = row.extJson[k]; });
  return r;
};
// 反向：把 _ext_xxx 收拢回 extJson 再提交
const buildPayload = (row: any) => {
  const extJson: any = {};
  Object.keys(row).forEach((k: string) => {
    if (k.startsWith('_ext_')) extJson[k.replace('_ext_', '')] = row[k];
  });
  return {
    id: row.id, recordYear: row.recordYear, batchNo: row.batchNo,
    invoiceNo: row.invoiceNo, invoiceDate: row.invoiceDate, contractNo: row.contractNo,
    lawyerName: row.lawyerName, invoiceAmount: row.invoiceAmount,
    taxAmount: row.taxAmount, invoiceTotal: row.invoiceTotal, extJson
  };
};

// ===== 导入 =====
const importDialog = reactive({ visible: false });
const importResult = reactive<any>({ successCount: 0, failCount: 0, failDetail: [] });
const onImport = async (e: any) => {
  const file = e.target.files[0];
  if (!file) return;
  importing.value = true;
  const res = await invoiceApi.importApi(file, parm.recordYear);
  importing.value = false;
  e.target.value = '';
  if (res && res.code == 200) {
    importResult.successCount = res.data.successCount;
    importResult.failCount = res.data.failCount;
    importResult.failDetail = res.data.failDetail || [];
    importDialog.visible = true;
    getList();
  }
};

// ===== 编辑 =====
const editDialog = reactive({ visible: false });
const editModel = ref<any>({});
const editBtn = (row: any) => {
  editModel.value = { ...row };
  editDialog.visible = true;
};
const commitEdit = async () => {
  const res = await invoiceApi.updateRecordApi(buildPayload(editModel.value));
  if (res && res.code == 200) {
    ElMessage.success(res.msg);
    editDialog.visible = false;
    getList();
  }
};
const deleteBtn = async (id: number) => {
  const confirm = await global.$myconfirm('确定删除该发票记录吗?');
  if (confirm) {
    const res = await invoiceApi.deleteRecordApi(id);
    if (res && res.code == 200) { ElMessage.success(res.msg); getList(); }
  }
};

// ===== 改动留痕 =====
const diffDialog = reactive({ visible: false });
const diffList = ref<any[]>([]);
const diffBtn = async (row: any) => {
  const res = await invoiceApi.diffApi(row.batchNo);
  if (res && res.code == 200) {
    diffList.value = res.data || [];
    diffDialog.visible = true;
  }
};

// ===== 上传原件 =====
const fileDialog = reactive<{ visible: boolean; row: any }>({ visible: false, row: null });
const fileModel = ref<any>({});
const pdfRef = ref<any>(null);
const fileBtn = (row: any) => {
  fileModel.value = { contractNo: row.contractNo || '', fileUrl: '', fileName: '' };
  fileDialog.row = row;
  fileDialog.visible = true;
};
const onPdf = async (e: any) => {
  const file = e.target.files[0];
  if (!file) return;
  const res = await invoiceApi.uploadFileApi(file);
  if (res && res.code == 200) {
    ElMessage.success('上传成功');
    fileModel.value.fileUrl = res.data;
    fileModel.value.fileName = file.name;
  }
};
const commitFile = async () => {
  const row = fileDialog.row;
  const res = await invoiceApi.saveFileApi({
    recordYear: parm.recordYear,
    invoiceId: row.id,
    contractNo: fileModel.value.contractNo,
    fileName: fileModel.value.fileName,
    fileUrl: fileModel.value.fileUrl
  });
  if (res && res.code == 200) {
    ElMessage.success(res.msg);
    fileDialog.visible = false;
  }
};

onMounted(() => { getList(); loadCols(); });
</script>

<style scoped></style>
