package com.ychs.web.admission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("admission_apply")
@Schema(name = "AdmissionApply", description = "入所审批申请")
public class AdmissionApply extends Model<AdmissionApply> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "申请人姓名")
    @TableField("applicant_name")
    private String applicantName;

    @Schema(description = "申请账户（登录账号）")
    @TableField("apply_account")
    private String applyAccount;

    @Schema(description = "身份证号")
    @TableField("id_card")
    private String idCard;

    @Schema(description = "性别 1男 0女")
    @TableField("gender")
    private Byte gender;

    @Schema(description = "手机号")
    @TableField("phone")
    private String phone;

    @Schema(description = "邮箱")
    @TableField("email")
    private String email;

    @Schema(description = "填好的审批表地址")
    @TableField("form_url")
    private String formUrl;

    @Schema(description = "审批状态 0待主任确认 1已通过 2已驳回")
    @TableField("status")
    private Byte status;

    @Schema(description = "审批人（主任用户ID）")
    @TableField("audit_by")
    private Long auditBy;

    @Schema(description = "审批人账户（登录账号）")
    @TableField("audit_account")
    private String auditAccount;

    @Schema(description = "审批时间")
    @TableField("audit_time")
    private Date auditTime;

    @Schema(description = "审批意见")
    @TableField("audit_remark")
    private String auditRemark;

    @Schema(description = "审批通过后生成的律师ID")
    @TableField("lawyer_id")
    private Long lawyerId;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @Schema(description = "删除标识 0正常 1删除")
    @TableField("del_flag")
    private Byte delFlag;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}