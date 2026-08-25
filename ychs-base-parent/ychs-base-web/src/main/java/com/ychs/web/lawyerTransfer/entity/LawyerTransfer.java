package com.ychs.web.lawyerTransfer.entity;

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
@TableName("lawyer_transfer")
@Schema(name = "LawyerTransfer", description = "律师转所记录")
public class LawyerTransfer extends Model<LawyerTransfer> {

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "律师ID")
    @TableField("lawyer_id")
    private Long lawyerId;

    @Schema(description = "转所类型 1调入 2调出")
    @TableField("transfer_type")
    private Byte transferType;

    @Schema(description = "原执业机构")
    @TableField("old_org")
    private String oldOrg;

    @Schema(description = "现执业机构")
    @TableField("new_org")
    private String newOrg;

    @Schema(description = "转所日期")
    @TableField("transfer_date")
    private Date transferDate;

    @Schema(description = "转所审批表PDF地址")
    @TableField("approval_file_url")
    private String approvalFileUrl;

    @Schema(description = "确认状态 0待确认 1已确认 2驳回")
    @TableField("status")
    private Byte status;

    @Schema(description = "确认人（行政主任用户ID）")
    @TableField("confirm_by")
    private Long confirmBy;

    @Schema(description = "审批人账户（登录账号）")
    @TableField("confirm_account")
    private String confirmAccount;

    @Schema(description = "确认时间")
    @TableField("confirm_time")
    private Date confirmTime;

    @Schema(description = "审批意见（驳回时记录驳回理由）")
    @TableField("audit_remark")
    private String auditRemark;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

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