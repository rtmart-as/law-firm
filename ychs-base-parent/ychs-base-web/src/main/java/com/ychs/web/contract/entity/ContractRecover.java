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
import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("contract_recover")
@Schema(name = "ContractRecover", description = "合同收回记录")
public class ContractRecover extends Model<ContractRecover> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "合同ID")
    @TableField("contract_id")
    private Long contractId;

    @Schema(description = "收回日期")
    @TableField("recover_date")
    private Date recoverDate;

    @Schema(description = "合同PDF地址")
    @TableField("file_url")
    private String fileUrl;

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