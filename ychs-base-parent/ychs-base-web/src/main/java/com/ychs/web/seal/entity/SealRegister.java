package com.ychs.web.seal.entity;

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
@TableName("seal_register")
@Schema(name = "SealRegister", description = "印章登记")
public class SealRegister extends Model<SealRegister> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "用章日期")
    @TableField("use_date")
    private Date useDate;

    @Schema(description = "使用原因")
    @TableField("use_reason")
    private String useReason;

    @Schema(description = "份数")
    @TableField("copy_count")
    private Integer copyCount;

    @Schema(description = "公章种类（填写）")
    @TableField("seal_type")
    private String sealType;

    @Schema(description = "经办律师ID")
    @TableField("handler_lawyer_id")
    private Long handlerLawyerId;

    @Schema(description = "经办律师姓名")
    @TableField("handler_name")
    private String handlerName;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

    @Schema(description = "审批状态 0待审批 1已通过 2已驳回")
    @TableField("status")
    private Byte status;

    @Schema(description = "登记账户（登录账号）")
    @TableField("register_account")
    private String registerAccount;

    @Schema(description = "审批人（用户ID）")
    @TableField("audit_by")
    private Long auditBy;

    @Schema(description = "审批账户（登录账号）")
    @TableField("audit_account")
    private String auditAccount;

    @Schema(description = "审批时间")
    @TableField("audit_time")
    private Date auditTime;

    @Schema(description = "审批意见")
    @TableField("audit_remark")
    private String auditRemark;

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