<template>
  <el-main :style="{ height: mianHeight + 'px' }">
    <!-- 数量统计 -->
    <el-row
        :gutter="20"
        type="flex"
        justify="center"
        style="margin-bottom: 40px"
    >
      <el-col :span="6">
        <div class="show-header" style="background: rgb(45, 183, 245)">
          <div class="show-num">{{ total.userCount }}</div>
          <div class="bottom-text">用户总数</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="show-header" style="background: rgb(237, 64, 20)">
          <div class="show-num">{{ total.lawyerCount }}</div>
          <div class="bottom-text">律师总数</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="show-header">
          <div class="show-num">{{ total.ongoingCount }}</div>
          <div class="bottom-text">进行案件</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="show-header" style="background: rgb(255, 153, 0)">
          <div class="show-num">{{ total.settledCount }}</div>
          <div class="bottom-text">结算案件</div>
        </div>
      </el-col>
    </el-row>
    <div style="display: flex">
      <el-card style="flex: 1">
        <template #header>
          <div class="card-header">
            <span style="color: var(--el-text-color-primary); font-weight: 600; margin-bottom: 10px"
            >每月案件统计</span
            >
          </div>
        </template>
        <div ref="myChart" style="width: 100%; height: 300px"></div>
      </el-card>
      <el-card style="margin-left: 20px; flex: 1">
        <template #header>
          <div class="card-header">
            <span style="color: var(--el-text-color-primary); font-weight: 600; margin-bottom: 10px"
            >案件类型分布</span
            >
          </div>
        </template>
        <div ref="myChart1" style="width: 100%; height: 300px"></div>
      </el-card>
      <el-card style="margin-left: 20px; flex: 1">
        <template #header>
          <div class="card-header">
            <span style="color: var(--el-text-color-primary); font-weight: 600; margin-bottom: 10px"
            >最受欢迎律师</span
            >
          </div>
        </template>
        <div ref="myChart2" style="width: 100%; height: 300px"></div>
      </el-card>
    </div>
    <el-card class="box-card" style="margin-top: 30px">
      <div slot="header" class="clearfix">
        <span style="color: var(--el-text-color-primary); font-weight: 600; margin-bottom: 10px">公告列表</span>
        <el-divider></el-divider>
      </div>
      <!-- 固定高度 + 滚动：显示全部公告，多余内容上下翻看 -->
      <div class="notice-scroll">
        <div v-for="(item, index) in noticeList" :key="index" class="text item">
          <span style="font-weight: 600; font-size: 14px; color: var(--el-text-color-primary)">{{ item.title }}</span>
          <span style="margin-left: 30px; font-size: 14px">{{ item.content }}</span>
          <span style="margin-left: 30px">{{ formatTime(item.createTime) }}</span>
          <el-divider></el-divider>
        </div>
        <el-empty v-if="noticeList.length == 0" description="暂无公告"></el-empty>
      </div>
    </el-card>
  </el-main>
</template>
<script setup lang="ts">
import { ref, nextTick, onMounted, onBeforeUnmount, reactive, watch } from "vue";
import useInstance from "@/hooks/useInstance";
import { themeStore } from "@/stores/theme/index.ts";
import {
  getTotalApi,
  getBestSaleApi,
  getEchartTotalApi,
  getCaseTypeApi,
  getNoticeListApi
} from "@/api/home/index.ts";

const mianHeight = ref(0);
const { global } = useInstance();
const tStore = themeStore();
const myChart = ref<HTMLElement>();
const myChart1 = ref<HTMLElement>();
const myChart2 = ref<HTMLElement>();
//保存 echarts 实例，用于窗口 resize 重绘
const chartInstances: any[] = [];
//三个图表的实例（复用，避免主题切换重绘时重复初始化）
const barChart = ref<any>();
const pieChart = ref<any>();
const ringChart = ref<any>();
//读取当前主题下 Element Plus 的常规文字色（白天深灰 #606266，暗色浅色，随 html.dark 自动切换）
const getTextColor = () =>
  getComputedStyle(document.documentElement)
    .getPropertyValue("--el-text-color-regular")
    .trim() || "#606266";

//柱状图：每月案件统计
const charts1 = async () => {
  if (!myChart.value) return;
  if (!barChart.value) {
    barChart.value = global.$echarts.init(myChart.value);
    chartInstances.push(barChart.value);
  }
  const echartInstance = barChart.value;
  let option = reactive({
    tooltip: {
      trigger: "axis",
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: [],
      axisLabel: { color: getTextColor() },
    },
    yAxis: {
      type: "value",
      axisLabel: { color: getTextColor() },
    },
    series: [
      {
        name: "案件数",
        data: [],
        type: "bar",
        barMaxWidth: 36,
        itemStyle: {
          color: "#2db7f5",
          borderRadius: [3, 3, 0, 0],
        },
      },
    ],
  });
  //动态获取数据
  let res = await getEchartTotalApi();
  if (res && res.code == 200) {
    option.xAxis.data = res.data.names;
    const seriesItem = option.series[0];
    if (seriesItem) {
      seriesItem.data = res.data.values || [];
    }
  }
  echartInstance.setOption(option);
};
//饼图：案件类型分布
const charts2 = async () => {
  if (!myChart1.value) return;
  if (!pieChart.value) {
    pieChart.value = global.$echarts.init(myChart1.value);
    chartInstances.push(pieChart.value);
  }
  const echartInstance = pieChart.value;
  let option = reactive({
    tooltip: {
      trigger: "item",
      formatter: "{b}: {c} ({d}%)",
    },
    legend: {
      orient: "vertical",
      left: "left",
      textStyle: { color: getTextColor() },
    },
    series: [
      {
        name: "案件类型",
        type: "pie",
        radius: "50%",
        data: [],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
      },
    ],
  });
  let res = await getCaseTypeApi();
  if (res && res.code == 200) {
    const seriesItem = option.series[0];
    if (seriesItem) {
      seriesItem.data = res.data || [];
    }
  }
  echartInstance.setOption(option);
};
//环图：最受欢迎律师
const charts3 = async () => {
  if (!myChart2.value) return;
  if (!ringChart.value) {
    ringChart.value = global.$echarts.init(myChart2.value);
    chartInstances.push(ringChart.value);
  }
  const echartInstance = ringChart.value;
  let option = reactive({
    tooltip: {
      trigger: "item",
      formatter: "{b}: {c} ({d}%)",
    },
    legend: {
      top: "5%",
      left: "center",
      textStyle: { color: getTextColor() },
    },
    series: [
      {
        name: "承办案件",
        type: "pie",
        radius: ["40%", "70%"],
        avoidLabelOverlap: false,
        label: {
          show: false,
          position: "center",
        },
        emphasis: {
          label: {
            show: true,
            fontSize: "24",
            fontWeight: "bold",
          },
        },
        labelLine: {
          show: false,
        },
        data: [],
      },
    ],
  });
  let res = await getBestSaleApi();
  if (res && res.code == 200) {
    const seriesItem = option.series[0];
    if (seriesItem) {
      seriesItem.data = res.data || [];
    }
  }
  echartInstance.setOption(option);
};
//总数查询
const total = reactive({
  userCount: 0,
  lawyerCount: 0,
  ongoingCount: 0,
  settledCount: 0,
});
const getTotal = async () => {
  let res = await getTotalApi();
  if (res && res.code == 200) {
    Object.assign(total, res.data);
  }
};
//公告
type NoticeItem = {
  title: string;
  content: string;
  createTime: string;
};
const noticeList = ref<NoticeItem[]>([]);
const getNoticeList = async () => {
  let res = await getNoticeListApi();
  if (res && res.code == 200) {
    noticeList.value = res.data || [];
  }
};
//时间格式化
const formatTime = (time: string) => {
  if (!time) return "";
  return String(time).replace("T", " ").substring(0, 19);
};
//窗口大小变化时重绘图表
const onResize = () => {
  chartInstances.forEach((c) => c && c.resize());
};
onMounted(() => {
  charts1();
  charts2();
  charts3();
  nextTick(() => {
    mianHeight.value = window.innerHeight - 100;
  });
  getTotal();
  getNoticeList();
  window.addEventListener("resize", onResize);
});
//主题切换时重绘图表，使坐标轴/图例文字颜色随主题变化
watch(
  () => tStore.getIsDark,
  () => {
    charts1();
    charts2();
    charts3();
  }
);
onBeforeUnmount(() => {
  window.removeEventListener("resize", onResize);
});
</script>
<style scoped lang="scss">
//公告列表：固定高度与上方图表区(300px)一致，超出滚动查看
.notice-scroll {
  height: 300px;
  overflow-y: auto;
}
.bottom-text {
  bottom: 0;
  width: 100%;
  background: rgba(0, 0, 0, 0.1);
  height: 25px;
  line-height: 25px;
  text-align: center;
  position: absolute;
  font-weight: 600;
}
.show-header {
  background: #00c0ef;
  color: #fff;
  height: 80px;
  border-radius: 5px;
  position: relative;
}
.show-num {
  font-size: 38px;
  font-weight: 600;
  padding: 5px;
}
</style>
