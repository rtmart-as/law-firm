<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
  <el-main>
    <!-- 搜索栏 -->
    <el-form :model="searchParm" :inline="true" size="default">
      <el-form-item>
        <el-input
            placeholder="请输入律师姓名"
            v-model="searchParm.name"
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-button icon="Search" @click="searchBtn">搜索</el-button>
        <el-button icon="Close" @click="resetBtn" type="danger" plain
        >重置</el-button>
        <el-button v-if="global.$hasPerm(['sys:lawyer:add'])" icon="Plus" type="primary" @click="addBtn">新增</el-button>
      </el-form-item>
    </el-form>
    <!-- 表格 -->
    <el-table :height="tableHeight" :data="tableList" border stripe>
      <el-table-column prop="name" label="姓名" align="center"></el-table-column>
      <el-table-column prop="gender" label="性别" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.gender == 1" type="primary" size="default" effect="dark">男</el-tag>
          <el-tag v-if="scope.row.gender == 0" type="danger" size="default" effect="dark">女</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="practiceCertNo" label="执业证号" align="center"></el-table-column>
      <el-table-column prop="phone" label="手机号码" align="center"></el-table-column>
      <el-table-column prop="email" label="邮箱" align="center"></el-table-column>
      <el-table-column prop="lawyerType" label="律师类型" align="center">
        <template #default="scope">
          {{scope.row.lawyerType===1?'专职律师':'兼职律师'}}
        </template>
      </el-table-column>
      <el-table-column prop="workStatus" label="在职状态" align="center">
        <template #default="scope">
          <el-tag
              v-if="scope.row.workStatus == 1"
              type="primary"
              size="default"
              effect="dark"
          >在职</el-tag
          >
          <el-tag
              v-if="scope.row.workStatus == 0"
              type="danger"
              size="default"
              effect="dark"
          >离职</el-tag>
        </template>
      </el-table-column>
      <el-table-column v-if="global.$hasPerm(['sys:lawyer:edit','sys:lawyer:reset','sys:lawyer:delete'])" align="center" width="500" label="操作">
        <template #default="scope">
          <el-button
              v-if="global.$hasPerm(['sys:lawyer:edit'])"
              type="primary"
              icon="Edit"
              size="default"
              @click="editBtn(scope.row)"
          >编辑</el-button>
          <el-button
              v-if="global.$hasPerm(['sys:lawyer:record'])"
              type="success"
              icon="Setting"
              size="default"
              @click="completeBtn(scope.row.id)"
          >完善档案（转所记录）</el-button>
          <el-button
              v-if="global.$hasPerm(['sys:lawyer:delete'])"
              type="danger"
              icon="Delete"
              size="default"
              @click="deleteBtn(scope.row.id)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <el-pagination
        @size-change="sizeChange"
        @current-change="currentChange"
        :current-page.sync="searchParm.currentPage"
        :page-sizes="[10, 20, 40, 80, 100]"
        :page-size="searchParm.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="searchParm.total"
        background
    >
    </el-pagination>

    <!-- 新增编辑 -->
    <SysDialog
        :title="dialog.title"
        :width="dialog.width"
        :height="dialog.height"
        :visible="dialog.visible"
        @on-close="onClose"
        @on-confirm="commit"
    >
      <template v-slot:content>
        <el-form
            :model="addModel"
            ref="addForm"
            :rules="rules"
            label-width="80px"
            :inline="false"
            size="default"
        >
          <el-row>
            <el-col :span="12" :offset="0">
              <el-form-item prop="name" label="姓名：" label-width="90">
                <el-input v-model="addModel.name"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12" :offset="0">
              <el-form-item prop="gender" label="性别：" label-width="90">
                <el-radio-group v-model="addModel.gender">
                  <el-radio :label="1">男</el-radio>
                  <el-radio :label="0">女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12" :offset="0">
              <el-form-item prop="phone" label="手机号码:" label-width="90">
                <el-input v-model="addModel.phone"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12" :offset="0">
              <el-form-item prop="email" label="邮箱：" label-width="90">
                <el-input v-model="addModel.email"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12" :offset="0">
              <el-form-item prop="practiceCertNo" label="执业证号：" label-width="90">
                <el-input v-model="addModel.practiceCertNo"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12" :offset="0">
              <el-form-item prop="lawyerType" label="律师类型：" label-width="90">
                <el-select v-model="addModel.lawyerType">
                  <el-option label="专职律师" :value="1"/>
                  <el-option label="兼职律师" :value="2"/>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12" :offset="0">
              <el-form-item prop="workStatus" label="在职状态：" label-width="90">
                <el-select v-model="addModel.workStatus">
                  <el-option label="在职" :value="1"/>
                  <el-option label="离职" :value="0"/>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12" :offset="0">
              <el-form-item prop="businessSpecialty" label="业务特长：" label-width="90">
                <el-input v-model="addModel.businessSpecialty"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </template>
    </SysDialog>
  </el-main>
</template>

<script setup lang="ts">
import { nextTick, onMounted, reactive, ref } from "vue";
import SysDialog from "@/components/SysDialog.vue";
import useDialog from "@/hooks/useDialog";
import { ElMessage,type FormInstance } from "element-plus";
import { addApi,editApi,deleteApi,getListApi} from "@/api/lawyer/index.ts";
import {type Lawyer} from "@/api/lawyer/Lawyer.ts";
import useInstance from "@/hooks/useInstance";
const {global} = useInstance()
import { useRouter } from "vue-router";
const router = useRouter();
//表单ref属性
const addForm = ref<FormInstance>();
//弹框属性
const { dialog, onClose, onShow } = useDialog();
//搜索栏绑定对象
const searchParm = reactive({
  name: "",
  currentPage: 1,
  pageSize: 10,
  total: 0,
});
//新增绑定对象
const addModel = reactive({
  id:"",
  name: "",
  gender: "",
  phone: "",
  email: "",
  practiceCertNo:"",
  lawyerType: "",
  workStatus: "",
  businessSpecialty:""
});
//表单验证规则
const rules = reactive({
  name: [
    {
      required: true,
      trigger: ["blur", "change"],
      message: "请输入姓名",
    },
  ],
  gender: [
    {
      required: true,
      trigger: ["blur", "change"],
      message: "请选择性别",
    },
  ],
  phone: [
    {
      required: true,
      trigger: ["blur", "change"],
      message: "请输入电话",
    },
  ],
  email: [
    {
      required: true,
      trigger: ["blur", "change"],
      message: "请输入email",
    },
  ]
});

const tags = ref('')
//新增按钮
const addBtn = () => {
  tags.value = '0';
  dialog.title = "新增";
  dialog.height = 230;
  //显示弹框
  onShow();
  //清空 addModel 的全部字段（含 editBtn 里 Object.assign 带进来的额外字段，如
  // 地址/生日/毕业院校/学历/身份证/加入时间等）。只靠 resetFields() 清不干净：
  // 它只重置绑了 el-form-item 的字段，且弹框首次打开时表单还没挂载(resetFields
  // 拿不到 addForm)。残留字段会让新增时带上上一个律师的旧数据，看起来像"默认值"。
  for (const key in addModel) {
    addModel[key as keyof typeof addModel] = "";
  }
  //等弹框内容真正挂载后再清除校验状态
  nextTick(() => {
    addForm.value?.clearValidate();
  });
};

//编辑
const editBtn = async(row: Lawyer) => {
  tags.value = '1'
  dialog.title = "编辑";
  dialog.height = 230;
  //显示弹框
  onShow();
  nextTick(() => {
    //数据回显
    Object.assign(addModel, row);
  });
  //清空表单
  addForm.value?.resetFields();
}

// 完善档案（转所记录）
// 用路径参数 /lawyer/detail/:id，标签页按 path 去重，从而支持同时打开多个律师的档案
const completeBtn = (id: number) => {
  router.push(`/lawyer/detail/${id}`);
};
//删除
const deleteBtn = async(id: number) => {
  console.log(id);
  const confirm = await global.$myconfirm('确定删除该数据吗?')
  if(confirm){
    let res = await deleteApi(id)
    if(res && res.code == 200){
      ElMessage.success(res.msg)
      await getList()
    }
  }
};

//提交表单
const commit = () => {
  //验证表单
  addForm.value?.validate(async (valid) => {
    console.log(addModel)
    if (valid) {
      let res = null;
      if(tags.value == '0'){
        res = await addApi(addModel);
      }else{
        res = await editApi(addModel)
      }
      if (res && res.code == 200) {
        ElMessage.success(res.msg);
        await getList()
        onClose();
      }
    }
  });
};
//表格数据
const tableList = ref([]);
//查询表格数据
const getList = async () => {
  let res = await getListApi(searchParm);
  if (res && res.code == 200) {
    tableList.value = res.data.records;
    searchParm.total = res.data.total;
  }
};
//页容量改变时触发
const sizeChange = (size: number) => {
  searchParm.pageSize = size;
  getList();
};
//页数改变时触发
const currentChange = (page: number) => {
  searchParm.currentPage = page;
  getList();
};
//表格高度
const tableHeight = ref(0);
//搜索按钮点击事件
const searchBtn = () => {
  getList();
};
//重置按钮点击事件
const resetBtn = () => {
  searchParm.name = "";
  searchParm.currentPage = 1;
  getList();
};
onMounted(() => {
  getList();
  nextTick(() => {
    tableHeight.value = window.innerHeight - 240;
  });
});
</script>

<style scoped></style>
