<template>
  <el-main>
    <el-card>
      <div class="search" style="margin-bottom:10px">
        <el-form inline>
          <el-form-item label="时间区间">
            <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD"
                            range-separator="至" start-placeholder="开始" end-placeholder="结束"></el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="loadAll">查询</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-tabs v-model="activeTab" type="border-card">
        <el-tab-pane label="管理费统计" name="manageFee">
          <el-table :data="list.manageFee" border stripe size="small">
            <el-table-column prop="case_type" label="案件类型"></el-table-column>
            <el-table-column prop="month" label="月份" width="100"></el-table-column>
            <el-table-column prop="total_manage_fee" label="管理费合计" align="right" width="130">
              <template #default="scope"><span>{{ fmt(scope.row.total_manage_fee) }}</span></template>
            </el-table-column>
            <el-table-column prop="case_count" label="案件数" align="center" width="90"></el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="管理费个人统计" name="manageFeeByLawyer">
          <el-table :data="list.manageFeeByLawyer" border stripe size="small">
            <el-table-column prop="lawyer_name" label="律师" width="100"></el-table-column>
            <el-table-column prop="month" label="月份" width="100"></el-table-column>
            <el-table-column prop="total_manage_fee" label="管理费合计" align="right" width="130">
              <template #default="scope"><span>{{ fmt(scope.row.total_manage_fee) }}</span></template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="应收案件统计" name="receivable">
          <el-table :data="list.receivable" border stripe size="small">
            <el-table-column prop="case_type" label="案件类型"></el-table-column>
            <el-table-column prop="month" label="月份" width="100"></el-table-column>
            <el-table-column prop="total_receipt_amount" label="收据金额合计" align="right" width="130">
              <template #default="scope"><span>{{ fmt(scope.row.total_receipt_amount) }}</span></template>
            </el-table-column>
            <el-table-column prop="case_count" label="案件数" align="center" width="90"></el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="案件汇总统计" name="summary">
          <el-table :data="list.summary" border stripe size="small">
            <el-table-column prop="case_type" label="案件类型"></el-table-column>
            <el-table-column prop="month" label="月份" width="100"></el-table-column>
            <el-table-column prop="total_amount" label="合同金额合计" align="right" width="130">
              <template #default="scope"><span>{{ fmt(scope.row.total_amount) }}</span></template>
            </el-table-column>
            <el-table-column prop="case_count" label="案件数" align="center" width="90"></el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="不开票案件统计" name="noInvoice">
          <el-table :data="list.noInvoice" border stripe size="small">
            <el-table-column prop="case_type" label="案件类型"></el-table-column>
            <el-table-column prop="month" label="月份" width="100"></el-table-column>
            <el-table-column prop="total_amount" label="合同金额合计" align="right" width="130">
              <template #default="scope"><span>{{ fmt(scope.row.total_amount) }}</span></template>
            </el-table-column>
            <el-table-column prop="case_count" label="案件数" align="center" width="90"></el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="发票个人明细-按月按律师" name="invoiceMonthly">
          <el-table :data="list.invoiceDetail.monthly" border stripe size="small">
            <el-table-column prop="lawyer_name" label="律师" width="100"></el-table-column>
            <el-table-column prop="month" label="月份" width="100"></el-table-column>
            <el-table-column prop="month_total" label="价税合计" align="right" width="130">
              <template #default="scope"><span>{{ fmt(scope.row.month_total) }}</span></template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="发票个人明细-年度合计" name="invoiceYear">
          <el-table :data="list.invoiceDetail.yearTotal" border stripe size="small">
            <el-table-column prop="lawyer_name" label="律师" width="100"></el-table-column>
            <el-table-column prop="year" label="年度" width="100"></el-table-column>
            <el-table-column prop="year_total" label="价税合计" align="right" width="130">
              <template #default="scope"><span>{{ fmt(scope.row.year_total) }}</span></template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="发票个人明细-全所每月" name="invoiceOffice">
          <el-table :data="list.invoiceDetail.officeMonthly" border stripe size="small">
            <el-table-column prop="month" label="月份" width="120"></el-table-column>
            <el-table-column prop="month_total" label="全所价税合计" align="right" width="150">
              <template #default="scope"><span>{{ fmt(scope.row.month_total) }}</span></template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="年度收案统计" name="yearSummary">
          <el-table :data="list.yearSummary" border stripe size="small">
            <el-table-column prop="year" label="年度" width="90"></el-table-column>
            <el-table-column prop="month" label="月份" width="80"></el-table-column>
            <el-table-column prop="lawyer_name" label="律师" width="100"></el-table-column>
            <el-table-column prop="total_amount" label="合同金额" align="right" width="130">
              <template #default="scope"><span>{{ fmt(scope.row.total_amount) }}</span></template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </el-main>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import * as contractApi from "@/api/contract/index.ts";

const activeTab = ref('manageFee');
const dateRange = ref<string[]>([]);
const list = reactive<any>({
  manageFee: [], manageFeeByLawyer: [], receivable: [], summary: [], noInvoice: [],
  invoiceDetail: { monthly: [], yearTotal: [], officeMonthly: [] }, yearSummary: []
});

const fmt = (v: any) => {
  if (v == null) return '0.00';
  return Number(v).toFixed(2);
};
const dateParm = () => {
  const [startDate = '', endDate = ''] = dateRange.value || [];
  return { startDate, endDate };
};
const loadAll = async () => {
  const parm = dateParm();
  const rs = await Promise.all([
    contractApi.statManageFeeApi(parm),
    contractApi.statManageFeeByLawyerApi(parm),
    contractApi.statReceivableApi(parm),
    contractApi.statSummaryApi(parm),
    contractApi.statNoInvoiceApi(parm),
    contractApi.statInvoiceDetailApi(parm),
    contractApi.statYearSummaryApi(parm)
  ]);
  const ok = (r: any) => (r && r.code == 200 ? r.data : []);
  list.manageFee = ok(rs[0]);
  list.manageFeeByLawyer = ok(rs[1]);
  list.receivable = ok(rs[2]);
  list.summary = ok(rs[3]);
  list.noInvoice = ok(rs[4]);
  const detail = rs[5] && rs[5].code == 200 ? rs[5].data : { monthly: [], yearTotal: [], officeMonthly: [] };
  list.invoiceDetail = detail;
  list.yearSummary = ok(rs[6]);
};

onMounted(() => { loadAll(); });
</script>

<style scoped></style>
