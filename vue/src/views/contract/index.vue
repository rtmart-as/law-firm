<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <el-main>
<el-card>
<div class="search">
    <el-form inline>
<el-form-item label="合同编号">
    <el-input v-model="parm.contractNo" placeholder="请输入" clearable></el-input>
</el-form-item>
<el-form-item label="案件类型">
    <el-select v-model="parm.caseType" placeholder="全部" clearable style="width:140px">
    <el-option v-for="c in caseTypes" :key="c" :label="c" :value="c"></el-option>
    </el-select>
    </el-form-item>
    <el-form-item label="承办律师">
    <el-input v-model="parm.lawyerName" placeholder="请输入" clearable></el-input>
</el-form-item>
<el-form-item label="状态">
    <el-select v-model="parm.status" placeholder="全部" clearable style="width:140px">
    <el-option label="正常" :value="1"></el-option>
    <el-option label="解除" :value="2"></el-option>
    <el-option label="变更" :value="3"></el-option>
    <el-option label="收回" :value="4"></el-option>
    </el-select>
    </el-form-item>
    <el-form-item label="领用日期">
    <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD"
range-separator="至" start-placeholder="开始" end-placeholder="结束"></el-date-picker>
    </el-form-item>
    <el-form-item>
    <el-button type="primary" icon="Search" @click="searchBtn">搜索</el-button>
    </el-form-item>
    </el-form>
    </div>

    <div style="margin-bottom:10px">
    <el-button v-if="global.$hasPerm(['sys:contract:add'])" type="primary" icon="Plus" @click="addBtn">新增合同</el-button>
    </div>

    <el-table :data="tableList" border stripe>
<el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
    <el-table-column prop="contractNo" label="合同编号" width="120"></el-table-column>
    <el-table-column prop="caseType" label="案件类型" width="90"></el-table-column>
    <el-table-column prop="cause" label="案由" min-width="110"></el-table-column>
    <el-table-column prop="clientName" label="委托人" width="90"></el-table-column>
    <el-table-column prop="lawyerName" label="承办律师" width="80"></el-table-column>
    <el-table-column prop="receiveDate" label="领用日期" width="100"></el-table-column>
    <el-table-column prop="contractAmount" label="合同金额" width="100" align="right"></el-table-column>
    <el-table-column prop="manageFee" label="管理费" width="90" align="right">
<template #default="scope"><span>{{ scope.row.manageFee == null ? '—' : scope.row.manageFee }}</span></template>
</el-table-column>
<el-table-column prop="acceptAmount" label="收案金额" width="90" align="right">
<template #default="scope"><span>{{ scope.row.acceptAmount == null ? '—' : scope.row.acceptAmount }}</span></template>
</el-table-column>
<el-table-column prop="invoiceFlag" label="开票" width="70" align="center">
<template #default="scope">
    <el-tag v-if="scope.row.invoiceFlag == 1" type="success">已开</el-tag>
    <el-tag v-else type="info">未开</el-tag>
    </template>
    </el-table-column>
    <el-table-column prop="status" label="状态" width="70" align="center">
<template #default="scope">
    <el-tag v-if="scope.row.status == 1" type="success">正常</el-tag>
    <el-tag v-else-if="scope.row.status == 2" type="danger">解除</el-tag>
    <el-tag v-else-if="scope.row.status == 3" type="warning">变更</el-tag>
    <el-tag v-else type="info">收回</el-tag>
    </template>
    </el-table-column>
    <el-table-column label="操作" width="360" align="center" fixed="right">
<template #default="scope">
    <el-button v-if="scope.row.status != 2 && global.$hasPerm(['sys:contract:edit'])" type="primary" size="small" @click="editBtn(scope.row)">编辑</el-button>
    <el-button v-if="scope.row.status != 2 && global.$hasPerm(['sys:contract:change'])" type="warning" size="small" @click="changeBtn(scope.row)">变更</el-button>
    <el-button v-if="scope.row.status != 2 && global.$hasPerm(['sys:contract:cancel'])" type="danger" size="small" @click="cancelBtn(scope.row)">解除</el-button>
    <el-button v-if="scope.row.status != 2 && scope.row.status != 4 && global.$hasPerm(['sys:contract:recover'])" type="success" size="small" @click="recoverBtn(scope.row)">收回</el-button>
    <el-button v-if="global.$hasPerm(['sys:contract:delete'])" type="danger" size="small" plain @click="deleteBtn(scope.row.id)">删除</el-button>
    </template>
    </el-table-column>
    </el-table>
    <el-pagination style="margin-top:10px" background layout="total,prev,pager,next,jumper"
:total="parm.total" :page-size="parm.pageSize" :current-page="parm.currentPage"
@current-change="pageChange"></el-pagination>
    </el-card>

    <!-- 新增 / 编辑合同 -->
    <SysDialog :title="dialog.title" :width="820" :height="520"
:visible="dialog.visible" @on-close="onClose" @on-confirm="commit">
    <template v-slot:content>
<el-form ref="form" :model="model" label-width="100px">
    <el-row :gutter="10">
    <el-col :span="12">
    <el-form-item label="合同编号"><el-input v-model="model.contractNo" placeholder="如 L2025001"></el-input></el-form-item>
</el-col>
<el-col :span="12">
    <el-form-item label="合同类型">
    <el-select v-model="model.contractType" placeholder="请选择" clearable style="width:100%">
    <el-option label="常年顾问" value="常年顾问"></el-option>
    <el-option label="专项服务" value="专项服务"></el-option>
    <el-option label="诉讼代理" value="诉讼代理"></el-option>
    </el-select>
    </el-form-item>
    </el-col>
    <el-col :span="12">
    <el-form-item label="案件类型">
    <el-select v-model="model.caseType" placeholder="请选择" clearable style="width:100%">
    <el-option v-for="c in caseTypes" :key="c" :label="c" :value="c"></el-option>
    </el-select>
    </el-form-item>
    </el-col>
    <el-col :span="12">
    <el-form-item label="案由"><el-input v-model="model.cause"></el-input></el-form-item>
</el-col>
<el-col :span="12">
    <el-form-item label="委托人"><el-input v-model="model.clientName"></el-input></el-form-item>
</el-col>
<el-col :span="12">
    <el-form-item label="承办律师">
    <el-select v-model="model.lawyerId" placeholder="请选择律师" style="width:100%" @change="selectLawyer">
    <el-option v-for="l in lawyerOptions" :key="l.id" :label="l.name" :value="l.id"></el-option>
    </el-select>
    </el-form-item>
    </el-col>
    <el-col :span="12">
    <el-form-item label="领用日期">
    <el-date-picker v-model="model.receiveDate" type="date" value-format="YYYY-MM-DD" style="width:100%"></el-date-picker>
    </el-form-item>
    </el-col>
    <el-col :span="12">
    <el-form-item label="领取人"><el-input v-model="model.receiver"></el-input></el-form-item>
</el-col>
<el-col :span="12">
    <el-form-item label="合同金额"><el-input-number v-model="model.contractAmount" :min="0" :precision="2" :controls="false" style="width:100%"></el-input-number></el-form-item>
</el-col>
<el-col :span="12">
    <el-form-item label="收据金额"><el-input-number v-model="model.receiptAmount" :min="0" :precision="2" :controls="false" style="width:100%"></el-input-number></el-form-item>
</el-col>
<el-col :span="12">
    <el-form-item label="开票金额"><el-input-number v-model="model.invoiceAmount" :min="0" :precision="2" :controls="false" style="width:100%"></el-input-number></el-form-item>
</el-col>
<el-col :span="12">
    <el-form-item label="是否交回">
    <el-select v-model="model.isReturned" style="width:100%">
    <el-option label="否" :value="0"></el-option>
    <el-option label="是" :value="1"></el-option>
    </el-select>
    </el-form-item>
    </el-col>
    <el-col :span="12">
    <el-form-item label="是否开票">
    <el-select v-model="model.invoiceFlag" style="width:100%">
    <el-option label="未开" :value="0"></el-option>
    <el-option label="已开" :value="1"></el-option>
    </el-select>
    </el-form-item>
    </el-col>
    <el-col :span="12">
    <el-form-item label="是否开收据">
    <el-select v-model="model.receiptFlag" style="width:100%">
    <el-option label="未开" :value="0"></el-option>
    <el-option label="已开" :value="1"></el-option>
    </el-select>
    </el-form-item>
    </el-col>
    <!-- 主任蓝字段：只有 sys:contract:managerEdit 权限可见可填 -->
    <template v-if="global.$hasPerm(['sys:contract:managerEdit'])">
    <el-col :span="12">
    <el-form-item label="管理费">
    <el-input-number v-model="model.manageFee" :min="0" :precision="2" :controls="false" style="width:100%"></el-input-number>
    </el-form-item>
    </el-col>
    <el-col :span="12">
    <el-form-item label="收案金额">
    <el-input-number v-model="model.acceptAmount" :min="0" :precision="2" :controls="false" style="width:100%"></el-input-number>
    </el-form-item>
    </el-col>
    <el-col :span="12">
    <el-form-item label="缴费时间">
    <el-date-picker v-model="model.payTime" type="date" value-format="YYYY-MM-DD" style="width:100%"></el-date-picker>
    </el-form-item>
    </el-col>
    </template>
    <el-col :span="24">
    <el-form-item label="备注"><el-input v-model="model.remark"></el-input></el-form-item>
</el-col>
</el-row>
</el-form>
</template>
</SysDialog>

<!-- 变更合同（追加/退律师费 + 情况说明PDF） -->
<el-dialog v-model="changeDialog.visible" :title="changeDialog.title" width="520" :close-on-click-modal="false">
    <el-form :model="changeModel" label-width="110px">
    <el-form-item label="变更类型">
    <el-radio-group v-model="changeModel.changeType">
    <el-radio :value="1">追加律师费</el-radio>
    <el-radio :value="2">退律师费</el-radio>
    </el-radio-group>
    </el-form-item>
    <el-form-item label="变更金额">
    <el-input-number v-model="changeModel.changeAmount" :min="0.01" :precision="2" :controls="false"></el-input-number>
    </el-form-item>
    <el-form-item label="变更日期">
    <el-date-picker v-model="changeModel.changeDate" type="date" value-format="YYYY-MM-DD"></el-date-picker>
    </el-form-item>
    <el-form-item label="情况说明PDF">
<input type="file" accept="application/pdf" ref="changeFileRef" style="display:none" @change="onChangeFile" />
    <el-button size="small" @click="changeFileRef?.click()">选择文件</el-button>
    <span style="margin-left:8px;color:#409EFF">{{ changeModel.reasonFileUrl || '' }}</span>
</el-form-item>
</el-form>
<template #footer>
<el-button @click="changeDialog.visible = false">取消</el-button>
    <el-button type="primary" @click="commitChange">确定</el-button>
    </template>
    </el-dialog>

    <!-- 解除合同（原因 + 解除PDF） -->
    <el-dialog v-model="cancelDialog.visible" :title="cancelDialog.title" width="520" :close-on-click-modal="false">
    <el-form :model="cancelModel" label-width="110px">
    <el-form-item label="解除原因"><el-input v-model="cancelModel.cancelReason" type="textarea"></el-input></el-form-item>
<el-form-item label="解除日期">
    <el-date-picker v-model="cancelModel.cancelDate" type="date" value-format="YYYY-MM-DD"></el-date-picker>
    </el-form-item>
    <el-form-item label="解除合同PDF">
<input type="file" accept="application/pdf" ref="cancelFileRef" style="display:none" @change="onCancelFile" />
    <el-button size="small" @click="cancelFileRef?.click()">选择文件</el-button>
    <span style="margin-left:8px;color:#409EFF">{{ cancelModel.fileUrl || '' }}</span>
</el-form-item>
<el-alert title="解除后合同金额、管理费、收案金额等将全部置为 0" type="warning" :closable="false"></el-alert>
    </el-form>
    <template #footer>
<el-button @click="cancelDialog.visible = false">取消</el-button>
    <el-button type="danger" @click="commitCancel">确定解除</el-button>
    </template>
    </el-dialog>

    <!-- 收回合同（交回原件 PDF） -->
    <el-dialog v-model="recoverDialog.visible" :title="recoverDialog.title" width="520" :close-on-click-modal="false">
    <el-form :model="recoverModel" label-width="110px">
    <el-form-item label="收回日期">
    <el-date-picker v-model="recoverModel.recoverDate" type="date" value-format="YYYY-MM-DD"></el-date-picker>
    </el-form-item>
    <el-form-item label="合同PDF">
<input type="file" accept="application/pdf" ref="recoverFileRef" style="display:none" @change="onRecoverFile" />
    <el-button size="small" @click="recoverFileRef?.click()">选择文件</el-button>
    <span style="margin-left:8px;color:#409EFF">{{ recoverModel.fileUrl || '' }}</span>
</el-form-item>
</el-form>
<template #footer>
<el-button @click="recoverDialog.visible = false">取消</el-button>
    <el-button type="success" @click="commitRecover">确定收回</el-button>
    </template>
    </el-dialog>
    </el-main>
    </template>

    <script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import SysDialog from "@/components/SysDialog.vue";
import useDialog from "@/hooks/useDialog";
import useInstance from "@/hooks/useInstance";
import * as contractApi from "@/api/contract/index.ts";
import * as lawyerApi from "@/api/lawyer/index.ts";
import { type Lawyer } from "@/api/lawyer/Lawyer.ts";

const { global } = useInstance();
const { dialog, onClose, onConfirm } = useDialog();

const caseTypes = ['民事', '刑事', '行政', '法援', '仲裁', '法律顾问', '专项法律服务', '刑事附带民事'];

const parm = reactive({ currentPage: 1, pageSize: 10, total: 0, contractNo: '', caseType: '', lawyerName: '', status: null as any, startDate: '', endDate: '' });
const dateRange = ref<string[]>([]);
const tableList = ref([]);
const lawyerOptions = ref<Lawyer[]>([]);
const model = ref<any>({});
const tags = ref('0');

const getList = async () => {
    parm.startDate = dateRange.value?.[0] || '';
    parm.endDate = dateRange.value?.[1] || '';
    const res = await contractApi.getListApi(parm);
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
const selectLawyer = (id: any) => {
    const lawyer = lawyerOptions.value.find((l: any) => l.id == id);
    if (lawyer) model.value.lawyerName = lawyer.name;
};

const addBtn = () => {
    tags.value = '0';
    model.value = {
        id: '', contractNo: '', contractType: '', caseType: '', cause: '', clientName: '',
        lawyerId: '', lawyerName: '', receiveDate: '', receiver: '', isReturned: 0,
        contractAmount: 0, receiptAmount: 0, invoiceAmount: 0, manageFee: 0, acceptAmount: 0,
        payTime: '', invoiceFlag: 0, receiptFlag: 0, remark: ''
    };
    dialog.title = '新增合同';
    dialog.visible = true;
};
const editBtn = (row: any) => {
    tags.value = '1';
    model.value = { ...row };
    dialog.title = '编辑合同';
    dialog.visible = true;
};
const commit = async () => {
    let res;
    if (tags.value == '0') res = await contractApi.addApi(model.value);
    else res = await contractApi.editApi(model.value);
    if (res && res.code == 200) {
        ElMessage.success(res.msg);
        dialog.visible = false;
        getList();
    }
};
const deleteBtn = async (id: number) => {
    const confirm = await global.$myconfirm('确定删除该合同吗?');
    if (confirm) {
        const res = await contractApi.deleteApi(id);
        if (res && res.code == 200) { ElMessage.success(res.msg); getList(); }
    }
};

// ===== 变更 =====
const changeDialog = reactive({ visible: false, title: '' });
const changeModel = ref<any>({});
const changeFileRef = ref<any>(null);
const changeBtn = (row: any) => {
    changeModel.value = { contractId: row.id, changeType: 1, changeAmount: 0, changeDate: '', reasonFileUrl: '' };
    changeDialog.title = `合同变更（${row.contractNo}）`;
    changeDialog.visible = true;
};
const onChangeFile = async (e: any) => {
    const file = e.target.files[0];
    if (!file) return;
    const res = await contractApi.uploadFileApi(file);
    if (res && res.code == 200) {
        ElMessage.success('上传成功');
        changeModel.value.reasonFileUrl = res.data;
    }
};
const commitChange = async () => {
    if (!changeModel.value.changeAmount || changeModel.value.changeAmount <= 0) {
        ElMessage.warning('请填写大于 0 的变更金额'); return;
    }
    const res = await contractApi.changeApi(changeModel.value);
    if (res && res.code == 200) {
        ElMessage.success(res.msg);
        changeDialog.visible = false;
        getList();
    }
};

// ===== 解除 =====
const cancelDialog = reactive({ visible: false, title: '' });
const cancelModel = ref<any>({});
const cancelFileRef = ref<any>(null);
const cancelBtn = (row: any) => {
    cancelModel.value = { contractId: row.id, cancelReason: '', cancelDate: '', fileUrl: '' };
    cancelDialog.title = `解除合同（${row.contractNo}）`;
    cancelDialog.visible = true;
};
const onCancelFile = async (e: any) => {
    const file = e.target.files[0];
    if (!file) return;
    const res = await contractApi.uploadFileApi(file);
    if (res && res.code == 200) {
        ElMessage.success('上传成功');
        cancelModel.value.fileUrl = res.data;
    }
};
const commitCancel = async () => {
    const res = await contractApi.cancelApi(cancelModel.value);
    if (res && res.code == 200) {
        ElMessage.success(res.msg);
        cancelDialog.visible = false;
        getList();
    }
};

// ===== 收回 =====
const recoverDialog = reactive({ visible: false, title: '' });
const recoverModel = ref<any>({});
const recoverFileRef = ref<any>(null);
const recoverBtn = (row: any) => {
    recoverModel.value = { contractId: row.id, recoverDate: '', fileUrl: '' };
    recoverDialog.title = `收回合同（${row.contractNo}）`;
    recoverDialog.visible = true;
};
const onRecoverFile = async (e: any) => {
    const file = e.target.files[0];
    if (!file) return;
    const res = await contractApi.uploadFileApi(file);
    if (res && res.code == 200) {
        ElMessage.success('上传成功');
        recoverModel.value.fileUrl = res.data;
    }
};
const commitRecover = async () => {
    const res = await contractApi.recoverApi(recoverModel.value);
    if (res && res.code == 200) {
        ElMessage.success(res.msg);
        recoverDialog.visible = false;
        getList();
    }
};

onMounted(() => { getList(); getLawyerOptions(); });
</script>

<style scoped></style>
