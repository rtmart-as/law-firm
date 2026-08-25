<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
  <el-main>
    <!-- 基本信息头 -->
    <el-card style="margin-bottom: 10px">
      <el-descriptions :column="4" title="律师基本信息" border>
        <el-descriptions-item label="姓名">{{ lawyer.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ lawyer.gender == 1 ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ fmtDate(lawyer.birthDate) }}</el-descriptions-item>
        <el-descriptions-item label="民族">{{ lawyer.nation }}</el-descriptions-item>

        <el-descriptions-item label="身份证号">{{ lawyer.idCard }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ lawyer.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ lawyer.email }}</el-descriptions-item>
        <el-descriptions-item label="现居住地">{{ lawyer.address }}</el-descriptions-item>

        <el-descriptions-item label="毕业院校">{{ lawyer.graduateSchool }}</el-descriptions-item>
        <el-descriptions-item label="专业">{{ lawyer.major }}</el-descriptions-item>
        <el-descriptions-item label="学历">{{ lawyer.education }}</el-descriptions-item>
        <el-descriptions-item label="政治面貌">{{ lawyer.politicalStatus }}</el-descriptions-item>

        <el-descriptions-item label="入党时间">{{ fmtDate(lawyer.partyTime) }}</el-descriptions-item>
        <el-descriptions-item label="执业证号">{{ lawyer.practiceCertNo }}</el-descriptions-item>
        <el-descriptions-item label="取得职业资格日期">{{ fmtDate(lawyer.qualificationGetDate) }}</el-descriptions-item>
        <el-descriptions-item label="律师类型">{{ lawyer.lawyerType == 2 ? '兼职律师' : '专职律师' }}</el-descriptions-item>

        <el-descriptions-item label="聘用日期">{{ fmtDate(lawyer.hireDate) }}</el-descriptions-item>
        <el-descriptions-item label="在职状态">{{ lawyer.workStatus == 1 ? '在职' : '离职' }}</el-descriptions-item>
        <el-descriptions-item label="是否合伙人">{{ lawyer.isPartner == 1 ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item v-if="lawyer.lawyerType == 2" label="兼职单位">{{ lawyer.partTimeCompany }}</el-descriptions-item>

        <el-descriptions-item label="人大代表/政协委员">{{ lawyer.isCpcOrCommittee == 1 ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item label="首次社保缴费日期">{{ fmtDate(lawyer.firstSocialDate) }}</el-descriptions-item>
        <el-descriptions-item label="社保编号">{{ lawyer.socialNo }}</el-descriptions-item>
        <el-descriptions-item label="社会职务">{{ lawyer.socialPost }}</el-descriptions-item>

        <el-descriptions-item label="业务特长">
          <el-button v-if="lawyer.businessSpecialty" type="primary" link @click="openView('业务特长', lawyer.businessSpecialty)">查看全部</el-button>
        </el-descriptions-item>
        <el-descriptions-item label="受过的奖励">
          <el-button v-if="lawyer.awardRecord" type="primary" link @click="openView('受过的奖励', lawyer.awardRecord)">查看全部</el-button>
        </el-descriptions-item>
        <el-descriptions-item label="受过的处分">
          <el-button v-if="lawyer.punishRecord" type="primary" link @click="openView('受过的处分', lawyer.punishRecord)">查看全部</el-button>
        </el-descriptions-item>
        <el-descriptions-item label="备注">
          <el-button v-if="lawyer.remark" type="primary" link @click="openView('备注', lawyer.remark)">查看全部</el-button>
        </el-descriptions-item>

      </el-descriptions>
      <div style="margin-top:10px">
        <el-button
            v-if="global.$hasPerm(['sys:lawyer:edit'])"
            type="primary" icon="Edit" @click="baseInfoEditBtn"
        >编辑基础信息</el-button>
        <el-button @click="$router.back()">返回</el-button>
      </div>
    </el-card>

    <!-- ============ 入所材料（入职时上传的资料，单独一栏） ============ -->
    <el-card v-if="admissionApply.id" style="margin-bottom: 10px">
      <template #header>入所材料</template>
      <div style="display: flex; flex-wrap: wrap; gap: 12px; align-items: flex-start;">
        <div v-for="att in admissionAtts" :key="att.id"
             style="border: 1px solid var(--el-border-color-light); border-radius: 6px; padding: 8px 12px; background: var(--el-fill-color-light); min-width: 90px; text-align: center;">
          <div style="font-size: 12px; color: var(--el-text-color-secondary); margin-bottom: 6px;">{{ att.attName }}</div>
          <el-link type="primary" :href="att.attUrl" target="_blank">查看</el-link>
        </div>
        <el-empty v-if="!admissionAtts.length" description="暂无入所材料" :image-size="40" />
      </div>
    </el-card>

    <el-card>
      <el-tabs v-model="activeTab">
        <!-- ============ 转所记录 ============ -->
        <el-tab-pane label="转所记录" name="transfer">
          <div style="margin-bottom:10px">
            <el-button v-if="global.$hasPerm(['sys:lawyerTransfer:add'])" type="primary" icon="Plus" @click="transferAddBtn">新增转所</el-button>
          </div>
          <el-table :data="transferList" border stripe>
            <el-table-column prop="transferType" label="类型" width="80" align="center">
              <template #default="scope">{{ scope.row.transferType == 1 ? '调入' : '调出' }}</template>
            </el-table-column>
            <el-table-column prop="oldOrg" label="原执业机构"></el-table-column>
            <el-table-column prop="newOrg" label="现执业机构"></el-table-column>
            <el-table-column prop="transferDate" label="转所日期"></el-table-column>
            <el-table-column label="审批表" align="center">
              <template #default="scope">
                <el-link v-if="scope.row.approvalFileUrl" type="primary" :href="scope.row.approvalFileUrl" target="_blank">查看PDF</el-link>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="scope">
                <el-tag v-if="scope.row.status == 0" type="warning">待确认</el-tag>
                <el-tag v-else-if="scope.row.status == 1" type="success">已确认</el-tag>
                <el-tag v-else type="danger">已驳回</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="auditRemark" label="驳回理由" min-width="120">
              <template #default="scope">
                <span v-if="scope.row.status == 2">{{ scope.row.auditRemark || '—' }}</span>
                <span v-else style="color:#999">—</span>
              </template>
            </el-table-column>
            <el-table-column prop="confirmAccount" label="审批人" width="110" align="center">
              <template #default="scope">
                <span>{{ scope.row.confirmAccount || '—' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="260" align="center">
              <template #default="scope">
                <el-button v-if="global.$hasPerm(['sys:lawyerTransfer:edit'])" type="primary" size="small" @click="transferEditBtn(scope.row)">编辑</el-button>
                <el-button v-if="global.$hasPerm(['sys:lawyerTransfer:confirm']) && scope.row.status == 0"
                           type="success" size="small" @click="transferConfirm(scope.row, 1)">确认</el-button>
                <el-button v-if="global.$hasPerm(['sys:lawyerTransfer:confirm']) && scope.row.status == 0"
                           type="danger" size="small" @click="transferConfirm(scope.row, 2)">驳回</el-button>
                <el-button v-if="global.$hasPerm(['sys:lawyerTransfer:delete'])" type="danger" size="small" plain @click="transferDelete(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- ============ 档案调转 ============ -->
        <el-tab-pane label="档案调转" name="archive">
          <div style="margin-bottom:10px">
            <el-button v-if="global.$hasPerm(['sys:lawyerArchiveTransfer:add'])" type="primary" icon="Plus" @click="archiveAddBtn">新增调转</el-button>
          </div>
          <el-table :data="archiveList" border stripe>
            <el-table-column prop="transferType" label="类型" width="80" align="center">
              <template #default="scope">{{ scope.row.transferType == 1 ? '调出' : '调入' }}</template>
            </el-table-column>
            <el-table-column prop="fromOrg" label="原托管机构"></el-table-column>
            <el-table-column prop="toOrg" label="现托管机构"></el-table-column>
            <el-table-column prop="transferDate" label="调转日期"></el-table-column>
            <el-table-column prop="remark" label="备注"></el-table-column>
            <el-table-column label="操作" width="160" align="center">
              <template #default="scope">
                <el-button v-if="global.$hasPerm(['sys:lawyerArchiveTransfer:edit'])" type="primary" size="small" @click="archiveEditBtn(scope.row)">编辑</el-button>
                <el-button v-if="global.$hasPerm(['sys:lawyerArchiveTransfer:delete'])" type="danger" size="small" plain @click="archiveDelete(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- ============ 证书 ============ -->
        <el-tab-pane label="证书" name="cert">
          <div style="margin-bottom:10px">
            <el-button v-if="global.$hasPerm(['sys:lawyerCert:add'])" type="primary" icon="Plus" @click="certAddBtn">新增证书</el-button>
          </div>
          <el-table :data="certList" border stripe>
            <el-table-column prop="certName" label="证书名称"></el-table-column>
            <el-table-column prop="certNo" label="证书编号"></el-table-column>
            <el-table-column prop="issueOrg" label="发证机关"></el-table-column>
            <el-table-column prop="issueDate" label="发证日期"></el-table-column>
            <el-table-column label="扫描件" align="center">
              <template #default="scope">
                <el-link v-if="scope.row.fileUrl" type="primary" :href="scope.row.fileUrl" target="_blank">查看</el-link>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注"></el-table-column>
            <el-table-column label="操作" width="160" align="center">
              <template #default="scope">
                <el-button v-if="global.$hasPerm(['sys:lawyerCert:edit'])" type="primary" size="small" @click="certEditBtn(scope.row)">编辑</el-button>
                <el-button v-if="global.$hasPerm(['sys:lawyerCert:delete'])" type="danger" size="small" plain @click="certDelete(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- ============ 学术成果 ============ -->
        <el-tab-pane label="学术成果" name="achievement">
          <div style="margin-bottom:10px">
            <el-button v-if="global.$hasPerm(['sys:lawyerAchievement:add'])" type="primary" icon="Plus" @click="achievementAddBtn">新增论文</el-button>
          </div>
          <el-table :data="achievementList" border stripe>
            <el-table-column prop="paperTitle" label="论文题目"></el-table-column>
            <el-table-column prop="journal" label="期刊"></el-table-column>
            <el-table-column prop="journalIssue" label="期号"></el-table-column>
            <el-table-column prop="publisher" label="出版社"></el-table-column>
            <el-table-column prop="publishDate" label="发表日期"></el-table-column>
            <el-table-column label="操作" width="160" align="center">
              <template #default="scope">
                <el-button v-if="global.$hasPerm(['sys:lawyerAchievement:edit'])" type="primary" size="small" @click="achievementEditBtn(scope.row)">编辑</el-button>
                <el-button v-if="global.$hasPerm(['sys:lawyerAchievement:delete'])" type="danger" size="small" plain @click="achievementDelete(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- ============ 法律顾问 ============ -->
        <el-tab-pane label="法律顾问" name="consult">
          <div style="margin-bottom:10px">
            <el-button v-if="global.$hasPerm(['sys:lawyerConsult:add'])" type="primary" icon="Plus" @click="consultAddBtn">新增顾问单位</el-button>
          </div>
          <el-table :data="consultList" border stripe>
            <el-table-column prop="companyName" label="单位名称"></el-table-column>
            <el-table-column prop="position" label="职务"></el-table-column>
            <el-table-column prop="startDate" label="开始日期"></el-table-column>
            <el-table-column prop="endDate" label="结束日期"></el-table-column>
            <el-table-column label="现任" width="80" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.isCurrent == 1 ? 'success' : 'info'">{{ scope.row.isCurrent == 1 ? '是' : '否' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160" align="center">
              <template #default="scope">
                <el-button v-if="global.$hasPerm(['sys:lawyerConsult:edit'])" type="primary" size="small" @click="consultEditBtn(scope.row)">编辑</el-button>
                <el-button v-if="global.$hasPerm(['sys:lawyerConsult:delete'])" type="danger" size="small" plain @click="consultDelete(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- ============ 转所记录弹框 ============ -->
    <SysDialog :title="transferDialog.title" :width="600" :height="360"
               :visible="transferDialog.visible" @on-close="transferOnClose" @on-confirm="transferCommit">
      <template v-slot:content>
        <el-form ref="transferForm" :model="transferModel" label-width="100px" size="default">
          <el-form-item label="转所类型">
            <el-radio-group v-model="transferModel.transferType">
              <el-radio :label="1">调入</el-radio>
              <el-radio :label="2">调出</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="原执业机构"><el-input v-model="transferModel.oldOrg"></el-input></el-form-item>
          <el-form-item label="现执业机构"><el-input v-model="transferModel.newOrg"></el-input></el-form-item>
          <el-form-item label="转所日期">
            <el-date-picker v-model="transferModel.transferDate" type="date" value-format="YYYY-MM-DD"></el-date-picker>
          </el-form-item>
          <el-form-item label="审批表PDF">
            <el-upload :show-file-list="false" :http-request="transferUpload">
              <el-button type="primary" plain>{{ transferModel.approvalFileUrl ? '已上传，点击重传' : '上传审批表' }}</el-button>
            </el-upload>
            <el-link v-if="transferModel.approvalFileUrl" :href="transferModel.approvalFileUrl" target="_blank" type="primary">查看已上传文件</el-link>
          </el-form-item>
          <el-form-item label="备注"><el-input v-model="transferModel.remark" type="textarea"></el-input></el-form-item>
        </el-form>
      </template>
    </SysDialog>

    <!-- ============ 档案调转弹框 ============ -->
    <SysDialog :title="archiveDialog.title" :width="600" :height="320"
               :visible="archiveDialog.visible" @on-close="archiveOnClose" @on-confirm="archiveCommit">
      <template v-slot:content>
        <el-form ref="archiveForm" :model="archiveModel" label-width="100px" size="default">
          <el-form-item label="调转类型">
            <el-radio-group v-model="archiveModel.transferType">
              <el-radio :label="1">调出</el-radio>
              <el-radio :label="2">调入</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="原托管机构"><el-input v-model="archiveModel.fromOrg"></el-input></el-form-item>
          <el-form-item label="现托管机构"><el-input v-model="archiveModel.toOrg"></el-input></el-form-item>
          <el-form-item label="调转日期">
            <el-date-picker v-model="archiveModel.transferDate" type="date" value-format="YYYY-MM-DD"></el-date-picker>
          </el-form-item>
          <el-form-item label="备注"><el-input v-model="archiveModel.remark"></el-input></el-form-item>
        </el-form>
      </template>
    </SysDialog>

    <!-- ============ 证书弹框 ============ -->
    <SysDialog :title="certDialog.title" :width="600" :height="340"
               :visible="certDialog.visible" @on-close="certOnClose" @on-confirm="certCommit">
      <template v-slot:content>
        <el-form ref="certForm" :model="certModel" label-width="100px" size="default">
          <el-form-item label="证书名称" required><el-input v-model="certModel.certName"></el-input></el-form-item>
          <el-form-item label="证书编号"><el-input v-model="certModel.certNo"></el-input></el-form-item>
          <el-form-item label="发证机关"><el-input v-model="certModel.issueOrg"></el-input></el-form-item>
          <el-form-item label="发证日期">
            <el-date-picker v-model="certModel.issueDate" type="date" value-format="YYYY-MM-DD"></el-date-picker>
          </el-form-item>
          <el-form-item label="扫描件">
            <el-upload :show-file-list="false" :http-request="certUpload">
              <el-button type="primary" plain>{{ certModel.fileUrl ? '已上传，点击重传' : '上传扫描件' }}</el-button>
            </el-upload>
            <el-link v-if="certModel.fileUrl" :href="certModel.fileUrl" target="_blank" type="primary">查看已上传文件</el-link>
          </el-form-item>
        </el-form>
      </template>
    </SysDialog>

    <!-- ============ 学术成果弹框 ============ -->
    <SysDialog :title="achievementDialog.title" :width="600" :height="360"
               :visible="achievementDialog.visible" @on-close="achievementOnClose" @on-confirm="achievementCommit">
      <template v-slot:content>
        <el-form ref="achievementForm" :model="achievementModel" label-width="100px" size="default">
          <el-form-item label="论文题目" required><el-input v-model="achievementModel.paperTitle"></el-input></el-form-item>
          <el-form-item label="发表期刊"><el-input v-model="achievementModel.journal"></el-input></el-form-item>
          <el-form-item label="期刊期号"><el-input v-model="achievementModel.journalIssue"></el-input></el-form-item>
          <el-form-item label="出版社"><el-input v-model="achievementModel.publisher"></el-input></el-form-item>
          <el-form-item label="发表日期">
            <el-date-picker v-model="achievementModel.publishDate" type="date" value-format="YYYY-MM-DD"></el-date-picker>
          </el-form-item>
        </el-form>
      </template>
    </SysDialog>

    <!-- ============ 法律顾问弹框 ============ -->
    <SysDialog :title="consultDialog.title" :width="600" :height="320"
               :visible="consultDialog.visible" @on-close="consultOnClose" @on-confirm="consultCommit">
      <template v-slot:content>
        <el-form ref="consultForm" :model="consultModel" label-width="100px" size="default">
          <el-form-item label="单位名称" required><el-input v-model="consultModel.companyName"></el-input></el-form-item>
          <el-form-item label="担任职务"><el-input v-model="consultModel.position"></el-input></el-form-item>
          <el-form-item label="起止日期">
            <el-date-picker v-model="consultDateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始" end-placeholder="结束"></el-date-picker>
          </el-form-item>
          <el-form-item label="是否现任">
            <el-switch v-model="consultModel.isCurrent" :active-value="1" :inactive-value="0"></el-switch>
          </el-form-item>
        </el-form>
      </template>
    </SysDialog>

    <!-- ============ 基础信息编辑弹框 ============ -->
    <SysDialog :title="baseDialog.title" :width="800" :height="600"
               :visible="baseDialog.visible" @on-close="baseOnClose" @on-confirm="baseCommit">
      <template v-slot:content>
        <div style="max-height: 560px; overflow-y: auto; padding-right: 8px;">
          <el-form ref="baseForm" :model="baseModel" :rules="baseRules" label-width="100px" size="default">
            <el-row>
              <el-col :span="12">
                <el-form-item prop="name" label="姓名"><el-input v-model="baseModel.name"></el-input></el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item prop="gender" label="性别">
                  <el-radio-group v-model="baseModel.gender">
                    <el-radio :label="1">男</el-radio>
                    <el-radio :label="0">女</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="出生日期">
                  <el-date-picker v-model="baseModel.birthDate" type="date" value-format="YYYY-MM-DD"></el-date-picker>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="民族"><el-input v-model="baseModel.nation"></el-input></el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="身份证号"><el-input v-model="baseModel.idCard"></el-input></el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号"><el-input v-model="baseModel.phone"></el-input></el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="邮箱"><el-input v-model="baseModel.email"></el-input></el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="现居住地"><el-input v-model="baseModel.address"></el-input></el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="毕业院校"><el-input v-model="baseModel.graduateSchool"></el-input></el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="专业"><el-input v-model="baseModel.major"></el-input></el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="学历"><el-input v-model="baseModel.education"></el-input></el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="政治面貌"><el-input v-model="baseModel.politicalStatus"></el-input></el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="入党时间">
                  <el-date-picker v-model="baseModel.partyTime" type="date" value-format="YYYY-MM-DD"></el-date-picker>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="执业证号"><el-input v-model="baseModel.practiceCertNo"></el-input></el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="取得职业资格日期">
                  <el-date-picker v-model="baseModel.qualificationGetDate" type="date" value-format="YYYY-MM-DD"></el-date-picker>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="律师类型">
                  <el-select v-model="baseModel.lawyerType">
                    <el-option label="专职律师" :value="1"></el-option>
                    <el-option label="兼职律师" :value="2"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="兼职单位"><el-input v-model="baseModel.partTimeCompany"></el-input></el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="是否合伙人">
                  <el-radio-group v-model="baseModel.isPartner">
                    <el-radio :label="1">是</el-radio>
                    <el-radio :label="0">否</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="聘用日期">
                  <el-date-picker v-model="baseModel.hireDate" type="date" value-format="YYYY-MM-DD"></el-date-picker>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="在职状态">
                  <el-select v-model="baseModel.workStatus">
                    <el-option label="在职" :value="1"></el-option>
                    <el-option label="离职" :value="0"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="人大代表/政协委员">
                  <el-radio-group v-model="baseModel.isCpcOrCommittee">
                    <el-radio :label="1">是</el-radio>
                    <el-radio :label="0">否</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="首次社保缴费日期">
                  <el-date-picker v-model="baseModel.firstSocialDate" type="date" value-format="YYYY-MM-DD"></el-date-picker>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="社保编号"><el-input v-model="baseModel.socialNo"></el-input></el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="社会职务"><el-input v-model="baseModel.socialPost"></el-input></el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="业务特长">
              <el-input v-model="baseModel.businessSpecialty" type="textarea" :rows="2"></el-input>
            </el-form-item>
            <el-form-item label="受过的奖励">
              <el-input v-model="baseModel.awardRecord" type="textarea" :rows="2"></el-input>
            </el-form-item>
            <el-form-item label="受过的处分">
              <el-input v-model="baseModel.punishRecord" type="textarea" :rows="2"></el-input>
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="baseModel.remark" type="textarea" :rows="2"></el-input>
            </el-form-item>
          </el-form>
        </div>
      </template>
    </SysDialog>

    <!-- 查看长文本（业务特长/受过的奖励/受过的处分/备注） -->
    <el-dialog v-model="viewTextVisible" :title="viewTextTitle" width="500px" append-to-body>
      <div style="white-space: pre-wrap; word-break: break-all; line-height: 1.8;">{{ viewTextContent }}</div>
    </el-dialog>
  </el-main>
</template>

<script setup lang="ts">
import { nextTick, onMounted, reactive, ref, watch } from "vue";
import { useRoute } from "vue-router";
import useInstance from "@/hooks/useInstance";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";
import SysDialog from "@/components/SysDialog.vue";
import { tabStore } from "@/stores/tabs";
import { getByIdApi, editApi } from "@/api/lawyer/index.ts";          // 需要新增：按id查律师
import { uploadFileApi } from "@/api/lawyer/index.ts";
import * as transferApi from "@/api/lawyerTransfer/index.ts";
import * as archiveApi from "@/api/lawyerArchiveTransfer/index.ts";
import * as certApi from "@/api/lawyerCert/index.ts";
import * as achievementApi from "@/api/lawyerAchievement/index.ts";
import * as consultApi from "@/api/lawyerConsult/index.ts";
import * as admissionApi from "@/api/admission/index.ts";

const { global } = useInstance();
const route = useRoute();
// 完善档案走路径参数 /lawyer/detail/:id；用 ref 便于在标签页间切换时(同一组件复用)重新加载
const currentLawyerId = ref(Number(route.params.id));

const activeTab = ref('transfer');
const lawyer = ref<any>({});

// ======== 转所记录 ========
const transferList = ref([]);
const transferDialog = reactive({ title: '新增转所', visible: false });
const transferForm = ref();
const transferModel = reactive<any>({
  id: '', lawyerId: currentLawyerId.value, transferType: 1, oldOrg: '', newOrg: '',
  transferDate: '', approvalFileUrl: '', remark: ''
});
const transferTags = ref('0');

const getTransferList = async () => {
  const res = await transferApi.getListApi({ currentPage: 1, pageSize: 100, lawyerId: currentLawyerId.value });
  if (res && res.code == 200) transferList.value = res.data.records;
};
const transferAddBtn = () => {
  transferTags.value = '0';
  transferDialog.title = '新增转所';
  transferDialog.visible = true;
  Object.assign(transferModel, { id: '', transferType: 1, oldOrg: '', newOrg: '', transferDate: '', approvalFileUrl: '', remark: '' });
};
const transferEditBtn = (row: any) => {
  transferTags.value = '1';
  transferDialog.title = '编辑转所';
  transferDialog.visible = true;
  Object.assign(transferModel, row);
};
const transferOnClose = () => { transferDialog.visible = false; };
const transferCommit = async () => {
  let res;
  if (transferTags.value == '0') res = await transferApi.addApi(transferModel);
  else res = await transferApi.editApi(transferModel);
  if (res && res.code == 200) {
    ElMessage.success(res.msg);
    transferDialog.visible = false;
    await getTransferList();
  }
};
const transferUpload = async (options: any) => {
  const res = await uploadFileApi(options.file);
  if (res && res.code == 200) {
    transferModel.approvalFileUrl = res.data;
    ElMessage.success('上传成功');
  }
};
const transferConfirm = async (row: any, status: number) => {
  let auditRemark = '确认通过';
  if (status == 2) {
    // 驳回时用 Element Plus 弹窗录入驳回理由
    const { value } = await ElMessageBox.prompt('请输入驳回理由', '驳回', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputPlaceholder: '请填写驳回理由',
      inputValidator: (val: string) => (val && val.trim() ? true : '驳回理由不能为空')
    }).catch(() => ({ value: null }));
    if (value == null) return; // 用户取消驳回
    auditRemark = value;
  }
  const res = await transferApi.confirmApi({ id: row.id, status, auditRemark });
  if (res && res.code == 200) {
    ElMessage.success(res.msg);
    await getTransferList();
  }
};
const transferDelete = async (id: number) => {
  const confirm = await global.$myconfirm('确定删除该转所记录吗?');
  if (confirm) {
    const res = await transferApi.deleteApi(id);
    if (res && res.code == 200) { ElMessage.success(res.msg); await getTransferList(); }
  }
};

// ======== 档案调转 ========
const archiveList = ref([]);
const archiveDialog = reactive({ title: '新增调转', visible: false });
const archiveModel = reactive<any>({ id: '', lawyerId: currentLawyerId.value, transferType: 1, fromOrg: '', toOrg: '', transferDate: '', remark: '' });
const archiveTags = ref('0');

const getArchiveList = async () => {
  const res = await archiveApi.getListApi({ currentPage: 1, pageSize: 100, lawyerId: currentLawyerId.value });
  if (res && res.code == 200) archiveList.value = res.data.records;
};
const archiveAddBtn = () => {
  archiveTags.value = '0'; archiveDialog.title = '新增调转'; archiveDialog.visible = true;
  Object.assign(archiveModel, { id: '', transferType: 1, fromOrg: '', toOrg: '', transferDate: '', remark: '' });
};
const archiveEditBtn = (row: any) => {
  archiveTags.value = '1'; archiveDialog.title = '编辑调转'; archiveDialog.visible = true;
  Object.assign(archiveModel, row);
};
const archiveOnClose = () => { archiveDialog.visible = false; };
const archiveCommit = async () => {
  let res;
  if (archiveTags.value == '0') res = await archiveApi.addApi(archiveModel);
  else res = await archiveApi.editApi(archiveModel);
  if (res && res.code == 200) { ElMessage.success(res.msg); archiveDialog.visible = false; await getArchiveList(); }
};
const archiveDelete = async (id: number) => {
  const confirm = await global.$myconfirm('确定删除该档案调转记录吗?');
  if (confirm) {
    const res = await archiveApi.deleteApi(id);
    if (res && res.code == 200) { ElMessage.success(res.msg); await getArchiveList(); }
  }
};

// ======== 证书 ========
const certList = ref([]);
const certDialog = reactive({ title: '新增证书', visible: false });
const certModel = reactive<any>({ id: '', lawyerId: currentLawyerId.value, certName: '', certNo: '', issueOrg: '', issueDate: '', fileUrl: '' });
const certTags = ref('0');

const getCertList = async () => {
  const res = await certApi.getListApi({ currentPage: 1, pageSize: 100, lawyerId: currentLawyerId.value });
  if (res && res.code == 200) certList.value = res.data.records;
};
const certAddBtn = () => {
  certTags.value = '0'; certDialog.title = '新增证书'; certDialog.visible = true;
  Object.assign(certModel, { id: '', certName: '', certNo: '', issueOrg: '', issueDate: '', fileUrl: '' });
};
const certEditBtn = (row: any) => {
  certTags.value = '1'; certDialog.title = '编辑证书'; certDialog.visible = true;
  Object.assign(certModel, row);
};
const certOnClose = () => { certDialog.visible = false; };
const certCommit = async () => {
  let res;
  if (certTags.value == '0') res = await certApi.addApi(certModel);
  else res = await certApi.editApi(certModel);
  if (res && res.code == 200) { ElMessage.success(res.msg); certDialog.visible = false; await getCertList(); }
};
const certUpload = async (options: any) => {
  const res = await uploadFileApi(options.file);
  if (res && res.code == 200) { certModel.fileUrl = res.data; ElMessage.success('上传成功'); }
};
const certDelete = async (id: number) => {
  const confirm = await global.$myconfirm('确定删除该证书吗?');
  if (confirm) {
    const res = await certApi.deleteApi(id);
    if (res && res.code == 200) { ElMessage.success(res.msg); await getCertList(); }
  }
};

// ======== 学术成果 ========
const achievementList = ref([]);
const achievementDialog = reactive({ title: '新增论文', visible: false });
const achievementModel = reactive<any>({ id: '', lawyerId: currentLawyerId.value, paperTitle: '', journal: '', journalIssue: '', publisher: '', publishDate: '' });
const achievementTags = ref('0');

const getAchievementList = async () => {
  const res = await achievementApi.getListApi({ currentPage: 1, pageSize: 100, lawyerId: currentLawyerId.value });
  if (res && res.code == 200) achievementList.value = res.data.records;
};
const achievementAddBtn = () => {
  achievementTags.value = '0'; achievementDialog.title = '新增论文'; achievementDialog.visible = true;
  Object.assign(achievementModel, { id: '', paperTitle: '', journal: '', journalIssue: '', publisher: '', publishDate: '' });
};
const achievementEditBtn = (row: any) => {
  achievementTags.value = '1'; achievementDialog.title = '编辑论文'; achievementDialog.visible = true;
  Object.assign(achievementModel, row);
};
const achievementOnClose = () => { achievementDialog.visible = false; };
const achievementCommit = async () => {
  let res;
  if (achievementTags.value == '0') res = await achievementApi.addApi(achievementModel);
  else res = await achievementApi.editApi(achievementModel);
  if (res && res.code == 200) { ElMessage.success(res.msg); achievementDialog.visible = false; await getAchievementList(); }
};
const achievementDelete = async (id: number) => {
  const confirm = await global.$myconfirm('确定删除该论文吗?');
  if (confirm) {
    const res = await achievementApi.deleteApi(id);
    if (res && res.code == 200) { ElMessage.success(res.msg); await getAchievementList(); }
  }
};

// ======== 法律顾问 ========
const consultList = ref([]);
const consultDialog = reactive({ title: '新增顾问单位', visible: false });
const consultDateRange = ref<string[]>([]);
const consultModel = reactive<any>({ id: '', lawyerId: currentLawyerId.value, companyName: '', position: '', startDate: '', endDate: '', isCurrent: 1 });
const consultTags = ref('0');

const getConsultList = async () => {
  const res = await consultApi.getListApi({ currentPage: 1, pageSize: 100, lawyerId: currentLawyerId.value });
  if (res && res.code == 200) consultList.value = res.data.records;
};
const consultAddBtn = () => {
  consultTags.value = '0'; consultDialog.title = '新增顾问单位'; consultDialog.visible = true;
  consultDateRange.value = [];
  Object.assign(consultModel, { id: '', companyName: '', position: '', startDate: '', endDate: '', isCurrent: 1 });
};
const consultEditBtn = (row: any) => {
  consultTags.value = '1'; consultDialog.title = '编辑顾问单位'; consultDialog.visible = true;
  Object.assign(consultModel, row);
  consultDateRange.value = [row.startDate || '', row.endDate || ''];
};
const consultOnClose = () => { consultDialog.visible = false; };
const consultCommit = async () => {
  consultModel.startDate = consultDateRange.value?.[0] || '';
  consultModel.endDate = consultDateRange.value?.[1] || '';
  let res;
  if (consultTags.value == '0') res = await consultApi.addApi(consultModel);
  else res = await consultApi.editApi(consultModel);
  if (res && res.code == 200) { ElMessage.success(res.msg); consultDialog.visible = false; await getConsultList(); }
};
const consultDelete = async (id: number) => {
  const confirm = await global.$myconfirm('确定删除该顾问单位吗?');
  if (confirm) {
    const res = await consultApi.deleteApi(id);
    if (res && res.code == 200) { ElMessage.success(res.msg); await getConsultList(); }
  }
};

// ======== 入所申请信息（按 lawyerId 关联审批通过生成的律师） ========
const admissionApply = ref<any>({});
const admissionAtts = ref<any[]>([]);
const loadAdmissionInfo = async (lawyerId: number) => {
  admissionApply.value = {};
  admissionAtts.value = [];
  if (!lawyerId) return;
  const res = await admissionApi.getListApi({ currentPage: 1, pageSize: 1, lawyerId });
  if (res && res.code == 200 && res.data.records?.length) {
    admissionApply.value = res.data.records[0];
    const attRes = await admissionApi.getAttachmentListApi(admissionApply.value.id);
    if (attRes && attRes.code == 200) admissionAtts.value = attRes.data || [];
  }
};

// ======== 基础信息编辑（补充新增律师时未填的信息） ========
const baseDialog = reactive({ title: '编辑基础信息', visible: false });
const baseForm = ref<FormInstance>();
const baseModel = reactive<any>({
  id: '', name: '', gender: 1, nation: '', birthDate: '', graduateSchool: '', major: '',
  education: '', politicalStatus: '', partyTime: '', idCard: '', phone: '', email: '',
  address: '', practiceCertNo: '', qualificationGetDate: '', businessSpecialty: '',
  isPartner: 0, hireDate: '', lawyerType: 1, partTimeCompany: '', isCpcOrCommittee: 0,
  awardRecord: '', punishRecord: '', workStatus: 1, remark: '', firstSocialDate: '',
  socialNo: '', socialPost: ''
});
const baseRules = reactive({
  name: [{ required: true, trigger: ['blur', 'change'], message: '请输入姓名' }]
});
// 后端未配置 Jackson 日期格式，java.util.Date 在 JSON 里是 epoch 毫秒数字，
// 统一转成 YYYY-MM-DD 字符串供 el-date-picker(value-format) 回显。
const fmtDate = (v: any) => {
  if (v === null || v === undefined || v === '') return '';
  if (typeof v === 'number') {
    const d = new Date(v);
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${d.getFullYear()}-${m}-${day}`;
  }
  return String(v).slice(0, 10);
};
const baseInfoEditBtn = () => {
  baseDialog.visible = true;
  const l = lawyer.value || {};
  Object.assign(baseModel, {
    id: l.id, name: l.name || '', gender: l.gender == 1 ? 1 : 0, nation: l.nation || '',
    birthDate: fmtDate(l.birthDate), graduateSchool: l.graduateSchool || '', major: l.major || '',
    education: l.education || '', politicalStatus: l.politicalStatus || '', partyTime: fmtDate(l.partyTime),
    idCard: l.idCard || '', phone: l.phone || '', email: l.email || '', address: l.address || '',
    practiceCertNo: l.practiceCertNo || '', qualificationGetDate: fmtDate(l.qualificationGetDate),
    businessSpecialty: l.businessSpecialty || '', isPartner: l.isPartner == 1 ? 1 : 0,
    hireDate: fmtDate(l.hireDate), lawyerType: l.lawyerType == 2 ? 2 : 1, partTimeCompany: l.partTimeCompany || '',
    isCpcOrCommittee: l.isCpcOrCommittee == 1 ? 1 : 0, awardRecord: l.awardRecord || '',
    punishRecord: l.punishRecord || '', workStatus: l.workStatus == 1 ? 1 : 0, remark: l.remark || '',
    firstSocialDate: fmtDate(l.firstSocialDate), socialNo: l.socialNo || '', socialPost: l.socialPost || ''
  });
  nextTick(() => {
    baseForm.value?.clearValidate();
  });
};
const baseOnClose = () => { baseDialog.visible = false; };

// ======== 查看长文本（业务特长/受过的奖励/受过的处分/备注） ========
const viewTextVisible = ref(false);
const viewTextTitle = ref('');
const viewTextContent = ref('');
const openView = (title: string, content: string) => {
  viewTextTitle.value = title;
  viewTextContent.value = content || '';
  viewTextVisible.value = true;
};

const baseCommit = () => {
  baseForm.value?.validate(async (valid) => {
    if (valid) {
      const res = await editApi(baseModel);
      if (res && res.code == 200) {
        ElMessage.success(res.msg);
        baseDialog.visible = false;
        // 刷新顶部基本信息
        const r = await getByIdApi(currentLawyerId.value);
        if (r && r.code == 200) lawyer.value = r.data;
      }
    }
  });
};

// 标签页标题带上律师姓名，便于"多开"时区分
const updateTabTitle = (name: string) => {
  route.meta.title = name ? `${name}·完善档案` : '完善档案';
  const store = tabStore();
  const tab = store.tabList.find((t) => t.path === route.path);
  if (tab) tab.title = route.meta.title as string;
};

// 按 id 加载律师基本信息 + 五个子列表
const loadAll = async (id: number) => {
  currentLawyerId.value = id;
  route.meta.title = '完善档案';
  // 同步各子表单的 lawyerId，新增子记录时落到当前律师名下
  transferModel.lawyerId = id;
  archiveModel.lawyerId = id;
  certModel.lawyerId = id;
  achievementModel.lawyerId = id;
  consultModel.lawyerId = id;
  const res = await getByIdApi(id);
  if (res && res.code == 200) {
    lawyer.value = res.data;
    updateTabTitle(res.data?.name);
  }
  await getTransferList();
  await getArchiveList();
  await getCertList();
  await getAchievementList();
  await getConsultList();
  await loadAdmissionInfo(id);
};

onMounted(() => {
  loadAll(Number(route.params.id));
});

// 支持"多开"：同一组件复用时（在多个完善档案标签间切换），按路由参数重新加载
watch(
  () => route.params.id,
  (newId) => {
    const id = Number(newId);
    if (id) loadAll(id);
  }
);
</script>

<style scoped></style>
