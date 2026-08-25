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
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
@TableName(value = "invoice_import_batch", autoResultMap = true)
@Schema(name = "InvoiceImportBatch", description = "发票导入批次")
public class InvoiceImportBatch extends Model<InvoiceImportBatch> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "登记年份")
    @TableField("record_year")
    private Integer recordYear;

    @Schema(description = "批次号")
    @TableField("batch_no")
    private String batchNo;

    @Schema(description = "上传文件名")
    @TableField("file_name")
    private String fileName;

    @Schema(description = "成功条数")
    @TableField("success_count")
    private Integer successCount;

    @Schema(description = "失败条数")
    @TableField("fail_count")
    private Integer failCount;

    @Schema(description = "失败明细")
    @TableField(value = "fail_detail", typeHandler = JacksonTypeHandler.class)
    private List<Map<String, Object>> failDetail;

    @Schema(description = "改动记录")
    @TableField(value = "change_log", typeHandler = JacksonTypeHandler.class)
    private List<Map<String, Object>> changeLog;

    @Schema(description = "导入人用户ID")
    @TableField("import_user")
    private Long importUser;

    @Schema(description = "导入时间")
    @TableField("import_time")
    private Date importTime;

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