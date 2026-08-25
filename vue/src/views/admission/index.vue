<template>
  <el-main>
    <el-card>
      <div class="search">
        <el-form inline>
          <el-form-item label="姓名">
            <el-input v-model="parm.applicantName" placeholder="请输入姓名" clearable @keyup.enter="searchBtn"></el-input>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="parm.status" placeholder="全部" clearable style="width:140px">
              <el-option label="待确认" :value="0"></el-option>
              <el-option label="已通过" :value="1"></el-option>
              <el-option label="已驳回" :value="2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="searchBtn">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableList" border stripe>
        <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
        <el-table-column prop="applicantName" label="姓名"></el-table-column>
        <el-table-column prop="applyAccount" label="申请账户"></el-table-column>
        <el-table-column prop="idCard" label="身份证号"></el-table-column>
        <el-table-column prop="phone" label="手机号"></el-table-column>
        <el-table-column prop="createTime" label="提交时间"></el-table-column>
        <el-table-column prop="status" label="状态" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.status == 0" type="warning">待确认</el-tag>
            <el-tag v-else-if="scope.row.status == 1" type="success">已通过</el-tag>
            <el-tag v-else type="danger">已驳回</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditRemark" label="驳回理由" min-width="140">
          <template #default="scope">
            <span v-if="scope.row.status == 2">{{ scope.row.auditRemark || '—' }}</span>
            <span v-else style="color:#999">—</span>
          </template>
        </el-table-column>
        <el-table-column prop="auditAccount" label="审批人" width="110" align="center">
          <template #default="scope">
            <span>{{ scope.row.auditAccount || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" align="center">
          <template #default="scope">
            <el-button type="primary" size="small" @click="detailBtn(scope.row)">查看附件</el-button>
            <el-button v-if="scope.row.status == 0" type="success" size="small" @click="auditBtn(scope.row, 1)">通过</el-button>
            <el-button v-if="scope.row.status == 0" type="danger" size="small" @click="auditBtn(scope.row, 2)">驳回</el-button>
            <el-button type="danger" size="small" plain @click="deleteBtn(scope.row.id)">删除</el-button>
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

    <!-- 附件查看弹框 -->
    <el-dialog title="审批表与附件" v-model="detailVisible" width="640px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="姓名">{{ detailRow.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ detailRow.idCard }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailRow.phone }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ detailRow.createTime }}</el-descriptions-item>
      </el-descriptions>
      <el-divider content-position="left">审批表</el-divider>
      <el-link v-if="detailRow.formUrl" :href="detailRow.formUrl" target="_blank" type="primary">查看审批表</el-link>
      <el-divider content-position="left">分类附件</el-divider>
      <div v-for="att in attList" :key="att.id" style="margin-bottom:6px">
        <el-link :href="att.attUrl" target="_blank" type="primary">{{ att.attName }}：{{ att.attUrl }}</el-link>
      </div>
      <div v-if="attList.length == 0" style="color:#999">暂无附件</div>
    </el-dialog>
  </el-main>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getListApi, auditApi, deleteApi, getAttachmentListApi, type AdmissionAttachment } from "@/api/admission/index.ts";
const parm = reactive({ currentPage: 1, pageSize: 10, total: 0, applicantName: '', status: null as any });
const tableList = ref([]);

const detailVisible = ref(false);
const detailRow = ref<any>({});
const attList = ref<AdmissionAttachment[]>([]);

const getList = async () => {
  const res = await getListApi(parm);
  if (res && res.code == 200) {
    tableList.value = res.data.records;
    parm.total = Number(res.data.total);
  }
};
const searchBtn = () => { parm.currentPage = 1; getList(); };
const pageChange = (val: number) => { parm.currentPage = val; getList(); };

const detailBtn = async (row: any) => {
  detailRow.value = row;
  const res = await getAttachmentListApi(row.id);
  if (res && res.code == 200) attList.value = res.data;
  detailVisible.value = true;
};

const auditBtn = async (row: any, status: number) => {
  let remark = '审批通过';
  if (status == 2) {
    // 驳回时用 Element Plus 弹窗录入驳回意见（原生 prompt 样式不统一）
    const { value } = await ElMessageBox.prompt('请输入驳回意见', '驳回', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputPlaceholder: '请填写驳回原因',
      inputValidator: (val: string) => (val && val.trim() ? true : '驳回意见不能为空')
    }).catch(() => ({ value: null }));
    if (value == null) return; // 用户取消驳回
    remark = value;
  }
  const res = await auditApi({
    id: row.id,
    status,
    auditRemark: remark || ''
  });
  if (res && res.code == 200) {
    ElMessage.success(res.msg);
    getList();
  }
};

const deleteBtn = async (id: number) => {
  const confirm = await ElMessageBox.confirm('确定删除该申请吗?', '提示', { type: 'warning' }).catch(() => false);
  if (confirm) {
    const res = await deleteApi(id);
    if (res && res.code == 200) { ElMessage.success(res.msg); getList(); }
  }
};

onMounted(getList);
</script>

<style scoped></style>
