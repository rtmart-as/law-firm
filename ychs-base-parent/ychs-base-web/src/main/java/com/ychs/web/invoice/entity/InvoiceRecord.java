package com.ychs.web.invoice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Data
@Accessors(chain = true)
@TableName(value = "invoice_record", autoResultMap = true)
@Schema(name = "InvoiceRecord", description = "发票登记")
public class InvoiceRecord extends Model<InvoiceRecord> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "登记年份")
    @TableField("record_year")
    private Integer recordYear;

    @Schema(description = "导入批次号")
    @TableField("batch_no")
    private String batchNo;

    @Schema(description = "发票号码")
    @TableField("invoice_no")
    private String invoiceNo;

    @Schema(description = "开票日期")
    @TableField("invoice_date")
    private Date invoiceDate;

    @Schema(description = "关联合同编号")
    @TableField("contract_no")
    private String contractNo;

    @Schema(description = "关联合同ID")
    @TableField("contract_id")
    private Long contractId;

    @Schema(description = "律师ID")
    @TableField("lawyer_id")
    private Long lawyerId;

    @Schema(description = "律师姓名")
    @TableField("lawyer_name")
    private String lawyerName;

    @Schema(description = "开票金额")
    @TableField("invoice_amount")
    private BigDecimal invoiceAmount;

    @Schema(description = "税额")
    @TableField("tax_amount")
    private BigDecimal taxAmount;

    @Schema(description = "价税合计")
    @TableField("invoice_total")
    private BigDecimal invoiceTotal;

    @Schema(description = "年度扩展列（键值对）")
    @TableField(value = "ext_json", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> extJson;

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