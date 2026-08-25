package com.ychs.web.contract.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("contract")
@Schema(name = "Contract", description = "委托代理合同")
public class Contract extends Model<Contract> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "合同编号")
    @TableField("contract_no")
    private String contractNo;

    @Schema(description = "合同类型")
    @TableField("contract_type")
    private String contractType;

    @Schema(description = "案件类型")
    @TableField("case_type")
    private String caseType;

    @Schema(description = "案由")
    @TableField("cause")
    private String cause;

    @Schema(description = "委托人")
    @TableField("client_name")
    private String clientName;

    @Schema(description = "承办律师ID")
    @TableField("lawyer_id")
    private Long lawyerId;

    @Schema(description = "承办律师姓名")
    @TableField("lawyer_name")
    private String lawyerName;

    @Schema(description = "合同领用日期")
    @TableField("receive_date")
    private Date receiveDate;

    @Schema(description = "领取人")
    @TableField("receiver")
    private String receiver;

    @Schema(description = "是否交回 0否 1是")
    @TableField("is_returned")
    private Byte isReturned;

    @Schema(description = "合同金额")
    @TableField("contract_amount")
    private BigDecimal contractAmount;

    @Schema(description = "收据金额")
    @TableField("receipt_amount")
    private BigDecimal receiptAmount;

    @Schema(description = "开票金额")
    @TableField("invoice_amount")
    private BigDecimal invoiceAmount;

    @Schema(description = "管理费（开票填0，主任填）")
    @TableField("manage_fee")
    private BigDecimal manageFee;

    @Schema(description = "收案金额（开票填0，主任填）")
    @TableField("accept_amount")
    private BigDecimal acceptAmount;

    @Schema(description = "缴费时间（主任填）")
    @TableField("pay_time")
    private Date payTime;

    @Schema(description = "是否已开票 0否 1是")
    @TableField("invoice_flag")
    private Byte invoiceFlag;

    @Schema(description = "是否已开收据 0否 1是")
    @TableField("receipt_flag")
    private Byte receiptFlag;

    @Schema(description = "合同状态 1正常 2解除 3变更 4收回")
    @TableField("status")
    private Byte status;

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