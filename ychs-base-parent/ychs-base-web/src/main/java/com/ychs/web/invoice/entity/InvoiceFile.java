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
@TableName("invoice_file")
@Schema(name = "InvoiceFile", description = "发票关联合同原件")
public class InvoiceFile extends Model<InvoiceFile> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "登记年份")
    @TableField("record_year")
    private Integer recordYear;

    @Schema(description = "发票记录ID")
    @TableField("invoice_id")
    private Long invoiceId;

    @Schema(description = "合同ID")
    @TableField("contract_id")
    private Long contractId;

    @Schema(description = "文件名")
    @TableField("file_name")
    private String fileName;

    @Schema(description = "文件地址")
    @TableField("file_url")
    private String fileUrl;

    @Schema(description = "上传人用户ID")
    @TableField("upload_user")
    private Long uploadUser;

    @Schema(description = "上传时间")
    @TableField("upload_time")
    private Date uploadTime;

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