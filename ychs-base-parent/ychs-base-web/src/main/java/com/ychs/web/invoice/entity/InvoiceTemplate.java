package com.ychs.web.invoice.entity;

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
@TableName("invoice_template")
@Schema(name = "InvoiceTemplate", description = "发票年度表头模板")
public class InvoiceTemplate extends Model<InvoiceTemplate> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "登记年份")
    @TableField("record_year")
    private Integer recordYear;

    @Schema(description = "列键")
    @TableField("col_key")
    private String colKey;

    @Schema(description = "列标题")
    @TableField("col_label")
    private String colLabel;

    @Schema(description = "列顺序")
    @TableField("col_order")
    private Integer colOrder;

    @Schema(description = "是否核心列 0否 1是")
    @TableField("is_core")
    private Byte isCore;

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