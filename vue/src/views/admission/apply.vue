<template>
  <el-main>
    <el-card>
      <template #header>入所审批申请</template>
      <el-form ref="applyForm" :model="applyModel" label-width="120px" style="max-width:640px">
        <el-form-item label="姓名" required>
          <el-input v-model="applyModel.applicantName"></el-input>
        </el-form-item>
        <el-form-item label="申请账户">
          <el-input :value="uStore.getUserName" disabled placeholder="自动带出当前登录账户"></el-input>
        </el-form-item>
        <el-form-item label="性别" required>
          <el-radio-group v-model="applyModel.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="身份证号" required>
          <el-input v-model="applyModel.idCard"></el-input>
        </el-form-item>
        <el-form-item label="手机号" required>
          <el-input v-model="applyModel.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="applyModel.email"></el-input>
        </el-form-item>
        <el-form-item label="审批表模板">
          <el-button type="primary" plain @click="downloadTpl">下载入所审批表模板</el-button>
        </el-form-item>
        <el-form-item label="上传审批表">
          <el-upload :show-file-list="false" :http-request="uploadForm">
            <el-button>{{ applyModel.formUrl ? '已上传，点击重传' : '上传填好的审批表' }}</el-button>
          </el-upload>
          <el-link v-if="applyModel.formUrl" :href="applyModel.formUrl" target="_blank" type="primary">查看已上传</el-link>
        </el-form-item>
        <el-form-item v-for="item in attTypes" :key="item.value" :label="item.label">
          <el-upload :show-file-list="false" :http-request="(opt:any) => uploadAtt(item.value, opt)">
            <el-button>{{ attachments[item.value] ? '已上传，点击重传' : '上传' }}</el-button>
          </el-upload>
          <el-link v-if="attachments[item.value]" :href="attachments[item.value]" target="_blank" type="primary">查看</el-link>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitApply">提交申请</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </el-main>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { downloadTemplateApi, addApplyApi, addAttachmentApi } from "@/api/admission/index.ts";
import { uploadFileApi } from "@/api/lawyer/index.ts";
import { userStore } from "@/stores/user";

const router = useRouter();
const uStore = userStore();
const applyModel = reactive<any>({ applicantName: '', gender: 1, idCard: '', phone: '', email: '', formUrl: '' });

const attTypes = [
  { label: '身份证', value: 1 },
  { label: '毕业证', value: 2 },
  { label: '学位证', value: 3 },
  { label: '法律职业资格证', value: 4 },
  { label: '蓝底一寸照片', value: 5 },
  { label: '个人简历', value: 6 },
  { label: '其他', value: 7 }
];
const attachments = reactive<any>({});

const downloadTpl = async () => {
  try {
    await downloadTemplateApi();
    ElMessage.success('模板已开始下载');
  } catch (e) {
    ElMessage.error('模板下载失败，请稍后重试');
  }
};

const uploadForm = async (options: any) => {
  const res = await uploadFileApi(options.file);
  if (res && res.code == 200) {
    applyModel.formUrl = res.data;
    ElMessage.success('审批表上传成功');
  }
};

const uploadAtt = async (type: number, options: any) => {
  const res = await uploadFileApi(options.file);
  if (res && res.code == 200) {
    attachments[type] = res.data;
    ElMessage.success('上传成功');
  }
};

const submitApply = async () => {
  if (!applyModel.applicantName || !applyModel.idCard || !applyModel.phone) {
    ElMessage.warning('请填写姓名、身份证号和手机号');
    return;
  }
  const res = await addApplyApi(applyModel);
  if (res && res.code == 200) {
    const applyId = res.data?.id;
    // 逐个保存分类附件
    for (const type of attTypes) {
      if (attachments[type.value]) {
        await addAttachmentApi({ applyId, attType: type.value, attName: type.label, attUrl: attachments[type.value] });
      }
    }
    ElMessage.success(res.msg);
    router.back();
  }
};
</script>

<style scoped></style>
