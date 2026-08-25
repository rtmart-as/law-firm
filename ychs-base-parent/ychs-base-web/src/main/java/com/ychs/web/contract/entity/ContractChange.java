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
@TableName("contract_change")
@Schema(name = "ContractChange", description = "合同变更记录")
public class ContractChange extends Model<ContractChange> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "合同ID")
    @TableField("contract_id")
    private Long contractId;

    @Schema(description = "变更类型 1追加律师费 2退律师费")
    @TableField("change_type")
    private Byte changeType;

    @Schema(description = "变更金额")
    @TableField("change_amount")
    private BigDecimal changeAmount;

    @Schema(description = "变更日期")
    @TableField("change_date")
    private Date changeDate;

    @Schema(description = "情况说明PDF地址")
    @TableField("reason_file_url")
    private String reasonFileUrl;

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