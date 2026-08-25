<template>
  <div class="logincontainer">
    <el-form
        class="loginform"
        :model="loginModel"
        ref="form"
        :rules="rules"
        :inline="false"
        size="large"
    >
      <el-form-item>
        <div class="logintitle">东奥律所</div>
      </el-form-item>
      <el-form-item prop="username">
        <el-input
            placeholder="请输入账户"
            v-model="loginModel.username"
        ></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
            placeholder="请输入密码"
            v-model="loginModel.password"
            type="password"
        ></el-input>
      </el-form-item>
      <el-form-item prop="code">
        <el-row style="width: 100%">
          <el-col :span="16" :offset="0">
            <el-input
                placeholder="请输入验证码"
                v-model="loginModel.code"
            ></el-input>
          </el-col>
          <el-col style="padding-left: 10px" :span="8" :offset="0">
            <img class="images" @click="getImg" :src="imgsrc" style="width: 120px;height: 50px;"/>
          </el-col>
        </el-row>
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12" :offset="0">
          <el-button @click="commit" class="mybtn" type="primary">登录</el-button>
        </el-col>
        <el-col :span="12" :offset="0">
          <el-button type="danger" plain class="mybtn">重置</el-button>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { type FormInstance } from "element-plus";
import { onMounted, reactive, ref } from "vue";
import { getImgApi,loginApi } from "@/api/user/index.ts";
import {userStore} from "@/stores/user/index.ts";
import { useRouter } from "vue-router";
const router = useRouter()
const store = userStore()
//表单ref属性
const form = ref<FormInstance>();
//表单绑定对象
const loginModel = reactive({
  username: "",
  password: "",
  code: "",
});
//表单验证规则
const rules = reactive({
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
  code: [
    {
      required: true,
      trigger: ["blur", "change"],
      message: "请输入验证码",
    },
  ],
});
const imgsrc = ref("");
//获取验证码
const getImg = async () => {
  let res = await getImgApi();
  if (res && res.code == 200) {
    imgsrc.value = res.data;
  }
};
//登录提交
const commit = ()=>{
  form.value?.validate(async(valid)=>{
    if(valid){
      let res = await loginApi(loginModel)
      if(res && res.code == 200){
        //存储用户信息
        console.log(res)
        store.setUserId(res.data.userId)
        store.setNickName(res.data.nickName)
        store.setUserName(res.data.username)
        // 存储token
        store.setToken(res.data.token)
        //跳转首页
        router.push({path:'/'})
      }
    }
  })
}
onMounted(() => {
  // 页面加载时获取验证码（后端会生成验证码并存入session）
  getImg();
});
</script>

<style scoped lang="scss">
.logincontainer {
  height: 100%;
  background-color: #fff;
  background-image: url("../../assets/login.jpg");
  display: flex;
  justify-content: center;
  align-items: center;
  .loginform {
    height: 320px;
    width: 450px;
    padding: 20px 35px;
    border-radius: 10px;
    background-color: #fff;
    .logintitle {
      display: flex;
      justify-content: center;
      color: #606266;
      width: 100%;
      font-size: 24px;
      font-weight: 600;
    }
    .images {
      height: 40px;
      width: 100%;
      cursor: pointer;
    }
    .mybtn {
      width: 100%;
    }
  }
}
</style>
