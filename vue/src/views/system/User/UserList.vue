<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
  <el-main>
    <!-- 搜索栏 -->
    <el-form :model="searchParm" :inline="true" size="default">
      <el-form-item>
        <el-input
            placeholder="请输入姓名"
            v-model="searchParm.nickName"
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-input
            placeholder="请输入电话"
            v-model="searchParm.phone"
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-button icon="Search" @click="searchBtn">搜索</el-button>
        <el-button icon="Close" type="danger" @click="resetBtn" plain>重置</el-button>
        <el-button icon="Plus" type="primary" @click="addBtn">新增</el-button>
      </el-form-item>
    </el-form>
    <!-- 表格 -->
    <el-table :height="tableHeight" :data="tableList" border stripe>
      <el-table-column prop="nickName" label="姓名" align="center"></el-table-column>
      <el-table-column prop="sex" label="性别" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.sex == '0'" type="primary" size="default" effect="dark" >男</el-tag>
          <el-tag v-if="scope.row.sex == '1'" type="danger" size="default" effect="dark" >女</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="电话" align="center"></el-table-column>
      <el-table-column prop="email" label="邮箱" align="center"></el-table-column>
      <el-table-column align="center" width="400" label="操作">
        <template #default="scope">
          <el-button type="primary" icon="Edit" size="default" @click="editBtn(scope.row)">编辑</el-button>
          <el-button type="warning" icon="Setting" size="default" @click="resetPasswordBtn(scope.row.userId)">重置密码
          </el-button>
          <el-button type="danger" icon="Delete" size="default" @click="deleteBtn(scope.row.userId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <el-pagination
        @size-change="sizeChange"
        @current-change="currentChange"
        :current-page.sync="searchParm.currentPage"
        :page-sizes="[10,20, 40, 80, 100]"
        :page-size="searchParm.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="searchParm.total" background>
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
              <el-form-item prop="nickName" label="姓名：">
                <el-input v-model="addModel.nickName"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12" :offset="0">
              <el-form-item prop="sex" label="性别：">
                <el-radio-group v-model="addModel.sex">
                  <el-radio :value="'0'">男</el-radio>
                  <el-radio :value="'1'">女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12" :offset="0">
              <el-form-item prop="phone" label="电话：">
                <el-input v-model="addModel.phone"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12" :offset="0">
              <el-form-item prop="email" label="邮箱：">
                <el-input v-model="addModel.email"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12" :offset="0">
              <el-form-item prop="roleId" label="角色：">
                <SelectChecked
                    ref="selectRef"
                    :options="options"
                    :bindValue="bindValue"
                    @selected="selected"
                ></SelectChecked>
              </el-form-item>
            </el-col>
            <el-col :span="12" :offset="0">
              <el-form-item prop="username" label="账户：">
                <el-input v-model="addModel.username"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12" :offset="0">
              <el-form-item prop="password" label="密码：">
                <el-input type="password" v-model="addModel.password"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <!-- 密码输入框只有上面这一个（新增/编辑都显示），原来多余的 v-if="tags=='0'" 重复密码框已删除 -->
        </el-form>
      </template>
    </SysDialog>
  </el-main>
</template>

<script setup lang="ts">
import {nextTick, onMounted, reactive, ref} from "vue";
import SysDialog from "@/components/SysDialog.vue";
import useDialog from "@/hooks/useDialog";
import {ElMessage, type FormInstance} from "element-plus";
import SelectChecked from "@/components/SelectChecked.vue";
import {getSelectApi} from '@/api/role/index.ts'
import {addApi, getListApi, editApi, getRoleListApi, deleteApi, resetPasswordApi} from '@/api/user/index.ts'
import {type User} from '@/api/user/UserModel'
import useInstance from "@/hooks/useInstance.ts";

const {global} = useInstance()
const selectRef = ref();
//下拉数据
let options = ref([])
//表单ref属性
const addForm = ref<FormInstance>();
//弹框属性
const {dialog, onClose, onShow} = useDialog();
//搜索栏绑定对象
const searchParm = reactive({
  phone: "",
  nickName: "",
  currentPage: 1,
  pageSize: 10,
  total: 0
});
//新增绑定对象
const addModel = reactive({
  userId: "",
  username: "",
  password: "",
  phone: "",
  email: "",
  sex: "",
  nickName: "",
  roleId: ''
});
//表单验证规则
const rules = reactive({
  nickName: [
    {
      required: true,
      trigger: ["blur", "change"],
      message: "请输入姓名",
    },
  ],
  sex: [
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
  username: [
    {
      required: true,
      trigger: ["blur", "change"],
      message: "请输入账户",
    },
  ],
  password: [
    {
      required: true,
      trigger: ["blur", "change"],
      message: "请输入密码",
    },
  ],
  roleId: [
    {
      required: true,
      trigger: ["blur", "change"],
      message: "请选择角色",
    },
  ]
});
//查询角色下拉数据
const getSelect = async () => {
  let res = await getSelectApi()
  if (res && res.code == 200) {
    options.value = [];
    options.value = res.data;
  }
}
//用户拥有的角色id
const bindValue = ref([])
const roleIds = ref('')
//根据用户id查询角色
const getRoleList = async (userId: string) => {
  let res = await getRoleListApi(userId)
  if (res && res.code == 200) {
    bindValue.value = res.data;
    console.log(res.data)
    roleIds.value = res.data.join(",")
    console.log(roleIds.value)
  }
}
const tags = ref('')
//新增按钮
const addBtn = () => {
  //标记为"新增"模式（tags 初始是 ''，不重置的话 commit() 会误走"编辑"PUT 接口，导致点新增却插不进数据）
  tags.value = '0';
  //清空下拉数据
  options.value = [];
  //获取下拉数据
  getSelect()
  dialog.title = "新增";
  dialog.height = 230;
  //显示弹框
  onShow();
  nextTick(() => {
    //清空下拉数据
    selectRef.value.clear()
  })
  //清空表单
  addForm.value?.resetFields()
};
//编辑
const editBtn = async (row: User) => {
  tags.value = '1'
  dialog.title = "编辑";
  dialog.height = 230;
  //清空下拉数据
  options.value = [];
  bindValue.value = [];
  //获取下拉数据
  await getSelect();
  //查询角色Id
  await getRoleList(row.userId);
  //显示弹框
  onShow();
  nextTick(() => {
    //数据回显
    Object.assign(addModel, row);
    //设置角色的id
    addModel.roleId = roleIds.value;
    addModel.password = ''
  });

  //清空表单
  addForm.value?.resetFields();
}
//删除
const deleteBtn = async (userId: string) => {
  console.log(userId);
  const confirm = await global.$myconfirm('确定删除该数据吗?')
  if (confirm) {
    let res = await deleteApi(userId)
    if (res && res.code == 200) {
      ElMessage.success(res.msg)
      getList()
    }
  }
};
//重置密码
const resetPasswordBtn = async (userId: string) => {
  const confirm = await global.$myconfirm("确定重置密码吗,重置之后密码是【666666】?");
  if (confirm) {
    let res = await resetPasswordApi({userId: userId});
    if (res && res.code == 200) {
      ElMessage.success(res.msg);
      getList();
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
      if (tags.value == '0') {
        res = await addApi(addModel);
      } else {
        res = await editApi(addModel)
      }
      if (res && res.code == 200) {
        ElMessage.success(res.msg);
        getList()
        onClose();
      }
    }
  });
};
//勾选的值
const selected = (value: Array<string | number>) => {
  console.log(value)
  addModel.roleId = value.join(',')
  console.log(addModel)
}
//表格数据
const tableList = ref([])
//查询表格数据
const getList = async () => {
  let res = await getListApi(searchParm)
  if (res && res.code == 200) {
    tableList.value = res.data.records;
    searchParm.total = res.data.total;
  }
}
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
const tableHeight = ref(0)
//搜索按钮点击事件
const searchBtn = () => {
  getList()
}
//重置按钮点击事件
const resetBtn = () => {
  searchParm.nickName = ''
  searchParm.phone = ''
  searchParm.currentPage = 1;
  getList()
}
onMounted(() => {
  getList()
  nextTick(() => {
    tableHeight.value = window.innerHeight - 240
  })
})
</script>

<style scoped></style>
