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
@TableName("contract_cancel")
@Schema(name = "ContractCancel", description = "合同解除记录")
public class ContractCancel extends Model<ContractCancel> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "合同ID")
    @TableField("contract_id")
    private Long contractId;

    @Schema(description = "解除原因")
    @TableField("cancel_reason")
    private String cancelReason;

    @Schema(description = "解除合同PDF地址")
    @TableField("file_url")
    private String fileUrl;

    @Schema(description = "解除后金额（完全解除为0）")
    @TableField("amount_after")
    private BigDecimal amountAfter;

    @Schema(description = "解除日期")
    @TableField("cancel_date")
    private Date cancelDate;

    @Schema(description = "操作人用户ID")
    @TableField("operator")
    private Long operator;

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